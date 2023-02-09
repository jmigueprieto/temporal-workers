package me.mprieto.temporal.mailgun.client;

import me.mprieto.temporal.model.email.EmailResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface EmailApi {
    @FormUrlEncoded
    @POST("messages")
    EmailResponse send(@Field("from") String from,
                       @Field("to") String to,
                       @Field("subject") String subject,
                       @Field("text") String text);
}
