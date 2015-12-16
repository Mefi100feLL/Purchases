package com.PopCorp.Purchases.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.PopCorp.Purchases.model.Category;
import com.PopCorp.Purchases.model.ListItem;
import com.PopCorp.Purchases.model.Region;
import com.PopCorp.Purchases.model.Sale;
import com.PopCorp.Purchases.model.Shop;
import com.PopCorp.Purchases.model.ShopCategory;
import com.PopCorp.Purchases.model.ShopSale;
import com.PopCorp.Purchases.model.ShoppingList;
import com.PopCorp.Purchases.model.skidkaonline.City;

import java.net.IDN;

public class DB {
    public static final String KEY_ID = "_id";

    public static final String TABLE_ALL_ITEMS = "AllItems";
    public static final String TABLE_FAVORITE_ITEMS = "Favorite";


    public static final String CREATE_TABLE_CATEGS = "CREATE TABLE IF NOT EXISTS " + ShopCategory.TABLE_CATEGORIES +
            "( " + KEY_ID + " integer primary key autoincrement, " + ShopCategory.KEY_CATEGS_NAME + " text, " + ShopCategory.KEY_CATEGS_COLOR + " integer);";

    public static final String CREATE_TABLE_LISTS = "CREATE TABLE IF NOT EXISTS " + ShoppingList.TABLE_LISTS +
            "( " + KEY_ID + " integer primary key autoincrement, " + ShoppingList.KEY_LISTS_NAME + " text, " + ShoppingList.KEY_LISTS_DATELIST + " text, " +
            ShoppingList.KEY_LISTS_ALARM + " text, " + ShoppingList.KEY_LISTS_CURRENCY + " text);";

    public static final String CREATE_TABLE_ITEMS = "CREATE TABLE IF NOT EXISTS " + ListItem.TABLE_ITEMS +
            "( " + KEY_ID + " integer primary key autoincrement, " + ListItem.KEY_ITEMS_DATELIST + " text, " + ListItem.KEY_ITEMS_NAME + " text, " +
            ListItem.KEY_ITEMS_COUNT + " integer, " + ListItem.KEY_ITEMS_EDIZM + " text, " + ListItem.KEY_ITEMS_COAST + " integer, " +
            ListItem.KEY_ITEMS_CATEGORY + " text, " + ListItem.KEY_ITEMS_SHOP + " text, " + ListItem.KEY_ITEMS_COMMENT + " text, " +
            ListItem.KEY_ITEMS_BUYED + " boolean, " + ListItem.KEY_ITEMS_IMPORTANT + " boolean, " + ListItem.KEY_ITEMS_SALE_ID + " integer);";

    public static final String CREATE_TABLE_SHOP_SALES = "CREATE TABLE IF NOT EXISTS " + ShopSale.TABLE_SALES +
            "( " + KEY_ID + " integer primary key autoincrement, " + ShopSale.KEY_SALE_NAME + " text, " +
            ShopSale.KEY_SALE_COMMENT + " text, " + ShopSale.KEY_SALE_COAST + " text, " + ShopSale.KEY_SALE_COUNT + " text, " +
            ShopSale.KEY_SALE_IMAGE_URL + " text, " + ShopSale.KEY_SALE_SHOP + " text, " + ShopSale.KEY_SALE_PERIOD_BEGIN + " text, " + ShopSale.KEY_SALE_PERIOD_FINISH + " text);";

    ////////////////////////////////////////////////////////////// ALL ITEMS ///////////////////////////////////////////////////
    public static final String KEY_ALL_ITEMS_NAME = "name";
    public static final String KEY_ALL_ITEMS_COUNT = "count";
    public static final String KEY_ALL_ITEMS_EDIZM = "edizm";
    public static final String KEY_ALL_ITEMS_COAST = "coast";
    public static final String KEY_ALL_ITEMS_CATEGORY = "category";
    public static final String KEY_ALL_ITEMS_SHOP = "shop";
    public static final String KEY_ALL_ITEMS_COMMENT = "comment";
    public static final String KEY_ALL_ITEMS_FAVORITE = "favorite";

    public static final String[] COLUMNS_ALL_ITEMS = new String[]{KEY_ALL_ITEMS_NAME, KEY_ALL_ITEMS_COUNT, KEY_ALL_ITEMS_EDIZM,
            KEY_ALL_ITEMS_COAST, KEY_ALL_ITEMS_CATEGORY, KEY_ALL_ITEMS_SHOP, KEY_ALL_ITEMS_COMMENT, KEY_ALL_ITEMS_FAVORITE};

    public static final String CREATE_TABLE_ALL_ITEMS = "CREATE TABLE IF NOT EXISTS " + TABLE_ALL_ITEMS +
            "( " + KEY_ID + " integer primary key autoincrement, " + KEY_ALL_ITEMS_NAME + " text, " +
            KEY_ALL_ITEMS_COUNT + " integer, " + KEY_ALL_ITEMS_EDIZM + " text, " + KEY_ALL_ITEMS_COAST + " integer, " +
            KEY_ALL_ITEMS_CATEGORY + " text, " + KEY_ALL_ITEMS_SHOP + " text, " + KEY_ALL_ITEMS_COMMENT + " text, " + KEY_ALL_ITEMS_FAVORITE + " boolean);";

    //////////////////////////////////////////////////////////// FAVORITE ITEMS ////////////////////////////////////////////////////////////
    public static final String KEY_FAVORITE_ITEMS_NAME = "name";
    private static final String KEY_FAVORITE_ITEMS_COUNT = "count";
    private static final String KEY_FAVORITE_ITEMS_EDIZM = "edizm";
    private static final String KEY_FAVORITE_ITEMS_COAST = "coast";
    private static final String KEY_FAVORITE_ITEMS_CATEGORY = "category";
    private static final String KEY_FAVORITE_ITEMS_SHOP = "shop";
    private static final String KEY_FAVORITE_ITEMS_COMMENT = "comment";

    public static final String CREATE_TABLE_FAVORITE_ITEMS = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITE_ITEMS +
            "( " + KEY_ID + " integer primary key autoincrement, " + KEY_FAVORITE_ITEMS_NAME + " text, " +
            KEY_FAVORITE_ITEMS_COUNT + " integer, " + KEY_FAVORITE_ITEMS_EDIZM + " text, " + KEY_FAVORITE_ITEMS_COAST + " integer, " +
            KEY_FAVORITE_ITEMS_CATEGORY + " text, " + KEY_FAVORITE_ITEMS_SHOP + " text, " + KEY_FAVORITE_ITEMS_COMMENT + " text);";


    ///////////////////////////////////////////////////////////// SALES /////////////////////////////////////////////////////////////////


    public static final String CREATE_TABLE_SALES = "CREATE TABLE IF NOT EXISTS " + Sale.TABLE_SALES +
            "( " + KEY_ID + " integer primary key autoincrement, " + Sale.KEY_SALE_ID + " text, " + Sale.KEY_SALE_NAME + " text, " +
            Sale.KEY_SALE_COMMENT + " text, " + Sale.KEY_SALE_COAST + " text, " + Sale.KEY_SALE_COUNT + " text, " +
            Sale.KEY_SALE_COAST_FOR + " text, " + Sale.KEY_SALE_IMAGE_URL + " text, " + Sale.KEY_SALE_IMAGE_ID + " text, " +
            Sale.KEY_SALE_SHOP + " text, " + Sale.KEY_SALE_CATEGORY + " text, " + Sale.KEY_SALE_CATEGORY_TYPE + " text, " + Sale.KEY_SALE_PERIOD_BEGIN + " text, " + Sale.KEY_SALE_PERIOD_FINISH + " text);";

    ////////////////////////////////////////////////////////////// CITIES //////////////////////////////////////////////////////////////

    public static final String CREATE_TABLE_CITIES = "CREATE TABLE IF NOT EXISTS " + Region.TABLE_CITIES +
            "( " + KEY_ID + " integer primary key autoincrement, " + Region.KEY_CITY_NAME + " text, " + Region.KEY_CITY_ID + " integer);";

    ///////////////////////////////////////////////////////////// SHOPES //////////////////////////////////////////////////////

    public static final String CREATE_TABLE_SHOPES = "CREATE TABLE IF NOT EXISTS " + Shop.TABLE_SHOPES +
            "( " + KEY_ID + " integer primary key autoincrement, " + Shop.KEY_SHOP_CITY_ID + " integer, " + Shop.KEY_SHOP_ID + " integer, " + Shop.KEY_SHOP_NAME + " text, "
            + Shop.KEY_SHOP_IMAGE_URL + " text, " + Shop.KEY_SHOP_COUNT_SALES + " text, " + Shop.KEY_SHOP_FAVORITE + " text);";

    public static final String CREATE_TABLE_SALE_CATEGS = "CREATE TABLE IF NOT EXISTS " + Category.TABLE_CATEGORIES +
            "( " + KEY_ID + " integer primary key autoincrement, " + Category.KEY_CATEGORY_TYPE + " integer, " + Category.KEY_CATEGORY_ID + " integer, " + Category.KEY_CATEGORY_NAME + " text, "
            + Category.KEY_CATEGORY_IMAGE_URL + " text, " + Category.KEY_CATEGORY_FAVORITE + " text);";


    public static final String CREATE_TABLE_SKIDKAONLINE_CATEGORIES = "CREATE TABLE IF NOT EXISTS " + com.PopCorp.Purchases.model.skidkaonline.Category.TABLE_CATEGORIES +
            "( " + KEY_ID + " integer primary key autoincrement, " + com.PopCorp.Purchases.model.skidkaonline.Category.KEY_CATEGORY_NAME + " text, " +
            com.PopCorp.Purchases.model.skidkaonline.Category.KEY_CATEGORY_URL + " text, " + com.PopCorp.Purchases.model.skidkaonline.Category.KEY_CATEGORY_CITY + " text, "
            + com.PopCorp.Purchases.model.skidkaonline.Category.KEY_CATEGORY_POSITION + " integer);";

    public static final String CREATE_TABLE_SKIDKAONLINE_CITYS = "CREATE TABLE IF NOT EXISTS " + City.TABLE_CITYS +
            "( " + KEY_ID + " integer primary key autoincrement, " + City.KEY_CITY_NAME + " text, " + City.KEY_CITY_URL + " text, " + City.KEY_CITY_REGION + " text);";

    public static final String CREATE_TABLE_SKIDKAONLINE_SHOPES = "CREATE TABLE IF NOT EXISTS " + com.PopCorp.Purchases.model.skidkaonline.Shop.TABLE_SHOPES +
            "( " + KEY_ID + " integer primary key autoincrement, " + com.PopCorp.Purchases.model.skidkaonline.Shop.KEY_SHOP_NAME + " text, " +
            com.PopCorp.Purchases.model.skidkaonline.Shop.KEY_SHOP_URL + " text, " + com.PopCorp.Purchases.model.skidkaonline.Shop.KEY_SHOP_IMAGE_URL + " text, "
            + com.PopCorp.Purchases.model.skidkaonline.Shop.KEY_SHOP_CITY + " text, " + com.PopCorp.Purchases.model.skidkaonline.Shop.KEY_SHOP_CATEGORY + " text, " +
            com.PopCorp.Purchases.model.skidkaonline.Shop.KEY_SHOP_FAVORITE + " text);";

    public static final String CREATE_TABLE_SKIDKAONLINE_SALES = "CREATE TABLE IF NOT EXISTS " + com.PopCorp.Purchases.model.skidkaonline.Sale.TABLE_SALES +
            "( " + KEY_ID + " integer primary key autoincrement, " + com.PopCorp.Purchases.model.skidkaonline.Sale.KEY_SALE_ID + " text, " +
            com.PopCorp.Purchases.model.skidkaonline.Sale.KEY_SALE_SHOP + " text, " + com.PopCorp.Purchases.model.skidkaonline.Sale.KEY_SALE_GROUP_NAME + " text, "
            + com.PopCorp.Purchases.model.skidkaonline.Sale.KEY_SALE_PERIOD_BEGIN + " text, " + com.PopCorp.Purchases.model.skidkaonline.Sale.KEY_SALE_PERIOD_FINISH + " text, " +
            com.PopCorp.Purchases.model.skidkaonline.Sale.KEY_SALE_IMAGE_URL + " text, " + com.PopCorp.Purchases.model.skidkaonline.Sale.KEY_SALE_SMALL_IMAGE_URL + " text);";

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private final Context context;
    private DBHelper DBHelper;
    private SQLiteDatabase db;
    private static DB instance;

    private DB(Context context) {
        this.context = context;
    }

    public static DB getInstance() {
        return instance;
    }

    public static void setInstance(Context context) {
        instance = new DB(context);
        instance.open();
    }

    private void open() {
        DBHelper = new DBHelper(context);
        try {
            db = DBHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            db = DBHelper.getReadableDatabase();
        }
    }

    private void openToRead() {
        DBHelper = new DBHelper(context);
        try {
            db = DBHelper.getReadableDatabase();
        } catch (SQLiteException e) {
            return;
        }
    }

    public Cursor getAllData(String table) {
        try {
            return db.query(table, null, null, null, null, null, null);
        } catch (SQLiteException e) {
            return null;
        }
    }

    public long addRec(String table, String[] columns, String[] values) {
        try {
            ContentValues cv = new ContentValues();
            for (int i = 0; i < columns.length; i++) {
                cv.put(columns[i], values[i]);
            }
            return db.insert(table, null, cv);
        } catch (SQLiteException e) {
            return -1;
        }
    }

    public Cursor getData(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        try {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        } catch (SQLiteException e) {
            return null;
        }
    }

    public Cursor getData(String table, String[] columns, String selection) {
        try {
            return db.query(table, columns, selection, null, null, null, null);
        } catch (SQLiteException e) {
            return null;
        }
    }

    public void deleteRows(String table, String uslovie) throws SQLiteException, IllegalStateException {
        if (uslovie == null) {
            db.execSQL("DELETE FROM " + table);
        } else {
            db.execSQL("DELETE FROM " + table + " WHERE " + uslovie);
        }
    }

    public void update(String table, String uslovie, String column, String value) {
        try {
            db.execSQL("UPDATE " + table + " SET " + column + "='" + value + "' WHERE " + uslovie + ";");
        } catch (SQLiteException ignored) {
        }
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(table, values, whereClause, whereArgs);
    }

    public int update(String table, String[] columns, String uslovie, String[] values) {
        ContentValues cv = new ContentValues();
        for (int i = 0; i < columns.length; i++) {
            cv.put(columns[i], values[i]);
        }
        return db.update(table, cv, uslovie, null);
    }

    static public String trans(String s) {
        String res = "a";
        for (byte i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                res += "_";
            } else {
                res += s.charAt(i);
            }
        }
        return res;
    }

    static public String untrans(String s) {
        String res = "";
        for (byte i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '_') {
                res += " ";
            } else {
                res += s.charAt(i);
            }
        }
        return res.substring(1);
    }

    public Cursor getShopes(long id) {
        return getData(Shop.TABLE_SHOPES, Shop.COLUMNS_SHOPES, Shop.KEY_SHOP_CITY_ID + "=" + id);
    }

    public Cursor getFavoriteShopes(long id) {
        return getData(Shop.TABLE_SHOPES, Shop.COLUMNS_SHOPES, Shop.KEY_SHOP_CITY_ID + "=" + id + " AND " + Shop.KEY_SHOP_FAVORITE + "='true'");
    }

    public Cursor getCategories() {
        return getAllData(Category.TABLE_CATEGORIES);
    }

    public Cursor getFavoriteCategories() {
        return getData(Category.TABLE_CATEGORIES, Category.COLUMNS_SALE_CATEGS, Category.KEY_CATEGORY_FAVORITE + "='true'");
    }

    public Cursor getSalesForShop(long shopId) {
        return getData(Sale.TABLE_SALES, Sale.COLUMNS_SALES, Sale.KEY_SALE_SHOP + "=" + shopId);
    }

    public Cursor getSalesForCategory(long categoryId, long typeId) {
        return getData(Sale.TABLE_SALES, Sale.COLUMNS_SALES, Sale.KEY_SALE_CATEGORY + "=" + categoryId + " AND " + Sale.KEY_SALE_CATEGORY_TYPE + "=" + typeId);
    }

    public Cursor getShop(String shopId) {
        Cursor result = getData(Shop.TABLE_SHOPES, Shop.COLUMNS_SHOPES, Shop.KEY_SHOP_ID + "=" + shopId);
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }

    public Cursor getCategory(String categoryId, String typeId) {
        Cursor result = getData(Category.TABLE_CATEGORIES, Category.COLUMNS_SALE_CATEGS, Category.KEY_CATEGORY_ID + "=" + categoryId + " AND " + Category.KEY_CATEGORY_TYPE + "=" + typeId);
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }

    public Cursor getSale(String saleId) {
        Cursor result = getData(Sale.TABLE_SALES, Sale.COLUMNS_SALES, Sale.KEY_SALE_ID + "=" + saleId);
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }

    public Cursor getSkidkaOnlineCategories(String city) {
        return getData(com.PopCorp.Purchases.model.skidkaonline.Category.TABLE_CATEGORIES, com.PopCorp.Purchases.model.skidkaonline.Category.COLUMNS_CATEGORIES,
                com.PopCorp.Purchases.model.skidkaonline.Category.KEY_CATEGORY_CITY + "='" + city + "'");
    }

    public Cursor getSkidkaOnlineCitys() {
        return getAllData(City.TABLE_CITYS);
    }

    public Cursor getSkidkaOnlineShops(String category) {
        return getData(com.PopCorp.Purchases.model.skidkaonline.Shop.TABLE_SHOPES, com.PopCorp.Purchases.model.skidkaonline.Shop.COLUMNS_SHOPES,
                com.PopCorp.Purchases.model.skidkaonline.Shop.KEY_SHOP_CATEGORY + "='" + category + "'");
    }

    public Cursor getSkidkaOnlineSalesForShop(String shop) {
        return getData(com.PopCorp.Purchases.model.skidkaonline.Sale.TABLE_SALES, com.PopCorp.Purchases.model.skidkaonline.Sale.COLUMNS_SALES,
                com.PopCorp.Purchases.model.skidkaonline.Sale.KEY_SALE_SHOP + "='" + shop + "'");
    }

    public Cursor getLists() {
        return getAllData(ShoppingList.TABLE_LISTS);
    }

    public Cursor getListItems(long date) {
        return getData(ListItem.TABLE_ITEMS, ListItem.COLUMNS_ITEMS, ListItem.KEY_ITEMS_DATELIST + "=" + date);
    }

    public Cursor getShopCategory(long id) {
        Cursor result = getData(ShopCategory.TABLE_CATEGORIES, ShopCategory.COLUMNS_CATEGS_WITH_ID, DB.KEY_ID + "=" + id);
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }

    public Cursor getShopCategory(String name) {
        Cursor result = getData(ShopCategory.TABLE_CATEGORIES, ShopCategory.COLUMNS_CATEGS_WITH_ID, ShopCategory.KEY_CATEGS_NAME + "='" + name + "'");
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }

    public Cursor getShopSale(long saleId) {
        Cursor result = getData(ShopSale.TABLE_SALES, ShopSale.COLUMNS_SALES_WITH_ID, KEY_ID + "=" + saleId);
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }

    public Cursor getList(long id) {
        Cursor result = getData(ShoppingList.TABLE_LISTS, ShoppingList.COLUMNS_LISTS_WITH_ID, KEY_ID + "=" + id);
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }

    public Cursor getShopCategories() {
        return getAllData(ShopCategory.TABLE_CATEGORIES);
    }
}
