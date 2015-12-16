package com.PopCorp.Purchases.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.activities.ShoppingListActivity;
import com.PopCorp.Purchases.adapters.ShoppingListsAdapter;
import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.callbacks.ShoppingListCallback;
import com.PopCorp.Purchases.loaders.skidkaonline.ShoppingListsLoader;
import com.PopCorp.Purchases.model.ShoppingList;
import com.PopCorp.Purchases.services.OfflineSpiceService;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class ShoppingListsFragment  extends Fragment implements RecyclerCallback, ShoppingListCallback {

    private final SpiceManager dbSpiceManager = new SpiceManager(OfflineSpiceService.class);

    private Toolbar toolBar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private ImageView emptyImage;
    private Button emptyButton;
    private LinearLayout emptyLayout;
    private ActionButton fab;

    private ShoppingListsAdapter adapter;
    private ArrayList<ShoppingList> objects = new ArrayList<>();

    private String[] arraySizesTable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping_lists, container, false);
        setHasOptionsMenu(true);

        toolBar = (Toolbar) getActivity().findViewById(R.id.activity_main_toolbar);
        progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_lists_progress);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_lists_recycler);
        emptyText = (TextView) rootView.findViewById(R.id.empty_text);
        emptyImage = (ImageView) rootView.findViewById(R.id.empty_image);
        emptyButton = (Button) rootView.findViewById(R.id.empty_button);
        emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty_layout);
        fab = (ActionButton) rootView.findViewById(R.id.fragment_lists_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForNewList();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), PreferencesManager.getInstance().getListTableSize());

        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        adapter = new ShoppingListsAdapter(getActivity(), this, this, objects, PreferencesManager.getInstance().getListComparator());
        recyclerView.setAdapter(adapter);

        loadFromDB();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        toolBar.setTitle(R.string.navigation_drawer_lists);
        fab.postDelayed(new Runnable() {
            @Override
            public void run() {
                showFab();
            }
        }, 300);
    }

    @Override
    public void onStart() {
        super.onStart();
        dbSpiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        fab.setVisibility(View.INVISIBLE);
        if (dbSpiceManager.isStarted()) {
            dbSpiceManager.shouldStop();
        }
        super.onStop();
    }

    private void loadFromDB() {
        dbSpiceManager.execute(new ShoppingListsLoader(), new PendingRequestListener<ShoppingList[]>() {
            @Override
            public void onRequestNotFound() {

            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ShoppingList[] lists) {
                for (ShoppingList list : lists) {
                    if (!objects.contains(list)) {
                        objects.add(list);
                    }
                }
                if (objects.size() > 0) {
                    showRecyclerView();
                } else {
                    showDefaultEmpty();
                }
                adapter.update();
            }
        });
    }

    private void showRecyclerView() {
        progressBar.setVisibility(View.INVISIBLE);
        emptyLayout.setVisibility(View.INVISIBLE);
    }

    private void showEmpty(int stringRes, int drawableRes, int buttonRes, View.OnClickListener listener) {
        progressBar.setVisibility(View.INVISIBLE);
        emptyLayout.setVisibility(View.VISIBLE);
        emptyText.setText(stringRes);
        emptyImage.setImageResource(drawableRes);
        emptyButton.setText(buttonRes);
        emptyButton.setOnClickListener(listener);
    }

    @Override
    public void onItemClick(View v, int position) {
        ShoppingList list = adapter.getPublishItems().get(position).getList();
        openList(list);
    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    @Override
    public void onEmpty(int stringRes, int drawableRes, int buttonRes, View.OnClickListener listener) {
        showEmpty(stringRes, drawableRes, buttonRes, listener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.shops, menu);
        super.onCreateOptionsMenu(menu, inflater);

        int groupId = 12;
        MenuItem item = menu.findItem(R.id.action_size_table);
        item.getSubMenu().clear();
        arraySizesTable = getResources().getStringArray(R.array.sizes_table_lists);
        for (String filterItem : arraySizesTable) {
            MenuItem addedItem = item.getSubMenu().add(groupId, filterItem.hashCode(), Menu.NONE, filterItem);
            if (filterItem.equals(String.valueOf(PreferencesManager.getInstance().getListTableSize()))) {
                addedItem.setChecked(true);
            }
        }
        item.getSubMenu().setGroupCheckable(groupId, true, true);
        item.getSubMenu().setGroupEnabled(groupId, true);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for (String filterItem : arraySizesTable) {
            if (item.getItemId() == filterItem.hashCode()) {
                PreferencesManager.getInstance().putListTableSize(Integer.parseInt(filterItem));
                item.setChecked(true);
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), Integer.parseInt(filterItem));
                recyclerView.setLayoutManager(layoutManager);
            }
        }
        return true;
    }

    private void showFab() {
        if (fab.getAnimation() != null) {
            if (!fab.getAnimation().hasEnded()) {
                return;
            }
        }
        fab.setShowAnimation(ActionButton.Animations.SCALE_UP);
        fab.show();
    }

    public void hideFab(Animation.AnimationListener listener) {
        if (fab.isHidden()) {
            listener.onAnimationEnd(fab.getHideAnimation());
        } else {
            if (fab.getAnimation() != null) {
                if (!fab.getAnimation().hasEnded()) {
                    return;
                }
            }
            fab.setHideAnimation(ActionButton.Animations.SCALE_DOWN);
            fab.getHideAnimation().setAnimationListener(listener);
            fab.hide();
        }
    }

    private void showDialogForNewList() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_new_list, null);

        final EditText textName = (EditText) layout.findViewById(R.id.dialog_new_list_edittext_name);
        final TextInputLayout textLayout = (TextInputLayout) layout.findViewById(R.id.dialog_new_list_input_layout);
        final Spinner spinner = (Spinner) layout.findViewById(R.id.dialog_new_list_spinner_currency);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.customView(layout, false);
        builder.title(R.string.dialog_title_new_list);
        builder.autoDismiss(false);
        builder.positiveText(R.string.dialog_button_create);
        builder.negativeText(R.string.dialog_button_cancel);
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                if (textName.getText().length() == 0) {
                    textLayout.setError(getString(R.string.error_input_name_of_list));
                    textLayout.setErrorEnabled(true);
                    return;
                }
                createNewList(textName.getText().toString(), (String) spinner.getSelectedItem());
                dialog.dismiss();
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                dialog.cancel();
            }
        });
        final MaterialDialog dialog = builder.build();


        Set<String> currencys = PreferencesManager.getInstance().getCurrencies();
        ArrayAdapter<String> adapterForSpinnerCurrency = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, currencys.toArray(new String[currencys.size()]));
        adapterForSpinnerCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterForSpinnerCurrency);
        spinner.setSelection(adapterForSpinnerCurrency.getPosition(PreferencesManager.getInstance().getCurrentCurrency()));

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        textName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(android.widget.TextView v, int actionId, KeyEvent event) {
                if (textName.getText().length() == 0) {
                    textLayout.setError(getString(R.string.error_input_name_of_list));
                    textLayout.setErrorEnabled(true);
                    return false;
                }
                createNewList(textName.getText().toString(), (String) spinner.getSelectedItem());
                dialog.dismiss();
                return true;
            }
        });
    }

    private void createNewList(String name, String currency) {
        ShoppingList newList = new ShoppingList(0, name, Calendar.getInstance().getTimeInMillis(), "", currency);
        newList.updateOrAddToDB();
        objects.add(newList);
        adapter.update();
        openList(newList);
    }

    private void openList(ShoppingList newList) {
        Intent intent = new Intent(getActivity(), ShoppingListActivity.class);
        intent.putExtra(ShoppingListActivity.CURRENT_LIST_ID, newList.getId());
        startActivity(intent);
    }

    @Override
    public void onChange(ShoppingList list) {

    }

    @Override
    public void onRemove(final ShoppingList list) {
        objects.remove(list);
        adapter.update();
        if (objects.size() == 0){
            showDefaultEmpty();
        }
        Snackbar snackbar = Snackbar.make(fab, getString(R.string.list) + " \"" + list.getName() + "\" " + getString(R.string.removed), Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        objects.add(list);
                        showRecyclerView();
                        adapter.update();
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        list.removeFromDB();
                    }
                });
        snackbar.show();
    }

    @Override
    public void onShare(ShoppingList list) {

    }

    @Override
    public void onSetAlarm(ShoppingList list) {

    }

    private void showDefaultEmpty(){
        showEmpty(R.string.empty_no_shopping_lists, R.drawable.ic_menu_gallery, R.string.button_add_list, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForNewList();
            }
        });
    }
}