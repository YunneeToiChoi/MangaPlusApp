package com.example.mangaplusapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import Helper.ActionHelper.KeyBoardHelper;
import Helper.DBHelper.UserDBHelper;
import Helper.LoadHelper.LoadFragment;
import Helper.ServiceHelper.OTP;

public class VerificationFragment extends Fragment{
    FirebaseAuth auth;
    String emailUser;
    TextView getEmailUserTxt,reSendOtp;
    UserDBHelper dbHelper;
    AppCompatButton submitOtp;
    OTP otpHelper;
    LoadFragment fragmentHelper;
    int userID;
    String keyOtp;
    EditText otp1Input,otp2Input,otp3Input,otp4Input;
    ImageButton backOTPBtn;

    //Resend OTP time
    private final int resendTime=60;
    //Event guards resend code
    private  boolean resendEnable= false;

    //Event guards change color submit when entering enough code
    private int selectedPosition=0;
    public VerificationFragment() {
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
        //
        View root = inflater.inflate(R.layout.fragment_verification, container, false);
        //=========================================GET ID=========================================//
        getEmailUserTxt=root.findViewById(R.id.forgot_EmailUserTxt);
        otp1Input=root.findViewById(R.id.otp1);
        otp2Input=root.findViewById(R.id.otp2);
        otp3Input=root.findViewById(R.id.otp3);
        otp4Input=root.findViewById(R.id.otp4);

        submitOtp=root.findViewById(R.id.sendOtp);
        reSendOtp=root.findViewById(R.id.reSendOtpTxt);
        backOTPBtn=root.findViewById(R.id.backOTPBtn);
        //****************************************************************************************//
        //=========================================GET DATA=======================================//
        SharedPreferences preferences = getContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        otpHelper = new OTP();
        dbHelper=new UserDBHelper(getContext());
        auth=FirebaseAuth.getInstance();
        //****************************************************************************************//
        //=========================================GET HELPER=======================================//
        //****************************************************************************************//
        //=========================================SET DATA=======================================//
        emailUser = preferences.getString("user_email",null);

        // Show registered or forgot emails
        getEmailUserTxt.setText(emailUser);
        //****************************************************************************************//
        //=========================================SEND OTP=======================================//
        keyOtp = otpHelper.generateOTP();
        otpHelper.sendOTPByEmail(keyOtp,emailUser);
        Log.d("asd", keyOtp);
        Toast.makeText(getContext(),"Send OTP successfully",Toast.LENGTH_SHORT).show();
        BackPageVertication();
        //Event start running timer resend OTP
        //        First run
        startCountDownTimer();
        //****************************************************************************************//
        //****************************************************************************************//

        //Listen for changes in each edittext
        TextWatcherListener(otp1Input,selectedPosition,otp1Input,otp2Input,otp3Input,otp4Input);
        TextWatcherListener(otp2Input,selectedPosition,otp1Input,otp2Input,otp3Input,otp4Input);
        TextWatcherListener(otp3Input,selectedPosition,otp1Input,otp2Input,otp3Input,otp4Input);
        TextWatcherListener(otp4Input,selectedPosition,otp1Input,otp2Input,otp3Input,otp4Input);

        //Event listening paste
        setPasteListener(otp1Input);
        setPasteListener(otp2Input);
        setPasteListener(otp3Input);
        setPasteListener(otp4Input);

        // Event listening delete
        DeleteMoveUp(otp1Input,otp1Input);
        DeleteMoveUp(otp2Input,otp1Input);
        DeleteMoveUp(otp3Input,otp2Input);
        DeleteMoveUp(otp4Input,otp3Input);



        reSendOtp.setOnClickListener(v->{
            if(resendEnable)
            {
                keyOtp = otpHelper.generateOTP();
                otpHelper.sendOTPByEmail(keyOtp,emailUser);
                Log.d("asd", keyOtp);
                startCountDownTimer();
            }
        });



        submitOtp.setOnClickListener(v->{
            String otp1 = otp1Input.getText().toString();
            String otp2 = otp2Input.getText().toString();
            String otp3 = otp3Input.getText().toString();
            String otp4 = otp4Input.getText().toString();
            String otp= otp1+otp2+otp3+otp4;
            Log.d("keyOTPtest", keyOtp);
            if(otp.length()==4)
            {
                if(otp.equals(keyOtp)){
                    //===============================Case forgot password=============================//
                    if(dbHelper.CheckEmailExists(emailUser)){
                        // khi có figma thì cho chạy vào form edit password
                        userID =dbHelper.loginUser(emailUser);
                        editor.putString("user_email", emailUser);
                        editor.putInt("user_id",userID); // put user id
                        editor.apply();
                        fragmentHelper = new LoadFragment();
                        fragmentHelper.loadFragment(getParentFragmentManager(),new CreatePasswordFragment(),false,R.id.forgotContainer);
                    }
                    //===============================Case Register ===================================//
                    else{
                        userID =dbHelper.loginUser(emailUser);
                        editor.putString("user_email", emailUser);
                        editor.apply();
                        // nav to new password and confirm password and insert data into database -> nav to login
                        fragmentHelper = new LoadFragment();
                        fragmentHelper.loadFragment(getParentFragmentManager(),new CreatePasswordFragment(),false,R.id.forgotContainer);
                    }
                }
                else{
                    Toast.makeText(getContext(),"Wrong OTP code", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(),"Not entering enough OTP code", Toast.LENGTH_SHORT).show();
            }
        });





        return root;
    }
    private  void BackPageVertication(){
        backOTPBtn.setOnClickListener(v->{
            if(dbHelper.CheckEmailExists(emailUser)){
                loadFragment(new ForgotFragment(),false);
            }
            else{
                Intent loadToRegister = new Intent(getContext(),RegisterActivity.class);
                startActivity(loadToRegister);
            }
        });
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
    //Check Editext has data or not
    private int ListenNullText(int SelectedPosition,EditText input1,EditText input2, EditText input3, EditText input4)
    {
        Editable editable1=input1.getText();
        Editable editable2=input2.getText();
        Editable editable3=input3.getText();
        Editable editable4=input4.getText();
        if (editable1.length()<=0)
        {
           SelectedPosition=0;
            showKeyBoard(input1);
        }
        else if(editable2.length()<=0)
        {
            SelectedPosition =1;
            showKeyBoard(input2);
        }
        else if (editable3.length()<=0)
        {
            SelectedPosition=2;
            showKeyBoard(input3);
        }
        else if (editable4.length()<=0)
        {
            SelectedPosition=3;
            showKeyBoard(input4);
        }
        else
        {
            submitOtp.setBackgroundResource(R.drawable.btn_accept);
        }
        return  SelectedPosition;
    }


    //Delete move on editext
    private void DeleteMoveUp(EditText InputAction,EditText InputMoveOn)
    {
        InputAction.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Editable editable=InputAction.getText();
                if (keyCode == KeyEvent.KEYCODE_DEL&& event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    submitOtp.setBackgroundResource(R.drawable.btn_defalt);
                    if(editable.length()<=0)
                    {
                        Log.d("move", "onKey: moveon");
                        showKeyBoard(InputMoveOn);
                    }
                    else
                    {
                        Log.d("stay", "onKey: Stayin");
                        showKeyBoard(InputAction);
                    }
                }
                return false;
            }
        });
    }

    // Listening event changes in Edittext
    private void TextWatcherListener(EditText Action,int SelectedPosition,EditText otp1Input,EditText otp2Input, EditText otp3Input, EditText otp4Input)
    {
        TextWatcher textWatcher=new TextWatcher( ) {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Xử lý trước khi văn bản thay đổi
                Log.d("Before Active", String.valueOf(selectedPosition));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Xử lý trong quá trình văn bản đang thay đổi
                Log.d("On Active", String.valueOf(selectedPosition));
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Xử lý sau khi văn bản đã thay đổi
                if(s.length()>0)
                {
                    if(SelectedPosition==0||otp1Input.isFocused())
                    {
                        selectedPosition=0;
                        ListenNullText(selectedPosition,otp1Input,otp2Input,otp3Input,otp4Input);
                    }
                    else if (selectedPosition==1||otp2Input.isFocused())
                    {
                        selectedPosition=1;
                        ListenNullText(selectedPosition,otp1Input,otp2Input,otp3Input,otp4Input);
                    }
                    else if (selectedPosition==2||otp3Input.isFocused())
                    {
                        selectedPosition=2;
                        ListenNullText(selectedPosition,otp1Input,otp2Input,otp3Input,otp4Input);
                    }
                    else if (selectedPosition==3||otp4Input.isFocused())
                    {
                        selectedPosition=3;
                        ListenNullText(selectedPosition,otp1Input,otp2Input,otp3Input,otp4Input);
                    }
                }
            }
        };
        Action.addTextChangedListener(textWatcher);
    }
    //Display keyboard and focus for each edittext
    private  void showKeyBoard(EditText otpET)
    {
        otpET.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //Show the keyboard doesn't need to interact with edittext
        inputMethodManager.showSoftInput(otpET,InputMethodManager.SHOW_IMPLICIT);
    }
    private void startCountDownTimer()
    {
        //if resendEnable=false then start the countdown
        resendEnable=false;
        reSendOtp.setTextColor(Color.parseColor("#99000000"));
        //The countdown time is 60x1000 and each countdown is 1s
        new CountDownTimer(resendTime *1000,1000)
        {
            //Update the countdown time after each time the time unit decreases
            @Override
            public void onTick(long millisUntilFinished) {
                reSendOtp.setText((String.valueOf(millisUntilFinished/1000))+"s");
            }
            //When finished, change the value to true and recolor the text and reset OTP
            @Override
            public void onFinish() {
                resendEnable=true;
                keyOtp = otpHelper.generateOTP();
                reSendOtp.setText("Resend Code");
                reSendOtp.setTextColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
            }
        }.start();
    }

    //Event listen paste
    private void setPasteListener(final EditText editText) {
        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Paste event handler
                pasteFromClipboard(editText);
                return true;
            }
        });
    }

    //Method Paste event handler
    private void pasteFromClipboard(EditText editText) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        // Check if the clipboard (used to receive paste data) is null or has data
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            //Get data from clipboard because only get the data item at item 0 position to test
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            if (item != null) {
                //Hash data into string arrays
                String clipboardText = item.getText().toString();
                if (clipboardText.length() == 4) {
                    // Put index data into each EditText
                    setEditTextValues(clipboardText);
                }
            }
        }
    }

    //Handle the cursor of editext and place each index data of the text in the correct editext
    private  void setEditTextValues(String text) {
        otp1Input.setText(String.valueOf(text.charAt(0)));
        otp2Input.setText(String.valueOf(text.charAt(1)));
        otp3Input.setText(String.valueOf(text.charAt(2)));
        otp4Input.setText(String.valueOf(text.charAt(3)));
        otp1Input.setSelection(otp2Input.getText().length());
        otp2Input.setSelection(otp2Input.getText().length());
        otp3Input.setSelection(otp3Input.getText().length());
        otp4Input.setSelection(otp4Input.getText().length());
    }
}