package com.PopCorp.Purchases.model;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;

public class ShopSale {

    public static final String TABLE_SALES = "Shop_sales";

    public static final String KEY_SALE_NAME = "title";
    public static final String KEY_SALE_COMMENT = "subtitle";
    public static final String KEY_SALE_COAST = "coast";
    public static final String KEY_SALE_COUNT = "count";
    public static final String KEY_SALE_IMAGE_URL = "image_url";
    public static final String KEY_SALE_SHOP = "shop";
    public static final String KEY_SALE_PERIOD_BEGIN = "period_begin";
    public static final String KEY_SALE_PERIOD_FINISH = "period_finish";

    public static final String[] COLUMNS_SALES = new String[]{KEY_SALE_NAME, KEY_SALE_COMMENT, KEY_SALE_COAST,
            KEY_SALE_COUNT, KEY_SALE_IMAGE_URL, KEY_SALE_SHOP, KEY_SALE_PERIOD_BEGIN, KEY_SALE_PERIOD_FINISH};

    public static final String[] COLUMNS_SALES_WITH_ID = new String[]{DB.KEY_ID, KEY_SALE_NAME, KEY_SALE_COMMENT, KEY_SALE_COAST,
            KEY_SALE_COUNT, KEY_SALE_IMAGE_URL, KEY_SALE_SHOP, KEY_SALE_PERIOD_BEGIN, KEY_SALE_PERIOD_FINISH};

    private long id;
    private String name;
    private String comment;
    private String coast;
    private String count;
    private String imageUrl;
    private String shop;
    private String periodBegin;
    private String periodFinish;

    public ShopSale(long id, String name, String comment, String coast, String count, String imageUrl, String shop, String periodBegin, String periodFinish) {
        setId(id);
        setName(name);
        setComment(comment);
        setCoast(coast);
        setCount(count);
        setImageUrl(imageUrl);
        setShop(shop);
        setPeriodBegin(periodBegin);
        setPeriodFinish(periodFinish);
    }

    public ShopSale(Cursor cursor) {
        this(
                cursor.getLong(cursor.getColumnIndex(DB.KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_SALE_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_SALE_COMMENT)),
                cursor.getString(cursor.getColumnIndex(KEY_SALE_COAST)),
                cursor.getString(cursor.getColumnIndex(KEY_SALE_COUNT)),
                cursor.getString(cursor.getColumnIndex(KEY_SALE_IMAGE_URL)),
                cursor.getString(cursor.getColumnIndex(KEY_SALE_SHOP)),
                cursor.getString(cursor.getColumnIndex(KEY_SALE_PERIOD_BEGIN)),
                cursor.getString(cursor.getColumnIndex(KEY_SALE_PERIOD_FINISH))
        );
    }

    public String[] getFields() {
        return new String[]{
                getName(),
                getComment(),
                getCoast(),
                getCount(),
                getImageUrl(),
                getShop(),
                getPeriodBegin(),
                getPeriodFinish()
        };
    }

    public boolean contentEquals(ShopSale shopSale) {
        if (getFields().length != shopSale.getFields().length) {
            throw new RuntimeException("Fields of shops are not equals length");
        }
        for (int i = 0; i < getFields().length - 1; i++) {
            if (!getFields()[i].equals(shopSale.getFields()[i])) {
                return false;
            }
        }
        return true;
    }

    public void update(ShopSale shopSale) {
        setName(shopSale.getName());
        setComment(shopSale.getComment());
        setCoast(shopSale.getCoast());
        setCount(shopSale.getCount());
        setImageUrl(shopSale.getImageUrl());
        setShop(shopSale.getShop());
        setPeriodBegin(shopSale.getPeriodBegin());
        setPeriodFinish(shopSale.getPeriodFinish());
    }

    public void updateOrAddToDB() {
        int countUpdated = DB.getInstance().update(TABLE_SALES, COLUMNS_SALES, DB.KEY_ID + "=" + getId(), getFields());
        if (countUpdated == 0) {
            id = DB.getInstance().addRec(TABLE_SALES, COLUMNS_SALES, getFields());
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_SALES, DB.KEY_ID + "=" + getId());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ShopSale)) return false;
        ShopSale shopSale = (ShopSale) object;
        return (getId() == shopSale.getId());
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCoast() {
        return coast;
    }

    public void setCoast(String coast) {
        this.coast = coast;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getPeriodBegin() {
        return periodBegin;
    }

    public void setPeriodBegin(String periodBegin) {
        this.periodBegin = periodBegin;
    }

    public String getPeriodFinish() {
        return periodFinish;
    }

    public void setPeriodFinish(String periodFinish) {
        this.periodFinish = periodFinish;
    }
}
