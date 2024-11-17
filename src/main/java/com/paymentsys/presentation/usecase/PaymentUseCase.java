package com.paymentsys.presentation.usecase;

import com.paymentsys.domains.dto.PaymentCallbackRequest;
import com.paymentsys.domains.dto.RequestPayDto;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

public interface PaymentUseCase {

    //결제 요청 데아터 조회
    RequestPayDto findRequestDto(String orderId);

    //결제(콜백)
    IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request);
}
