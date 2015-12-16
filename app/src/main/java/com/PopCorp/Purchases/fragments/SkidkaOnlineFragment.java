package com.PopCorp.Purchases.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.activities.MainActivity;
import com.PopCorp.Purchases.activities.skidkaonline.CityActivity;
import com.PopCorp.Purchases.activities.skidkaonline.ShopesActivity;
import com.PopCorp.Purchases.adapters.skidkaonline.CategoriesAdapter;
import com.PopCorp.Purchases.callbacks.RecyclerCallback;
import com.PopCorp.Purchases.loaders.skidkaonline.CategoriesInternetLoader;
import com.PopCorp.Purchases.loaders.skidkaonline.CategoriesLoader;
import com.PopCorp.Purchases.model.skidkaonline.Category;
import com.PopCorp.Purchases.services.OfflineSpiceService;
import com.PopCorp.Purchases.services.SkidkaOnlineSpiceService;
import com.PopCorp.Purchases.utils.PreferencesManager;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class SkidkaOnlineFragment extends Fragment implements RecyclerCallback {

    private final SpiceManager dbSpiceManager = new SpiceManager(OfflineSpiceService.class);
    private final SpiceManager spiceManager = new SpiceManager(SkidkaOnlineSpiceService.class);

    private Toolbar toolBar;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView emptyText;
    private ImageView emptyImage;
    private Button emptyButton;
    private LinearLayout emptyLayout;

    private ArrayList<Category> objects = new ArrayList<>();
    private CategoriesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_skidkaonline, container, false);
        setHasOptionsMenu(true);

        toolBar = (Toolbar) getActivity().findViewById(R.id.activity_main_toolbar);
        progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_skidkaonline_progress);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_skidkaonline_recycler);
        swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_skidkaonline_refresh);
        emptyText = (TextView) rootView.findViewById(R.id.empty_text);
        emptyImage = (ImageView) rootView.findViewById(R.id.empty_image);
        emptyButton = (Button) rootView.findViewById(R.id.empty_button);
        emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty_layout);

        swipeRefresh.setColorSchemeResources(R.color.swipe_refresh_color_one, R.color.swipe_refresh_color_two, R.color.swipe_refresh_color_three);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing(true);
                loadFromNetwork();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        adapter = new CategoriesAdapter(getActivity(), this, objects);
        recyclerView.setAdapter(adapter);

        if (PreferencesManager.getInstance().getCity().isEmpty()) {
            showEmpty(R.string.empty_select_city, R.drawable.ic_menu_gallery, R.string.button_select_city, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getActivity(), CityActivity.class), MainActivity.CITY_REQUEST_CODE);
                }
            });
        } else {
            loadFromDB();
        }
        return rootView;
    }

    private void refreshing(boolean refresh) {
        swipeRefresh.setRefreshing(refresh);
        swipeRefresh.setEnabled(!refresh);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolBar.setTitle(R.string.navigation_drawer_skidkaonline);
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
                    showRecyclerView();
                    refreshing(true);
                }
                adapter.update();
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
                } else {
                    showEmpty(R.string.empty_no_categories_try_update, R.drawable.ic_menu_gallery, R.string.button_update, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            update();
                        }
                    });
                }
                adapter.update();
            }
        });
    }

    private void update() {
        showProgress();
        loadFromNetwork();
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

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(View v, int position) {
        Category category = adapter.getPublishItems().get(position);
        Intent intent = new Intent(getActivity(), ShopesActivity.class);
        intent.putExtra(ShopesActivity.CURRENT_CATEGORY, category);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    @Override
    public void onEmpty(int stringRes, int drawableRes, int buttonRes, View.OnClickListener listener) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.CITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                update();
            }
        }
    }
}
