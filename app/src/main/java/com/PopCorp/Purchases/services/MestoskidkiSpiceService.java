package com.PopCorp.Purchases.services;

import com.PopCorp.Purchases.net.API;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

public class MestoskidkiSpiceService extends RetrofitGsonSpiceService {

    public static final String BASE_URL = "http://mestoskidki.ru/";
    public static final String VIEW_SHOP_URL = BASE_URL + "/view_shop.php?";
    public static final String VIEW_SALE_URL = BASE_URL + "view_sale.php?";
    public static final String VIEW_CATEGORY_URL = BASE_URL + "cat_sale.php?";

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