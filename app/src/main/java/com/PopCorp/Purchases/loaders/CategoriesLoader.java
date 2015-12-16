package com.PopCorp.Purchases.loaders;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.Category;
import com.PopCorp.Purchases.model.CategoryType;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;

public class CategoriesLoader extends SpiceRequest<Category[]> {

    public CategoriesLoader(){
        super(Category[].class);
    }

    @Override
    public Category[] loadDataFromNetwork() throws Exception {
        ArrayList<Category> result = new ArrayList<>();
        Cursor cursor = DB.getInstance().getCategories();
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
        CategoryType type = CategoryType.create(cursor.getInt(cursor.getColumnIndex(Category.KEY_CATEGORY_TYPE)));
        return Category.create(
                (long) cursor.getInt(cursor.getColumnIndex(Category.KEY_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndex(Category.KEY_CATEGORY_NAME)),
                type,
                cursor.getString(cursor.getColumnIndex(Category.KEY_CATEGORY_IMAGE_URL)),
                Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Category.KEY_CATEGORY_FAVORITE)))
        );
    }
}