package com.PopCorp.Purchases.model.skidkaonline;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.PopCorp.Purchases.db.DB;

import java.util.HashSet;
import java.util.Set;

public class Shop implements Comparable<Shop>,Parcelable {

    public static final String TABLE_SHOPES = "Skidkaonline_Shopes";

    public static final String KEY_SHOP_NAME = "name";
    public static final String KEY_SHOP_URL = "url";
    public static final String KEY_SHOP_IMAGE_URL = "image_url";
    public static final String KEY_SHOP_CITY = "city";
    public static final String KEY_SHOP_CATEGORY = "category";
    public static final String KEY_SHOP_FAVORITE = "favorite";

    public static final String[] COLUMNS_SHOPES = new String[]{KEY_SHOP_NAME, KEY_SHOP_URL, KEY_SHOP_IMAGE_URL, KEY_SHOP_CITY, KEY_SHOP_CATEGORY, KEY_SHOP_FAVORITE};

    private String name;
    private String url;
    private String image;
    private String city;
    private String category;
    private boolean favorite;

    private static Set<Shop> shops = new HashSet<>();

    public static Shop create(String name, String url, String image, String city, String category, boolean favorite) {
        Shop newShop = new Shop(name, url, image, city, category, favorite);
        for (Shop shop : shops) {
            if (shop.getUrl().equals(url)) {
                if (!shop.contentEquals(newShop)) {
                    shop.update(newShop);
                    shop.updateOrAddToDB();
                }
                return shop;
            }
        }
        shops.add(newShop);
        return newShop;
    }

    private Shop(String name, String url, String image, String city, String category, boolean favorite) {
        setName(name);
        setUrl(url);
        setImage(image);
        setCity(city);
        setCategory(category);
        setFavorite(favorite);
    }

    public String[] getFields() {
        return new String[]{
                getName(),
                getUrl(),
                getImage(),
                getCity(),
                getCategory(),
                String.valueOf(isFavorite())
        };
    }

    public boolean contentEquals(Shop shop) {
        if (getFields().length != shop.getFields().length) {
            throw new RuntimeException("Fields of shops are not equals length");
        }
        for (int i = 0; i < getFields().length - 2; i++) {
            if (!getFields()[i].equals(shop.getFields()[i])) {
                return false;
            }
        }
        return true;
    }

    public void update(Shop shop) {
        setName(shop.getName());
        setUrl(shop.getUrl());
        setImage(shop.getImage());
        setCity(shop.getCity());
        setCategory(shop.getCategory());
    }

    public void updateOrAddToDB() {
        int countUpdated = DB.getInstance().update(TABLE_SHOPES, COLUMNS_SHOPES, KEY_SHOP_URL + "='" + getUrl() + "'", getFields());
        if (countUpdated == 0) {
            DB.getInstance().addRec(TABLE_SHOPES, COLUMNS_SHOPES, getFields());
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_SHOPES, KEY_SHOP_URL + "='" + getUrl() + "'");
    }

    @Override
    public int compareTo(@NonNull Shop another) {
        int result = getName().compareToIgnoreCase(another.getName());
        if (result == 0) {
            result = getUrl().compareToIgnoreCase(another.getUrl());
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Shop)) return false;
        Shop shop = (Shop) object;
        return getUrl().equals(shop.getUrl());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeString(getUrl());
        dest.writeString(getImage());
        dest.writeString(getCity());
        dest.writeString(getCategory());
        dest.writeString(String.valueOf(isFavorite()));
    }

    public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };

    private Shop(Parcel parcel) {
        setName(parcel.readString());
        setUrl(parcel.readString());
        setImage(parcel.readString());
        setCity(parcel.readString());
        setCategory(parcel.readString());
        setFavorite(Boolean.valueOf(parcel.readString()));
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
