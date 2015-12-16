package com.PopCorp.Purchases.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.activities.SalesActivity;
import com.PopCorp.Purchases.adapters.CategoriesAdapter;
import com.PopCorp.Purchases.adapters.SpinnerAdapter;
import com.PopCorp.Purchases.callbacks.CategoryFavoriteCallback;
import com.PopCorp.Purchases.callbacks.DialogFavoriteCategoriesCallback;
import com.PopCorp.Purchases.callbacks.DialogFavoriteShopsCallback;
import com.PopCorp.Purchases.callbacks.DialogRegionsCallback;
import com.PopCorp.Purchases.controllers.DialogController;
import com.PopCorp.Purchases.loaders.CategoriesInternetLoader;
import com.PopCorp.Purchases.loaders.CategoriesLoader;
import com.PopCorp.Purchases.loaders.ShopesInternetLoader;
import com.PopCorp.Purchases.loaders.ShopesLoader;
import com.PopCorp.Purchases.model.Category;
import com.PopCorp.Purchases.model.Shop;
import com.PopCorp.Purchases.services.LoadingSalesService;
import com.PopCorp.Purchases.services.MestoskidkiSpiceService;
import com.PopCorp.Purchases.services.OfflineSpiceService;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.afollestad.materialdialogs.MaterialDialog;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class CategoriesFragment extends Fragment implements CategoryFavoriteCallback {

    private final SpiceManager dbSpiceManager = new SpiceManager(OfflineSpiceService.class);
    private final SpiceManager spiceManager = new SpiceManager(MestoskidkiSpiceService.class);

    private Toolbar toolBar;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView emptyText;
    private ImageView emptyImage;
    private Button emptyButton;
    private LinearLayout emptyLayout;
    private ActionButton fab;

    private CategoriesAdapter adapter;
    private ArrayList<Category> objects = new ArrayList<>();

    private String currentFilter = CategoriesAdapter.FILTER_ALL;
    private String[] arraySizesTable;
    private MaterialDialog shopsDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        setHasOptionsMenu(true);

        toolBar = (Toolbar) getActivity().findViewById(R.id.activity_main_toolbar);
        progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_categories_progress);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_categories_recycler);
        swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_categories_refresh);
        emptyText = (TextView) rootView.findViewById(R.id.empty_text);
        emptyImage = (ImageView) rootView.findViewById(R.id.empty_image);
        emptyButton = (Button) rootView.findViewById(R.id.empty_button);
        emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty_layout);

        fab = (ActionButton) rootView.findViewById(R.id.fragment_categories_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndLoadFavoriteCategs()) {
                    loadShopsFromDB();
                }
            }
        });

        spinner = (Spinner) getActivity().findViewById(R.id.activity_main_toolbar_spinner);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), new String[]{getString(R.string.spinner_all_categories), getString(R.string.spinner_favorite_categories)});
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (objects.size() > 0) {
                    if (position == 0) {
                        currentFilter = CategoriesAdapter.FILTER_ALL;
                    } else {
                        currentFilter = CategoriesAdapter.FILTER_FAVORITE;
                    }
                    adapter.getFilter().filter(currentFilter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swipeRefresh.setColorSchemeResources(R.color.swipe_refresh_color_one, R.color.swipe_refresh_color_two, R.color.swipe_refresh_color_three);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!PreferencesManager.getInstance().getRegionId().isEmpty()) {
                    refreshing(true);
                    loadFromNetwork();
                } else{
                    refreshing(false);
                }
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), PreferencesManager.getInstance().getCategoryTableSize());

        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        adapter = new CategoriesAdapter(getActivity(), this, objects);
        recyclerView.setAdapter(adapter);

        if (PreferencesManager.getInstance().getRegionId().isEmpty()) {
            showEmpty(R.string.empty_categories_select_region, R.drawable.ic_menu_gallery, R.string.button_select_region, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogController.showDialogWithRegions(getActivity(), new DialogRegionsCallback() {
                        @Override
                        public void onSelected() {
                            showProgress();
                            loadFromNetwork();
                        }
                    });
                }
            });
        } else {
            loadFromDB();
        }
        return rootView;
    }

    private boolean checkAndLoadFavoriteCategs() {
        boolean categoriesIsFavorite = false;
        for (Category category : objects) {
            if (category.isFavorite()) {
                categoriesIsFavorite = true;
                break;
            }
        }
        if (!categoriesIsFavorite) {
            if (objects.size() == 0) {
                loadFromNetwork();
            } else {
                DialogController.showDialogForFavoriteCategories(getActivity(), objects, new DialogFavoriteCategoriesCallback() {
                    @Override
                    public void onSelected(ArrayList<Category> categories) {
                        loadShopsFromDB();
                    }
                });
            }
            return false;
        }
        return true;
    }

    private void loadShopsFromDB() {
        shopsDialog = new MaterialDialog.Builder(getActivity())
                .content(R.string.dialog_content_loading_shops)
                .progress(true, 0)
                .build();
        shopsDialog.show();
        dbSpiceManager.execute(new ShopesLoader(), new PendingRequestListener<Shop[]>() {
            @Override
            public void onRequestNotFound() {

            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(Shop[] shops) {
                if (shops.length == 0) {
                    loadCategsFromNetwork();
                    return;
                }
                checkFavoriteShops(shops);
            }
        });
    }

    private void loadCategsFromNetwork() {
        spiceManager.execute(new ShopesInternetLoader(), new PendingRequestListener<Shop[]>() {
            @Override
            public void onRequestNotFound() {

            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(Shop[] shops) {
                for (Shop shop : shops) {
                    shop.updateOrAddToDB();
                }
                if (shops.length != 0) {
                    checkFavoriteShops(shops);
                } else {

                }
            }
        });
    }

    private void checkFavoriteShops(Shop[] shops) {
        ArrayList<Shop> shopes = new ArrayList<>();
        boolean shopsIsFavorite = false;
        for (Shop category : shops) {
            shopes.add(category);
            if (category.isFavorite()) {
                shopsIsFavorite = true;
            }
        }
        shopsDialog.dismiss();
        if (!shopsIsFavorite) {
            DialogController.showDialogForFavoriteShops(getActivity(), shopes, new DialogFavoriteShopsCallback() {
                @Override
                public void onSelected(ArrayList<Shop> shops) {
                    startLoadingSales();
                }
            });
            return;
        }
        startLoadingSales();
    }

    private void startLoadingSales() {
        getActivity().startService(new Intent(getActivity(), LoadingSalesService.class));
    }


    private void refreshing(boolean refresh) {
        swipeRefresh.setRefreshing(refresh);
        swipeRefresh.setEnabled(!refresh);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (objects.size() > 0) {
            toolBar.setTitle("");
        } else {
            toolBar.setTitle(R.string.navigation_drawer_categories);
        }
        if (!PreferencesManager.getInstance().getRegionId().isEmpty()) {
            fab.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFab();
                }
            }, 300);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
        dbSpiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        if (dbSpiceManager.isStarted()) {
            dbSpiceManager.shouldStop();
        }
        super.onStop();
    }

    private void loadFromDB() {
        dbSpiceManager.execute(new CategoriesLoader(), new PendingRequestListener<Category[]>() {
            @Override
            public void onRequestNotFound() {

            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(Category[] categories) {
                for (Category category : categories) {
                    if (!objects.contains(category)) {
                        objects.add(category);
                    }
                }
                if (objects.size() > 0) {
                    setFavoriteFilter();
                    showRecyclerView();
                    refreshing(true);
                }
                adapter.getFilter().filter(currentFilter);
                loadFromNetwork();
            }
        });
    }

    private void loadFromNetwork() {
        spiceManager.execute(new CategoriesInternetLoader(), new PendingRequestListener<Category[]>() {
            @Override
            public void onRequestNotFound() {
                refreshing(false);
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                refreshing(false);
            }

            @Override
            public void onRequestSuccess(Category[] categories) {
                refreshing(false);
                ArrayList<Category> tmp = new ArrayList<>(Arrays.asList(categories));
                ListIterator<Category> iterator = objects.listIterator();
                while (iterator.hasNext()) {
                    Category category = iterator.next();
                    if (!tmp.contains(category)) {
                        category.removeFromDB();
                        iterator.remove();
                    }
                }
                for (Category category : categories) {
                    if (!objects.contains(category)) {
                        objects.add(category);
                        category.updateOrAddToDB();
                    } else {
                        Category existed = objects.get(objects.indexOf(category));
                        if (!category.contentEquals(existed)) {
                            existed.update(category);
                            existed.updateOrAddToDB();
                        }
                    }
                }
                if (objects.size() > 0) {
                    showRecyclerView();
                    showFab();
                } else {
                    showEmpty(R.string.empty_no_categories_restart_app, R.drawable.ic_menu_gallery, R.string.button_restart_app, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            restartApp();
                        }
                    });
                }
                adapter.getFilter().filter(currentFilter);
            }
        });
    }

    private void restartApp() {

    }

    private void checkFilterToAll() {
        spinner.setSelection(0);
        showRecyclerView();
    }

    private void setFavoriteFilter() {
        if (objects.size() > 0) {
            toolBar.setTitle("");
            spinner.setVisibility(View.VISIBLE);
            for (Category category : objects) {
                if (category.isFavorite()) {
                    currentFilter = CategoriesAdapter.FILTER_FAVORITE;
                    spinner.setSelection(1);
                    return;
                }
            }
            spinner.setSelection(0);
        }
    }

    private void showRecyclerView() {
        spinner.setVisibility(View.VISIBLE);
        toolBar.setTitle("");
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

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(View v, int position) {
        Category category = adapter.getPublishItems().get(position);
        Intent intent = new Intent(getActivity(), SalesActivity.class);
        intent.putExtra(SalesActivity.CURRENT_CATEGORY, category);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    @Override
    public void onEmpty(int stringRes, int drawableRes, int buttonRes, View.OnClickListener listener) {
        if (listener == null) {
            if (currentFilter.equals(CategoriesAdapter.FILTER_ALL)) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restartApp();
                    }
                };
            } else {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkFilterToAll();
                    }
                };
            }
        }
        if (PreferencesManager.getInstance().getRegionId().isEmpty()) {
            showEmpty(R.string.empty_shops_select_region, R.drawable.ic_menu_gallery, R.string.button_select_region, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogController.showDialogWithRegions(getActivity(), new DialogRegionsCallback() {
                        @Override
                        public void onSelected() {
                            showProgress();
                            loadFromNetwork();
                        }
                    });
                }
            });
        } else {
            showEmpty(stringRes, drawableRes, buttonRes, listener);
        }
    }

    @Override
    public void onFavoriteChanged(Category category) {
        adapter.getFilter().filter(currentFilter);
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
            if (filterItem.equals(String.valueOf(PreferencesManager.getInstance().getCategoryTableSize()))) {
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
                PreferencesManager.getInstance().putCategoryTableSize(Integer.parseInt(filterItem));
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
}
