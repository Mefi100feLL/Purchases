package com.PopCorp.Purchases.model;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;

import java.util.HashSet;
import java.util.Set;

public class ShopCategory implements Comparable<ShopCategory> {

    public static final String TABLE_CATEGORIES = "Categories";

    public static final String KEY_CATEGS_NAME = "name";
    public static final String KEY_CATEGS_COLOR = "color";

    public static final String[] COLUMNS_CATEGS = new String[]{KEY_CATEGS_NAME, KEY_CATEGS_COLOR};

    public static final String[] COLUMNS_CATEGS_WITH_ID = new String[]{DB.KEY_ID, KEY_CATEGS_NAME, KEY_CATEGS_COLOR};

    private long id;
    private String name;
    private int color;

    private static Set<ShopCategory> categories = new HashSet<>();

    public static ShopCategory create(long id, String name, int color) {
        ShopCategory newCategory = new ShopCategory(id, name, color);
        return checkCategory(newCategory);
    }

    public static ShopCategory create(long id) {
        ShopCategory newCategory = null;
        Cursor cursor = DB.getInstance().getShopCategory(id);
        if (cursor != null) {
            newCategory = new ShopCategory(cursor);
            cursor.close();
        }
        return checkCategory(newCategory);
    }

    public static ShopCategory create(String name) {
        ShopCategory newCategory = null;
        Cursor cursor = DB.getInstance().getShopCategory(name);
        if (cursor != null) {
            newCategory = new ShopCategory(cursor);
            cursor.close();
        }
        return checkCategory(newCategory);
    }

    public static ShopCategory create(Cursor cursor) {
        ShopCategory newCategory;
        newCategory = new ShopCategory(cursor);
        return checkCategory(newCategory);
    }

    private static ShopCategory checkCategory(ShopCategory newCategory) {
        for (ShopCategory category : categories) {
            if (category.getId() == newCategory.getId()) {
                if (!category.contentEquals(newCategory)) {
                    category.update(newCategory);
                    category.updateOrAddToDB();
                }
                return category;
            }
        }
        categories.add(newCategory);
        return newCategory;
    }

    private ShopCategory(Cursor cursor) {
        this(
                cursor.getLong(cursor.getColumnIndex(DB.KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_CATEGS_NAME)),
                cursor.getInt(cursor.getColumnIndex(KEY_CATEGS_COLOR))
        );
    }

    private ShopCategory(long id, String name, int color) {
        setId(id);
        setName(name);
        setColor(color);
    }

    public String[] getFields() {
        return new String[]{
                getName(),
                String.valueOf(getColor())
        };
    }

    public boolean contentEquals(ShopCategory category) {
        if (getFields().length != category.getFields().length) {
            throw new RuntimeException("Fields of shops are not equals length");
        }
        for (int i = 0; i < getFields().length - 1; i++) {
            if (!getFields()[i].equals(category.getFields()[i])) {
                return false;
            }
        }
        return true;
    }

    public void update(ShopCategory category) {
        setName(category.getName());
        setColor(category.getColor());
    }

    public void updateOrAddToDB() {
        int countUpdated = DB.getInstance().update(TABLE_CATEGORIES, COLUMNS_CATEGS, DB.KEY_ID + "=" + getId(), getFields());
        if (countUpdated == 0) {
            id = DB.getInstance().addRec(TABLE_CATEGORIES, COLUMNS_CATEGS, getFields());
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_CATEGORIES, DB.KEY_ID + "=" + getId());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ShopCategory)) return false;
        ShopCategory category = (ShopCategory) object;
        return (getId() == category.getId());
    }

    @Override
    public int compareTo(ShopCategory another) {
        int result = 0;
        if (!equals(another)){
            if (getId() < another.getId()){
                result = -1;
            } else if (getId() > another.getId()){
                result = 1;
            }
        }
        return result;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
