package com.learning.paymentsoftwaretesting.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import com.learning.paymentsoftwaretesting.customer.CustomerRegistrationRequest;
import com.learning.paymentsoftwaretesting.customer.CustomerResponse;
import com.learning.paymentsoftwaretesting.exception.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class PaymentServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreatePaymentSuccessfully() throws Exception {
        // Given
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "chiva",
                "+85510337766",
                "em@gmail.com"
        );

        // ... Register customer and expect success with status 200
        ResultActions customerRegisterResultActions = mockMvc.perform(
                put("/v1/customers/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRegistrationRequest))
        ).andExpect(status().isOk());

        String customerDataAsString = customerRegisterResultActions.andReturn().getResponse().getContentAsString();
        SuccessResponse<CustomerResponse> responseSuccessResponse = objectMapper.readValue(customerDataAsString, new TypeReference<>() {});
        log.info("success response type of customer: {}", responseSuccessResponse);
        CustomerResponse customerResponse = responseSuccessResponse.getData();

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                customerResponse.id(),
                BigDecimal.valueOf(100.00),
                Currency.USD,
                "x0x0x0x0",
                "Payment from integration test."
        );

        // When payment is sent
        ResultActions paymentResultActions = mockMvc.perform(
                post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest))
        );

        // Then expect success with data id not empty
        paymentResultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(MessageResponseCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").isNotEmpty());

        String paymentDataAsString = paymentResultActions.andReturn().getResponse().getContentAsString();
        SuccessResponse<PaymentResponse> paymentResponseSuccessResponse = objectMapper.readValue(paymentDataAsString, new TypeReference<>() {});
        log.info("success response type of payment: {}", paymentResponseSuccessResponse);
        PaymentResponse paymentResponse = paymentResponseSuccessResponse.getData();

        // ... Find payment in database
        ResultActions getPaymentResultActions = mockMvc.perform(get("/v1/payments/{id}", paymentResponse.id()));
        getPaymentResultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(MessageResponseCode.SUCCESS.getCode()));
    }
}
