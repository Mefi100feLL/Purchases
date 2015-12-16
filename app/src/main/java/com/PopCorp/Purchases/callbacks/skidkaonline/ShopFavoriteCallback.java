package com.PopCorp.Purchases.callbacks.skidkaonline;

import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.model.skidkaonline.Shop;

public interface ShopFavoriteCallback extends RecyclerCallback {

    void onFavoriteChanged(Shop shop);
}
