package com.PopCorp.Purchases.callbacks;

import com.PopCorp.Purchases.model.Shop;

import java.util.ArrayList;

public interface DialogFavoriteShopsCallback {

    void onSelected(ArrayList<Shop> shops);
}
