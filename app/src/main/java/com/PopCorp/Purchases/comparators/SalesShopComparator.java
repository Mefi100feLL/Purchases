package com.PopCorp.Purchases.comparators;

import com.PopCorp.Purchases.decorators.SaleDecorator;
import com.PopCorp.Purchases.decorators.SaleShopDecorator;

import java.util.Comparator;

public class SalesShopComparator implements Comparator<SaleDecorator> {

    @Override
    public int compare(SaleDecorator lhs, SaleDecorator rhs) {
        SaleShopDecorator left = (SaleShopDecorator) lhs;
        SaleShopDecorator right = (SaleShopDecorator) rhs;

        int result;
        result = left.getShop().compareTo(right.getShop());
        if (result == 0){
            if (left.isHeader() && !right.isHeader()){
                return -1;
            }
            if (!left.isHeader() && right.isHeader()){
                return 1;
            }
            if (!(left.isHeader() || right.isHeader())){
                left.getSale().compareTo(right.getSale());
            }
        }

        return result;
    }
}