package com.PopCorp.Purchases.loaders.skidkaonline;

import com.PopCorp.Purchases.model.skidkaonline.Sale;
import com.PopCorp.Purchases.model.skidkaonline.Shop;
import com.PopCorp.Purchases.net.API;
import com.PopCorp.Purchases.net.NetHelper;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.client.Response;

public class SalesInternetLoader extends RetrofitSpiceRequest<Sale[], API> {

    private Shop shop;

    public SalesInternetLoader(Shop shop) {
        super(Sale[].class, API.class);
        this.shop = shop;
    }

    @Override
    public Sale[] loadDataFromNetwork() throws Exception {
        ArrayList<Sale> result = new ArrayList<>();
        int page = 1;
        while (true) {
            Response response = getService().getSalesForShop(shop.getUrl().substring(1), page++);
            if (response != null) {
                String body = NetHelper.stringFromResponse(response);
                ArrayList<Sale> sales = getSales(body);
                int size = result.size();
                for (Sale sale : sales) {
                    if (!result.contains(sale)) {
                        result.add(sale);
                    }
                }
                if (result.size() == size) {
                    break;
                }
            }
        }
        return result.toArray(new Sale[result.size()]);
    }

    private ArrayList<Sale> getSales(String body) {
        ArrayList<Sale> result = new ArrayList<>();

        ArrayList<String> groupNames = new ArrayList<>();
        ArrayList<String> groupDates = new ArrayList<>();

        Matcher matcherGroup = Pattern.compile("<strong>[^<]+</strong><br/>\\([^\\)]+\\)</a>").matcher(body);
        while (matcherGroup.find()) {
            String finded = matcherGroup.group();
            String name = "";
            String period = "";
            Matcher matcherName = Pattern.compile("(<strong>)*[^<]+").matcher(finded);
            if (matcherName.find()) {
                String tmpString = matcherName.group();
                if (tmpString.contains("strong")) {
                    name = tmpString.substring(8);
                } else {
                    name = tmpString;
                }
            }
            Matcher matcherPeriod = Pattern.compile("<br/>\\([^\\)]+\\)+").matcher(finded);
            if (matcherPeriod.find()) {
                String tmpString = matcherPeriod.group();
                period = tmpString.substring(6, tmpString.length() - 1);
            }
            groupNames.add(name);
            groupDates.add(period);
        }

        Matcher matcherSale = Pattern.compile("<a href=\"[^\"]+\" class=\"image-link\"><span class=\"over\">&nbsp;</span> <img src=\"[^\"]+\" width=\"[0-9]*\" height=\"[0-9]*\"").matcher(body);
        while (matcherSale.find()) {
            String id = "";
            String smallImage = "";
            String foundedString = matcherSale.group();
            Matcher matcherId = Pattern.compile("<a href=\"[^\"]+").matcher(foundedString);
            if (matcherId.find()) {
                String tmpString = matcherId.group();
                String[] array = tmpString.split("/");
                id = array[array.length - 1];
            }
            Matcher matcherImage = Pattern.compile("<img src=\"[^\"]+").matcher(foundedString);
            if (matcherImage.find()) {
                smallImage = matcherImage.group().substring(10);
                smallImage = smallImage.replaceFirst("\\?t=t[0-9]+", "");
            }
            String image = smallImage.replaceAll("\\-[0-9]+\\.", ".");
            Sale sale = new Sale(id, shop.getUrl(), "", "", "", image, smallImage);
            result.add(sale);
        }

        if (groupNames.size() == groupDates.size() && groupNames.size() == result.size()) {
            for (int i = 0; i < result.size(); i++) {
                Sale sale = result.get(i);
                sale.setGroupName(groupNames.get(i));
                String period = groupDates.get(i).toLowerCase();
                String[] periods = period.split(" - ");
                setPeriods(sale, periods);
            }
        } else {
            throw new RuntimeException("sizes of sales are not equals");
        }
        return result;
    }

    private void setPeriods(Sale sale, String[] periods) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
        int year;
        try {
            SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
            Date finish = format.parse(periods[1]);
            Calendar cal = Calendar.getInstance();
            cal.setTime(finish);
            year = cal.get(Calendar.YEAR);
            sale.setPeriodFinish(formatter.format(finish));
        } catch (ParseException e) {
            throw new RuntimeException("Can not parse date");
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
            Date begin = format.parse(periods[0]);
            sale.setPeriodBegin(formatter.format(begin));
        } catch (ParseException e) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("d MMMM", new Locale("ru"));
                Date begin = format.parse(periods[0]);
                Calendar cal = Calendar.getInstance();
                cal.setTime(begin);
                cal.set(Calendar.YEAR, year);
                sale.setPeriodBegin(formatter.format(cal.getTime()));
            } catch (ParseException e1) {
                throw new RuntimeException("Can not parse date");
            }
        }
    }
}