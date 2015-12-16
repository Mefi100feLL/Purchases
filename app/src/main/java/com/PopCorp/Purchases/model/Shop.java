package com.PopCorp.Purchases.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.PopCorp.Purchases.db.DB;

import java.util.HashSet;
import java.util.Set;

public class Shop implements Comparable<Shop>, Parcelable {

    public static final String TABLE_SHOPES = "Shopes";

    public static final String KEY_SHOP_CITY_ID = "id_city";
    public static final String KEY_SHOP_ID = "id";
    public static final String KEY_SHOP_NAME = "name";
    public static final String KEY_SHOP_IMAGE_URL = "image_url";
    public static final String KEY_SHOP_COUNT_SALES = "count_sales";
    public static final String KEY_SHOP_FAVORITE = "favorite";

    public static final String[] COLUMNS_SHOPES = new String[]{KEY_SHOP_CITY_ID, KEY_SHOP_ID, KEY_SHOP_NAME, KEY_SHOP_IMAGE_URL, KEY_SHOP_COUNT_SALES, KEY_SHOP_FAVORITE};

    private long id;
    private String name;
    private String imageUrl;
    private long regionId;
    private boolean favorite;
    private int countSales;

    private static Set<Shop> shops = new HashSet<>();

    public static Shop create(long id, String name, String imageUrl, long region, boolean favorite, int countSales){
        Shop newShop = new Shop(id, name, imageUrl, region, favorite, countSales);
        for (Shop shop : shops){
            if (shop.getId() == id){
                if (!shop.contentEquals(newShop)){
                    shop.update(newShop);
                    shop.updateOrAddToDB();
                }
                return shop;
            }
        }
        shops.add(newShop);
        return newShop;
    }

    private Shop(long id, String name, String imageUrl, long regionId, boolean favorite, int countSales){
        setId(id);
        setName(name);
        setImageUrl(imageUrl);
        setRegionId(regionId);
        setFavorite(favorite);
        setCountSales(countSales);
    }

    public String[] getFields(){
        return new String[]{
                String.valueOf(getRegionId()),
                String.valueOf(getId()),
                getName(),
                getImageUrl(),
                String.valueOf(getCountSales()),
                String.valueOf(isFavorite())
        };
    }

    public boolean contentEquals(Shop shop){
        if (getFields().length != shop.getFields().length){
            throw new RuntimeException("Fields of shops are not equals length");
        }
        for (int i = 0; i<getFields().length - 2; i++){
            if (!getFields()[i].equals(shop.getFields()[i])){
                return false;
            }
        }
        return true;
    }

    public void update(Shop shop){
        setName(shop.getName());
        setImageUrl(shop.getImageUrl());
        setRegionId(shop.getRegionId());
        setCountSales(shop.getCountSales());
    }

    public void updateOrAddToDB(){
        int countUpdated = DB.getInstance().update(TABLE_SHOPES, COLUMNS_SHOPES, KEY_SHOP_ID + "=" + getId(), getFields());
        if (countUpdated == 0){
            DB.getInstance().addRec(TABLE_SHOPES, COLUMNS_SHOPES, getFields());
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_SHOPES, KEY_SHOP_ID + "=" + getId());
    }

    @Override
    public int compareTo(@NonNull Shop another) {
        if (getId() < another.getId()){
            return 1;
        } else if (getId() > another.getId()){
            return -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object object){
        if (!(object instanceof Shop)) return false;
        Shop shop = (Shop) object;
        return getId() == shop.getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeLong(regionId);
        dest.writeString(String.valueOf(favorite));
        dest.writeInt(countSales);
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
        setId(parcel.readLong());
        setName(parcel.readString());
        setImageUrl(parcel.readString());
        setRegionId(parcel.readLong());
        setFavorite(Boolean.valueOf(parcel.readString()));
        setCountSales(parcel.readInt());
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getCountSales() {
        return countSales;
    }

    public void setCountSales(int countSales) {
        this.countSales = countSales;
    }
}
