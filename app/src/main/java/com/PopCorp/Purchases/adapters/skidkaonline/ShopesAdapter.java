package com.PopCorp.Purchases.adapters.skidkaonline;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.callbacks.skidkaonline.ShopFavoriteCallback;
import com.PopCorp.Purchases.model.skidkaonline.Shop;
import com.PopCorp.Purchases.utils.UIL;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ShopesAdapter extends RecyclerView.Adapter<ShopesAdapter.ViewHolder> implements Filterable {

    public static final String FILTER_ALL = "";
    public static final String FILTER_FAVORITE = "favorite";

    private final ShopFavoriteCallback callback;
    private ArrayList<Shop> objects;
    private final SortedList<Shop> publishItems;

    private String currentFilter = FILTER_ALL;

    public ShopesAdapter(ShopFavoriteCallback callback, ArrayList<Shop> objects) {
        this.callback = callback;
        this.objects = objects;
        publishItems = new SortedList<>(Shop.class, new SortedList.Callback<Shop>() {
            @Override
            public boolean areContentsTheSame(Shop oneItem, Shop twoItem) {
                return oneItem.contentEquals(twoItem);
            }

            @Override
            public boolean areItemsTheSame(Shop oneItem, Shop twoItem) {
                return oneItem.equals(twoItem);
            }

            @Override
            public int compare(Shop oneItem, Shop twoItem) {
                return oneItem.compareTo(twoItem);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }
        });
        for (Shop shop : objects) {
            publishItems.add(shop);
        }
    }

    public SortedList<Shop> getPublishItems() {
        return publishItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View view;
        public final ImageView image;
        public final TextView name;
        public final ImageView favorite;
        private ClickListener clickListener;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            image = (ImageView) view.findViewById(R.id.shop_image);
            name = (TextView) view.findViewById(R.id.shop_name);
            favorite = (ImageView) view.findViewById(R.id.shop_favorite);
            view.setOnClickListener(this);
        }

        public interface ClickListener {
            void onClick(View v, int position);
        }

        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return publishItems.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Shop shop = publishItems.get(position);

        ImageLoader.getInstance().displayImage(shop.getImage(), holder.image, UIL.getImageOptions());

        holder.name.setText(shop.getName());

        if (shop.isFavorite()) {
            holder.favorite.setImageResource(R.mipmap.ic_star_amber_24dp);
        } else {
            holder.favorite.setImageResource(R.mipmap.ic_star_outline_amber_24dp);
        }
        holder.favorite.setTag(shop);
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(v.getTag() instanceof Shop))
                    throw new RuntimeException("Shop in tag instanceof error");
                Shop shop = (Shop) v.getTag();
                shop.setFavorite(!shop.isFavorite());
                notifyItemChanged(publishItems.indexOf(shop));
                shop.updateOrAddToDB();
                callback.onFavoriteChanged(shop);
            }
        });

        holder.setClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                callback.onItemClick(v, position);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_skidkaonline, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<Shop> newItems = (ArrayList<Shop>) results.values;
                update(newItems);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Shop> FilteredArrayNames = new ArrayList<>();

                currentFilter = (String) constraint;
                if (constraint.equals(FILTER_ALL)) {
                    results.count = objects.size();
                    results.values = objects;
                    return results;
                } else {
                    for (Shop shop : objects) {
                        if (shop.isFavorite()) {
                            FilteredArrayNames.add(shop);
                        }
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                return results;
            }
        };

        return filter;
    }

    private void update(ArrayList<Shop> newItems) {
        ArrayList<Shop> arrayForRemoving = new ArrayList<>();
        for (int i = 0; i < publishItems.size(); i++) {
            Shop shop = publishItems.get(i);
            if (!newItems.contains(shop)) {
                arrayForRemoving.add(shop);
            }
        }
        for (Shop shop : arrayForRemoving) {
            publishItems.remove(shop);
        }

        for (Shop shop : newItems) {
            if (publishItems.indexOf(shop) == SortedList.INVALID_POSITION) {
                publishItems.add(shop);
            }
        }
        if (publishItems.size() == 0) {
            if (currentFilter.equals(FILTER_ALL)) {
                callback.onEmpty(R.string.empty_no_shops_restart_app, R.drawable.ic_menu_gallery, R.string.button_restart_app, null);
            } else {
                callback.onEmpty(R.string.empty_no_favorite_shops, R.drawable.ic_menu_gallery, R.string.button_show_all_shops, null);
            }
        }
    }
}