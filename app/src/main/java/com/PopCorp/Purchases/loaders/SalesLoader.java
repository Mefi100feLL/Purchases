package com.PopCorp.Purchases.loaders;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.Category;
import com.PopCorp.Purchases.model.Sale;
import com.PopCorp.Purchases.model.Shop;
import com.octo.android.robospice.request.SpiceRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SalesLoader extends SpiceRequest<Sale[]> {

    private Shop shop;
    private Category category;

    private static final DateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));

    public SalesLoader(Shop shop) {
        super(Sale[].class);
        this.shop = shop;
    }

    public SalesLoader(Category category) {
        super(Sale[].class);
        this.category = category;
    }

    @Override
    public Sale[] loadDataFromNetwork() throws Exception {
        ArrayList<Sale> result = new ArrayList<>();
        Cursor cursor = null;
        if (shop != null) {
            cursor = DB.getInstance().getSalesForShop(shop.getId());
        } else if (category != null) {
            cursor = DB.getInstance().getSalesForCategory(category.getId(), category.getType().getId());
        }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.add(getSale(cursor));
                while (cursor.moveToNext()) {
                    result.add(getSale(cursor));
                }
            }
            cursor.close();
        }
        return result.toArray(new Sale[result.size()]);
    }

    public static Sale getSale(Cursor cursor) throws ParseException {
        String shopId = cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_SHOP));
        Shop shop = ShopesLoader.getShop(DB.getInstance().getShop(shopId));
        String categoryId = cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_CATEGORY));
        String typeId = cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_CATEGORY_TYPE));
        Category category = null;
        if (!categoryId.isEmpty()){
            category = CategoriesLoader.getCategory(DB.getInstance().getCategory(categoryId, typeId));
        }
        return new Sale(
                (long) cursor.getInt(cursor.getColumnIndex(Sale.KEY_SALE_ID)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_NAME)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_COMMENT)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_COAST)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_COUNT)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_COAST_FOR)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_IMAGE_URL)),
                cursor.getLong(cursor.getColumnIndex(Sale.KEY_SALE_IMAGE_ID)),
                shop,
                category,
                format.parse(cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_PERIOD_BEGIN))),
                format.parse(cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_PERIOD_FINISH)))
        );
    }
}