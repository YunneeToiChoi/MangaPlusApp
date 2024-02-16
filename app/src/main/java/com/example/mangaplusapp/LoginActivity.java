package com.example.mangaplusapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import Database.CreateDatabase;
import mvp.UI.Login.LoginPresenter;
import mvp.UI.Login.MVPLoginView;

public class LoginActivity extends AppCompatActivity implements MVPLoginView {
    EditText emailTxt, passwordTxt;
    TextView forgotPasswordTxt,toSignUpTxt;
    Button btnLoginTxt;
    CreateDatabase db;
    int idUser;

    //Create sign in Google
    GoogleSignInOptions gso;
//    GoogleSignInClient gsc;
    ImageView googleBtn;
    //End sign in Google
     private LoginPresenter loginPresenter;
    //Call PresenterLogin
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //===============================Begin get id for login basic=============================//
        toSignUpTxt = findViewById(R.id.toSignUp);
        emailTxt = (EditText) findViewById(R.id.editTextEmail);
        passwordTxt = (EditText) findViewById(R.id.editTextPassword);
        btnLoginTxt = (Button) findViewById(R.id.loginBtn_act);
        forgotPasswordTxt = (TextView) findViewById(R.id.forgotPassword_act);
        //================================End get id for login basic==============================//
        //****************************************************************************************//
        //================================Begin get data for login basic==========================//
        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        //================================END get data for login basic============================//
        //****************************************************************************************//
        //===============================Begin get id for login with social=======================//
        googleBtn = findViewById(R.id.googleBtn_login);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        //===============================End get id for login with social=========================//
        //****************************************************************************************//
        //===============================CONNECT DATABASE=========================================//
        db = new CreateDatabase(this);
        db.open();
        //=============================== End Connect DataBase====================================//
        //****************************************************************************************//
        //=============================== BEGIN NAVIGATE LAYOUT===================================//
        navigateLayout();
        //=============================== END NAVIGATE LAYOUT===================================//
        //****************************************************************************************//
        //===============================Begin Connect Login With Social==========================//
                                        // Process IS EMPTY //
        //===============================End Connect Login With Social============================//
        //****************************************************************************************//
        //===============================BEGIN LOGIC LOGIN BASIC==================================//
        //===============================CREATE CALL PRESENTER====================================//
        loginPresenter=new LoginPresenter(this);
        //===============================END CALL PRESENTER====================================//
        btnLoginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            // this event final
            public void onClick(View v) {
                  loginPresenter.receivedHandleLogin(emailTxt,passwordTxt,db,idUser,editor);
                  //đang ở ActView thông báo cho presenter khi đươc click sự kiện
            }
        });
        //===============================END LOGIC LOGIN BASIC====================================//
        //****************************************************************************************//
        //****************************************************************************************//
        //===============================Begin login with social==================================//
    }
    void navigateLayout(){
        //==================================BEGIN NAV TO SIGN UP==================================//
        toSignUpTxt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); // bat su kien
            startActivity(intent); // chay su kien
        });
        //====================================END NAV TO SIGN UP==================================//
        //==============================BEGIN NAV TO FORGOT PASSWORD==============================//
        forgotPasswordTxt.setOnClickListener(v->{
            Intent intent = new Intent(LoginActivity.this, ForgotControlActivity.class); // bat su kien
            startActivity(intent); // chay su kien
        });
        //****************************************************************************************//
    }

    void navToSuccess(){
        finish();
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }
//    void signInWithSocial(){
//        Intent signInIntent = gsc.getSignInIntent();
//        startActivityForResult(signInIntent,1000);
//    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }
    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.forgotContainer, fragment, fragment.getClass().getSimpleName());
        } else {
            fragmentTransaction.replace(R.id.forgotContainer, fragment, fragment.getClass().getSimpleName());
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        fragmentTransaction.commit();
    }
 //Callback by Presenter
    @Override
    public void LoginSuccess() {
        Toast.makeText(LoginActivity.this,"Sign Ip Successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void LoginFailed() {
        Toast.makeText(LoginActivity.this,"Invalid Credentials", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void LoginDefalt() {
        Toast.makeText(LoginActivity.this,"Please enter all fields", Toast.LENGTH_SHORT).show();
    }
}