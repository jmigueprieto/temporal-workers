package me.mprieto.temporal.activities;

import me.mprieto.temporal.activities.model.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class SessionActivityImpl implements SessionActivity {

    //TODO Remove the hardcoded sessions and add a way to add and close sessions through the console
    private final Map<String, Session> sessions = Map.of(
            "session_01", Session.builder()
                    .status(Session.Status.OPEN)
                    .stripeCustomerId("cus_IzssscT57x9e8K")
                    .amount(79700)
                    .build(),
            "session_02", Session.builder()
                    .status(Session.Status.OPEN)
                    .stripeCustomerId("cus_IzssscT57x9e8K")
                    .amount(10000)
                    .build(),
            "session_03", Session.builder()
                    .status(Session.Status.OPEN)
                    .stripeCustomerId("cus_IzssscT57x9e8K")
                    .amount(9999)
                    .build());

    @Override
    public Session findSessionById(String sessionId) {
        var session = sessions.get(sessionId);
        if (session == null) {
            log.warn("Session '{}' not found", sessionId);
        } else {
            log.info("Found session '{}' with id '{}'", sessionId, sessionId);
        }

        return session;
    }

    @Override
    public void closeSession(String sessionId) {
        var session = sessions.get(sessionId);
        if (session == null) {
            log.warn("Session '{}' not found", sessionId);
            return;
        }

        if (session.getStatus() == Session.Status.CLOSED) {
            log.warn("Session '{}' is already closed", sessionId);
            return;
        }

        session.setStatus(Session.Status.CLOSED);
    }
}