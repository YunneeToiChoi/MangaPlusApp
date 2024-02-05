package com.example.mangaplusapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.example.mangaplusapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;

import Adapter.TruyenTranhAdapter;
import object.TruyenTranh;

public class MainActivity extends AppCompatActivity{
    GridView gdvDSTruyen;
    TruyenTranhAdapter adapter;
    ArrayList<TruyenTranh> truyenTranhArrayList;
    EditText edtTimKiem;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private FormLayout formLayout;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // set content phải trước focus nha
        drawerLayout = (DrawerLayout) findViewById(R.id.main_activity_drawer);
        loadFragment(new HomeFragment(),false);
        // Đang bị bug chồng layout
        focusFragment();
        loadFragmentDrawer(new DrawerFragment()); // Load navigationView từ DrawerFragment --> navigationView ở
        openMenuDrawer();


        // END FORM Validition
    }

    private void focusFragment() { // ham chay ra main cua fragment
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navHome) {
                    loadFragment(new HomeFragment(), false);
                } else if (itemId == R.id.navHot) {
                    loadFragment(new HotFragment(), false);
                } else if (itemId == R.id.navSearch) {
                    loadFragment(new SearchFragment(), false);
                } else if (itemId == R.id.navCreator) {
                    loadFragment(new CreatorFragment(), false);
                } else if (itemId == R.id.navLibrary) {
                    loadFragment(new LibraryFragment(), false);
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment, fragment.getClass().getSimpleName());
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment, fragment.getClass().getSimpleName());
        }
        fragmentTransaction.commit();
    }
    private void loadFragmentDrawer(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navigation_drawer_container,fragment);
        fragmentTransaction.commit();
    }
    private void openMenuDrawer(){
        ImageButton btn = (ImageButton) findViewById(R.id.header_menu_drawer);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationView = (NavigationView) findViewById(R.id.navigation_drawer_container);
                toolbar = (Toolbar) findViewById(R.id.toolbar_main);
                setSupportActionBar(toolbar);
                navigationView.bringToFront();
                toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawerLayout.addDrawerListener(toggle);
                toggle.syncState();
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    //Function bắt drawerLayout ở Class khác
    public DrawerLayout getDrawerLayout(){
        return drawerLayout;
    }
    public BottomNavigationView getBottomNavigationView(){
        return bottomNavigationView;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}