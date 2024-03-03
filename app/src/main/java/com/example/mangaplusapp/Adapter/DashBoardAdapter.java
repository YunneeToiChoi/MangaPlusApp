package com.example.mangaplusapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mangaplusapp.Activity.Base.ChapterPdfActivity;
import com.example.mangaplusapp.Fragment.DashBoardMangaListFragment;
import com.example.mangaplusapp.R;
import com.example.mangaplusapp.databinding.ItemDashboardBinding;
import com.example.mangaplusapp.object.Category;
import com.example.mangaplusapp.object.Chapter;
import com.example.mangaplusapp.util.ActivityUtils;
import com.example.mangaplusapp.util.filter.FilterCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.DashBoardViewHolder> implements Filterable {
    private Context context;
    private List<Category> categoryList, filterList;
    private List<Chapter> chapterList;
    private ItemDashboardBinding binding;
    private FilterCategory filterCategory;
    public void setData(Context context, List<Category> categoryList){
        this.context = context;
        this.categoryList = categoryList;
        this.filterList = categoryList;
    }
    public DashBoardAdapter(){

    }
    public DashBoardAdapter(Context context, List<Chapter> chapterList){
        this.context = context;
        this.chapterList = chapterList;
    }
    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public DashBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemDashboardBinding.inflate(LayoutInflater.from(context), parent, false);
        return new DashBoardViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull DashBoardViewHolder holder, int position) {
        if(categoryList != null && !categoryList.isEmpty()){
            Category category = categoryList.get(position);// Take item in category list at present position

            holder.dashText.setText(category.getNAME_CATEGORY());
            holder.dashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure wanna delete this category ?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "Deleting...", Toast.LENGTH_LONG).show();
                                    deleteCategory(category);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCategoryDialog(category);
                }
            });
        }
        if(chapterList != null && !chapterList.isEmpty()){
            Chapter chapter = chapterList.get(position);
            holder.dashText.setText(chapter.getNAME_CHAPTER());
            holder.dashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure delete this chapter")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "Deleting...", Toast.LENGTH_LONG).show();
                                    deleteChapter(chapter);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.startNewActivity(context, ChapterPdfActivity.class,
                            "ID_CHAPTER", chapter.getID_CHAPTER(),
                            "NAME_CHAPTER", chapter.getNAME_CHAPTER(),
                            "ID_MANGA_CHAPTER", chapter.getID_MANGA_CHAPTER(),
                            "MANGA_CHAPTER", chapter.getMANGA_CHAPTER(),
                            "PDF_CHAPTER", chapter.getPDF_CHAPTER());
                }
            });
        }
    }
    private void deleteChapter(Chapter chapter) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chapters");
        String chapterId = chapter.getID_CHAPTER();
        reference.child(chapterId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Delete " + chapter.getNAME_CHAPTER() + " successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context," " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }
    private void showCategoryDialog(Category category) {
         DashBoardMangaListFragment dialogFragment = new DashBoardMangaListFragment(category);
         dialogFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), "category_dialog");
    }
    private void deleteCategory(Category category) {
        //get id from object
        String idCategory = category.getID_CATEGORY();
        //Attach to Firebase > Categories > ID_CATEGORY
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
        reference.child(idCategory)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Delete " + category.getNAME_CATEGORY() + " successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context," " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (categoryList != null){
            return categoryList.size();
        }
        else if (chapterList != null){
            return chapterList.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        if (filterCategory == null){
            filterCategory = new FilterCategory(filterList, this);
        }
        return filterCategory;
    }

    public static class DashBoardViewHolder extends RecyclerView.ViewHolder {
        TextView dashText;
        ImageButton dashButton;

        public DashBoardViewHolder(@NonNull View itemView) {
            super(itemView);
            dashText = (TextView) itemView.findViewById(R.id.itemDashText);
            dashButton = (ImageButton) itemView.findViewById(R.id.itemDashBtn);
        }
    }
}
