package com.PopCorp.Purchases.adapters;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.callbacks.ShoppingListCallback;
import com.PopCorp.Purchases.decorators.ShoppingListDecorator;
import com.PopCorp.Purchases.model.ShoppingList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ShoppingList> objects;
    private final SortedList<ShoppingListDecorator> publishItems;

    private final RecyclerCallback callback;
    private ShoppingListCallback listCallback;

    private Comparator<ShoppingListDecorator> comparator;

    public ShoppingListsAdapter(Context context, RecyclerCallback callback, ShoppingListCallback listCallback, ArrayList<ShoppingList> objects, Comparator<ShoppingListDecorator> decoratorComparator) {
        this.context = context;
        this.callback = callback;
        this.listCallback = listCallback;
        this.objects = objects;
        this.comparator = decoratorComparator;
        publishItems = new SortedList<>(ShoppingListDecorator.class, new SortedList.Callback<ShoppingListDecorator>() {
            @Override
            public boolean areContentsTheSame(ShoppingListDecorator oneItem, ShoppingListDecorator twoItem) {
                return oneItem.getList().contentEquals(twoItem.getList());
            }

            @Override
            public boolean areItemsTheSame(ShoppingListDecorator oneItem, ShoppingListDecorator twoItem) {
                return oneItem.equals(twoItem);
            }

            @Override
            public int compare(ShoppingListDecorator oneItem, ShoppingListDecorator twoItem) {
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
        update();
    }

    public SortedList<ShoppingListDecorator> getPublishItems() {
        return publishItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View view;
        public final TextView name;
        public final TextView items;
        public final ImageView overflow;
        private ClickListener clickListener;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.list_name);
            items = (TextView) view.findViewById(R.id.list_items);
            overflow = (ImageView) view.findViewById(R.id.list_overflow);
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
        ShoppingListDecorator decorator = publishItems.get(position);

        holder.name.setText(decorator.getList().getName());
        holder.items.setText(decorator.getItems());

        holder.overflow.setTag(decorator);
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(v.getTag() instanceof ShoppingListDecorator))
                    throw new RuntimeException("Shop in tag instanceof error");
                ShoppingListDecorator decorator = (ShoppingListDecorator) v.getTag();
                showPopupMenu(v, decorator.getList());
            }
        });

        holder.setClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                callback.onItemClick(v, position);
            }
        });
    }

    public void showPopupMenu(View view, final ShoppingList list) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.popup_for_list);
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            popupMenu.show();
            return;
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_change_list: {
                        listCallback.onChange(list);
                        return true;
                    }
                    case R.id.action_remove_list: {
                        listCallback.onRemove(list);
                        return true;
                    }
                    case R.id.action_send_list: {
                        listCallback.onShare(list);
                        return true;
                    }
                    case R.id.action_put_alarm: {
                        listCallback.onSetAlarm(list);
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_list, parent, false);
        return new ViewHolder(v);
    }

    public void update() {
        ArrayList<ShoppingListDecorator> arrayForRemoving = new ArrayList<>();
        for (int i = 0; i < publishItems.size(); i++) {
            ShoppingListDecorator decorator = publishItems.get(i);
            if (!objects.contains(decorator.getList())) {
                arrayForRemoving.add(decorator);
            }
        }
        for (ShoppingListDecorator decorator : arrayForRemoving) {
            publishItems.remove(decorator);
        }

        for (ShoppingList list : objects) {
            ShoppingListDecorator decorator = new ShoppingListDecorator(list);
            if (publishItems.indexOf(decorator) == SortedList.INVALID_POSITION) {
                publishItems.add(decorator);
            }
        }
    }
}