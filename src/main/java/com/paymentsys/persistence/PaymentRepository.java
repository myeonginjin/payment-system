package com.paymentsys.persistence;

import com.paymentsys.jpa.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
