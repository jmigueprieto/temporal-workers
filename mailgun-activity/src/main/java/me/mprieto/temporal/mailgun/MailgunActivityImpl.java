package me.mprieto.temporal.mailgun;

import io.split.client.SplitClient;
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
    private final SplitClient splitClient;

    @Override
    public EmailResponse sendEmail(EmailRequest request) {
        var from = request.getFrom() != null ? request.getFrom() : defaultFrom;
        var emailText = getEmailText(request);

        log.info("Sending email with Mailgun {}", request);
        return emailApi.send(from, request.getTo(), request.getSubject(), emailText);
    }

    private String getEmailText(EmailRequest request) {
        // we could use this to enable and test mailgun templates
        var treatment = splitClient.getTreatment(request.getUserId(), "feature_x_email_footer");

        if ("on".equals(treatment)) {
            return String.format("%s\n\n---\nDon't forget to try out our new awesome feature X!", request.getText());
        }

        return request.getText();
    }
}
