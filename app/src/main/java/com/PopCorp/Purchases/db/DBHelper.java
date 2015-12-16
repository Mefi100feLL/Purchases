package com.PopCorp.Purchases.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.model.ListItem;
import com.PopCorp.Purchases.model.Region;
import com.PopCorp.Purchases.model.Sale;
import com.PopCorp.Purchases.model.ShopCategory;
import com.PopCorp.Purchases.model.ShoppingList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "PopCorp.Purchases.DB";
    private static final int DB_VERSION = 6;

    private Context context;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB.CREATE_TABLE_LISTS);
        db.execSQL(DB.CREATE_TABLE_ITEMS);
        db.execSQL(DB.CREATE_TABLE_ALL_ITEMS);
        db.execSQL(DB.CREATE_TABLE_FAVORITE_ITEMS);
        db.execSQL(DB.CREATE_TABLE_SALES);
        db.execSQL(DB.CREATE_TABLE_CITIES);
        db.execSQL(DB.CREATE_TABLE_SHOPES);
        db.execSQL(DB.CREATE_TABLE_SALE_CATEGS);

        db.execSQL(DB.CREATE_TABLE_SKIDKAONLINE_CATEGORIES);
        db.execSQL(DB.CREATE_TABLE_SKIDKAONLINE_CITYS);
        db.execSQL(DB.CREATE_TABLE_SKIDKAONLINE_SHOPES);
        db.execSQL(DB.CREATE_TABLE_SKIDKAONLINE_SALES);
        db.execSQL(DB.CREATE_TABLE_SHOP_SALES);

        addCategs(db);
        addAllProducts(db);
        addAllCities(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            updateTableAllItems(db, oldVersion);
            updateTableWithItems(db, oldVersion);
            updateTableFavorites(db, oldVersion);
            updateTableLists(db, oldVersion);
            if (oldVersion < 3) {
                addAllProducts(db);
            }
            if (oldVersion < 4) {
                addCategs(db);
                changeFavorites(db);
                db.execSQL(DB.CREATE_TABLE_SALES);
                db.execSQL(DB.CREATE_TABLE_CITIES);
                db.execSQL(DB.CREATE_TABLE_SHOPES);
                addAllCities(db);
            }
            if (oldVersion < 5) {
                db.execSQL(DB.CREATE_TABLE_SALE_CATEGS);
                db.execSQL("ALTER TABLE " + Sale.TABLE_SALES + " ADD COLUMN " + Sale.KEY_SALE_CATEGORY + " text;");
            }
            if (oldVersion < 6){
                db.execSQL(DB.CREATE_TABLE_SKIDKAONLINE_CATEGORIES);
                db.execSQL(DB.CREATE_TABLE_SKIDKAONLINE_CITYS);
                db.execSQL(DB.CREATE_TABLE_SKIDKAONLINE_SHOPES);
                db.execSQL(DB.CREATE_TABLE_SKIDKAONLINE_SALES);

                db.execSQL("ALTER TABLE " + ListItem.TABLE_ITEMS + " ADD COLUMN " + ListItem.KEY_ITEMS_SALE_ID + " integer;");

                db.execSQL(DB.CREATE_TABLE_SHOP_SALES);

                db.execSQL("ALTER TABLE " + Sale.TABLE_SALES + " ADD COLUMN " + Sale.KEY_SALE_CATEGORY_TYPE + " text;");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in updating DB");
        }
    }

    private void changeFavorites(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + DB.TABLE_ALL_ITEMS + " ADD COLUMN " + DB.KEY_ALL_ITEMS_FAVORITE + " boolean;");
        Cursor cursor = db.query(DB.TABLE_FAVORITE_ITEMS, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                db.execSQL("UPDATE " + DB.TABLE_ALL_ITEMS + " SET " + DB.KEY_ALL_ITEMS_FAVORITE + "='" + "true" + "' WHERE " + DB.KEY_ALL_ITEMS_NAME + "='" + cursor.getString(cursor.getColumnIndex(DB.KEY_FAVORITE_ITEMS_NAME)) + "'" + ";");
                while (cursor.moveToNext()) {
                    db.execSQL("UPDATE " + DB.TABLE_ALL_ITEMS + " SET " + DB.KEY_ALL_ITEMS_FAVORITE + "='" + "true" + "' WHERE " + DB.KEY_ALL_ITEMS_NAME + "='" + cursor.getString(cursor.getColumnIndex(DB.KEY_FAVORITE_ITEMS_NAME)) + "'" + ";");
                }
            }
            cursor.close();
        }
        removeTable(db, DB.TABLE_FAVORITE_ITEMS);
    }

    private void addCategs(SQLiteDatabase db) {
        db.execSQL(DB.CREATE_TABLE_CATEGS);
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(context.getResources().getColor(R.color.md_brown_500));
        colors.add(context.getResources().getColor(R.color.md_orange_500));
        colors.add(context.getResources().getColor(R.color.md_blue_500));
        colors.add(context.getResources().getColor(R.color.md_teal_500));
        colors.add(context.getResources().getColor(R.color.md_deep_purple_500));
        colors.add(context.getResources().getColor(R.color.md_green_500));

        categories.add(context.getResources().getString(R.string.default_categ_one));
        categories.add(context.getResources().getString(R.string.default_categ_two));
        categories.add(context.getResources().getString(R.string.default_categ_three));
        categories.add(context.getResources().getString(R.string.default_categ_four));
        categories.add(context.getResources().getString(R.string.default_categ_five));
        categories.add(context.getResources().getString(R.string.default_categ_six));

        ContentValues cv = new ContentValues();
        for (int i = 0; i < colors.size(); i++) {
            cv.put(ShopCategory.COLUMNS_CATEGS[0], categories.get(i));
            cv.put(ShopCategory.COLUMNS_CATEGS[1], colors.get(i));
            try {
                db.insert(ShopCategory.TABLE_CATEGORIES, null, cv);
            } catch (SQLiteException e) {
                throw new RuntimeException("Error when inserting categories");
            }
        }
    }

    private void updateTableAllItems(SQLiteDatabase db, int oldVersion) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DB.trans("All") + "( _id integer primary key autoincrement, name text, count integer, edizm text, coast integer, category text, shop text, comment text);");
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + DB.trans("All") + " ADD COLUMN edizm text;");
            db.execSQL("ALTER TABLE " + DB.trans("All") + " ADD COLUMN category text;");
            db.execSQL("ALTER TABLE " + DB.trans("All") + " ADD COLUMN count integer;");
            db.execSQL("ALTER TABLE " + DB.trans("All") + " ADD COLUMN coast integer;");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + DB.trans("All") + " ADD COLUMN shop text;");
            db.execSQL("ALTER TABLE " + DB.trans("All") + " ADD COLUMN comment text;");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + DB.trans("All") + " RENAME TO " + DB.TABLE_ALL_ITEMS + ";");
        }
    }

    private void updateTableWithItems(SQLiteDatabase db, int oldVersion) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DB.trans("Lists") + "( _id integer primary key autoincrement, name text, date text, datealarm text);");
        if (oldVersion < 3) {
            updateTableItemsForOldDataBases(db, oldVersion);
        }
        if (oldVersion < 4) {
            moveItemsFromTablesToOneTableWithItems(db);
        }
    }

    private void updateTableItemsForOldDataBases(SQLiteDatabase db, int oldVersion) {
        Cursor cursorLists = db.query(DB.trans("Lists"), null, null, null, null, null, null);
        if (cursorLists == null) {
            return;
        }
        if (cursorLists.moveToFirst()) {
            changeTablesWithItemsForOldDataBases(db, cursorLists, oldVersion);
            while (cursorLists.moveToNext()) {
                changeTablesWithItemsForOldDataBases(db, cursorLists, oldVersion);
            }
        }
        cursorLists.close();
    }

    private void changeTablesWithItemsForOldDataBases(SQLiteDatabase db, Cursor cursorLists, int oldVersion) {
        db.execSQL("ALTER TABLE " + DB.trans(cursorLists.getString(cursorLists.getColumnIndex("name")) + cursorLists.getString(cursorLists.getColumnIndex("date"))) + " ADD COLUMN category text;");
        db.execSQL("ALTER TABLE " + DB.trans(cursorLists.getString(cursorLists.getColumnIndex("name")) + cursorLists.getString(cursorLists.getColumnIndex("date"))) + " RENAME TO " + DB.trans(cursorLists.getString(cursorLists.getColumnIndex("date"))) + ";");

        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + DB.trans(cursorLists.getString(cursorLists.getColumnIndex("date"))) + " ADD COLUMN shop text;");
            db.execSQL("ALTER TABLE " + DB.trans(cursorLists.getString(cursorLists.getColumnIndex("date"))) + " ADD COLUMN comment text;");
            db.execSQL("ALTER TABLE " + DB.trans(cursorLists.getString(cursorLists.getColumnIndex("date"))) + " ADD COLUMN important text;");
        }
    }

    private void moveItemsFromTablesToOneTableWithItems(SQLiteDatabase db) {
        db.execSQL(DB.CREATE_TABLE_ITEMS);
        Cursor cursorLists = db.query(DB.trans("Lists"), null, null, null, null, null, null);
        if (cursorLists == null) {
            return;
        }
        if (cursorLists.moveToFirst()) {
            moveItemsToTable(db, DB.trans(cursorLists.getString(cursorLists.getColumnIndex("date"))));
            removeTable(db, DB.trans(cursorLists.getString(cursorLists.getColumnIndex("date"))));
            while (cursorLists.moveToNext()) {
                moveItemsToTable(db, DB.trans(cursorLists.getString(cursorLists.getColumnIndex("date"))));
                removeTable(db, DB.trans(cursorLists.getString(cursorLists.getColumnIndex("date"))));
            }
        }
        cursorLists.close();
    }

    private void removeTable(SQLiteDatabase db, String table) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
    }

    private void moveItemsToTable(SQLiteDatabase db, String oldTable) {
        Cursor cursorItems = db.query(oldTable, null, null, null, null, null, null);
        if (cursorItems == null) {
            return;
        }
        if (cursorItems.moveToFirst()) {
            putItemsInTableFromCursor(db, cursorItems, DB.untrans(oldTable));
            while (cursorItems.moveToNext()) {
                putItemsInTableFromCursor(db, cursorItems, DB.untrans(oldTable));
            }
        }
        cursorItems.close();
    }

    private void putItemsInTableFromCursor(SQLiteDatabase db, Cursor cursorItems, String datelist) {
        ContentValues cv = new ContentValues();
        for (int i = 0; i < cursorItems.getColumnCount(); i++) {
            if (cursorItems.getColumnName(i).equals("_id")) {
                continue;
            }
            cv.put(cursorItems.getColumnName(i), cursorItems.getString(i));
        }
        cv.put(ListItem.KEY_ITEMS_DATELIST, datelist);
        db.insert(ListItem.TABLE_ITEMS, null, cv);
    }

    private void updateTableFavorites(SQLiteDatabase db, int oldVersion) {
        if (oldVersion == 3) {
            db.execSQL("ALTER TABLE " + DB.trans("Favorite") + " RENAME TO " + DB.TABLE_FAVORITE_ITEMS + ";");
        } else if (oldVersion < 4) {
            db.execSQL(DB.CREATE_TABLE_FAVORITE_ITEMS);
        }
    }

    private void updateTableLists(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + DB.trans("Lists") + " RENAME TO " + ShoppingList.TABLE_LISTS + ";");
            db.execSQL("ALTER TABLE " + ShoppingList.TABLE_LISTS + " ADD COLUMN " + ShoppingList.KEY_LISTS_CURRENCY + " text;");
            ContentValues cv = new ContentValues();
            cv.put(ShoppingList.KEY_LISTS_CURRENCY, context.getString(R.string.default_one_currency));
            db.update(ShoppingList.TABLE_LISTS, cv, null, null);
        }
    }

    private void addAllProducts(SQLiteDatabase db) {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> categoryes = new ArrayList<>();
        ArrayList<String> counts = new ArrayList<>();
        ArrayList<String> coasts = new ArrayList<>();
        ArrayList<String> edizms = new ArrayList<>();
        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.all);
            String tag = "";
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("name")) {
                            tag = "name";
                        } else if (xpp.getName().equals("category")) {
                            tag = "category";
                        } else if (xpp.getName().equals("count")) {
                            tag = "count";
                        } else if (xpp.getName().equals("coast")) {
                            tag = "coast";
                        } else if (xpp.getName().equals("edizm")) {
                            tag = "edizm";
                        } else {
                            tag = "";
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (tag.equals("name")) {
                            names.add(xpp.getText());
                        }
                        if (tag.equals("category")) {
                            categoryes.add(xpp.getText());
                        }
                        if (tag.equals("coast")) {
                            coasts.add(xpp.getText());
                        }
                        if (tag.equals("count")) {
                            counts.add(xpp.getText());
                        }
                        if (tag.equals("edizm")) {
                            edizms.add(xpp.getText());
                        }
                        break;

                    default:
                        break;
                }
                xpp.next();
            }
        } catch (XmlPullParserException | IOException ignored) {
        }

        String[] columns = new String[]{"name", "category", "count", "edizm", "coast"};
        ContentValues cv = new ContentValues();
        for (int i = 0; i < names.size(); i++) {
            cv.put(columns[0], names.get(i));
            cv.put(columns[1], categoryes.get(i));
            cv.put(columns[2], counts.get(i));
            cv.put(columns[3], edizms.get(i));
            cv.put(columns[4], coasts.get(i));
            try {
                db.insert(DB.TABLE_ALL_ITEMS, null, cv);
            } catch (SQLiteException ignored) {
            }
        }
    }


    private void addAllCities(SQLiteDatabase db) {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.cities);
            String tag = "";
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("name")) {
                            tag = "name";
                        } else if (xpp.getName().equals("id")) {
                            tag = "id";
                        } else {
                            tag = "";
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (tag.equals("name")) {
                            names.add(xpp.getText());
                        }
                        if (tag.equals("id")) {
                            ids.add(xpp.getText());
                        }
                        break;
                    default:
                        break;
                }
                xpp.next();
            }
        } catch (XmlPullParserException | IOException ignored) {
        }

        String[] columns = new String[]{"name", "id"};
        ContentValues cv = new ContentValues();
        for (int i = 0; i < names.size(); i++) {
            cv.put(columns[0], names.get(i));
            cv.put(columns[1], ids.get(i));
            try {
                db.insert(Region.TABLE_CITIES, null, cv);
            } catch (SQLiteException ignored) {
            }
        }
    }
}
