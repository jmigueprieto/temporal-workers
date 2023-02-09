package me.mprieto.temporal.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import lombok.extern.slf4j.Slf4j;
import me.mprieto.temporal.stripe.StripeActivity;

import java.util.HashMap;

@Slf4j
public class StripeActivityImpl implements StripeActivity {

    @Override
    public void charge(String customerId, long due) {
        log.info("Charging '{}' to customer '{}'", due, customerId);

        var params = new HashMap<String, Object>();
        params.put("currency", "usd");
        params.put("amount", due);
        params.put("customer", customerId);
        params.put("description", "Charges from Temporal StripeActivity");

        try {
            Charge.create(params);
            log.info("Successfully charged '{}' to customer '{}'", due, customerId);
        } catch (StripeException e) {
            log.error("Error while charging '{}' to customer '{}'", due, customerId, e);
            throw new RuntimeException(e);
        }
    }
}
