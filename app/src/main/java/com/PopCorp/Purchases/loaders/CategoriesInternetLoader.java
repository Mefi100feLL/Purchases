package com.PopCorp.Purchases.loaders;

import com.PopCorp.Purchases.model.Category;
import com.PopCorp.Purchases.model.CategoryType;
import com.PopCorp.Purchases.net.API;
import com.PopCorp.Purchases.net.NetHelper;
import com.PopCorp.Purchases.services.MestoskidkiSpiceService;
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
        Response response = getService().getCategories();
        if (response != null) {
            String body = NetHelper.stringFromResponse(response);
            result = getCategories(body);
        }
        return result.toArray(new Category[result.size()]);
    }

    private ArrayList<Category> getCategories(String body) {
        ArrayList<Category> result = new ArrayList<>();

        ArrayList<String> linksForImage = new ArrayList<>();
        Matcher matcherLinks = Pattern.compile("src='img/[.[^']]+'").matcher(body);
        while (matcherLinks.find()) {
            String tmpString = matcherLinks.group();
            String url = MestoskidkiSpiceService.BASE_URL + tmpString.substring(5, tmpString.length() - 1);
            linksForImage.add(url);
        }

        ArrayList<Long> ids = new ArrayList<>();
        ArrayList<CategoryType> types = new ArrayList<>();
        Matcher matcherIds = Pattern.compile("<td><a href='view_cat\\.php\\?city=1&cat[0-9]*=[^']+").matcher(body);
        while (matcherIds.find()) {
            String tmpString = matcherIds.group();
            if (tmpString.contains("cat2")) {
                types.add(CategoryType.create(2));
            } else {
                types.add(CategoryType.create(1));
            }
            String[] split = tmpString.split("=");
            long id = Integer.valueOf(split[split.length - 1]);
            ids.add(id);
        }

        ArrayList<String> names = new ArrayList<>();
        Matcher matcherNames = Pattern.compile("<p class='shop2'>[^<]+").matcher(body);
        while (matcherNames.find()) {
            String tmpString = matcherNames.group();
            String name = tmpString.substring(17, tmpString.length());
            names.add(name);
        }

        for (int i = 0; i < names.size(); i++) {
            Category category = Category.create(ids.get(i), names.get(i), types.get(i), linksForImage.get(i), false);
            result.add(category);
        }
        return result;
    }
}