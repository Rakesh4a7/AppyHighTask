package com.rakesh.appyhightask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.rakesh.appyhightask.R;
import com.rakesh.appyhightask.adapter.NewsAdapter;
import com.rakesh.appyhightask.databinding.ActivityMainBinding;
import com.rakesh.appyhightask.model.NewsList;
import com.rakesh.appyhightask.response.LocationResponse;
import com.rakesh.appyhightask.response.NewsResponse;
import com.rakesh.appyhightask.viewmodel.MyViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private MyViewModel viewModel;
    private ActivityMainBinding binding;
    private NewsList firstNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
        viewModel = new ViewModelProvider(this).get(MyViewModel.class);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        final AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(false);
        binding.firstNewsLayout.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.GONE);

        binding.firstNewsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstNews != null){
                    Intent intent = new Intent(MainActivity.this, NewsDetail.class);
                    intent.putExtra("GSON", new Gson().toJson(firstNews));
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this,"some error occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });

        fetchNews();

        binding.homeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNews();
            }
        });

    }

    private void fetchNews() {
        binding.homeRefresh.setRefreshing(true);
        viewModel.getLocation().observe(this, new Observer<LocationResponse>() {
            @Override
            public void onChanged(LocationResponse locationResponse) {

                if (locationResponse != null){
                    Toast.makeText(MainActivity.this,"Current Location : " + locationResponse.getCountry(),Toast.LENGTH_LONG).show();
                    viewModel.getNews(locationResponse.getCountryCode().toLowerCase()).observe(MainActivity.this, new Observer<NewsResponse>() {
                        @Override
                        public void onChanged(NewsResponse newsResponse) {
                            try {

                                if (newsResponse != null && newsResponse.getTotalResults() > 0){
                                    ArrayList<NewsList> newsList = newsResponse.getNewsLists();

                                    int position = 0;
                                    for (NewsList item : newsList){
                                        if (item.getUrlToImage() != null && item.getTitle() != null){
                                            Glide.with(MainActivity.this).load(item.getUrlToImage()).placeholder(R.drawable.ic_sms_grey).into(binding.image);
                                            if (item.getAuthor() != null){
                                                binding.author.setText("By "+ item.getAuthor());
                                            }else {
                                                binding.author.setVisibility(View.GONE);
                                            }
                                            binding.title.setText(item.getTitle());
                                            firstNews = item;
                                            break;
                                        }else{
                                            position++;
                                        }
                                    }

                                    ArrayList<NewsList> restNewsList = new ArrayList<>();
                                    for (int i =0 ;i<newsList.size(); i++){
                                        if (i != position && Objects.requireNonNull(!newsList.get(i).getUrl().contains("timesofindia"))){
                                            restNewsList.add(newsList.get(i));
                                        }
                                    }
                                    NewsAdapter newsAdapter = new NewsAdapter(restNewsList,MainActivity.this);
                                    binding.recyclerView.setAdapter(newsAdapter);
                                    newsAdapter.notifyDataSetChanged();
                                    binding.homeRefresh.setRefreshing(false);
                                    binding.firstNewsLayout.setVisibility(View.VISIBLE);
                                    binding.recyclerView.setVisibility(View.VISIBLE);
                                }else {
                                    binding.homeRefresh.setRefreshing(false);
                                    Toast.makeText(MainActivity.this,"Some error occurred, Please try aging later",Toast.LENGTH_LONG).show();
                                }

                            }catch (NullPointerException e){
                                Log.e("mainActivity",e.getMessage());
                            }
                        }
                    });
                }else{
                    binding.homeRefresh.setRefreshing(false);
                    Toast.makeText(MainActivity.this,"Some error occurred, Please try aging later",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
