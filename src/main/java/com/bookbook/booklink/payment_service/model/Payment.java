package com.bookbook.booklink.payment_service.model;

import com.bookbook.booklink.auth_service.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "결제 내역 엔티티 - 결제, 취소 등 이력을 기록")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Schema(
            description = "결제 내역 고유 식별자 (UUID)",
            example = "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Min(value = 0, message = "금액은 0 이상이어야 합니다.")
    @Column(nullable = false)
    @Schema(
            description = "결제 금액",
            example = "15000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(
            description = "결제 수단 (예: CARD, TRANSFER)",
            example = "CARD",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(
            description = "결제 상태 (예: PENDING, APPROVED, REJECTED)",
            example = "PENDING",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private PaymentStatus status;

    @Size(max = 100)
    @Column(nullable = false)
    @Schema(
            description = "우리 도메인에서 생성한 고유 결제 ID",
            example = "order_1a2b3c4d5e",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull
    @Schema(description = "포인트를 사용한 사용자")
    private Member member;

    @CreationTimestamp
    @Column(nullable = false)
    @Schema(
            description = "결제 생성 시각 (자동 기록)",
            example = "2025-09-26T19:20:30",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime createdAt;

    public void paymentApprove() {
        this.status = PaymentStatus.APPROVED;
    }

    public void paymentCancel() {
        this.status = PaymentStatus.CANCEL;
    }

    public void paymentReject() {
        this.status = PaymentStatus.REJECTED;
    }
}
