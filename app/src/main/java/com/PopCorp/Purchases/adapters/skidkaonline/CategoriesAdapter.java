package com.PopCorp.Purchases.adapters.skidkaonline;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.model.skidkaonline.Category;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>{

    private final RecyclerCallback callback;
    private ArrayList<Category> objects;
    private final SortedList<Category> publishItems;
    private int[] icons;
    private String[] urls;

    public CategoriesAdapter(Context context, RecyclerCallback callback, ArrayList<Category> objects) {
        this.callback = callback;
        this.objects = objects;
        loadIcons(context);
        urls = context.getResources().getStringArray(R.array.categories_urls);
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

    private void loadIcons(Context context) {
        final TypedArray ta = context.getResources().obtainTypedArray(R.array.categories_icons);
        icons = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            icons[i] = ta.getResourceId(i, R.mipmap.ic_help_circle_grey600_24dp);
        }
        ta.recycle();
    }

    public SortedList<Category> getPublishItems() {
        return publishItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View view;
        public final ImageView image;
        public final TextView name;
        private ClickListener clickListener;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            image = (ImageView) view.findViewById(R.id.category_skidkaonline_image);
            name = (TextView) view.findViewById(R.id.category_skidkaonline_name);
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

        int icon = R.mipmap.ic_help_circle_grey600_24dp;
        for (int i = 0; i < urls.length; i++){
            if ((category.getUrl().contains(urls[i]))){
                icon = icons[i];
            }
        }
        holder.image.setImageResource(icon);
        holder.name.setText(category.getName());

        holder.setClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                callback.onItemClick(v, position);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_skidkaonline, parent, false);
        return new ViewHolder(v);
    }

    public void update(){
        ArrayList<Category> arrayForRemove = new ArrayList<>();
        for (int i=0; i< publishItems.size(); i++){
            Category category = publishItems.get(i);
            if (!objects.contains(category)){
                arrayForRemove.add(category);
            }
        }
        for (Category category : arrayForRemove){
            publishItems.remove(category);
        }
        for (Category category : objects){
            int index = publishItems.indexOf(category);
            if (index == SortedList.INVALID_POSITION){
                publishItems.add(category);
            } else{
                publishItems.updateItemAt(index, category);
            }
        }
    }
}
