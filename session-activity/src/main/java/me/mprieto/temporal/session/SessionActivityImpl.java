package me.mprieto.temporal.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mprieto.temporal.activities.SessionActivity;
import me.mprieto.temporal.model.session.Session;
import me.mprieto.temporal.session.client.SessionApi;

@RequiredArgsConstructor
@Slf4j
public class SessionActivityImpl implements SessionActivity {

    private final SessionApi sessionApi;

    @Override
    public Session findSessionById(String sessionId) {
        var session = sessionApi.getSession(sessionId);
        if (session == null) {
            log.warn("Session '{}' not found", sessionId);
        } else {
            log.info("Found session '{}' with id '{}'", sessionId, sessionId);
        }

        return session;
    }

    @Override
    public void closeSession(String sessionId) {
        //TODO in this implementation using Wiremock Cloud this doesn't make much sense
    }
}
