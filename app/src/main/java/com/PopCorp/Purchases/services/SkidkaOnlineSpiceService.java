package com.PopCorp.Purchases.services;

import com.PopCorp.Purchases.net.API;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

public class SkidkaOnlineSpiceService extends RetrofitGsonSpiceService {

    public static final String BASE_URL = "http://skidkaonline.ru";

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(API.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }
}