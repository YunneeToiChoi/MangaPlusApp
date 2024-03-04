package com.example.mangaplusapp.Activity.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mangaplusapp.Activity.Admin.DashBoardAdminActivity;
import com.example.mangaplusapp.Activity.Admin.MangaDetailAdminActivity;
import com.example.mangaplusapp.Activity.Base.BaseActivity;
import com.example.mangaplusapp.Adapter.ChapterAdapter;
import com.example.mangaplusapp.R;
import com.example.mangaplusapp.databinding.ActivityMangaDetailAdminBinding;
import com.example.mangaplusapp.databinding.ActivityMangaDetailBinding;
import com.example.mangaplusapp.object.Chapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MangaDetailActivity extends BaseActivity {

    ActivityMangaDetailBinding binding;
    private List<Chapter> chapterList = new ArrayList<>();
    // Khởi tạo adapter trước khi hiển thi
    private ChapterAdapter chapterAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMangaDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTextItem();
        loadChapters();
    }

    private void loadChapters() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chapters");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chapterList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Chapter chapter = ds.getValue(Chapter.class);
                    if (chapter != null && chapter.getID_MANGA_CHAPTER().equals(getIntent().getExtras().getString("ID_MANGA"))) {
                        chapterList.add(chapter);
                    }
                }
                //setup LayoutManager
                binding.mangaDetailRcv.setLayoutManager(new LinearLayoutManager(MangaDetailActivity.this,LinearLayoutManager.VERTICAL, false));
                //setup adapter
                chapterAdapter = new ChapterAdapter();
                chapterAdapter.setData(MangaDetailActivity.this, chapterList);
                //set adapter
                binding.mangaDetailRcv.setAdapter(chapterAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MangaDetailActivity.this, "The loading mangas was interrupted",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setTextItem() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        binding.mangaDetailDescription.setText(extras.getString("DESCRIPTION_MANGA"));
        binding.mangaDetailTitle.setText(extras.getString("NAME_MANGA"));
        Glide.with(binding.mangaDetailImg)
                .load(extras.getString("PICTURE_MANGA"))
                .into(binding.mangaDetailImg);

    }
    @Override
    public void onBackPressed() {
        // Xử lý sự kiện khi nút back được nhấn trong activity này
        // Ví dụ: Trở về màn hình trước đó hoặc thoát ứng dụng
        startActivity(new Intent(MangaDetailActivity.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }
}