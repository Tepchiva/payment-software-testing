package com.learning.paymentsoftwaretesting.customer;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import com.learning.paymentsoftwaretesting.exception.AppException;
import com.learning.paymentsoftwaretesting.mapper.CustomerMapper;
import com.learning.paymentsoftwaretesting.utils.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    public CustomerResponse registerNewCustomer(CustomerRegistrationRequest customerRegistrationRequest) {

        MDC.put("email", customerRegistrationRequest.email());
        MDC.put("phone_no", customerRegistrationRequest.phoneNo());

        phoneNumberValidator(customerRegistrationRequest.phoneNo());

        customerRepository
                .findCustomerByPhoneNo(customerRegistrationRequest.phoneNo())
                .ifPresent(
                        customer -> {
                            if (customer.getName().equalsIgnoreCase(customerRegistrationRequest.name())) {
                                throw new AppException(MessageResponseCode.CUSTOMER_ALREADY_REGISTERED, customer.getName());
                            }
                            throw new AppException(MessageResponseCode.PHONE_NUMBER_ALREADY_REGISTERED, customer.getPhoneNo());
                        }
                );

        Customer customer = customerRepository.save(CustomerMapper.INSTANCE.mapToCustomer(customerRegistrationRequest));
        return CustomerMapper.INSTANCE.mapToCustomerResponse(customer);
    }

    private void phoneNumberValidator(String phoneNumber) {
        /// validate phone number is valid
        if (!phoneNumberValidator.test(phoneNumber)) {
            throw new AppException(MessageResponseCode.PHONE_NUMBER_NOT_VALID, phoneNumber);
        }
    }

    public CustomerResponse getCustomerByPhoneNo(String phoneNo) {

        phoneNumberValidator(phoneNo);

        return customerRepository
                .findCustomerByPhoneNo(phoneNo)
                .map(CustomerMapper.INSTANCE::mapToCustomerResponse)
                .orElseThrow(
                        () -> new AppException(MessageResponseCode.RESOURCE_NOT_FOUND, "Customer")
                );
    }
}
