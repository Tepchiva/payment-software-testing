package com.learning.paymentsoftwaretesting.mapper;

import com.learning.paymentsoftwaretesting.customer.Customer;
import com.learning.paymentsoftwaretesting.customer.CustomerRegistrationRequest;
import com.learning.paymentsoftwaretesting.customer.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerMapper {

     CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

     @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
     Customer mapToCustomer(CustomerRegistrationRequest customerRegistrationRequest);

     CustomerResponse mapToCustomerResponse(Customer customer);
}
