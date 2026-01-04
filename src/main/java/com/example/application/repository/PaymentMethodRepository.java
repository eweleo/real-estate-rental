package com.example.application.repository;

import com.example.application.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
        List<PaymentMethod> findByUserIdOrderByIsDefaultDescAddedDateDesc(Long userId);
        Optional<PaymentMethod> findByUserIdAndIsDefaultTrue(Long userId);
}
