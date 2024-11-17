package com.paymentsys.presentation.usecase;

import com.paymentsys.jpa.Member;
import com.paymentsys.jpa.Order;

public interface OrderUseCase {
    Order autoOrder(Member member);
}
