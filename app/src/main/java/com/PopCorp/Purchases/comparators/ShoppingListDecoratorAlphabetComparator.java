package com.PopCorp.Purchases.comparators;

import com.PopCorp.Purchases.decorators.ShoppingListDecorator;

import java.util.Comparator;

public class ShoppingListDecoratorAlphabetComparator implements Comparator<ShoppingListDecorator> {

    @Override
    public int compare(ShoppingListDecorator lhs, ShoppingListDecorator rhs) {
        return lhs.getList().getName().compareToIgnoreCase(rhs.getList().getName());
    }
}