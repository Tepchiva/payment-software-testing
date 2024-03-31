package com.learning.paymentsoftwaretesting.customer;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record CustomerRegistrationRequest(@Nonnull String name, @Nonnull String phoneNo, String email) { }
