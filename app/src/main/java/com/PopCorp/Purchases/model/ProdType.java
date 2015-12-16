package com.PopCorp.Purchases.model;

public class ProdType extends CategoryType{

    public ProdType(int id) {
        setId(id);
    }

    @Override
    public String getTypeUrl() {
        return "cat=" + getId();
    }
}
