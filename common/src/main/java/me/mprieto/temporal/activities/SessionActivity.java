package me.mprieto.temporal.activities;

import me.mprieto.temporal.model.session.Session;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SessionActivity {

    @ActivityMethod
    Session findSessionById(String sessionId);

    @ActivityMethod
    void closeSession(String sessionId);
}
