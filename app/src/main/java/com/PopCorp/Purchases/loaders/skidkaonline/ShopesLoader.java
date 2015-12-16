package com.PopCorp.Purchases.loaders.skidkaonline;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.skidkaonline.Shop;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;

public class ShopesLoader extends SpiceRequest<Shop[]> {

    private String category;

    public ShopesLoader(String category){
        super(Shop[].class);
        this.category = category;
    }

    @Override
    public Shop[] loadDataFromNetwork() throws Exception {
        ArrayList<Shop> result = new ArrayList<>();
        Cursor cursor = DB.getInstance().getSkidkaOnlineShops(category);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.add(getShop(cursor));
                while (cursor.moveToNext()) {
                    result.add(getShop(cursor));
                }
            }
            cursor.close();
        }
        return result.toArray(new Shop[result.size()]);
    }

    public static Shop getShop(Cursor cursor) {
        return Shop.create(
                cursor.getString(cursor.getColumnIndex(Shop.KEY_SHOP_NAME)),
                cursor.getString(cursor.getColumnIndex(Shop.KEY_SHOP_URL)),
                cursor.getString(cursor.getColumnIndex(Shop.KEY_SHOP_IMAGE_URL)),
                cursor.getString(cursor.getColumnIndex(Shop.KEY_SHOP_CITY)),
                cursor.getString(cursor.getColumnIndex(Shop.KEY_SHOP_CATEGORY)),
                Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Shop.KEY_SHOP_FAVORITE)))
        );
    }
}