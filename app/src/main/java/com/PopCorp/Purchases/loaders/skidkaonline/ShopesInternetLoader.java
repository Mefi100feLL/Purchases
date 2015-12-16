package com.PopCorp.Purchases.loaders.skidkaonline;

import com.PopCorp.Purchases.model.skidkaonline.Category;
import com.PopCorp.Purchases.model.skidkaonline.Shop;
import com.PopCorp.Purchases.net.API;
import com.PopCorp.Purchases.net.NetHelper;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.client.Response;

public class ShopesInternetLoader extends RetrofitSpiceRequest<Shop[], API> {

    private Category category;

    public ShopesInternetLoader(Category category) {
        super(Shop[].class, API.class);
        this.category = category;
    }

    @Override
    public Shop[] loadDataFromNetwork() throws Exception {
        ArrayList<Shop> result = new ArrayList<>();
        Response response = getService().getShopsForCategory(category.getUrl().substring(1));
        if (response != null) {
            String body = NetHelper.stringFromResponse(response);
            result = getShops(body);
        }
        return result.toArray(new Shop[result.size()]);
    }

    private ArrayList<Shop> getShops(String body) {
        ArrayList<Shop> result = new ArrayList<>();

        Matcher matcherShop = Pattern.compile("<a data-placement=\"left\" data-toggle=\"tooltip\" title=\"[^\"]*\" href=\"[^\"]*\"> <span class=\"img\"><img src=\"[^\"]*\" alt=\"\" /></span> <span class=\"text\">[^<]*</span>").matcher(body);
        while (matcherShop.find()) {
            String url = "";
            String name = "";
            String image = "";
            String foundedString = matcherShop.group();
            if (!foundedString.contains(category.getName())){
                continue;
            }
            Matcher matcherUrl = Pattern.compile("href=\"[^\"]+").matcher(foundedString);
            if (matcherUrl.find()) {
                url = matcherUrl.group().substring(6);
            }
            Matcher matcherName = Pattern.compile("<span class=\"text\">[^<]*").matcher(foundedString);
            if (matcherName.find()) {
                name = matcherName.group().substring(19);
            }
            Matcher matcherImage = Pattern.compile("src=\"[^\"]+").matcher(foundedString);
            if (matcherImage.find()) {
                image = matcherImage.group().substring(5).replaceFirst("\\-[0-9]+\\.", ".");
                image = image.replaceFirst("\\?t=t[0-9]+", "");
            }
            Shop shop = Shop.create(name, url, image, category.getCity(), category.getUrl(), false);
            if (!result.contains(shop)) {
                result.add(shop);
            }
        }
        return result;
    }
}