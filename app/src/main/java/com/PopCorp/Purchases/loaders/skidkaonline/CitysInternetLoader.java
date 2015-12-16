package com.PopCorp.Purchases.loaders.skidkaonline;

import com.PopCorp.Purchases.model.skidkaonline.City;
import com.PopCorp.Purchases.net.API;
import com.PopCorp.Purchases.net.NetHelper;
import com.PopCorp.Purchases.services.SkidkaOnlineSpiceService;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.client.Response;

public class CitysInternetLoader extends RetrofitSpiceRequest<City[], API> {

    public CitysInternetLoader() {
        super(City[].class, API.class);
    }

    @Override
    public City[] loadDataFromNetwork() throws Exception {
        ArrayList<City> result = new ArrayList<>();
        Response response = getService().getSkidkaonlineCitys();
        if (response != null) {
            String body = NetHelper.stringFromResponse(response);
            ArrayList<String> urlsPages = getPagesUrls(body);
            for (String urlPage : urlsPages) {
                result.addAll(getCitys(urlPage));
            }
        }
        return result.toArray(new City[result.size()]);
    }

    private ArrayList<String> getPagesUrls(String body) {
        ArrayList<String> result = new ArrayList<>();
        Matcher matcherUrls = Pattern.compile("cities/\\?a=[^\"]+").matcher(body);
        while (matcherUrls.find()) {
            String findedString = matcherUrls.group();
            result.add(findedString);
        }
        return result;
    }

    private ArrayList<City> getCitys(String pageUrl) {
        ArrayList<City> result = new ArrayList<>();

        Response response = getService().getSkidkaonlineForUrl(pageUrl);
        if (response != null) {
            String body = NetHelper.stringFromResponse(response);
            Matcher matcher = Pattern.compile("<a data-toggle=\"tooltip\" title=\"[^\"]*\" href=\"[^\"]+\"><span class=\"text\">[^<]+</span>").matcher(body);
            while (matcher.find()){
                String region = "";
                String name = "";
                String url = "";
                String findedString = matcher.group();
                Matcher matcherRegion = Pattern.compile("title=\"[^\"]*").matcher(findedString);
                if (matcherRegion.find()){
                    region = matcherRegion.group().substring(7);
                }
                Matcher matcherName = Pattern.compile("text\">[^<]+").matcher(findedString);
                if (matcherName.find()){
                    name = matcherName.group().substring(6);
                }
                Matcher matcherUrl = Pattern.compile("href=\"[^\"]+").matcher(findedString);
                if (matcherUrl.find()){
                    url = matcherUrl.group().substring(6);
                }
                City city = City.create(name, url, region);
                if (!result.contains(city)){
                    result.add(city);
                }
            }
        }
        return result;
    }
}