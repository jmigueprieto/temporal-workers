package me.mprieto.temporal.stripe;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface StripeActivity {

    /**
     * Charges the customer the due amount.
     * <p>
     * It uses the user's default payment method.
     *
     * @param customerId customer id
     * @param due        amount to charge
     */
    @ActivityMethod
    void charge(String customerId, long due);

}
