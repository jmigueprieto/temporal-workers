package me.mprieto.temporal.mailgun.client;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmailApiFactory {

    public static EmailApi create(String baseUrl, String username, String password) {
        var okHttpClient = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            var originalRequest = chain.request();

            var builder = originalRequest.newBuilder()
                    .header("Authorization", Credentials.basic(username, password));

            var newRequest = builder.build();
            return chain.proceed(newRequest);
        }).build();

        var retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(SynchronousCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(EmailApi.class);
    }
}
