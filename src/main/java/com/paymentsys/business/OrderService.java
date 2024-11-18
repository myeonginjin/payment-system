package com.paymentsys.business;

import com.paymentsys.jpa.Member;
import com.paymentsys.jpa.Order;
import com.paymentsys.jpa.Payment;
import com.paymentsys.jpa.enums.PaymentStatus;
import com.paymentsys.persistence.OrderRepository;
import com.paymentsys.persistence.PaymentRepository;
import com.paymentsys.presentation.usecase.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Order autoOrder(Member member) {

        //임시 결제 내역 생성
        Payment payment = Payment.builder()
                .price(1000L)
                .status(PaymentStatus.READY)
                .build();

        paymentRepository.save(payment);

        // 주문 생성
        Order order = Order.builder()
                .member(member)
                .price(1000L)
                .itemName("명인이에게 후원")
                .orderUid(UUID.randomUUID().toString())
                .payment(payment)
                .build();

        return orderRepository.save(order);
    }
}
