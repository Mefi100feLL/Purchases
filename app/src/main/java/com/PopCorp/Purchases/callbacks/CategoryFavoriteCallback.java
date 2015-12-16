package com.PopCorp.Purchases.callbacks;

import com.PopCorp.Purchases.model.Category;

public interface CategoryFavoriteCallback extends RecyclerCallback {

    void onFavoriteChanged(Category category);
}
