package com.bookbook.booklink.community.group_service.model;

import com.bookbook.booklink.auth_service.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모임(그룹)에 참여한 사용자")
public class GroupMember {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "고유 식별 ID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull
    @Schema(description = "참여한 사용자")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    @NotNull
    @Schema(description = "사용자가 참여한 모임")
    private Group group;

    public static GroupMember addMember(Group group, Member host) {
        return GroupMember.builder()
                .group(group)
                .member(host)
                .build();
    }
}
