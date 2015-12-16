package com.PopCorp.Purchases.model.skidkaonline;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.PopCorp.Purchases.db.DB;

import java.util.HashSet;
import java.util.Set;

public class Category implements Comparable<Category>, Parcelable {

    public static final String TABLE_CATEGORIES = "SkidkaOnline_categories";

    public static final String KEY_CATEGORY_NAME = "name";
    public static final String KEY_CATEGORY_URL = "url";
    public static final String KEY_CATEGORY_CITY = "city";
    public static final String KEY_CATEGORY_POSITION = "position";

    public static final String[] COLUMNS_CATEGORIES = new String[]{KEY_CATEGORY_NAME, KEY_CATEGORY_URL, KEY_CATEGORY_CITY, KEY_CATEGORY_POSITION};

    private String city;
    private String name;
    private String url;
    private int position;

    private static Set<Category> categories = new HashSet<>();

    public static Category create(String city, String name, String url, int position) {
        Category newCategory = new Category(city, name, url, position);
        for (Category category : categories) {
            if (category.getUrl().equals(newCategory.getUrl())) {
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

    private Category(String city, String name, String url, int position) {
        setCity(city);
        setName(name);
        setUrl(url);
        setPosition(position);
    }

    public String[] getFields() {
        return new String[]{
                getName(),
                getUrl(),
                getCity(),
                String.valueOf(position)
        };
    }

    public boolean contentEquals(Category category) {
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

    public void update(Category category) {
        setName(category.getName());
        setUrl(category.getUrl());
        setCity(category.getCity());
        setPosition(category.getPosition());
    }

    public void updateOrAddToDB() {
        int countUpdated = DB.getInstance().update(TABLE_CATEGORIES, COLUMNS_CATEGORIES, KEY_CATEGORY_URL + "='" + getUrl() + "'", getFields());
        if (countUpdated == 0) {
            DB.getInstance().addRec(TABLE_CATEGORIES, COLUMNS_CATEGORIES, getFields());
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_CATEGORIES, KEY_CATEGORY_URL + "='" + getUrl() + "'");
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Category)) return false;
        Category category = (Category) object;
        return (getUrl().equals(category.getUrl()));
    }

    @Override
    public int compareTo(@NonNull Category another) {
        if (getPosition() < another.getPosition()) {
            return -1;
        } else if (getPosition() > another.getPosition()) {
            return 1;
        }
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getCity());
        dest.writeString(getName());
        dest.writeString(getUrl());
        dest.writeInt(position);
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    private Category(Parcel parcel) {
        setCity(parcel.readString());
        setName(parcel.readString());
        setUrl(parcel.readString());
        setPosition(parcel.readInt());
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
