package com.PopCorp.Purchases.model;

import java.util.HashSet;
import java.util.Set;

abstract public class CategoryType {

    private int id;

    private static Set<CategoryType> types = new HashSet<>();

    public static CategoryType create(int id){
        for (CategoryType type : types){
            if (type.getId() == id){
                return type;
            }
        }
        CategoryType type;
        switch (id){
            case 1:
                type = new ProdType(id);
                break;
            case 2:
                type = new PromType(id);
                break;
            default:
                throw new RuntimeException("Unknown type of category");
        }
        types.add(type);
        return type;
    }

    abstract public String getTypeUrl();

    @Override
    public boolean equals(Object object){
        if (!(object instanceof CategoryType)) return false;
        CategoryType type = (CategoryType) object;
        return getId() == type.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
