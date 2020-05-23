package com.rakesh.appyhightask.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.rakesh.appyhightask.repository.Repository;
import com.rakesh.appyhightask.response.LocationResponse;
import com.rakesh.appyhightask.response.NewsResponse;
import com.rakesh.appyhightask.response.NotificationResponse;

public class MyViewModel extends ViewModel {

    private Repository repository;

    public MyViewModel(){
        repository = new Repository();
    }

    public LiveData<NewsResponse> getNews(String location){
        return repository.fetchNews(location);
    }

    public LiveData<LocationResponse> getLocation(){
        return repository.getLocation();
    }

    public LiveData<NotificationResponse> push(String title, String body, String url, String imageUrl){
        return repository.pushNotify(title,body,url,imageUrl);
    }

    public LiveData<NotificationResponse> pushWithoutImage(String title, String body, String url){
        return repository.pushNotifyWithoutImage(title,body,url);
    }

}
