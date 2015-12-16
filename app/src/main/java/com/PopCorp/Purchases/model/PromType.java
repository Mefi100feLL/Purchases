package com.PopCorp.Purchases.model;

public class PromType extends CategoryType{

    public PromType(int id) {
        setId(id);
    }

    @Override
    public String getTypeUrl() {
        return "cat2=" + getId();
    }
}