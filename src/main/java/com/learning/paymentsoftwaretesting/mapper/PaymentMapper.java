package com.learning.paymentsoftwaretesting.mapper;

import com.learning.paymentsoftwaretesting.payment.Payment;
import com.learning.paymentsoftwaretesting.payment.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    // ... map payment request to Payment object
    @Mapping(target = "id", ignore = true)
    Payment mapToPayment(PaymentRequest paymentRequest);

}
