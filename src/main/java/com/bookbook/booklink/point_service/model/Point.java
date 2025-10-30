package com.bookbook.booklink.point_service.model;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "포인트 엔티티 - 회원의 포인트 잔액을 관리하는 객체")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point {

    @Id
    @Column(name = "member_id")
    @Schema(description = "포인트 엔티티의 ID (Member ID와 동일)")
    private UUID memberId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    @NotNull
    @Schema(description = "포인트를 소유한 사용자 엔티티")
    private Member member;

    @Min(value = 0, message = "잔액은 양수여야합니다.")
    @Column(nullable = false)
    @Schema(
            description = "포인트 잔액",
            example = "10000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer balance;

    /**
     * 포인트 사용 (차감)
     *
     * @param usedPoint 사용된 포인트
     */
    public void usePoint(Integer usedPoint) {
        if (this.balance < usedPoint) {
            throw new CustomException(ErrorCode.POINT_NOT_ENOUGH);
        }
        this.balance -= usedPoint;
    }

    public void addPoint(Integer addedPoint) {
        this.balance += addedPoint;
    }
}
