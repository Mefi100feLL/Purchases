package com.PopCorp.Purchases.loaders.skidkaonline;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.skidkaonline.Sale;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;

public class SalesLoader extends SpiceRequest<Sale[]> {

    private String shop;

    public SalesLoader(String shop){
        super(Sale[].class);
        this.shop = shop;
    }

    @Override
    public Sale[] loadDataFromNetwork() throws Exception {
        ArrayList<Sale> result = new ArrayList<>();
        Cursor cursor = DB.getInstance().getSkidkaOnlineSalesForShop(shop);
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

    public static Sale getSale(Cursor cursor) {
        return new Sale(
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_ID)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_SHOP)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_GROUP_NAME)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_PERIOD_BEGIN)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_PERIOD_FINISH)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_IMAGE_URL)),
                cursor.getString(cursor.getColumnIndex(Sale.KEY_SALE_SMALL_IMAGE_URL))
        );
    }
}