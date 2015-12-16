package com.PopCorp.Purchases.loaders.skidkaonline;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.skidkaonline.City;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;

public class CitysLoader extends SpiceRequest<City[]> {

    public CitysLoader(){
        super(City[].class);
    }

    @Override
    public City[] loadDataFromNetwork() throws Exception {
        ArrayList<City> result = new ArrayList<>();
        Cursor cursor = DB.getInstance().getSkidkaOnlineCitys();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.add(getCity(cursor));
                while (cursor.moveToNext()) {
                    result.add(getCity(cursor));
                }
            }
            cursor.close();
        }
        return result.toArray(new City[result.size()]);
    }

    public static City getCity(Cursor cursor) {
        return City.create(
                cursor.getString(cursor.getColumnIndex(City.KEY_CITY_NAME)),
                cursor.getString(cursor.getColumnIndex(City.KEY_CITY_URL)),
                cursor.getString(cursor.getColumnIndex(City.KEY_CITY_REGION))
        );
    }
}