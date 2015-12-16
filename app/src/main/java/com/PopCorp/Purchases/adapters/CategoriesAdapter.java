package com.PopCorp.Purchases.adapters;

import android.content.Context;
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
import com.PopCorp.Purchases.callbacks.CategoryFavoriteCallback;
import com.PopCorp.Purchases.model.Category;
import com.PopCorp.Purchases.utils.UIL;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> implements Filterable {

    public static final String FILTER_ALL = "";
    public static final String FILTER_FAVORITE = "favorite";

    private final CategoryFavoriteCallback callback;
    private final Context context;
    private ArrayList<Category> objects;
    private final SortedList<Category> publishItems;
    private String currentFilter = FILTER_ALL;

    public CategoriesAdapter(Context context, CategoryFavoriteCallback callback, ArrayList<Category> objects) {
        this.context = context;
        this.callback = callback;
        this.objects = objects;
        publishItems = new SortedList<>(Category.class, new SortedList.Callback<Category>() {
            @Override
            public boolean areContentsTheSame(Category oneItem, Category twoItem) {
                return oneItem.contentEquals(twoItem);
            }

            @Override
            public boolean areItemsTheSame(Category oneItem, Category twoItem) {
                return oneItem.equals(twoItem);
            }

            @Override
            public int compare(Category oneItem, Category twoItem) {
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
        for (Category category : objects) {
            publishItems.add(category);
        }
    }

    public SortedList<Category> getPublishItems() {
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
            image = (ImageView) view.findViewById(R.id.category_image);
            name = (TextView) view.findViewById(R.id.category_name);
            favorite = (ImageView) view.findViewById(R.id.category_favorite);
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
        Category category = publishItems.get(position);

        ImageLoader.getInstance().displayImage(category.getImageUrl(), holder.image, UIL.getImageOptions());

        holder.name.setText(category.getName());

        if (category.isFavorite()){
            holder.favorite.setImageResource(R.mipmap.ic_star_amber_24dp);
        } else{
            holder.favorite.setImageResource(R.mipmap.ic_star_outline_amber_24dp);
        }
        holder.favorite.setTag(category);
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(v.getTag() instanceof Category))
                    throw new RuntimeException("Category in tag instanceof error");
                Category shop = (Category) v.getTag();
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<Category> newItems = (ArrayList<Category>) results.values;
                update(newItems);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Category> FilteredArrayNames = new ArrayList<>();

                currentFilter = (String) constraint;
                if (constraint.equals(FILTER_ALL)) {
                    results.count = objects.size();
                    results.values = objects;
                    return results;
                } else {
                    for (Category category : objects){
                        if (category.isFavorite()){
                            FilteredArrayNames.add(category);
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

    private void update(ArrayList<Category> newItems) {
        ArrayList<Category> arrayForRemoving = new ArrayList<>();
        for (int i = 0; i < publishItems.size(); i++) {
            Category category = publishItems.get(i);
            if (!newItems.contains(category)) {
                arrayForRemoving.add(category);
            }
        }
        for (Category category : arrayForRemoving) {
            publishItems.remove(category);
        }

        for (Category category : newItems){
            if (publishItems.indexOf(category) == SortedList.INVALID_POSITION){
                publishItems.add(category);
            }
        }
        if (publishItems.size() == 0) {
            if (currentFilter.equals(FILTER_ALL)){
                callback.onEmpty(R.string.empty_no_categories_restart_app, R.drawable.ic_menu_gallery, R.string.button_restart_app, null);
            } else{
                callback.onEmpty(R.string.empty_no_favorite_categories, R.drawable.ic_menu_gallery, R.string.button_show_all_categories, null);
            }
        }
    }
}