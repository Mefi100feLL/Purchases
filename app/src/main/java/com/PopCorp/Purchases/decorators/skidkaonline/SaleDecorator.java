package com.PopCorp.Purchases.decorators.skidkaonline;

import android.support.annotation.NonNull;

import com.PopCorp.Purchases.model.skidkaonline.Sale;

public class SaleDecorator implements Comparable<SaleDecorator> {

    private Sale sale;
    private boolean header;
    private String name;

    public SaleDecorator(Sale sale, boolean header, String name) {
        setSale(sale);
        setHeader(header);
        setName(name);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SaleDecorator)) return false;

        SaleDecorator another = (SaleDecorator) object;
        if (isHeader() && another.isHeader()) {
            return (getName().equals(another.getName()));
        }
        if (!(isHeader() || another.isHeader())) {
            return getSale().equals(another.getSale());
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull SaleDecorator another) {
        int result;
        result = getName().compareTo(another.getName());
        if (result == 0){
            if (isHeader() && !another.isHeader()){
                return -1;
            }
            if (!isHeader() && another.isHeader()){
                return 1;
            }
            if (!(isHeader() || another.isHeader())){
                result = getSale().compareTo(another.getSale());
            }
        }
        return result;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
