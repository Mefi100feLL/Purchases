package com.PopCorp.Purchases.model;

import android.database.Cursor;

import com.PopCorp.Purchases.db.DB;

import java.util.ArrayList;

public class ShoppingList {

    public static final String TABLE_LISTS = "Lists";

    public static final String KEY_LISTS_NAME = "name";
    public static final String KEY_LISTS_DATELIST = "date";
    public static final String KEY_LISTS_ALARM = "datealarm";
    public static final String KEY_LISTS_CURRENCY = "currency";

    public static final String[] COLUMNS_LISTS = new String[]{KEY_LISTS_NAME, KEY_LISTS_DATELIST, KEY_LISTS_ALARM, KEY_LISTS_CURRENCY};

    public static final String[] COLUMNS_LISTS_WITH_ID = new String[]{DB.KEY_ID, KEY_LISTS_NAME, KEY_LISTS_DATELIST, KEY_LISTS_ALARM, KEY_LISTS_CURRENCY};

    private long id;
    private String name;
    private long date;
    private String dateAlarm;
    private String currency;

    private ArrayList<ListItem> items = new ArrayList<>();

    public ShoppingList(long id, String name, long date, String dateAlarm, String currency) {
        setId(id);
        setName(name);
        setDate(date);
        setDateAlarm(dateAlarm);
        setCurrency(currency);
        loadItems();
    }

    private void loadItems() {
        Cursor cursor = DB.getInstance().getListItems(date);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                items.add(new ListItem(cursor));
                while (cursor.moveToNext()) {
                    items.add(new ListItem(cursor));
                }
            }
            cursor.close();
        }
    }

    public String[] getFields() {
        return new String[]{
                getName(),
                String.valueOf(getDate()),
                getDateAlarm(),
                getCurrency()
        };
    }

    public boolean contentEquals(ShoppingList list) {
        if (getFields().length != list.getFields().length) {
            throw new RuntimeException("Fields of shops are not equals length");
        }
        for (int i = 0; i < getFields().length - 1; i++) {
            if (!getFields()[i].equals(list.getFields()[i])) {
                return false;
            }
        }
        return true;
    }

    public void update(ShoppingList list) {
        setName(list.getName());
        setDate(list.getDate());
        setDateAlarm(list.getDateAlarm());
        setCurrency(list.getCurrency());
    }

    public void updateOrAddToDB() {
        int countUpdated = DB.getInstance().update(TABLE_LISTS, COLUMNS_LISTS, DB.KEY_ID + "=" + getId(), getFields());
        if (countUpdated == 0) {
            id = DB.getInstance().addRec(TABLE_LISTS, COLUMNS_LISTS, getFields());
        }
        for (ListItem item : items){
            item.updateOrAddToDB();
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_LISTS, DB.KEY_ID + "=" + getId());
        for (ListItem item : items){
            item.removeFromDB();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ShoppingList)) return false;
        ShoppingList list = (ShoppingList) object;
        return (getId() == list.getId());
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

    public String getDateAlarm() {
        return dateAlarm;
    }

    public void setDateAlarm(String dateAlarm) {
        this.dateAlarm = dateAlarm;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<ListItem> getItems() {
        return items;
    }
}
