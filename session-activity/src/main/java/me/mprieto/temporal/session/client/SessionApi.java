package me.mprieto.temporal.session.client;

import me.mprieto.temporal.model.session.Session;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SessionApi {

    @GET("sessions/{id}")
    Session getSession(@Path("id") String id);

}
