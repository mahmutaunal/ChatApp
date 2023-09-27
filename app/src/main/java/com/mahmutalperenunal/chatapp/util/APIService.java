package com.mahmutalperenunal.chatapp.util;

import com.mahmutalperenunal.chatapp.notification.MyResponse;
import com.mahmutalperenunal.chatapp.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAKUYYlYM:APA91bHFB-gBhw6fz0Vujj7_SHelXxM9OdIKoJXca965xQ58zlAMG4ySNWN9fkxdALCSwtN40koJyybZeAfK0tDzCib8G48o1mRXqOVPEZqxtOUuV53G9FOUOQSOI341kFPXmilTApOr"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
