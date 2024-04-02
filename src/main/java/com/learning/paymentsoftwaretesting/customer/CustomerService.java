package com.learning.paymentsoftwaretesting.customer;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import com.learning.paymentsoftwaretesting.exception.AppException;
import com.learning.paymentsoftwaretesting.mapper.CustomerMapper;
import com.learning.paymentsoftwaretesting.utils.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    public void registerNewCustomer(CustomerRegistrationRequest customerRegistrationRequest) {

        phoneNumberValidator(customerRegistrationRequest.phoneNo());

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
