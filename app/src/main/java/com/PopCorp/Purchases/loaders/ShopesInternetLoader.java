package com.PopCorp.Purchases.loaders;

import com.PopCorp.Purchases.model.Shop;
import com.PopCorp.Purchases.net.API;
import com.PopCorp.Purchases.net.NetHelper;
import com.PopCorp.Purchases.services.MestoskidkiSpiceService;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.client.Response;

public class ShopesInternetLoader extends RetrofitSpiceRequest<Shop[], API> {

    public ShopesInternetLoader() {
        super(Shop[].class, API.class);
    }

    @Override
    public Shop[] loadDataFromNetwork() throws Exception {
        ArrayList<Shop> result = new ArrayList<>();
        Response response = getService().getShopes(String.valueOf(PreferencesManager.getInstance().getRegionId()));
        if (response != null) {
            String body = NetHelper.stringFromResponse(response);
            result = getShops(body);
        }
        return result.toArray(new Shop[result.size()]);
    }

    private ArrayList<Shop> getShops(String body) {
        ArrayList<Shop> result = new ArrayList<>();

        ArrayList<String> imageUrls = new ArrayList<>();
        Matcher matcherLinks = Pattern.compile("src='img/[.[^']]+'").matcher(body);
        while (matcherLinks.find()) {
            String tmpString = matcherLinks.group();
            String url = MestoskidkiSpiceService.BASE_URL + tmpString.substring(5, tmpString.length() - 1);
            imageUrls.add(url);
        }

        int i = 0;
        Matcher matcherBegin = Pattern.compile("<div class=\'left_text2\'><a href=\'[.[^&']]+&shop=[0-9]+\' class='left_links2'>[.[^<]]+").matcher(body);
        while (matcherBegin.find()) {
            String tmpString = matcherBegin.group();
            long id = 0;
            String name = "";
            int countSalesShop = 0;
            Matcher matcherForKey = Pattern.compile("shop=[0-9]+").matcher(tmpString);
            if (matcherForKey.find()) {
                id = Long.valueOf(matcherForKey.group().substring(5));
            }
            Matcher matcherForName = Pattern.compile("links2\'>[.[^(]]+").matcher(tmpString);
            if (matcherForName.find()) {
                name = matcherForName.group().substring(8, matcherForName.group().length() - 1);
            }
            Matcher matcherForCount = Pattern.compile("\\([0-9]+\\)").matcher(tmpString);
            if (matcherForCount.find()) {
                countSalesShop = Integer.valueOf(matcherForCount.group().substring(1, matcherForCount.group().length() - 1));
            }
            Shop shop = Shop.create(id, name, imageUrls.get(i++), Long.valueOf(PreferencesManager.getInstance().getRegionId()), false, countSalesShop);
            result.add(shop);
        }
        return result;
    }
}