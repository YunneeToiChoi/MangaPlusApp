package com.example.mangaplusapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.utilities.CorePalette;

import java.util.ArrayList;
import java.util.List;

import Adapter.CategoryAdapter;
import Adapter.ImageSliderAdapter;
import Adapter.TruyenTranhAdapter;
import object.Category;
import object.TruyenTranh;
public class HomeFragment extends Fragment {
    View view;
    CategoryAdapter categoryAdapter;
    TruyenTranhAdapter truyenTranhAdapter;
    ImageSliderAdapter imageSliderAdapter;
    RecyclerView recyclerViewCategory;
    ViewPager2 viewPager2;
    Handler handler = new Handler();
    List<Category> categoryList = new ArrayList<Category>();
    List<TruyenTranh> truyenTranhList = new ArrayList<TruyenTranh>();
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar  = view.findViewById(R.id.main_header);
        ((MainActivity)requireActivity()).setSupportActionBar(toolbar);
        AddListCnT();
        SetContentImageSlider();
        SetContentRecycleView();
        setRecyclerViewAnimation();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private void setRecyclerViewAnimation(){
        final CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.imgslider_container_collapsing);
        AppBarLayout appBarLayout = view.findViewById(R.id.imgslider_container);
        Toolbar toolbar = view.findViewById(R.id.imgslider_toolbar);
        ((MainActivity) requireActivity()).setSupportActionBar(toolbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isToolbarExpanded = true;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) scrollRange;
                if (percentage < 0.5) {
                    if (!isToolbarExpanded) {
                        isToolbarExpanded = true;
                    }
                } else {
                    if (isToolbarExpanded) {
                        isToolbarExpanded = false;
                    }
                }
            }
        });

    }
    private void AddListCnT(){

        truyenTranhList.add(new TruyenTranh("Hinh 1",R.drawable.backround_register));
        truyenTranhList.add(new TruyenTranh("Hinh 2",R.drawable.backround_register));
        truyenTranhList.add(new TruyenTranh("Hinh 3",R.drawable.backround_register));
        truyenTranhList.add(new TruyenTranh("Hinh 4",R.drawable.backround_register));

        truyenTranhList.add(new TruyenTranh("Hinh 1",R.drawable.backround_register));
        truyenTranhList.add(new TruyenTranh("Hinh 2",R.drawable.backround_register));
        truyenTranhList.add(new TruyenTranh("Hinh 3",R.drawable.backround_register));
        truyenTranhList.add(new TruyenTranh("Hinh 4",R.drawable.backround_register));

        categoryList.add(new Category("Cate1", truyenTranhList));
        categoryList.add(new Category("Cate2", truyenTranhList));
        categoryList.add(new Category("Cate3", truyenTranhList));
        categoryList.add(new Category("Cate4", truyenTranhList));
    }
    // Set content for recycleView
    private void SetContentRecycleView(){
        recyclerViewCategory = view.findViewById(R.id.rcv_category);

        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 1);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),RecyclerView.VERTICAL,false);
        recyclerViewCategory.setLayoutManager(layoutManager);// Set form for recycleView category

        categoryAdapter = new CategoryAdapter(view.getContext());
        categoryAdapter.SetData(categoryList);// set categoryList for adapter

        recyclerViewCategory.setAdapter(categoryAdapter);

        recyclerViewCategory.setNestedScrollingEnabled(false);

    }
    private void SetContentImageSlider(){
        viewPager2 = view.findViewById(R.id.vp2_image_slider);

        imageSliderAdapter = new ImageSliderAdapter(truyenTranhList, viewPager2);
        viewPager2.setAdapter(imageSliderAdapter);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(sliderRunnable);
                handler.postDelayed(sliderRunnable, 5000);
            }
        });
    }
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };
}
