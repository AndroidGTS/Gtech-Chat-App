package com.gtechapps.chatmessager.Fragments;

import com.gtechapps.chatmessager.Notifications.MyResponse;
import com.gtechapps.chatmessager.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA097xB_4:APA91bF4-nLO5nT2yMN5gp9gR9g5WexHJFWBG0-9Mq6jZ26D3bBf38PAwfcvjWTDE3V9Ix0DLrPpPIRi70dub4C0JTqpKM8bAPgsPoC4ox0RD-rQoY3bLEyPrqANDnxUOVBJOV79jIQi"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
