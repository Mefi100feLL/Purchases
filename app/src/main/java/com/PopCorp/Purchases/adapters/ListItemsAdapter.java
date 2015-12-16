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
import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.comparators.ListItemComparator;
import com.PopCorp.Purchases.model.ListItem;
import com.PopCorp.Purchases.model.ShopSale;
import com.PopCorp.Purchases.utils.UIL;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ListItemsAdapter extends RecyclerView.Adapter<ListItemsAdapter.ViewHolder> implements Filterable {

    public static final String FILTER_ALL = "";

    private String currency;

    private final RecyclerCallback callback;
    private final Context context;
    private ListItemComparator comparator;
    private ArrayList<ListItem> objects;
    private final SortedList<ListItem> publishItems;

    public ListItemsAdapter(Context context, RecyclerCallback callback, ArrayList<ListItem> objects, String currency) {
        this.context = context;
        comparator = new ListItemComparator(context);
        this.callback = callback;
        this.objects = objects;
        this.currency = currency;
        publishItems = new SortedList<>(ListItem.class, new SortedList.Callback<ListItem>() {
            @Override
            public boolean areContentsTheSame(ListItem oneItem, ListItem twoItem) {
                return oneItem.contentEquals(twoItem);
            }

            @Override
            public boolean areItemsTheSame(ListItem oneItem, ListItem twoItem) {
                return oneItem.equals(twoItem);
            }

            @Override
            public int compare(ListItem oneItem, ListItem twoItem) {
                return comparator.compare(oneItem, twoItem);
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
        for (ListItem listItem : objects) {
            publishItems.add(listItem);
        }
    }

    public SortedList<ListItem> getPublishItems() {
        return publishItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View view;
        public final ImageView image;
        public final TextView name;
        public final ImageView favorite;
        public final TextView count;
        public final TextView totalCoastOne;
        public final TextView comment;
        public final TextView totalCoastTwo;
        private ClickListener clickListener;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            image = (ImageView) view.findViewById(R.id.listitem_image);
            name = (TextView) view.findViewById(R.id.listitem_name);
            favorite = (ImageView) view.findViewById(R.id.listitem_favorite);
            count = (TextView) view.findViewById(R.id.listitem_count);
            totalCoastOne = (TextView) view.findViewById(R.id.listitem_total_coast_one);
            comment = (TextView) view.findViewById(R.id.listitem_comment);
            totalCoastTwo = (TextView) view.findViewById(R.id.listitem_total_coast_two);
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
        ListItem listItem = publishItems.get(position);

        if (listItem.getSale() != null) {
            holder.image.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(listItem.getSale().getImageUrl(), holder.image, UIL.getImageOptions());
            holder.image.setTag(listItem.getSale());
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShopSale sale = (ShopSale) v.getTag();
                    openSale(sale);
                }
            });
        } else {
            holder.image.setVisibility(View.GONE);
        }

        holder.name.setText(listItem.getName());
        if (listItem.isImportant()) {
            holder.favorite.setVisibility(View.VISIBLE);
        } else {
            holder.favorite.setVisibility(View.GONE);
        }

        String shop = "";
        if (!listItem.getShop().isEmpty()) {
            shop = context.getString(R.string.listitem_shop).replace("shop", listItem.getShop());
        }
        if (listItem.getComment().isEmpty()) {
            if (!listItem.getShop().isEmpty()) {
                holder.comment.setVisibility(View.VISIBLE);
                holder.comment.setText(shop);
                holder.totalCoastOne.setVisibility(View.GONE);
            } else {
                holder.totalCoastTwo.setVisibility(View.GONE);
                holder.comment.setVisibility(View.GONE);
            }
            shop = "";
        } else {
            holder.comment.setVisibility(View.VISIBLE);
            holder.totalCoastOne.setVisibility(View.GONE);
            holder.comment.setText(listItem.getComment());
        }

        String count = context.getString(R.string.listitem_count);
        count = count.replace("count", listItem.getCount().toString());
        count = count.replace("edizm", listItem.getEdizm());
        count = count.replace("coast", listItem.getCoast().toString());
        count = count.replace("currency", currency);
        count = count.replace("shop", shop);
        holder.count.setText(count);

        holder.setClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                callback.onItemClick(v, position);
            }
        });
    }

    private void openSale(ShopSale sale) {

    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listitem, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<ListItem> newItems = (ArrayList<ListItem>) results.values;
                update(newItems);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<ListItem> FilteredArrayNames = new ArrayList<>();

                if (constraint.equals(FILTER_ALL)) {
                    results.count = objects.size();
                    results.values = objects;
                    return results;
                } else {
                    for (ListItem listItem : objects) {
                        if (listItem.getShop().equals(constraint)) {
                            FilteredArrayNames.add(listItem);
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

    private void update(ArrayList<ListItem> newItems) {
        ArrayList<ListItem> arrayForRemoving = new ArrayList<>();
        for (int i = 0; i < publishItems.size(); i++) {
            ListItem listItem = publishItems.get(i);
            if (!newItems.contains(listItem)) {
                arrayForRemoving.add(listItem);
            }
        }
        for (ListItem listItem : arrayForRemoving) {
            publishItems.remove(listItem);
        }

        for (ListItem listItem : newItems) {
            int index = publishItems.indexOf(listItem);
            if (index == SortedList.INVALID_POSITION) {
                publishItems.add(listItem);
            } else {
                publishItems.updateItemAt(index, listItem);
            }
        }
    }
}