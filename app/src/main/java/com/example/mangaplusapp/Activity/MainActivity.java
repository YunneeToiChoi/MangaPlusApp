package com.example.mangaplusapp.Activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.mangaplusapp.Fragment.CreatorFragment;
import com.example.mangaplusapp.Fragment.HomeFragment;
import com.example.mangaplusapp.Fragment.HotFragment;
import com.example.mangaplusapp.Fragment.LibraryFragment;
import com.example.mangaplusapp.Fragment.SearchFragment;
import com.example.mangaplusapp.Fragment.UserProfileFragment;
import com.example.mangaplusapp.Helper.DBHelper.UserDBHelper;
import com.example.mangaplusapp.R;
import com.example.mangaplusapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity{
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    TextView userNameTxt;
    String userName;
    ImageView imgViewUser;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // set content phải trước focus nha
        loadFragment(new HomeFragment(),false, R.menu.home_fragment_header_menu);
        focusFragment();
        loadMenuDrawer();
        setInfo();
    }
    private void setInfo(){
        navigationView = findViewById(R.id.navigation_drawer_container);
        userNameTxt = navigationView.getHeaderView(0).findViewById(R.id.menu_drawer_header_username);
        imgViewUser=  navigationView.getHeaderView(0).findViewById(R.id.menu_drawer_header_image_user);
        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        UserDBHelper dbUserHelper = new UserDBHelper(this);
        userID = preferences.getInt("user_id",-1);
        if(userID==-1){
            Log.d("Main ACtivity", "can not get userID");
        }
        else{
            Log.d("Main ACtivity", " get userID success ");
        }
        userName = dbUserHelper.getUserName(userID);
        userNameTxt.setText(userName);
        String userIMG = dbUserHelper.getPicture(userID);
        Glide.with(this).load(userIMG).into(imgViewUser);

    }
    /*Non-override Function*/
    private void updateBottomNavigationView() {

        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        if (backStackEntryCount > 0) {
            // There are fragments in the back stack, get the top one
            FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(backStackEntryCount - 1);
            String fragmentTag = backEntry.getName();

            // Update Bottom Navigation View based on the fragment tag
            if (fragmentTag != null) {
                switch (fragmentTag) {
                    case "HomeFragment":
                        bottomNavigationView.getMenu().findItem(R.id.navHome).setChecked(true);
                        break;
                    case "HotFragment":
                        bottomNavigationView.getMenu().findItem(R.id.navHot).setChecked(true);
                        break;
                    case "UserProfileFragment":
                        bottomNavigationView.getMenu().findItem(R.id.navProfile).setChecked(true);
                        break;
                    case "CreatorFragment":
                        bottomNavigationView.getMenu().findItem(R.id.navCreator).setChecked(true);
                        break;
                    case "SearchFragment":
                        bottomNavigationView.getMenu().findItem(R.id.navSearch).setChecked(true);
                        break;
                    // Add more cases for other fragments
                }
            }
        } else {
            // No fragments in the back stack, set the default item
            bottomNavigationView.getMenu().findItem(R.id.navHome).setChecked(true);
        }
    }
    private void focusFragment() { // ham chay ra main cua fragment
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                //Load Fragment and Menu
                if (itemId == R.id.navHome) {
                    loadFragment(new HomeFragment(), false, R.menu.home_fragment_header_menu);
                } else if (itemId == R.id.navHot) {
                    loadFragment(new HotFragment(), false, R.menu.hot_fragment_header_menu);
                } else if (itemId == R.id.navSearch) {
                    loadFragment(new SearchFragment(), false, R.menu.search_fragment_header_menu);
                } else if (itemId == R.id.navCreator) {
                    loadFragment(new CreatorFragment(), false, R.menu.creator_fragment_header_menu);
                } else if (itemId == R.id.navProfile) {
                    loadFragment(new UserProfileFragment(), false, R.menu.library_fragment_header_menu);
                }
                return true;
            }
        });
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                updateBottomNavigationView();
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized,int menuResId) {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment, fragment.getClass().getSimpleName());
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment, fragment.getClass().getSimpleName());
        }
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName()); // Ad tag to Stack with the same name of Fragment
        fragmentTransaction.commit();

        // Set the toolbar menu for the current fragment
        binding.mainHeader.getMenu().clear();
        getMenuInflater().inflate(menuResId, binding.mainHeader.getMenu());
    }
    private void loadMenuDrawer(){
        toolbar = (Toolbar) findViewById(R.id.main_header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView = (NavigationView) findViewById(R.id.navigation_drawer_container);
        navigationView.bringToFront();
        drawerLayout = (DrawerLayout) findViewById(R.id.main_activity_drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    /*Override Function*/
    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            int backStackEntryCount = fragmentManager.getBackStackEntryCount(); // Take Length of Stack
            //Take Fragment below first Fragment of stack
            FragmentManager.BackStackEntry backEnd = fragmentManager.getBackStackEntryAt(backStackEntryCount - 2);
            String fragmentTag = backEnd.getName();
            //Hook Fragment with fragmentTag
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

            // Update the toolbar menu for the current fragment
            if (currentFragment instanceof HomeFragment) {
                binding.mainHeader.getMenu().clear();
                getMenuInflater().inflate(R.menu.home_fragment_header_menu, binding.mainHeader.getMenu());
            } else if (currentFragment instanceof HotFragment) {
                binding.mainHeader.getMenu().clear();
                getMenuInflater().inflate(R.menu.hot_fragment_header_menu, binding.mainHeader.getMenu());
            } else if (currentFragment instanceof SearchFragment){
                binding.mainHeader.getMenu().clear();
                getMenuInflater().inflate(R.menu.search_fragment_header_menu, binding.mainHeader.getMenu());
            }else if (currentFragment instanceof CreatorFragment){
                binding.mainHeader.getMenu().clear();
                getMenuInflater().inflate(R.menu.creator_fragment_header_menu, binding.mainHeader.getMenu());
            }else{
                binding.mainHeader.getMenu().clear();
                getMenuInflater().inflate(R.menu.library_fragment_header_menu, binding.mainHeader.getMenu());
            }
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.home_fm_search) {
            // Handle action for HomeFragment
            loadFragment(new SearchFragment(),false, R.menu.search_fragment_header_menu);
            return true;
        } else if (itemId == R.id.hot_fm_profile) {
            // Handle action for HotFragment
            Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
            return true;
        }
        //If you wanna more feature add more condition
        return super.onOptionsItemSelected(item);
    }
}