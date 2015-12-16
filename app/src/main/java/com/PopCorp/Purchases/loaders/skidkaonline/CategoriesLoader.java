package com.PopCorp.Purchases.loaders.skidkaonline;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.skidkaonline.Category;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;

public class CategoriesLoader extends SpiceRequest<Category[]> {

    public CategoriesLoader(){
        super(Category[].class);
    }

    @Override
    public Category[] loadDataFromNetwork() throws Exception {
        ArrayList<Category> result = new ArrayList<>();
        Cursor cursor = DB.getInstance().getSkidkaOnlineCategories(PreferencesManager.getInstance().getCity());
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.add(getCategory(cursor));
                while (cursor.moveToNext()) {
                    result.add(getCategory(cursor));
                }
            }
            cursor.close();
        }
        return result.toArray(new Category[result.size()]);
    }

    public static Category getCategory(Cursor cursor) {
        return Category.create(
                cursor.getString(cursor.getColumnIndex(Category.KEY_CATEGORY_CITY)),
                cursor.getString(cursor.getColumnIndex(Category.KEY_CATEGORY_NAME)),
                cursor.getString(cursor.getColumnIndex(Category.KEY_CATEGORY_URL)),
                cursor.getInt(cursor.getColumnIndex(Category.KEY_CATEGORY_POSITION))
        );
    }
}