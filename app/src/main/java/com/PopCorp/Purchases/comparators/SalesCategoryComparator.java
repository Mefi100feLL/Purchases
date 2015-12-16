package com.PopCorp.Purchases.comparators;

import com.PopCorp.Purchases.decorators.SaleCategoryDecorator;
import com.PopCorp.Purchases.decorators.SaleDecorator;

import java.util.Comparator;

public class SalesCategoryComparator implements Comparator<SaleDecorator> {

    @Override
    public int compare(SaleDecorator lhs, SaleDecorator rhs) {
        SaleCategoryDecorator left = (SaleCategoryDecorator) lhs;
        SaleCategoryDecorator right = (SaleCategoryDecorator) rhs;

        int result;
        result = left.getCategory().compareTo(right.getCategory());
        if (result == 0){
            if (left.isHeader() && !right.isHeader()){
                return -1;
            }
            if (!left.isHeader() && right.isHeader()){
                return 1;
            }
            if (!(left.isHeader() || right.isHeader())){
                result = left.getSale().compareTo(right.getSale());
            }
        }

        return result;
    }
}