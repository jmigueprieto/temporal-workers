package me.mprieto.temporal.session;

import me.mprieto.temporal.session.model.Session;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SessionActivity {

    @ActivityMethod
    Session findSessionById(String sessionId);

    @ActivityMethod
    void closeSession(String sessionId);
}
