package com.example.mangaplusapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import Helper.ActionHelper.KeyBoardHelper;
import Helper.DBHelper.UserDBHelper;
import Helper.LoadHelper.LoadFragment;

public class CreatePasswordFragment extends Fragment {

    EditText getUserNameTxt,getUserPasswordTxt,getUserRePasswordTxt;
    String userName_register,userEmail,userPassword,userRePassword;
    UserDBHelper dbHelper;
    AppCompatButton btnSubmit;
    RelativeLayout layoutInput;
    RelativeLayout layoutInputUserName;
    ImageButton backRegisterBtn;
    int userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Ẩn keyboard
        // Inflate the layout for this fragment
        View root =inflater.inflate(R.layout.fragment_create_password, container, false);
        dbHelper= new UserDBHelper(getContext());
        SharedPreferences preferences = getContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        userId=preferences.getInt("user_id",-1);
        if(userId==-1)
        {
            Log.d("sss", "null-1 ");
        }
        else
        {
            Log.d("aaa", String.valueOf(userId));
        }
        layoutInputUserName=root.findViewById(R.id.InputUNCP);
        btnSubmit = root.findViewById(R.id.btnSubmitInfo);
        getUserNameTxt = root.findViewById(R.id.userNewNameTxt);
        getUserPasswordTxt = root.findViewById(R.id.userNewPasswordTxt);
        getUserRePasswordTxt = root.findViewById(R.id.userRePasswordTxt);
        layoutInput=root.findViewById(R.id.userInputInfo_layout);
        backRegisterBtn=root.findViewById(R.id.backRegisterBtn);
        userEmail =preferences.getString("user_email",null);

        backRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new VerificationFragment(),false);
            }
        });

        //===================================ForgotPassword Case==================================//
        if(dbHelper.CheckEmailExists(userEmail)){
            layoutInput.removeView(layoutInputUserName);
            btnSubmit.setOnClickListener(v->{
                userPassword=getUserPasswordTxt.getText().toString();
                userRePassword = getUserRePasswordTxt.getText().toString();
                if(userPassword.equals(" ")|| userRePassword.equals(" ")){
                    Toast.makeText(getContext(),"Password and Confirm Password not matches", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(dbHelper.validPassword(userPassword)){
                        //=================================SUCCESSFUL=============================//
                        Log.d("user_email", userEmail);
                        Log.d("user_password", userPassword);
                        Log.d("user_rePassword", userRePassword);

                        dbHelper.UpdatePassword(userId,userPassword.trim());
                        Toast.makeText(getContext(),"RePassword Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(),LoginActivity.class);
                        startActivity(intent);
                        //=================================SUCCESSFUL=============================//
                    }
                    else if(userPassword.length() <8){
                        Toast.makeText(getContext(),"Please enter password length >= 8", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(),"Please enter password valid", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //===================================New Account Case=====================================//
        else{
            btnSubmit.setOnClickListener(v->{
                userName_register = getUserNameTxt.getText().toString();
                userPassword=getUserPasswordTxt.getText().toString();
                userRePassword = getUserRePasswordTxt.getText().toString();
                if(userPassword.equals(" ")|| userRePassword.equals(" ")){
                    Toast.makeText(getContext(),"Password and Confirm Password not matches", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(dbHelper.validPassword(userPassword)){
                        //=================================SUCCESSFUL=============================//
                        dbHelper.insertData(userEmail,userPassword,userName_register);
                        Log.d("user_email", userEmail);
                        Log.d("user_password", userPassword);
                        Log.d("user_rePassword", userRePassword);
                        Log.d("user_name", userName_register);
                        Toast.makeText(getContext(),"Register Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(),LoginActivity.class);
                        startActivity(intent);
                        //=================================SUCCESSFUL=============================//
                    }
                    else if(userPassword.length() <8){
                        Toast.makeText(getContext(),"Please enter password length >= 8", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(),"Please enter password valid", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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