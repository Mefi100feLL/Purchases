package com.PopCorp.Purchases.loaders;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.Shop;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;

public class ShopesLoader extends SpiceRequest<Shop[]> {

    public ShopesLoader(){
        super(Shop[].class);
    }

    @Override
    public Shop[] loadDataFromNetwork() throws Exception {
        ArrayList<Shop> result = new ArrayList<>();
        Cursor cursor = DB.getInstance().getShopes(Long.valueOf(PreferencesManager.getInstance().getRegionId()));
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
                (long) cursor.getInt(cursor.getColumnIndex(Shop.KEY_SHOP_ID)),
                cursor.getString(cursor.getColumnIndex(Shop.KEY_SHOP_NAME)),
                cursor.getString(cursor.getColumnIndex(Shop.KEY_SHOP_IMAGE_URL)),
                (long) cursor.getInt(cursor.getColumnIndex(Shop.KEY_SHOP_CITY_ID)),
                Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Shop.KEY_SHOP_FAVORITE))),
                Integer.valueOf(cursor.getString(cursor.getColumnIndex(Shop.KEY_SHOP_COUNT_SALES)))
        );
    }
}