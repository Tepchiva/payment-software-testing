package com.learning.paymentsoftwaretesting.payment;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import com.learning.paymentsoftwaretesting.customer.CustomerRegistrationRequest;
import com.learning.paymentsoftwaretesting.customer.CustomerResponse;
import com.learning.paymentsoftwaretesting.exception.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.Objects;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Transactional
//@TestPropertySource(
//        properties = {
//                "stripe.enabled=false"
//        }
//)
class PaymentServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldCreatePaymentSuccessfully() {
        // Given
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "chiva",
                "+85510337766",
                ""
        );

        // ... Register customer and expect success with status 200
        ResponseEntity<SuccessResponse<CustomerResponse>> customerResponseEntity = restTemplate.exchange(
                "/v1/customers/registration",
                HttpMethod.PUT,
                new HttpEntity<>(customerRegistrationRequest),
                new ParameterizedTypeReference<>() {}
        );
        // ... Expect success with status 201 (created)
        assertThat(customerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CustomerResponse customerResponse = Objects.requireNonNull(customerResponseEntity.getBody()).getData();

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                customerResponse.id(),
                BigDecimal.valueOf(100.00),
                Currency.USD,
                "tok_visa",
                "Payment from integration test."
        );

        // When payment is sent
        ResponseEntity<SuccessResponse<PaymentResponse>> paymentResponseEntity = restTemplate.exchange(
                "/v1/payments",
                HttpMethod.POST,
                new HttpEntity<>(paymentRequest),
                new ParameterizedTypeReference<>() {}
        );

        // ... Expect success with status 200
        assertThat(paymentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SuccessResponse<PaymentResponse> paymentResponseSuccessResponse = paymentResponseEntity.getBody();
        // ... Expect response code is SUC-000
        assertThat(Objects.requireNonNull(paymentResponseSuccessResponse).getCode()).isEqualTo(MessageResponseCode.SUCCESS.getCode());
        // ... Expect data id is not id
        assertThat(Objects.requireNonNull(paymentResponseSuccessResponse.getData()).id()).isNotNull();

        // ... Find payment just make
        ResponseEntity<SuccessResponse<PaymentResponse>> getPaymentResponseEntity = restTemplate.exchange(
                "/v1/payments/{id}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                Objects.requireNonNull(paymentResponseSuccessResponse.getData()).id()
        );

        // ... Expect success with status 200
        assertThat(getPaymentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SuccessResponse<PaymentResponse> getPaymentResponseSuccessResponse = getPaymentResponseEntity.getBody();
        // ... Expect response code is SUC-000
        assertThat(Objects.requireNonNull(getPaymentResponseSuccessResponse).getCode()).isEqualTo(MessageResponseCode.SUCCESS.getCode());
        assertThat(Objects.requireNonNull(getPaymentResponseSuccessResponse.getData()).id()).isEqualTo(Objects.requireNonNull(paymentResponseSuccessResponse.getData()).id());
    }
}
