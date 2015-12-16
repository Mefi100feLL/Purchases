package com.PopCorp.Purchases.comparators;

import com.PopCorp.Purchases.decorators.ShoppingListDecorator;

import java.util.Comparator;

public class ShoppingListDecoratorDateComparator implements Comparator<ShoppingListDecorator> {

    @Override
    public int compare(ShoppingListDecorator lhs, ShoppingListDecorator rhs) {
        if (lhs.getList().getId() < rhs.getList().getId()){
            return -1;
        }
        if (lhs.getList().getId() > rhs.getList().getId()){
            return 1;
        }
        return 0;
    }
}