package com.PopCorp.Purchases.loaders;

import com.PopCorp.Purchases.model.Sale;
import com.PopCorp.Purchases.model.Shop;
import com.PopCorp.Purchases.net.API;
import com.PopCorp.Purchases.net.NetHelper;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.client.Response;

public class SalesInternetFastLoader extends RetrofitSpiceRequest<Sale[], API> {

    private Shop shop;
    private final DateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));

    public SalesInternetFastLoader(Shop shop) {
        super(Sale[].class, API.class);
        this.shop = shop;
    }

    @Override
    public Sale[] loadDataFromNetwork() throws Exception {
        ArrayList<Sale> result = new ArrayList<>();
        int countPages = 1;
        Response response = getService().getShop(PreferencesManager.getInstance().getRegionId(), shop.getId());
        if (response != null) {
            String body = NetHelper.stringFromResponse(response);
            countPages = getPageCount(body);
        }
        for (int page = 1; page < countPages + 1; page++) {
            Response responseForPage = getService().getSalesForPage(PreferencesManager.getInstance().getRegionId(), String.valueOf(shop.getId()), page);
            if (responseForPage != null) {
                String body = NetHelper.stringFromResponse(responseForPage);
                result.addAll(getSales(body));
            }
        }
        return result.toArray(new Sale[result.size()]);
    }

    private int getPageCount(String body) {
        Matcher matcher = Pattern.compile("[0-9]+(\")*><b>&#062;&#062;").matcher(body);
        if (matcher.find()) {
            Matcher matcher1 = Pattern.compile("[.[^\">]]+").matcher(matcher.group());
            if (matcher1.find()) {
                return Integer.valueOf(matcher1.group());
            }
        }
        return 1;
    }

    private ArrayList<Sale> getSales(String body) throws ParseException {
        ArrayList<Sale> result = new ArrayList<>();


        ArrayList<Long> linksSale = new ArrayList<>();
        ArrayList<String> titlesSale = new ArrayList<>();
        ArrayList<String> imagesSale = new ArrayList<>();
        ArrayList<Long> imagesIdsSale = new ArrayList<>();
        ArrayList<String> periodesSale = new ArrayList<>();

        Matcher matcherForLinkSale = Pattern.compile("&id=[0-9]+").matcher(body);
        while (matcherForLinkSale.find()) {
            linksSale.add(Long.valueOf(matcherForLinkSale.group().substring(4, matcherForLinkSale.group().length())));
        }
        Matcher matcherForTitleSale = Pattern.compile("alt='[.[^']]+").matcher(body);
        while (matcherForTitleSale.find()) {
            titlesSale.add(matcherForTitleSale.group().substring(5, matcherForTitleSale.group().length()));
        }
        Matcher matcherForImageSale = Pattern.compile("src='http:[^']+").matcher(body);
        while (matcherForImageSale.find()) {
            String imageUrl = matcherForImageSale.group().substring(5, matcherForImageSale.group().length());
            String id = imageUrl.substring(imageUrl.length() - 10, imageUrl.length() - 4);
            imageUrl = imageUrl.replaceFirst("s[0-9]+", id);
            imagesSale.add(imageUrl);
            imagesIdsSale.add(Long.valueOf(id));
        }
        Matcher matcherForPeriodSale = Pattern.compile("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}[0-9\\-\\.]*").matcher(body);
        while (matcherForPeriodSale.find()) {
            periodesSale.add(matcherForPeriodSale.group());
        }

        for (int i = 0; i < linksSale.size(); i++) {
            String periodBegin;
            String periodEnd;
            if (periodesSale.get(i).length() <= 10) {
                periodBegin = periodesSale.get(i);
                periodEnd = periodesSale.get(i);
            } else {
                String[] periodes = periodesSale.get(i).split("-");
                periodBegin = periodes[0];
                periodEnd = periodes[1];
            }

            Sale sale = new Sale(linksSale.get(i), titlesSale.get(i), "", "", "", "", imagesSale.get(i), imagesIdsSale.get(i), shop, null, format.parse(periodBegin), format.parse(periodEnd));
            result.add(sale);
        }
        return result;
    }
}