package com.learning.paymentsoftwaretesting.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @Query(value = "select customer from Customer customer where customer.phoneNo = :phone_no")
    Optional<Customer> findCustomerByPhoneNo(@Param("phone_no") String phoneNo);
}
