package com.example.mangaplusapp.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mangaplusapp.Activity.User.MangaDetailActivity;
import com.example.mangaplusapp.Fragment.SearchFragment;
import com.example.mangaplusapp.R;
import com.example.mangaplusapp.databinding.ItemFavoriteBinding;
import com.example.mangaplusapp.object.Mangas;
import com.example.mangaplusapp.util.ActivityUtils;
import com.example.mangaplusapp.util.filter.FilterManga;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> implements Filterable {
    List<Mangas> mangasList, filterList;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    ItemFavoriteBinding binding;
    SearchFragment searchFragment;
    private FilterManga filterManga;
    public FavoriteAdapter(Context context){
        this.context = context;
    }

    public FavoriteAdapter(List<Mangas> mangasList, Context context) {
        this.mangasList = mangasList;
        this.context = context;
    }

    public FavoriteAdapter(List<Mangas> mangasList, Context context, SearchFragment searchFragment) {
        this.mangasList = mangasList;
        this.context = context;
        this.searchFragment = searchFragment;
    }

    public void setData(List<Mangas> mangasList){
        this.mangasList = mangasList;
        this.filterList = mangasList;
    }

    public void setFilterManga(List<Mangas> truyenTranhList) {this.mangasList = truyenTranhList;}
    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        binding = ItemFavoriteBinding.inflate(LayoutInflater.from(context), parent, false);
        return new FavoriteViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Mangas mangas = mangasList.get(position);
        setFavorite(mangas.getID_MANGA(), holder);
        holder.favoName.setText(mangas.getNAME_MANGA());
        String price = context.getString(R.string.PRICE,mangas.getPRICE_MANGA());
        if (Float.parseFloat(mangas.getPRICE_MANGA()) == 0) {
            holder.favoPrice.setText(R.string.priceFree);
        }
        else{
            holder.favoPrice.setText(price);
        }

        Glide.with(context)
                .load(mangas.getPICTURE_MANGA())
                .into(holder.favoImg);
        if (searchFragment != null){
            holder.favoButtonCard.setVisibility(View.INVISIBLE);
        }else {
            holder.favoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFavorite(mangas.getID_MANGA(), holder);
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startNewActivity(context, MangaDetailActivity.class,
                        "ID_MANGA", mangas.getID_MANGA(),
                        "NAME_MANGA", mangas.getNAME_MANGA(),
                        "PICTURE_MANGA", mangas.getPICTURE_MANGA(),
                        "DESCRIPTION_MANGA", mangas.getDESCRIPTION_MANGA(),
                        "PREMIUM_MANGA",String.valueOf(mangas.isPREMIUM_MANGA()),
                        "VIEW_MANGA", String.valueOf(mangas.getVIEW_MANGA()),
                        "BOUGHT_MANGA",String.valueOf(mangas.getBOUGHT_MANGA()),
                        "PRICE_MANGA", mangas.getPRICE_MANGA());
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mangasList != null) return mangasList.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        if(filterManga == null){
            filterManga = new FilterManga(filterList,this);
        }
        return filterManga;
    }

    protected void removeFromFavorite(String mangaIdToRemove, FavoriteViewHolder holder){
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context,R.string.isNotLogin, Toast.LENGTH_SHORT).show();
            return;
        }else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Favorites").child(mangaIdToRemove)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            holder.itemView.setVisibility(View.GONE);
                            Toast.makeText(context, R.string.removeFavoriteSuccess, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, R.string.removeFavoriteFail, Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void toggleFavorite(String mangaId, FavoriteViewHolder holder) {
        DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("Favorites");
        userFavoritesRef.child(mangaId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().getKey();
                        if (dataSnapshot.exists()) {
                            // Manga is already in favorites, remove it
                            removeFromFavorite(mangaId, holder);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void setFavorite(String mangaId, FavoriteViewHolder holder){
        DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("Favorites");
        userFavoritesRef.child(mangaId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.favoButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#C3662D")));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView favoImg;
        TextView favoName, favoPrice;
        ImageButton favoButton;
        CardView favoButtonCard;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            favoButton = (ImageButton) itemView.findViewById(R.id.itemFavoButton);
            favoImg = (ImageView) itemView.findViewById(R.id.itemFavoImg);
            favoName = (TextView) itemView.findViewById(R.id.itemFavoName);
            favoPrice = (TextView) itemView.findViewById(R.id.itemFavoPrice);
            favoButtonCard = (CardView) itemView.findViewById(R.id.itemFavoCardButton);
        }
    }
}
