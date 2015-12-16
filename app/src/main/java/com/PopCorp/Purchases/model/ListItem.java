package com.PopCorp.Purchases.model;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;

import java.io.IOException;
import java.math.BigDecimal;

public class ListItem {

    public static final String TABLE_ITEMS = "Items";

    public static final String KEY_ITEMS_DATELIST = "date";
    public static final String KEY_ITEMS_NAME = "name";
    public static final String KEY_ITEMS_COUNT = "count";
    public static final String KEY_ITEMS_EDIZM = "edizm";
    public static final String KEY_ITEMS_COAST = "coast";
    public static final String KEY_ITEMS_CATEGORY = "category";
    public static final String KEY_ITEMS_SHOP = "shop";
    public static final String KEY_ITEMS_COMMENT = "comment";
    public static final String KEY_ITEMS_BUYED = "buyed";
    public static final String KEY_ITEMS_IMPORTANT = "important";
    public static final String KEY_ITEMS_SALE_ID = "sale_id";

    public static final String[] COLUMNS_ITEMS = new String[]{KEY_ITEMS_DATELIST, KEY_ITEMS_NAME, KEY_ITEMS_COUNT, KEY_ITEMS_EDIZM,
            KEY_ITEMS_COAST, KEY_ITEMS_CATEGORY, KEY_ITEMS_SHOP, KEY_ITEMS_COMMENT, KEY_ITEMS_BUYED, KEY_ITEMS_IMPORTANT, KEY_ITEMS_SALE_ID};

    private long id;
    private String dateList;
    private String name;
    private BigDecimal count;
    private String edizm;
    private BigDecimal coast;
    private ShopCategory category;
    private String shop;
    private String comment;
    private boolean buyed;
    private boolean important;
    private ShopSale sale;

    public ListItem(long id, String dateList, String name, String count, String edizm, String coast, ShopCategory category, String shop, String comment, boolean buyed, boolean important, ShopSale sale) {
        setId(id);
        setDateList(dateList);
        setName(name);
        setCount(new BigDecimal(count));
        setEdizm(edizm);
        setCoast(new BigDecimal(coast));
        setCategory(category);
        setShop(shop);
        setComment(comment);
        setBuyed(buyed);
        setImportant(important);
        setSale(sale);
    }

    public ListItem(Cursor cursor) {
        String categId = cursor.getString(cursor.getColumnIndex(ListItem.KEY_ITEMS_CATEGORY));
        try {
            long id = Long.valueOf(categId);
            category = ShopCategory.create(id);
        } catch (Exception e) {
            category = ShopCategory.create(categId);
        }

        long saleId = cursor.getLong(cursor.getColumnIndex(ListItem.KEY_ITEMS_SALE_ID));
        if (saleId != 0) {
            Cursor saleCursor = DB.getInstance().getShopSale(saleId);
            if (saleCursor != null) {
                sale = new ShopSale(saleCursor);
                saleCursor.close();
            }
        }
        setId(cursor.getLong(cursor.getColumnIndex(DB.KEY_ID)));
        setDateList(cursor.getString(cursor.getColumnIndex(ListItem.KEY_ITEMS_DATELIST)));
        setName(cursor.getString(cursor.getColumnIndex(ListItem.KEY_ITEMS_NAME)));
        setCount(new BigDecimal(cursor.getString(cursor.getColumnIndex(ListItem.KEY_ITEMS_COUNT))));
        setEdizm(cursor.getString(cursor.getColumnIndex(ListItem.KEY_ITEMS_EDIZM)));
        setCoast(new BigDecimal(cursor.getString(cursor.getColumnIndex(ListItem.KEY_ITEMS_COAST))));
        setShop(cursor.getString(cursor.getColumnIndex(ListItem.KEY_ITEMS_SHOP)));
        setComment(cursor.getString(cursor.getColumnIndex(ListItem.KEY_ITEMS_COMMENT)));
        setBuyed(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ListItem.KEY_ITEMS_BUYED))));
        setImportant(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ListItem.KEY_ITEMS_IMPORTANT))));
    }

    public String[] getFields() {
        String categoryId = getCategory() != null ? String.valueOf(getCategory().getId()) : "";
        String saleId = getSale() != null ? String.valueOf(getSale().getId()) : "";
        return new String[]{
                getDateList(),
                getName(),
                String.valueOf(getCount()),
                getEdizm(),
                String.valueOf(getCoast()),
                categoryId,
                getShop(),
                getComment(),
                String.valueOf(isBuyed()),
                String.valueOf(isImportant()),
                saleId
        };
    }

    public boolean contentEquals(ListItem listItem) {
        if (getFields().length != listItem.getFields().length) {
            throw new RuntimeException("Fields of shops are not equals length");
        }
        for (int i = 0; i < getFields().length - 1; i++) {
            if (!getFields()[i].equals(listItem.getFields()[i])) {
                return false;
            }
        }
        return true;
    }

    public void update(ListItem listItem) {
        setDateList(listItem.getDateList());
        setName(listItem.getName());
        setCount(listItem.getCount());
        setEdizm(listItem.getEdizm());
        setCoast(listItem.getCoast());
        setCategory(listItem.getCategory());
        setShop(listItem.getShop());
        setComment(listItem.getComment());
        setBuyed(listItem.isBuyed());
        setImportant(listItem.isImportant());
        setSale(listItem.getSale());
    }

    public void updateOrAddToDB() {
        int countUpdated = DB.getInstance().update(TABLE_ITEMS, COLUMNS_ITEMS, DB.KEY_ID + "=" + getId(), getFields());
        if (countUpdated == 0) {
            DB.getInstance().addRec(TABLE_ITEMS, COLUMNS_ITEMS, getFields());
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_ITEMS, DB.KEY_ID + "=" + getId());
        sale.removeFromDB();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ListItem)) return false;
        ListItem listItem = (ListItem) object;
        return (getId() == listItem.getId());
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDateList() {
        return dateList;
    }

    public void setDateList(String dateList) {
        this.dateList = dateList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public String getEdizm() {
        return edizm;
    }

    public void setEdizm(String edizm) {
        this.edizm = edizm;
    }

    public BigDecimal getCoast() {
        return coast;
    }

    public void setCoast(BigDecimal coast) {
        this.coast = coast;
    }

    public ShopCategory getCategory() {
        return category;
    }

    public void setCategory(ShopCategory category) {
        this.category = category;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isBuyed() {
        return buyed;
    }

    public void setBuyed(boolean buyed) {
        this.buyed = buyed;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public ShopSale getSale() {
        return sale;
    }

    public void setSale(ShopSale sale) {
        this.sale = sale;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }
}
