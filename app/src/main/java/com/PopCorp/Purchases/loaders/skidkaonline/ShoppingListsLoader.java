package com.PopCorp.Purchases.loaders.skidkaonline;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;
import com.PopCorp.Purchases.model.ShoppingList;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;

public class ShoppingListsLoader extends SpiceRequest<ShoppingList[]> {

    public ShoppingListsLoader(){
        super(ShoppingList[].class);
    }

    @Override
    public ShoppingList[] loadDataFromNetwork() throws Exception {
        ArrayList<ShoppingList> result = new ArrayList<>();
        Cursor cursor = DB.getInstance().getLists();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result.add(getList(cursor));
                while (cursor.moveToNext()) {
                    result.add(getList(cursor));
                }
            }
            cursor.close();
        }
        return result.toArray(new ShoppingList[result.size()]);
    }

    public static ShoppingList getList(Cursor cursor) {
        return new ShoppingList(
                cursor.getLong(cursor.getColumnIndex(DB.KEY_ID)),
                cursor.getString(cursor.getColumnIndex(ShoppingList.KEY_LISTS_NAME)),
                cursor.getLong(cursor.getColumnIndex(ShoppingList.KEY_LISTS_DATELIST)),
                cursor.getString(cursor.getColumnIndex(ShoppingList.KEY_LISTS_ALARM)),
                cursor.getString(cursor.getColumnIndex(ShoppingList.KEY_LISTS_CURRENCY))
        );
    }
}