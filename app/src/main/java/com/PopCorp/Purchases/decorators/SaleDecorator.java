package com.PopCorp.Purchases.decorators;

import com.PopCorp.Purchases.model.Sale;

public abstract class SaleDecorator {

    private Sale sale;
    private boolean header;

    public SaleDecorator(Sale sale, boolean isHeader){
        setSale(sale);
        setHeader(isHeader);
    }

    abstract public String getName();

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }
}
