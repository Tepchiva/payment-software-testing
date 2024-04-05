package com.learning.paymentsoftwaretesting.customer;

import com.learning.paymentsoftwaretesting.exception.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    @PutMapping("/registration")
    public ResponseEntity<SuccessResponse<CustomerResponse>> registerNewCustomer(@RequestBody @Validated CustomerRegistrationRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(customerService.registerNewCustomer(request)), HttpStatus.CREATED);
    }
}
