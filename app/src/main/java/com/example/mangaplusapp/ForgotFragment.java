package com.example.mangaplusapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import Helper.ActionHelper.KeyBoardHelper;
import Helper.DBHelper.UserDBHelper;

public class ForgotFragment extends Fragment {
    AppCompatButton SendOtpBtn;
    String userEmail;
    UserDBHelper db;
    int userID;
    EditText getUserEmailTxt;
    ImageButton BackbtnLogin;
    public ForgotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Ẩn keyboard
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_forgot, container, false);
//        KeyBoardHelper.ActionRemoveKeyBoardForFragment(root,requireContext());

        SendOtpBtn = root.findViewById(R.id.btnForgotSendOtp);
        //========================================GET ID==========================================//
        getUserEmailTxt=root.findViewById(R.id.forgotEmailUser_Input);
        BackbtnLogin=root.findViewById(R.id.backForgotBtn);
        //****************************************************************************************//
        //=====================================GET DATABASE=======================================//
        db=new UserDBHelper(getContext());
        SharedPreferences preferences = getContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        userID=preferences.getInt("user_id",-1);
        //=====================================Send EMAIL=======================================//
        SendOtpBtn.setOnClickListener(v->{
            if(getUserEmailTxt.getText().toString().isEmpty()){
                Toast.makeText(getContext() ,"Please enter your email", Toast.LENGTH_SHORT).show();
            }
            else{
                boolean checkExists = db.CheckEmailExists(getUserEmailTxt.getText().toString());
                if(checkExists){
                    userEmail = getUserEmailTxt.getText().toString(); // get user email input
                    editor.putString("user_email", userEmail); // put to session
                    editor.apply(); // apply session
                    //*DEBUG*//
                    String xx = preferences.getString("user_email","error"); // get data form session
                    Log.d("FORGOT FRAGMENT SEND EMAIL ERROR", xx); // log cat
                    //*DEBUG*//
                    loadFragment(new VerificationFragment(),false);
                }
                else{
                    Toast.makeText(getContext() ,"Email not exists in our app", Toast.LENGTH_SHORT).show();
                    // new Feature : new button nav to Register

                }
            }
        });
        BackbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getContext(),LoginActivity.class);
               startActivity(intent);
            }
        });
        return root;
    }
    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.forgotContainer, fragment, fragment.getClass().getSimpleName());
        } else {
            fragmentTransaction.replace(R.id.forgotContainer, fragment, fragment.getClass().getSimpleName());
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        fragmentTransaction.commit();
    }

}