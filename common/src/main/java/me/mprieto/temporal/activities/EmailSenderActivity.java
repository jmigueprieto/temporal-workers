package me.mprieto.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import me.mprieto.temporal.model.email.EmailRequest;
import me.mprieto.temporal.model.email.EmailResponse;

@ActivityInterface
public interface EmailSenderActivity {

    @ActivityMethod
    EmailResponse sendEmail(EmailRequest request);
}
