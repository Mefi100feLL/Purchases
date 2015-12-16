package com.PopCorp.Purchases.callbacks;

import com.PopCorp.Purchases.model.Shop;

public interface ShopFavoriteCallback extends RecyclerCallback {

    void onFavoriteChanged(Shop shop);
}
