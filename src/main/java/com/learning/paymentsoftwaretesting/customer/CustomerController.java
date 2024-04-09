package com.learning.paymentsoftwaretesting.customer;

import com.learning.paymentsoftwaretesting.exception.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final RestTemplate restTemplate;

    @PutMapping("/registration")
    public ResponseEntity<SuccessResponse<CustomerResponse>> registerNewCustomer(@RequestBody @Validated CustomerRegistrationRequest request) {

        restTemplate.getForObject("https://www.youtube.com/", String.class);

        return ResponseEntity.ok(new SuccessResponse<>(customerService.registerNewCustomer(request)));
    }
}
