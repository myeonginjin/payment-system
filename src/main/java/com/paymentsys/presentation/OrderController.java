package com.paymentsys.presentation;

import com.paymentsys.business.MemberService;
import com.paymentsys.business.OrderService;
import com.paymentsys.jpa.Member;
import com.paymentsys.jpa.Order;
import com.paymentsys.presentation.usecase.MemberUseCase;
import com.paymentsys.presentation.usecase.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final MemberUseCase memberService;
    private final OrderUseCase orderService;

    @GetMapping("/order")
    public String order(@RequestParam(name = "message", required = false) String message,
                        @RequestParam(name = "orderUid", required = false) String id,
                        Model model) {

        model.addAttribute("message", message);
        model.addAttribute("orderUid", id);

        return "order";
    }

    @PostMapping("/order")
    public String autoOrder() {
        Member member = memberService.autoRegister();
        Order order = orderService.autoOrder(member);

        String message = "주문 실패";
        if(order != null) {
            message = "주문 성공";
        }

        String encode = URLEncoder.encode(message, StandardCharsets.UTF_8);

        return "redirect:/order?message="+encode+"&orderUid="+order.getOrderUid();
    }
}
