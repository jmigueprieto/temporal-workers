package me.mprieto.temporal.mailgun;

import me.mprieto.temporal.activities.EmailSenderActivity;
import me.mprieto.temporal.mailgun.client.EmailApi;
import me.mprieto.temporal.model.email.EmailRequest;
import me.mprieto.temporal.model.email.EmailResponse;

public class MailgunActivityImpl implements EmailSenderActivity {

    private final EmailApi emailApi;
    private final String defaultFrom;

    public MailgunActivityImpl(EmailApi emailApi, String defaultFrom) {
        this.emailApi = emailApi;
        this.defaultFrom = defaultFrom;
    }

    @Override
    public EmailResponse sendEmail(EmailRequest request) {
        var from = request.getFrom() != null? request.getFrom() : defaultFrom;
        return emailApi.send(from, request.getTo(), request.getSubject(), request.getText());
    }
}
