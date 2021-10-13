package com.example.demo_project.Notification;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.Call;

public interface APISERVICE {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAzv-TVcg:APA91bFIcbiezSYhMKGwTSaaQQUCCWIixl93eRMz4LIfnPXVi2AiKXhkEBEyuaS2_dUJlm2ofYobCwsRD6rOv7kGAykIvYxW6YMDgFyRruzWFaid3QZJ-ht3t75xExjfZh2qlMShXOP"

    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}
