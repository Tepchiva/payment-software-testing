package com.learning.paymentsoftwaretesting.customer;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import com.learning.paymentsoftwaretesting.exception.AppException;
import com.learning.paymentsoftwaretesting.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public void registerNewCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        customerRepository
                .findCustomerByPhoneNo(customerRegistrationRequest.phoneNo())
                .ifPresentOrElse(
                        customer -> {
                            if (customer.getName().equalsIgnoreCase(customerRegistrationRequest.name())) {
                                return;
                            }
                            throw new AppException(MessageResponseCode.PHONE_NUMBER_ALREADY_REGISTERED, customer.getPhoneNo());
                        },
                        () -> customerRepository.save(CustomerMapper.INSTANCE.mapToCustomer(customerRegistrationRequest))
                );
    }

    public CustomerResponse getCustomerByPhoneNo(String phoneNo) {
        return customerRepository
                .findCustomerByPhoneNo(phoneNo)
                .map(CustomerMapper.INSTANCE::mapToCustomerResponse)
                .orElseThrow(
                        () -> new AppException(MessageResponseCode.RESOURCE_NOT_FOUND, "Customer")
                );
    }
}
