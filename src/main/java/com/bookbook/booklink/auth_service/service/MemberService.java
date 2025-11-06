package com.bookbook.booklink.auth_service.service;

import com.bookbook.booklink.common.event.LockEvent;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.service.IdempotencyService;
import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.model.dto.request.SignUpReqDto;
import com.bookbook.booklink.auth_service.model.dto.request.UpdateReqDto;
import com.bookbook.booklink.auth_service.model.dto.response.ProfileResDto;
import com.bookbook.booklink.auth_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdempotencyService idempotencyService;

    /**
     * 회원가입
     * <p>
     * 동일 traceId 요청 시 중복 처리 방지를 위해 Redis Lock 체크 수행
     *
     * @param signUpReqDto 회원 가입 정보 DTO
     * @param traceId         요청 멱등성 체크용 ID (클라이언트 전달)
     * @return 등록된 Member ID
     */
    @Transactional
    public Member signUp(SignUpReqDto signUpReqDto, String traceId) {

        log.info("[MemberService] [traceId={}] signup member initiate, name={}",
                traceId, signUpReqDto.getName());

        String key = idempotencyService.generateIdempotencyKey("member:signup", traceId);

        // Redis Lock으로 멱등성 체크
        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        // 이메일 중복 확인
        if (memberRepository.existsByEmail(signUpReqDto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (memberRepository.existsByNickname(signUpReqDto.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(signUpReqDto.getPassword());

        // Library 엔티티 생성 후 DB 저장
        Member newMember = Member.ofLocalSignup(signUpReqDto,encodedPassword);
        Member saveMember = memberRepository.save(newMember);

        log.info("[MemberService] [traceId={}] signup member success, name={}",
                traceId, saveMember.getName());

        return saveMember;
    }

    /**
     * 주어진 UUID로 Member 엔티티를 조회합니다.
     * <p>
     * DB에서 해당 ID의 Member가 존재하지 않을 경우 {@link CustomException}을 발생시킵니다.
     * 이 메서드는 여러 서비스 계층에서 공통적으로 사용할 수 있는
     * "회원 조회 유틸리티" 메서드 역할을 합니다.
     *
     * @param memberID 조회할 회원의 UUID
     * @return Member 엔티티
     * @throws CustomException {@link ErrorCode#USER_NOT_FOUND} - 회원이 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Member getMemberOrThrow(UUID memberID){
        return memberRepository.findById(memberID)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 로그인한 사용자의 프로필 정보를 조회합니다.
     * <p>
     * 내부적으로 {@link #getMemberOrThrow(UUID)}를 호출하여 Member 엔티티를 가져온 뒤,
     * {@link ProfileResDto} 형태로 변환하여 반환합니다.
     *
     * @param memberId 로그인한 회원의 UUID
     * @return 프로필 응답 DTO ({@link ProfileResDto})
     * @throws CustomException {@link ErrorCode#USER_NOT_FOUND} - 회원이 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public ProfileResDto getMyProfile(UUID memberId) {
        Member member = getMemberOrThrow(memberId);
        return ProfileResDto.from(member);
    }

    /**
     * 회원 프로필 수정
     *
     * <p>회원 ID를 통해 기존 회원 엔티티를 조회한 뒤,
     * 요청 DTO(UpdateReqDto)에서 전달된 값으로 회원 정보를 업데이트합니다.
     *
     * <ul>
     *   <li>닉네임, 주소, 전화번호, 프로필 이미지 등 기본 프로필 정보 수정 가능</li>
     *   <li>존재하지 않는 회원 ID일 경우 {@link CustomException} with {@link ErrorCode#USER_NOT_FOUND} 발생</li>
     *   <li>트랜잭션 내에서 동작하며 수정된 회원 엔티티는 DB에 즉시 반영됩니다.</li>
     * </ul>
     *
     * @param memberId 수정할 회원의 고유 ID(UUID)
     * @param reqDto   회원 수정 요청 DTO (닉네임, 주소, 전화번호, 프로필 이미지 포함)
     * @return 수정이 완료된 회원 엔티티
     * @throws CustomException 회원 ID가 존재하지 않을 경우 예외 발생
     */
    @Transactional
    public Member updateProfile(UUID memberId, UpdateReqDto reqDto) {
        Member member = getMemberOrThrow(memberId);
        member.updateMemberInfo(reqDto);
        return memberRepository.save(member);
    }
}
    