package com.example.mangaplusapp;

import android.app.Application;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import Adapter.TruyenTranhAdapter;
import object.TruyenTranh;
public class HomeFragment extends Fragment {
    GridView gdvDSTruyen;
    TruyenTranhAdapter adapter;
    ArrayList<TruyenTranh> truyenTranhArrayList;
    EditText edtTimKiem;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        super.onCreateView(inflater, container, savedInstanceState); // Gọi super trước
        init();
        anhXa(view);
        setClick();
        setUp();
        onPause();
        onResume();
        return view;
    }
    private void init(){ // focus vao
        truyenTranhArrayList= new ArrayList<>();
        truyenTranhArrayList.add(new TruyenTranh("Quynh Mai","Chapter 1","https://th.bing.com/th/id/R.75a1f21171a65133e7bda08d8fbe332c?rik=3yOyZZQwy%2f1jWw&pid=ImgRaw&r=0"));
        truyenTranhArrayList.add(new TruyenTranh("ket","Chapter 1","https://th.bing.com/th/id/R.75a1f21171a65133e7bda08d8fbe332c?rik=3yOyZZQwy%2f1jWw&pid=ImgRaw&r=0"));
        truyenTranhArrayList.add(new TruyenTranh("nniao","Chapter 1","https://th.bing.com/th/id/R.75a1f21171a65133e7bda08d8fbe332c?rik=3yOyZZQwy%2f1jWw&pid=ImgRaw&r=0"));
        truyenTranhArrayList.add(new TruyenTranh("Oni Chi Chi","Chapter 1","https://th.bing.com/th/id/R.75a1f21171a65133e7bda08d8fbe332c?rik=3yOyZZQwy%2f1jWw&pid=ImgRaw&r=0"));
        truyenTranhArrayList.add(new TruyenTranh("Boku No Pico","Chapter 1","https://th.bing.com/th/id/R.75a1f21171a65133e7bda08d8fbe332c?rik=3yOyZZQwy%2f1jWw&pid=ImgRaw&r=0"));
        truyenTranhArrayList.add(new TruyenTranh("khoy","Chapter 1","https://th.bing.com/th/id/R.75a1f21171a65133e7bda08d8fbe332c?rik=3yOyZZQwy%2f1jWw&pid=ImgRaw&r=0"));
        truyenTranhArrayList.add(new TruyenTranh("khoy","Chapter 1","https://th.bing.com/th/id/R.75a1f21171a65133e7bda08d8fbe332c?rik=3yOyZZQwy%2f1jWw&pid=ImgRaw&r=0"));
        adapter = new TruyenTranhAdapter(getActivity(), 0, truyenTranhArrayList);
    }

    private void anhXa(View view ){
        gdvDSTruyen = view.findViewById(R.id.gdvDSTruyen);

    }
    private void setUp(){
        gdvDSTruyen.setAdapter(adapter);
    }
    private void setClick(){

    }
    public void onPause() {
        super.onPause();
        gdvDSTruyen.setAdapter(null);
    }
    public void onResume() {
        super.onResume();
        gdvDSTruyen.setAdapter(adapter); // Gán lại adapter khi fragment được hiển thị
    }
}
