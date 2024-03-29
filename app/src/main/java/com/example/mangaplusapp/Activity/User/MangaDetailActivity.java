package com.example.mangaplusapp.Activity.User;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import static java.security.AccessController.getContext;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.mangaplusapp.Activity.Base.BaseActivity;
import com.example.mangaplusapp.Activity.Base.ChapterPdfActivity;
import com.example.mangaplusapp.Adapter.ChapterAdapter;
import com.example.mangaplusapp.R;
import com.example.mangaplusapp.databinding.ActivityMangaDetailBinding;
import com.example.mangaplusapp.object.Chapters;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;


public class MangaDetailActivity extends BaseActivity {

    Intent intent;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    ActivityMangaDetailBinding binding;
    BiometricPrompt biometricPrompt;
    LinearLayout creditCardImg,momoImg;
    String mangaId, nameManga, mangaPicture, mangaDescription, mangaView, mangaPrice;
    int countChapter;
    private List<Chapters> chapterList = new ArrayList<>();
    // Khởi tạo adapter trước khi hiển thi
    private ChapterAdapter chapterAdapter ;
    private static SharedPreferences sharedPreferences;
    private Boolean mangaPremium;
    private boolean checkBiometric;

    public interface OnPurchasedMangaIdsLoadedListener {
        void onPurchasedMangaIdsLoaded(Boolean premium);
    }

    public interface OnLoadComplete{
        void onLoadComplete(int count);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMangaDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = this.getSharedPreferences("user_session", Context.MODE_PRIVATE);

        intent = getIntent();
        mangaPremium = Boolean.parseBoolean(intent.getStringExtra("PREMIUM_MANGA"));
        mangaId = intent.getStringExtra("ID_MANGA");
        nameManga = intent.getStringExtra("NAME_MANGA");
        mangaPicture = intent.getStringExtra("PICTURE_MANGA");
        mangaDescription = intent.getStringExtra("DESCRIPTION_MANGA");
        mangaView = intent.getStringExtra("VIEW_MANGA");
        mangaPrice = intent.getStringExtra("PRICE_MANGA");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        onClickEvent();
        setFavorite();
        loadPurchasedMangaIds(new OnPurchasedMangaIdsLoadedListener() {
            @Override
            public void onPurchasedMangaIdsLoaded(Boolean premium) {
                onClickPayment(premium);
            }
        });
        loadCountChapter(new OnLoadComplete() {
            @Override
            public void onLoadComplete(int count) {
                countChapter = count;
                setTextItem();
            }
        });
    }

    private void onClickPayment(Boolean isPremium){
        binding.BuyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBioMetricSpperted();
                ActionBioMetric();
                checkBiometric=sharedPreferences.getBoolean("keyBiometric",false);
                if (isPremium) {
                    if(checkBiometric)
                    {
                        ShowDialog();
                    }
                    else {
                        BiometricPrompt.PromptInfo.Builder promptinfo= dialogMetric();
                        promptinfo.setDeviceCredentialAllowed(true);
                        biometricPrompt.authenticate(promptinfo.build());
                    }
                }else {
                    DatabaseReference chapterRef = FirebaseDatabase.getInstance().getReference("Chapters");
                    chapterRef.orderByChild("ID_MANGA_CHAPTER").equalTo(mangaId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Chapters chapter = dataSnapshot.getValue(Chapters.class);
                                        if (chapter.getNAME_CHAPTER().contains("1")) {
                                            updateCountView(chapter);
                                            startNewActivityAndFinishCurrent(ChapterPdfActivity.class,
                                                    "ID_CHAPTER", chapter.getID_CHAPTER(),
                                                    "NAME_CHAPTER", chapter.getNAME_CHAPTER(),
                                                    "ID_MANGA_CHAPTER", chapter.getID_MANGA_CHAPTER(),
                                                    "MANGA_CHAPTER", chapter.getMANGA_CHAPTER(),
                                                    "PDF_CHAPTER", chapter.getPDF_CHAPTER());
                                            break;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
        });
    }

    private void updateCountView(Chapters chapter){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Mangas");
        reference.child(chapter.getID_MANGA_CHAPTER()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long currentCountView = snapshot.child("VIEW_MANGA").getValue(Long.class);
                Long newCountView = currentCountView != null ? currentCountView + 1 : 1;
                snapshot.getRef().child("VIEW_MANGA").setValue(newCountView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    protected void addToFavorite(){
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(this,R.string.isNotLogin, Toast.LENGTH_SHORT).show();
            return;
        }else {
            long timestamp = System.currentTimeMillis();
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("ID_MANGA", mangaId);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Favorites").child(mangaId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MangaDetailActivity.this, R.string.addFavoriteSuccess, Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    protected void removeFromFavorite(String mangaIdToRemove){
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(this,R.string.isNotLogin, Toast.LENGTH_SHORT).show();
            return;
        }else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Favorites").child(mangaIdToRemove)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(MangaDetailActivity.this, R.string.removeFavoriteSuccess, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MangaDetailActivity.this, R.string.removeFavoriteFail, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MangaDetailActivity.this, R.string.dataOccurred, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void toggleFavorite() {
        DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("Favorites");
        userFavoritesRef.child(mangaId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().getKey();
                        if (dataSnapshot.exists()) {
                            // Manga is already in favorites, remove it
                            binding.OverlayIconFavorite.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#42000000")));
                            binding.IconFavorite.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                            removeFromFavorite(mangaId);
                        } else {
                            // Manga is not in favorites, add it
                            binding.OverlayIconFavorite.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#42F44336")));
                            binding.IconFavorite.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                            addToFavorite();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void setFavorite(){
        DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("Favorites");
        userFavoritesRef.child(mangaId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            binding.OverlayIconFavorite.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#42F44336")));
                            binding.IconFavorite.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void loadPurchasedMangaIds(OnPurchasedMangaIdsLoadedListener listener) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("HistoryPayment")
                .child(mangaId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isPremium = true;
                if(snapshot.exists()){
                    binding.BuyBook.setText(R.string.Reading);
                    isPremium = false;
                }else if (!snapshot.exists() && mangaPremium){
                    binding.BuyBook.setText(R.string.Buy_book);
                }else isPremium = false;
                listener.onPurchasedMangaIdsLoaded(isPremium);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadCountChapter(OnLoadComplete listener){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chapters");
        reference.orderByChild("ID_MANGA_CHAPTER").equalTo(mangaId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) count++;

                        listener.onLoadComplete(count);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setTextItem() {
        binding.mangaDetailDescription.setText(mangaDescription);
        binding.mangaDetailTitle.setText(intent.getStringExtra("NAME_MANGA"));
        if (!isDestroyed()) {
            Glide.with(binding.mangaDetailImg)
                    .load(intent.getStringExtra("PICTURE_MANGA"))
                    .into(binding.mangaDetailImg);
        }
        binding.mangaDetailReadNumber.setText(mangaView);
        binding.mangaDetailChapterNumber.setText(String.valueOf(countChapter));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void ShowDialog()
    {
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_option_payment);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        creditCardImg = dialog.findViewById(R.id.creditCardPay);
        momoImg = dialog.findViewById(R.id.momoPay);
        creditCardImg.setOnClickListener(v->{
            dialog.dismiss();
            startNewActivity(PaymentStripeActivity.class,
                    "ID_MANGA", mangaId,
                    "PICTURE_MANGA", mangaPicture,
                    "NAME_MANGA",nameManga,
                    "PRICE_MANGA", mangaPrice);
            dialog.dismiss();
        });
        momoImg.setOnClickListener(v->{
            dialog.dismiss();
            startNewActivity(PaymentActivity.class,
                    "ID_MANGA", mangaId,
                    "PICTURE_MANGA", mangaPicture,
                    "NAME_MANGA",nameManga,
                    "PRICE_MANGA", mangaPrice);
            dialog.dismiss();
        });
    }

    private void onClickEvent(){
        binding.backDetailProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.ContentShowmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.mangaDetailDescription.getMaxLines() == Integer.MAX_VALUE) {
                    binding.mangaDetailDescription.setLineSpacing(10,1);
                    binding.mangaDetailDescription.setLetterSpacing(0.02f);
                    binding.mangaDetailDescription.setMaxLines(10);
                    binding.ContentShowmore.setText(R.string.Showmore);
                    binding.ContentShowmore.setTextColor(Color.BLACK);
                    binding.mangaDetailDescription.animate().translationY(0).alpha(0.3f).setDuration(500).start();
                } else {
                    binding.mangaDetailDescription.setMaxLines(Integer.MAX_VALUE);
                    binding.mangaDetailDescription.setLineSpacing(20,1);
                    binding.mangaDetailDescription.setLetterSpacing(0.035f);
                    binding.ContentShowmore.setText(R.string.Hideless);
                    binding.ContentShowmore.setTextColor(Color.GRAY);
                    binding.mangaDetailDescription.animate().translationY(8).alpha(0.7f).setDuration(500).start();
                }
            }
        });
        binding.mangaDetailFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });
    }

    private BiometricPrompt.PromptInfo.Builder dialogMetric()
    {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.Mangaplusneedstoconfirm))
                .setSubtitle(getString(R.string.Fingerprintverification));
    }

    private void checkBioMetricSpperted()
    {
        BiometricManager biometricManager = BiometricManager.from(this);
        String info=" ";
        switch (biometricManager.canAuthenticate(BIOMETRIC_WEAK | BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                info="App can authenticate using biometric.";
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info="No biometric features available on this device.";
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info="Biometric features are currently unavailable.";
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                info="Need register at least one finger print.";
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivity(enrollIntent);
                break;
        }
    }

    private  void ActionBioMetric()
    {
        Executor executor= ContextCompat.getMainExecutor(this);
        biometricPrompt=new BiometricPrompt(MangaDetailActivity.this,executor, new BiometricPrompt.AuthenticationCallback(){
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), R.string.bioError + errString.toString(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        R.string.bioSuccess, Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("keyBiometric",true);
                editor.apply();
                editor.commit();
                ShowDialog();
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), R.string.bioFail,
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}