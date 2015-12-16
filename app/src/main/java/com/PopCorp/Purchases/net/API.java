package com.PopCorp.Purchases.net;

import retrofit.client.Response;
import retrofit.http.EncodedPath;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface API {

    @FormUrlEncoded
    @POST("/")
    Response getShopes(@Field("city") String city);

    @GET("/cat_sale.php")
    Response getCategories();

    @GET("/view_shop.php?")
    Response getShop(@Query("city") String city, @Query("shop") long shop);

    @GET("/view_shop.php")
    Response getSalesForPage(@Query("city") String city, @Query("shop") String shop, @Query("page") int page);

    @GET("/{city}")
    Response getSkidkaonlineCategories(@EncodedPath("city") String city);

    @GET("/cities/")
    Response getSkidkaonlineCitys();

    @GET("/{url}")
    Response getSkidkaonlineForUrl(@EncodedPath("url") String url);

    @GET("/{url}")
    Response getShopsForCategory(@EncodedPath("url") String categoryUrl);

    @GET("/{shop}?is_ajax=1")
    Response getSalesForShop(@EncodedPath("shop") String shop, @Query("page")int page);
}
