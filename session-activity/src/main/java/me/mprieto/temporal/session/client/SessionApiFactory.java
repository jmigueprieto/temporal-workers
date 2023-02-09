package me.mprieto.temporal.session.client;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionApiFactory {

    public static SessionApi create(String baseUrl) {
        var retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(SynchronousCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(SessionApi.class);
    }
}
