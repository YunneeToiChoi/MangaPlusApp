package com.example.mangaplusapp.Activity.User;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mangaplusapp.Activity.Base.BaseActivity;
import com.example.mangaplusapp.R;
import com.example.mangaplusapp.databinding.ActivityPaymentBinding;
import com.example.mangaplusapp.databinding.ActivityPaymentStripeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentStripeActivity extends BaseActivity {
    ActivityPaymentStripeBinding activityPaymentBinding;
    AppCompatButton btn;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration configuration;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    private String mangaId;
    String userName;
    int price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityPaymentBinding = ActivityPaymentStripeBinding.inflate(getLayoutInflater());
        setContentView(activityPaymentBinding.getRoot());
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        userName=currentUser.getDisplayName();
        price=1000;
        mangaId = getIntent().getStringExtra("ID_MANGA");
        fetchApi();
        btn = findViewById(R.id.btnSubmitPaymentStripe);
        btn.setOnClickListener(v -> {
            if(paymentIntentClientSecret!=null){
                paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret,
                        new PaymentSheet.Configuration("MangaPlus",configuration));
            }
            else{
                Toast.makeText(getApplicationContext(),"Wait a second, page is loading,...",Toast.LENGTH_SHORT).show();
            }
        });
        paymentSheet=new PaymentSheet(this,this::onPaymentSheetResult);

        hookIntent();

    }
    private void hookIntent(){
        Intent intent = getIntent();
        Glide.with(this)
                .load(intent.getStringExtra("PICTURE_MANGA"))
                .into(activityPaymentBinding.mangaDetailImg);
        activityPaymentBinding.NameProduct.setText(intent.getStringExtra("NAME_MANGA"));
        activityPaymentBinding.TotalPrice.setText(intent.getStringExtra("PRICE_MANGA") + "$");
        activityPaymentBinding.UserName.setText(userName);

        activityPaymentBinding.backToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void dialogSuccess(){
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success_payment);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onBackPressed();
            }
        });
    }
    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult){
        if(paymentSheetResult instanceof  PaymentSheetResult.Canceled){
            Toast.makeText(this,"Canceled", Toast.LENGTH_SHORT).show();
        }
        if(paymentSheetResult instanceof  PaymentSheetResult.Failed){
            Toast.makeText(this,((PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage(), Toast.LENGTH_SHORT).show();
        }
        if(paymentSheetResult instanceof  PaymentSheetResult.Completed){
            isBought();
            dialogSuccess();
        }
    }
    public void isBought(){
        if(currentUser == null){
            Toast.makeText(this,"You're not login", Toast.LENGTH_SHORT).show();
            return;
        }else {
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("ID_MANGA", mangaId);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(currentUser.getUid()).child("HistoryPayment").child(mangaId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(PaymentStripeActivity.this,"Payment Success", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void fetchApi(){ // POST
        RequestQueue queue = Volley.newRequestQueue(this);
            String url ="https://1a00-2405-4802-8127-cf0-95d-67fb-554b-62e9.ngrok-free.app/";
    
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            configuration= new PaymentSheet.CustomerConfiguration(
                                    jsonObject.getString("customer"),
                                    jsonObject.getString("ephemeralKey")
                            );
                            paymentIntentClientSecret = jsonObject.getString("paymentIntent");
                            PaymentConfiguration.init(getApplicationContext(),jsonObject.getString("publishableKey"));
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("authKey", "abc");
                // Thêm tên người dùng vào yêu cầu POST
                paramV.put("userName", userName);
                Log.d("userName", userName);
                paramV.put("amount", String.valueOf(price));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}