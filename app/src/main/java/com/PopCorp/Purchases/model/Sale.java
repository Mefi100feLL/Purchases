package com.PopCorp.Purchases.model;

import com.PopCorp.Purchases.db.DB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Sale implements Comparable<Sale> {

    public static final String TABLE_SALES = "Sales";

    public static final String KEY_SALE_ID = "id_sale";
    public static final String KEY_SALE_NAME = "title";
    public static final String KEY_SALE_COMMENT = "subtitle";
    public static final String KEY_SALE_COAST = "coast";
    public static final String KEY_SALE_COUNT = "count";
    public static final String KEY_SALE_COAST_FOR = "coast_for";
    public static final String KEY_SALE_IMAGE_URL = "image_url";
    public static final String KEY_SALE_IMAGE_ID = "id_image";
    public static final String KEY_SALE_SHOP = "shop";
    public static final String KEY_SALE_CATEGORY = "category";
    public static final String KEY_SALE_CATEGORY_TYPE = "category_type";
    public static final String KEY_SALE_PERIOD_BEGIN = "period_begin";
    public static final String KEY_SALE_PERIOD_FINISH = "period_finish";

    public static final String[] COLUMNS_SALES = new String[]{KEY_SALE_ID, KEY_SALE_NAME, KEY_SALE_COMMENT, KEY_SALE_COAST,
            KEY_SALE_COUNT, KEY_SALE_COAST_FOR, KEY_SALE_IMAGE_URL, KEY_SALE_IMAGE_ID, KEY_SALE_SHOP, KEY_SALE_CATEGORY, KEY_SALE_CATEGORY_TYPE, KEY_SALE_PERIOD_BEGIN, KEY_SALE_PERIOD_FINISH};

    private long id;
    private String name;
    private String comment;
    private String coast;
    private String count;
    private String coastFor;
    private String imageUrl;
    private long imageId;
    private Shop shop;
    private Category category;
    private Date periodBegin;
    private Date periodFinish;

    private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));

    public Sale(long id, String name, String comment, String coast, String count, String coastFor, String imageUrl, long imageId, Shop shop, Category category, Date periodBegin, Date periodFinish) {
        setId(id);
        setName(name);
        setComment(comment);
        setCoast(coast);
        setCount(count);
        setCoastFor(coastFor);
        setImageUrl(imageUrl);
        setImageId(imageId);
        setShop(shop);
        setCategory(category);
        setPeriodBegin(periodBegin);
        setPeriodFinish(periodFinish);
    }

    public String[] getFields() {
        return new String[]{
                String.valueOf(getId()),
                getName(),
                getComment(),
                getCoast(),
                getCount(),
                getCoastFor(),
                getImageUrl(),
                String.valueOf(getImageId()),
                String.valueOf(getShop().getId()),
                String.valueOf(getCategory().getId()),
                String.valueOf(getCategory().getType().getId()),
                formatter.format(getPeriodBegin()),
                formatter.format(getPeriodFinish())
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
        setName(sale.getName());
        if (!sale.getComment().isEmpty()) {
            setComment(sale.getComment());
        }
        if (!sale.getCoast().isEmpty()) {
            setCoast(sale.getCoast());
        }
        if (!sale.getCount().isEmpty()) {
            setCount(sale.getCount());
        }
        if (!sale.getCoastFor().isEmpty()) {
            setCoastFor(sale.getCoastFor());
        }
        if (!sale.getImageUrl().isEmpty()) {
            setImageUrl(sale.getImageUrl());
        }
        if (sale.getImageId() != 0) {
            setImageId(sale.getImageId());
        }
        if (sale.getShop() != null) {
            setShop(sale.getShop());
        }
        if (sale.getCategory() != null) {
            setCategory(sale.getCategory());
        }
        if (sale.getPeriodBegin() != null) {
            setPeriodBegin(sale.getPeriodBegin());
        }
        if (sale.getPeriodFinish() != null) {
            setPeriodFinish(sale.getPeriodFinish());
        }
    }

    public void updateOrAddToDB() {
        int countUpdated = DB.getInstance().update(TABLE_SALES, COLUMNS_SALES, KEY_SALE_ID + "=" + getId(), getFields());
        if (countUpdated == 0) {
            DB.getInstance().addRec(TABLE_SALES, COLUMNS_SALES, getFields());
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_SALES, KEY_SALE_ID + "=" + getId());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Sale)) return false;
        Sale sale = (Sale) object;
        return getId() == sale.getId();
    }

    @Override
    public int compareTo(Sale another) {
        if (getId() < another.getId()){
            return -1;
        } else if (getId() > another.getId()){
            return 1;
        }
        return 0;
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

    public String getCoastFor() {
        return coastFor;
    }

    public void setCoastFor(String coastFor) {
        this.coastFor = coastFor;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Date getPeriodBegin() {
        return periodBegin;
    }

    public void setPeriodBegin(Date periodBegin) {
        this.periodBegin = periodBegin;
    }

    public Date getPeriodFinish() {
        return periodFinish;
    }

    public void setPeriodFinish(Date periodFinish) {
        this.periodFinish = periodFinish;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
}