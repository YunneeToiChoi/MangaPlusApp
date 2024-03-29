package com.example.mangaplusapp.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mangaplusapp.Activity.Base.LoginActivity;
import com.example.mangaplusapp.Activity.User.ForgotControlActivity;
import com.example.mangaplusapp.Activity.User.MainActivity;
import com.example.mangaplusapp.Activity.User.RegisterActivity;
import com.example.mangaplusapp.Helper.ActionHelper.KeyBoardHelper;
import com.example.mangaplusapp.Helper.DBHelper.UserDBHelper;
import com.example.mangaplusapp.Helper.LoadHelper.LoadFragment;
import com.example.mangaplusapp.R;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChangeEmailFragment extends Fragment {
    AppCompatButton AcceptnewEmail;
    UserDBHelper db;
    EditText InputEmail;
    ImageButton Backbtn;
    LoadFragment fragmentHelper;
    String NewEmail;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public ChangeEmailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_changeemail, container, false);

        ScrollView mainLayout = root.findViewById(R.id.OverlayEditEmail);
        KeyBoardHelper.ActionRemoveKeyBoardForFragment(mainLayout, requireContext());
        AcceptnewEmail = root.findViewById(R.id.btnchangeNewEmail);
        InputEmail = root.findViewById(R.id.userNewNameTxt);
        Backbtn = root.findViewById(R.id.backEditEmailBtn);

        db = new UserDBHelper(getContext());

        preferences = getContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        editor=preferences.edit();

        BackToPro();
        AcceptnewEmail.setOnClickListener(v->{
            NewEmail=InputEmail.getText().toString();
                checkEmailExistsAndProceed(NewEmail);
        });
        return root;
    }

    private void BackToPro()
    {
        Backbtn.setOnClickListener(v->{
            Intent intent=new Intent(getContext(), MainActivity.class);
            intent.putExtra("BackToProfile", 1);
            startActivity(intent);
            getActivity().finish();
        });
    }

    private void checkEmailExistsAndProceed(final String email) {
        db.checkEmailExists(email, new UserDBHelper.userCheckFirebaseListener() {
            @Override
            public void onEmailCheckResult(boolean exists) {
                if (exists) {
                    Toast.makeText(getContext(), R.string.emailExists, Toast.LENGTH_SHORT).show();
                } else {
                    if (db.validEmail(email)) {
                        editor.putString("user_email", email);
                        editor.apply(); // apply session
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("KeyChangeEmail", true);
                        VerificationFragment fragment = new VerificationFragment();
                        fragment.setArguments(bundle);
                        fragmentHelper = new LoadFragment();
                        fragmentHelper.loadFragment(getParentFragmentManager(), fragment, false, R.id.editFmContainer);
                    } else {
                        Toast.makeText(getContext(), R.string.typeEmailValid, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
