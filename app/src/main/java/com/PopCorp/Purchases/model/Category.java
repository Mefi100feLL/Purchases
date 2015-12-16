package com.PopCorp.Purchases.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.PopCorp.Purchases.db.DB;

import java.util.HashSet;
import java.util.Set;

public class Category implements Comparable<Category>, Parcelable {

    public static final String TABLE_CATEGORIES = "Sale_categories";

    public static final String KEY_CATEGORY_TYPE = "type";
    public static final String KEY_CATEGORY_ID = "id";
    public static final String KEY_CATEGORY_NAME = "name";
    public static final String KEY_CATEGORY_IMAGE_URL = "image_url";
    public static final String KEY_CATEGORY_FAVORITE = "favorite";

    public static final String[] COLUMNS_SALE_CATEGS = new String[]{KEY_CATEGORY_TYPE, KEY_CATEGORY_ID, KEY_CATEGORY_NAME, KEY_CATEGORY_IMAGE_URL, KEY_CATEGORY_FAVORITE};

    private long id;
    private String name;
    private CategoryType type;
    private String imageUrl;
    private boolean favorite;

    private static Set<Category> categories = new HashSet<>();

    public static Category create(long id, String name, CategoryType type, String imageUrl, boolean favorite) {
        Category newCategory = new Category(id, name, type, imageUrl, favorite);
        for (Category category : categories) {
            if (category.getId() == id && category.getType().equals(type)) {
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

    private Category(long id, String name, CategoryType type, String imageUrl, boolean favorite) {
        setId(id);
        setName(name);
        setType(type);
        setImageUrl(imageUrl);
        setFavorite(favorite);
    }

    public String[] getFields() {
        return new String[]{
                String.valueOf(getType().getId()),
                String.valueOf(getId()),
                getName(),
                getImageUrl(),
                String.valueOf(isFavorite())
        };
    }

    public boolean contentEquals(Category category) {
        if (getFields().length != category.getFields().length) {
            throw new RuntimeException("Fields of shops are not equals length");
        }
        for (int i = 0; i < getFields().length - 2; i++) {
            if (!getFields()[i].equals(category.getFields()[i])) {
                return false;
            }
        }
        return true;
    }

    public void update(Category category) {
        setName(category.getName());
        setType(category.getType());
        setImageUrl(category.getImageUrl());
    }

    public void updateOrAddToDB() {
        int countUpdated = DB.getInstance().update(TABLE_CATEGORIES, COLUMNS_SALE_CATEGS, KEY_CATEGORY_ID + "=" + getId() + " AND " + KEY_CATEGORY_TYPE + "=" + type.getId(), getFields());
        if (countUpdated == 0) {
            DB.getInstance().addRec(TABLE_CATEGORIES, COLUMNS_SALE_CATEGS, getFields());
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_CATEGORIES, KEY_CATEGORY_ID + "=" + getId() + " AND " + KEY_CATEGORY_TYPE + "=" + type.getId());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Category)) return false;
        Category category = (Category) object;
        if (getId() == category.getId()) {
            if (getType().equals(category.getType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull Category another) {
        if (!getType().equals(another.getType())) {
            if (getType().getId() < another.getType().getId()) {
                return -1;
            } else if (getType().getId() > another.getType().getId()) {
                return 1;
            }
        }
        if (getId() < another.getId()) {
            return -1;
        } else if (getId() > another.getId()) {
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
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(type.getId());
        dest.writeString(imageUrl);
        dest.writeString(String.valueOf(favorite));
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
        setId(parcel.readLong());
        setName(parcel.readString());
        setType(CategoryType.create(parcel.readInt()));
        setImageUrl(parcel.readString());
        setFavorite(Boolean.valueOf(parcel.readString()));
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

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
