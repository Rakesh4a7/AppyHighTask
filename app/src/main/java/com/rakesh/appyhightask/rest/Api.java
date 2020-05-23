package com.rakesh.appyhightask.rest;


import com.rakesh.appyhightask.model.Notify;
import com.rakesh.appyhightask.response.LocationResponse;
import com.rakesh.appyhightask.response.NewsResponse;
import com.rakesh.appyhightask.response.NotificationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @GET("json")
    Call<LocationResponse> getLocation();

    @GET("top-headlines")
    Call<NewsResponse> getNewsList(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

    @POST("notifications")
    Call<NotificationResponse> pushNotification(
            @Header("Authorization") String Authorization,
            @Header("Content-Type") String contentType,
            @Body Notify notify
    );

}
