package com.learning.paymentsoftwaretesting.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MessageResponseCode {
    SUCCESS("SUC-000", "Success", HttpStatus.OK),
    ERROR("ERR-000", "Error", HttpStatus.INTERNAL_SERVER_ERROR),
    PHONE_NUMBER_ALREADY_REGISTERED("ERR-001", "Phone number [%s] already registered", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("ERR-002", "%s resource not found", HttpStatus.NOT_FOUND),
    CURRENCY_NOT_SUPPORTED("ERR-003", "Currency [%s] not supported", HttpStatus.BAD_REQUEST),
    CARD_NOT_DEBITED("ERR-004", "Card not debited for customer [%s]", HttpStatus.BAD_REQUEST),
    AMOUNT_BELOW_MINIMUM("ERR-005", "Amount must be at least [%s]c", HttpStatus.BAD_REQUEST),
    FAILED_PAYMENT("ERR-006", "Failed to process payment", HttpStatus.EXPECTATION_FAILED)
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
