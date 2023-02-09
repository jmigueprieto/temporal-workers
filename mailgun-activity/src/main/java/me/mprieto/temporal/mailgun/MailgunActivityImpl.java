package me.mprieto.temporal.mailgun;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mprieto.temporal.activities.EmailSenderActivity;
import me.mprieto.temporal.mailgun.client.EmailApi;
import me.mprieto.temporal.model.email.EmailRequest;
import me.mprieto.temporal.model.email.EmailResponse;

@RequiredArgsConstructor
@Slf4j
public class MailgunActivityImpl implements EmailSenderActivity {

    private final EmailApi emailApi;
    private final String defaultFrom;

    @Override
    public EmailResponse sendEmail(EmailRequest request) {
        var from = request.getFrom() != null ? request.getFrom() : defaultFrom;
        log.info("Sending email with Mailgun {}", request);
        return emailApi.send(from, request.getTo(), request.getSubject(), request.getText());
    }
}
