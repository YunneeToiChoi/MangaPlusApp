package com.example.mangaplusapp;

import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;
import androidx.constraintlayout.widget.Group;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mangaplusapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class DrawerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_drawer,container,false);
        //Đã include ở fragment_drawer nên có thể bắt được từ rootView mà không cần bắt thêm View của menu_drawer_header
        TextView textView = rootView.findViewById(R.id.menu_drawer_header_username);
        textView.setText("HOANG UYEN");
        ImageView imageView = rootView.findViewById(R.id.menu_drawer_header_image_user);
        imageView.setImageResource(R.drawable.ic_personal);
        /*------------------------------------------------*/
        NavigationView navigationView = rootView.findViewById(R.id.navigation_drawer_fragment);
        focusFrament(navigationView);
        return rootView;
    }
    private void focusFrament(NavigationView view){
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                BottomNavigationView bottomNavigationView = ((MainActivity)requireActivity()).getBottomNavigationView() ;// Gán id của BottomNavigation từ MainActivity vào.
                if(itemId == R.id.menu_drawer_favorite){
                    loadFragment(new HotFragment(),false);
                    bottomNavigationView.getMenu().findItem(R.id.navHot).setChecked(true);
                } else if (itemId == R.id.menu_drawer_store_download) {
                    loadFragment(new LibraryFragment(),false);
                    bottomNavigationView.getMenu().findItem(R.id.navLibrary).setChecked(true);
                }
                DrawerLayout drawerLayout = ((MainActivity)requireActivity()).getDrawerLayout();// Gán id của DrawerLayout từ MainActivity vào
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //Do chưa design xong nên chỉ demo 2 item ở 2 group khác nhau để xem bắt được không.
        //Còn lỗi đã đổi fragment nhưng trên bottom navigation không thay đổi theo (fixed tạm)| Tao thấy kiểu như vẫn là fix cứng nên chưa ưng lắm
        //Còn lỗi chưa tắt được drawer khi chọn item (fixed)
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment, fragment.getClass().getSimpleName());
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment, fragment.getClass().getSimpleName());
        }
        fragmentTransaction.commit();
    }
}