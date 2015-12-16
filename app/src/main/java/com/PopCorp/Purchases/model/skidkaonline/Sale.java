package com.PopCorp.Purchases.model.skidkaonline;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.PopCorp.Purchases.db.DB;

public class Sale implements Comparable<Sale>, Parcelable {

    public static final String TABLE_SALES = "Skidkaonline_Sales";

    public static final String KEY_SALE_ID = "id";
    public static final String KEY_SALE_SHOP = "shop";
    public static final String KEY_SALE_GROUP_NAME = "group_name";
    public static final String KEY_SALE_PERIOD_BEGIN = "periodBegin";
    public static final String KEY_SALE_PERIOD_FINISH = "periodFinish";
    public static final String KEY_SALE_IMAGE_URL = "image_url";
    public static final String KEY_SALE_SMALL_IMAGE_URL = "small_image_url";

    public static final String[] COLUMNS_SALES = new String[]{KEY_SALE_ID, KEY_SALE_SHOP, KEY_SALE_GROUP_NAME, KEY_SALE_PERIOD_BEGIN, KEY_SALE_PERIOD_FINISH, KEY_SALE_IMAGE_URL, KEY_SALE_SMALL_IMAGE_URL};

    private String id;
    private String shop;
    private String groupName;
    private String periodBegin;
    private String periodFinish;
    private String imageUrl;
    private String smallImageUrl;

    public Sale(String id, String shop, String groupName, String periodBegin, String periodFinish, String imageUrl, String smallImageUrl) {
        this.id = id;
        this.shop = shop;
        this.groupName = groupName;
        this.periodBegin = periodBegin;
        this.periodFinish = periodFinish;
        this.imageUrl = imageUrl;
        this.smallImageUrl = smallImageUrl;
    }

    public String[] getFields() {
        return new String[]{
                getId(),
                getShop(),
                getGroupName(),
                getPeriodBegin(),
                getPeriodFinish(),
                getImageUrl(),
                getSmallImageUrl()
        };
    }

    public boolean contentEquals(Sale sale) {
        if (getFields().length != sale.getFields().length) {
            throw new RuntimeException("Fields of shops are not equals length");
        }
        for (int i = 0; i < getFields().length - 1; i++) {
            if (!getFields()[i].equals(sale.getFields()[i])) {
                return false;
            }
        }
        return true;
    }

    public void update(Sale sale) {
        setShop(sale.getShop());
        setGroupName(sale.getGroupName());
        setPeriodBegin(sale.getPeriodBegin());
        setPeriodFinish(sale.getPeriodFinish());
        setImageUrl(sale.getImageUrl());
        setSmallImageUrl(sale.getSmallImageUrl());
    }

    public void updateOrAddToDB() {
        int countUpdated = DB.getInstance().update(TABLE_SALES, COLUMNS_SALES, KEY_SALE_ID + "='" + getId() + "'", getFields());
        if (countUpdated == 0) {
            DB.getInstance().addRec(TABLE_SALES, COLUMNS_SALES, getFields());
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_SALES, KEY_SALE_ID + "='" + getId() + "'");
    }

    @Override
    public int compareTo(@NonNull Sale another) {
        int result = 0;
        if (!getGroupName().isEmpty() && !another.getGroupName().isEmpty()){
            result = getGroupName().compareToIgnoreCase(another.getGroupName());
        } else if (getGroupName().isEmpty() && !another.getGroupName().isEmpty()){
            result = 1;
        } else if (!getGroupName().isEmpty() && another.getGroupName().isEmpty()){
            result = -1;
        }
        if (result == 0){
            getId().compareToIgnoreCase(another.getId());
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Sale)) return false;
        Sale sale = (Sale) object;
        return getId().equals(sale.getId());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getShop());
        dest.writeString(getGroupName());
        dest.writeString(getPeriodBegin());
        dest.writeString(getPeriodFinish());
        dest.writeString(getImageUrl());
        dest.writeString(getSmallImageUrl());
    }

    public static final Parcelable.Creator<Sale> CREATOR = new Parcelable.Creator<Sale>() {
        public Sale createFromParcel(Parcel in) {
            return new Sale(in);
        }

        public Sale[] newArray(int size) {
            return new Sale[size];
        }
    };

    private Sale(Parcel parcel) {
        setId(parcel.readString());
        setShop(parcel.readString());
        setGroupName(parcel.readString());
        setPeriodBegin(parcel.readString());
        setPeriodFinish(parcel.readString());
        setImageUrl(parcel.readString());
        setSmallImageUrl(parcel.readString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

}
