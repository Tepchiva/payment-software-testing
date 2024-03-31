package com.learning.paymentsoftwaretesting.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/registration")
    public void registerNewCustomer(@RequestBody @Validated CustomerRegistrationRequest request) {
        customerService.registerNewCustomer(request);
    }
}
