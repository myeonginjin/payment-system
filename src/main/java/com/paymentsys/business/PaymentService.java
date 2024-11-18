package com.paymentsys.business;

import com.paymentsys.domains.dto.PaymentCallbackRequest;
import com.paymentsys.domains.dto.RequestPayDto;
import com.paymentsys.jpa.Order;
import com.paymentsys.jpa.enums.PaymentStatus;
import com.paymentsys.persistence.OrderRepository;
import com.paymentsys.persistence.PaymentRepository;
import com.paymentsys.presentation.usecase.PaymentUseCase;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentUseCase {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    @Override
    @Transactional
    public RequestPayDto findRequestDto(String orderUId) {
        Order order = orderRepository.findOrderAndPaymentAndMember(orderUId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        return RequestPayDto.builder()
                .buyerName(order.getMember().getUsername())
                .buyerEmail(order.getMember().getEmail())
                .buyerAddress(order.getMember().getAddress())
                .paymentPrice(order.getPayment().getPrice())
                .itemName(order.getItemName())
                .orderUid(order.getOrderUid())
                .build();
    }

    @Override
    @Transactional
    public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {
        try {

            log.info("결제 요청 처리 시작");

            try {
                // 결제 단건 조회(아임포트)
                IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
                // 주문내역 조회
                Order order = orderRepository.findOrderAndPayment(request.getOrderUid())
                        .orElseThrow(() -> new IllegalArgumentException("주문 내역이 없습니다."));

                // 결제 완료가 아니면
                if (!iamportResponse.getResponse().getStatus().equals("paid")) {
                    // 주문, 결제 삭제
                    orderRepository.delete(order);
                    paymentRepository.delete(order.getPayment());

                    throw new RuntimeException("결제 미완료");
                }

                // DB에 저장된 결제 금액
                Long price = order.getPayment().getPrice();
                // 실 결제 금액
                int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

                // 결제 금액 검증
                if (iamportPrice != price) {
                    // 주문, 결제 삭제
                    orderRepository.delete(order);
                    paymentRepository.delete(order.getPayment());

                    // 결제금액 위변조로 의심되는 결제금액을 취소(아임포트)
                    iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true, new BigDecimal(iamportPrice)));

                    throw new RuntimeException("결제금액 위변조 의심");
                }

                // 결제 상태 변경
                order.getPayment().changePaymentBySuccess(PaymentStatus.OK, iamportResponse.getResponse().getImpUid());

                return iamportResponse;

            } catch (IamportResponseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
