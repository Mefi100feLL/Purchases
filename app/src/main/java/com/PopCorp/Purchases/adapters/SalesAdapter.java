package com.PopCorp.Purchases.adapters;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
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
import com.PopCorp.Purchases.decorators.SaleDecorator;
import com.PopCorp.Purchases.model.Sale;
import com.PopCorp.Purchases.utils.UIL;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> implements Filterable {

    protected ArrayList<Sale> objects;
    private Context context;
    private RecyclerCallback callback;
    protected SortedList<SaleDecorator> publishItems;
    private Comparator<SaleDecorator> comparator;

    public SalesAdapter(Context context, RecyclerCallback callback, ArrayList<Sale> objects, Comparator<SaleDecorator> saleComparator) {
        this.context = context;
        this.callback = callback;
        this.objects = objects;
        this.comparator = saleComparator;
        publishItems = new SortedList<>(SaleDecorator.class, new SortedList.Callback<SaleDecorator>() {
            @Override
            public boolean areContentsTheSame(SaleDecorator oneItem, SaleDecorator twoItem) {
                return oneItem.equals(twoItem);
            }

            @Override
            public boolean areItemsTheSame(SaleDecorator oneItem, SaleDecorator twoItem) {
                return oneItem.equals(twoItem);
            }

            @Override
            public int compare(SaleDecorator oneItem, SaleDecorator twoItem) {
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
        update(objects);
    }

    public void setLayoutManager(GridLayoutManager layoutManager, final int countColumns) {
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (publishItems.get(position).isHeader()) {
                    return countColumns;
                }
                return 1;
            }
        });
    }

    public SortedList<SaleDecorator> getPublishItems() {
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
            image = (ImageView) view.findViewById(R.id.sale_image);
            name = (TextView) view.findViewById(R.id.header_text);
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
        SaleDecorator decorator = publishItems.get(position);

        if (decorator.isHeader()) {
            holder.name.setText(decorator.getName());
            holder.setClickListener(new ViewHolder.ClickListener() {
                @Override
                public void onClick(View v, int position) {

                }
            });
        } else {
            Sale sale = decorator.getSale();
            ImageLoader.getInstance().displayImage(sale.getImageUrl(), holder.image, UIL.getImageOptions());

            holder.setClickListener(new ViewHolder.ClickListener() {
                @Override
                public void onClick(View v, int position) {
                    callback.onItemClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (publishItems.get(position).isHeader()) {
            return 1;
        }
        return 2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v;
        if (position == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale, parent, false);
        }

        return new ViewHolder(v);
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<Sale> newItems = (ArrayList<Sale>) results.values;
                update(newItems);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Sale> FilteredArrayNames = getFilterResults(constraint);

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                return results;
            }
        };

        return filter;
    }

    abstract ArrayList<Sale> getFilterResults(CharSequence constraint);

    abstract void update(ArrayList<Sale> sales);

    public ArrayList<Sale> getSales() {
        ArrayList<Sale> result = new ArrayList<>();
        for (int i = 0; i < publishItems.size(); i++) {
            SaleDecorator decorator = publishItems.get(i);
            if (!decorator.isHeader()) {
                result.add(decorator.getSale());
            }
        }
        return result;
    }

    public abstract int indexOf(Sale sale);
}