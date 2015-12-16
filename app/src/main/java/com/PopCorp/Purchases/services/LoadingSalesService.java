package com.PopCorp.Purchases.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.loaders.CategoriesLoader;
import com.PopCorp.Purchases.loaders.ShopesLoader;
import com.PopCorp.Purchases.model.Category;
import com.PopCorp.Purchases.model.CategoryType;
import com.PopCorp.Purchases.model.Sale;
import com.PopCorp.Purchases.model.Shop;
import com.PopCorp.Purchases.utils.NotificationManager;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.PopCorp.Purchases.utils.UIL;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoadingSalesService extends Service {

    public static final String EXTRA_STOP = "stop";
    public static final String EXTRA_SHOP_ID = "shop_id";

    private ExecutorService executor;
    private long shopId;
    private NotificationManager notificationManager;
    private OkHttpClient client = new OkHttpClient();
    private ArrayList<HashMap<Integer, Integer>> allCategories = new ArrayList<>();
    private boolean stopped = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newFixedThreadPool(1);
        notificationManager = new NotificationManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ((startId == 1)) {
            if (intent != null) {
                long shopId = intent.getLongExtra(EXTRA_SHOP_ID, 0);
                if (shopId != 0) {
                    this.shopId = shopId;
                }
            }
            executor.submit(runnable);
        }
        if (intent != null) {
            if (intent.getBooleanExtra(EXTRA_STOP, false)) {
                stopped = true;
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        finish();
    }

    private void finish() {
        executor.shutdownNow();
        stopped = true;
        notificationManager.cancelLoading();
        Intent intentUpdate = new Intent("com.PopCorp.Purchases.ACTION.UpdateSales");
        sendBroadcast(intentUpdate);
    }

    private ArrayList<Category> categs;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                initializeCategories();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<Shop> shops = loadFavoriteShopsFromDB();
            categs = loadFavoriteCategsFromDB();

            for (Shop shop : shops) {
                if (stopped) return;
                if (shopId != 0 && shopId != shop.getId()) {
                    continue;
                }
                try {
                    loadSalesForShop(shop);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            finish();
        }
    };

    private void loadSalesForShop(Shop shop) throws IOException {
        ArrayList<String> links = new ArrayList<>();
        notificationManager.loadingSalesForShop(shop);
        int countPages = getCountPages(shop);

        for (int page = 1; page < countPages + 1; page++) {
            links.addAll(getLinksForPage(shop, page));
        }

        for (int i = 0; i < links.size(); i++) {
            notificationManager.setProgressOfLoadingSales(shop, i, links.size());
            loadSale(shop, links.get(i));
        }
    }

    private void loadSale(Shop shop, String link) throws IOException {
        if (stopped) return;
        Request request = new Request.Builder()
                .url(MestoskidkiSpiceService.VIEW_SALE_URL + "city=" + PreferencesManager.getInstance().getRegionId() + "&id=" + link)
                .build();

        Response response = client.newCall(request).execute();
        String body = response.body().string();


        String name = getName(body);
        String comment = getComment(body);
        String coast = getCoast(body);
        String count = getCount(body);
        String coastFor = getCoastFor(body);
        String imageUrl = getImageUrl(body);
        String imageId = getImageId(body);
        Date[] periods;
        try {
            periods = getPeriods(body);
        } catch (ParseException e) {
            return;
        }
        Category categ = getCategory(body);
        if (categ == null) return;
        if (stopped) return;
        DisplayImageOptions options = UIL.getDownloadOptions();
        ImageLoader.getInstance().loadImage(imageUrl, options, null);
        Sale sale = new Sale(Long.valueOf(link), name, comment, coast, count, coastFor, imageUrl, Long.valueOf(imageId), shop, categ, periods[0], periods[1]);
        if (stopped) return;
        sale.updateOrAddToDB();
    }

    public static Date[] getPeriods(String page) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
        Date[] result = new Date[2];
        Matcher matcher = Pattern.compile("Период акции:<br><br>(<font color='red'>)*[.[^<]]+").matcher(page);
        if (matcher.find()) {
            String finded = matcher.group();
            finded = finded.replaceFirst("<font color='red'>", "");
            String period = finded.substring(21, finded.length());
            if (period.length() == 10) {
                result[0] = formatter.parse(period);
                result[1] = formatter.parse(period);
            } else {
                result[0] = formatter.parse(period.substring(0, 10));
                result[1] = formatter.parse(period.substring(13));
            }
        }
        return result;
    }

    public static String getCoast(String page) {
        String result = "";
        Matcher matcher = Pattern.compile("Цена: [.[^<]]+").matcher(page);
        if (matcher.find()) {
            result = matcher.group().substring(6);
        }
        return result;
    }

    public static String getCount(String page) {
        String result = "";
        Matcher matcher = Pattern.compile("(Вес|ъем):[^<]+").matcher(page);
        if (matcher.find()) {
            result = matcher.group().substring(5);
        }
        return result;
    }

    public static String getCoastFor(String page) {
        String result = "";
        Matcher matcher = Pattern.compile("Цена за [.[^:]]+: [.[^<]]+").matcher(page);
        if (matcher.find()) {
            Matcher matcher2 = Pattern.compile(": [.[^<]]+").matcher(matcher.group());
            if (matcher2.find()) {
                result = matcher2.group().substring(2);
            }
        }
        return result;
    }

    public static String getName(String page) {
        String title = "";
        Matcher matcher = Pattern.compile("<p class='larger'><strong>[.[^<]]+").matcher(page);
        if (matcher.find()) {
            title = matcher.group().substring(26).trim();
        }
        return title;
    }

    public static String getComment(String page) {
        String subTitle = "";
        Matcher matcher = Pattern.compile("</strong><br>[^']+").matcher(page);
        if (matcher.find()) {
            subTitle = matcher.group().substring(13, matcher.group().length() - 17).trim();
        }
        return subTitle;
    }

    public static String getImageId(String page) {
        String imageId = "";
        Matcher matcher = Pattern.compile("src='http://mestoskidki.ru/skidki/[.[^[.jpg]]]+.jpg").matcher(page);
        if (matcher.find()) {
            String finded = matcher.group();
            imageId = finded.substring(finded.length() - 10, finded.length() - 4);
        }
        return imageId;
    }

    public static String getImageUrl(String page) {
        String imageUrl = "";
        Matcher matcher = Pattern.compile("src='http://mestoskidki.ru/skidki/[.[^[.jpg]]]+.jpg").matcher(page);
        if (matcher.find()) {
            imageUrl = matcher.group().substring(5);
        }
        return imageUrl;
    }

    public ArrayList<String> getLinksForPage(Shop shop, int page) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        Request request = new Request.Builder()
                .url(MestoskidkiSpiceService.VIEW_SHOP_URL + "city=" + PreferencesManager.getInstance().getRegionId() + "&shop=" + shop.getId() + "&page=" + page)
                .build();

        Response response = client.newCall(request).execute();

        Matcher matcherForLinkSale = Pattern.compile("&id=[.[^']]+").matcher(response.body().string());
        while (matcherForLinkSale.find()) {
            result.add(matcherForLinkSale.group().substring(4, matcherForLinkSale.group().length()));
        }
        return result;
    }

    private int getCountPages(Shop shop) throws IOException {
        Request request = new Request.Builder()
                .url(MestoskidkiSpiceService.VIEW_SHOP_URL + "city=" + PreferencesManager.getInstance().getRegionId() + "&shop=" + shop.getId())
                .build();

        Response response = client.newCall(request).execute();

        Matcher matcher = Pattern.compile("[0-9]+(\")*><b>&#062;&#062;").matcher(response.body().string());
        if (matcher.find()) {
            Matcher matcher1 = Pattern.compile("[.[^\">]]+").matcher(matcher.group());
            if (matcher1.find()) {
                return Integer.valueOf(matcher1.group());
            }
        }
        return 1;
    }

    private ArrayList<Category> loadFavoriteCategsFromDB() {
        ArrayList<Category> result = new ArrayList<>();
        Cursor cursor = DB.getInstance().getFavoriteCategories();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.add(CategoriesLoader.getCategory(cursor));
                while (cursor.moveToNext()) {
                    result.add(CategoriesLoader.getCategory(cursor));
                }
            }
            cursor.close();
        }
        return result;
    }

    private ArrayList<Shop> loadFavoriteShopsFromDB() {
        ArrayList<Shop> result = new ArrayList<>();
        Cursor cursor = DB.getInstance().getFavoriteShopes(Long.valueOf(PreferencesManager.getInstance().getRegionId()));
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.add(ShopesLoader.getShop(cursor));
                while (cursor.moveToNext()) {
                    result.add(ShopesLoader.getShop(cursor));
                }
            }
            cursor.close();
        }
        return result;
    }

    private Category getCategory(String page) {
        int id = -1;
        CategoryType type = CategoryType.create(1);
        Matcher matcher = Pattern.compile("view_cat[.]php[?]city=[0-9]+&cat[0-9=]+").matcher(page);
        if (matcher.find()) {
            String[] m = matcher.group().split("=");
            int categ = Integer.valueOf(m[m.length - 1]);
            int index = 0;
            if (m[m.length - 2].contains("cat2")) {
                index = 1;
                type = CategoryType.create(2);
            }
            id = allCategories.get(index).get(categ);
        }
        for (Category category : categs) {
            if (category.getType().equals(type)) {
                if (id == category.getId()) {
                    return category;
                }
            }
        }
        return null;
    }

    private ArrayList<ArrayList<String>> initializeCategories() throws IOException {
        ArrayList<ArrayList<String>> cats = new ArrayList<>();
        for (int catId = 1; catId < 3; catId++) {
            HashMap<Integer, Integer> categs = new HashMap<>();
            Request request = new Request.Builder()
                    .url(MestoskidkiSpiceService.VIEW_CATEGORY_URL + "city=" + PreferencesManager.getInstance().getRegionId() + "&catid=" + catId)
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();

            Matcher matcher = Pattern.compile("view_cat[.]php[?]city=[0-9]+&cat[0-9]*=[0-9]+").matcher(body);
            int mainCateg = -1;
            while (matcher.find()) {
                String[] m = matcher.group().split("=");
                int categ = Integer.valueOf(m[m.length - 1]);
                if (categ < 100) {
                    mainCateg = categ;
                    categs.put(mainCateg, mainCateg);
                } else {
                    categs.put(categ, mainCateg);
                }
            }
            allCategories.add(categs);
        }
        return cats;
    }
}