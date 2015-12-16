package com.PopCorp.Purchases.model.skidkaonline;

import android.support.annotation.NonNull;

import com.PopCorp.Purchases.db.DB;

import java.util.HashSet;
import java.util.Set;

public class City implements Comparable<City> {

    public static final String TABLE_CITYS = "Skidkaonline_citys";

    public static final String KEY_CITY_NAME = "name";
    public static final String KEY_CITY_URL = "url";
    public static final String KEY_CITY_REGION = "region";

    public static final String[] COLUMNS_CITYS = new String[]{KEY_CITY_NAME, KEY_CITY_URL, KEY_CITY_REGION};

    private String name;
    private String url;
    private String region;

    private static Set<City> citys = new HashSet<>();

    public static City create(String name, String url, String region) {
        City newCity = new City(name, url, region);
        for (City city : citys) {
            if (city.getUrl().equals(newCity.getUrl())) {
                if (!city.contentEquals(newCity)) {
                    city.update(newCity);
                    city.updateOrAddToDB();
                }
                return city;
            }
        }
        citys.add(newCity);
        return newCity;
    }

    private City(String name, String url, String region){
        setName(name);
        setUrl(url);
        setRegion(region);
    }

    public String[] getFields() {
        return new String[]{
                getName(),
                getUrl(),
                getRegion()
        };
    }

    public boolean contentEquals(City city) {
        if (getFields().length != city.getFields().length) {
            throw new RuntimeException("Fields of shops are not equals length");
        }
        for (int i = 0; i < getFields().length - 1; i++) {
            if (!getFields()[i].equals(city.getFields()[i])) {
                return false;
            }
        }
        return true;
    }

    public void update(City city) {
        setName(city.getName());
        setRegion(city.getRegion());
    }

    public void updateOrAddToDB() {
        int countUpdated = DB.getInstance().update(TABLE_CITYS, COLUMNS_CITYS, KEY_CITY_URL + "='" + getUrl() + "'", getFields());
        if (countUpdated == 0) {
            DB.getInstance().addRec(TABLE_CITYS, COLUMNS_CITYS, getFields());
        }
    }

    public void removeFromDB() {
        DB.getInstance().deleteRows(TABLE_CITYS, KEY_CITY_URL + "='" + getUrl() + "'");
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof City)) return false;
        City city = (City) object;
        if (getUrl().equals(city.getUrl())) {
            if (getRegion().equals(city.getRegion())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull City another) {
        int result = getName().compareToIgnoreCase(another.getName());
        if (result == 0){
            result = getRegion().compareToIgnoreCase(another.getRegion());
        }
        if (result == 0){
            result = getUrl().compareToIgnoreCase(another.getUrl());
        }
        return result;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
