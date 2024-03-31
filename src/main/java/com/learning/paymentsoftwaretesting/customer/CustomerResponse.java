package com.learning.paymentsoftwaretesting.customer;

import jakarta.annotation.Nonnull;
import java.util.UUID;

public record CustomerResponse(@Nonnull UUID id, @Nonnull String name, @Nonnull String phoneNo, String email) { }
