package com.PopCorp.Purchases.loaders.skidkaonline;

import com.PopCorp.Purchases.model.skidkaonline.Category;
import com.PopCorp.Purchases.net.API;
import com.PopCorp.Purchases.net.NetHelper;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.client.Response;

public class CategoriesInternetLoader extends RetrofitSpiceRequest<Category[], API> {

    public CategoriesInternetLoader() {
        super(Category[].class, API.class);
    }

    @Override
    public Category[] loadDataFromNetwork() throws Exception {
        ArrayList<Category> result = new ArrayList<>();
        Response response = getService().getSkidkaonlineCategories(PreferencesManager.getInstance().getCity().replaceAll("/", ""));
        if (response != null) {
            String body = NetHelper.stringFromResponse(response);
            result = getCategories(body);
        }
        return result.toArray(new Category[result.size()]);
    }

    private ArrayList<Category> getCategories(String body) {
        ArrayList<Category> result = new ArrayList<>();
        ArrayList<Category> tmpArray = new ArrayList<>();

        String city = PreferencesManager.getInstance().getCity();

        Matcher matcher = Pattern.compile("<a href=\"" + city + "[^\"]+\"><span class=\"text\">[^<]+").matcher(body);
        int position = 0;
        while (matcher.find()){
            String url = "";
            String name = "";
            String findedString = matcher.group();
            Matcher matcherUrl = Pattern.compile("href=\"" + city + "[^\"]+").matcher(findedString);
            if (matcherUrl.find()){
                url = matcherUrl.group().substring(6);
            }
            Matcher matcherName = Pattern.compile("text\">[^<]+").matcher(findedString);
            if (matcherName.find()){
                name = matcherName.group().substring(6);
            }
            Category category = Category.create(city, name, url, position++);
            if (!tmpArray.contains(category)){
                tmpArray.add(category);
            }
        }

        ArrayList<String> realCategories = new ArrayList<>();
        Matcher matcherReal = Pattern.compile("<a data-placement=\"left\" data-toggle=\"tooltip\" title=\"[^\"]+").matcher(body);
        while (matcherReal.find()){
            String findedString = matcherReal.group();
            Matcher matcherName = Pattern.compile("title=\"[^\"]+").matcher(findedString);
            if (matcherName.find()){
                String name = matcherName.group().substring(7);
                if (!realCategories.contains(name)) {
                    realCategories.add(name);
                }
            }
        }

        for (String name : realCategories){
            for (Category category : tmpArray){
                if (name.equals(category.getName()) && !result.contains(category)){
                    result.add(category);
                }
            }
        }
        return result;
    }
}