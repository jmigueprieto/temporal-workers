package me.mprieto.temporal.exceptions;

import io.temporal.failure.TemporalFailure;

public class CheckoutException extends TemporalFailure {
    public CheckoutException(String message) {
        super(message, message, null);
    }
}
