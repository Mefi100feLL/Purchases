package com.PopCorp.Purchases.model;

import java.util.HashSet;
import java.util.Set;

public class Region {

    public static final String TABLE_CITIES = "Cities";

    public static final String KEY_CITY_ID = "id";
    public static final String KEY_CITY_NAME = "name";

    public static final String[] COLUMNS_CITIES = new String[]{KEY_CITY_ID, KEY_CITY_NAME};

    private long id;
    private String name;

    private static Set<Region> regions = new HashSet<>();

    public static Region create(long id, String name){
        for (Region region : regions){
            if (region.getId() == id){
                return region;
            }
        }
        Region region = new Region(id, name);
        regions.add(region);
        return region;
    }

    private Region(long id, String name){
        setId(id);
        setName(name);
    }

    @Override
    public boolean equals(Object object){
        if (!(object instanceof Region)) return false;
        Region region = (Region) object;
        return getId() == region.getId();
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
}
