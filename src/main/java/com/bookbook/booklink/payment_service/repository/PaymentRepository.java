package com.bookbook.booklink.payment_service.repository;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.payment_service.model.Payment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    boolean existsByPaymentId(String paymentId);

    Optional<Payment> findByPaymentId(String paymentId);

    List<Payment> findAllByMember(@NotNull Member member);
}
    