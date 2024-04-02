package com.learning.paymentsoftwaretesting.utils;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PhoneNumberValidator implements Predicate<String> {

    private static final String PHONE_NUMBER_REGEX ="^\\+(?:\\d ?){6,14}\\d$";
    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);

    @Override
    public boolean test(String phoneNumber) {
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
