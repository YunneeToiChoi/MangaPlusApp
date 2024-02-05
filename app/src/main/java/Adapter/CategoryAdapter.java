package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mangaplusapp.R;

import java.util.List;

import object.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{
    private Context context;
    private List<Category> categoryList;
    public CategoryAdapter(Context context){
        this.context = context;
    }
    public void SetData(List<Category> categoryList){
        this.categoryList = categoryList;
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position); // Take item in category list at present position
        if (category == null){
            return;
        }
        holder.categoryName.setText(category.getCategoryName());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,RecyclerView.HORIZONTAL, false);
        holder.rcvTruyenTranh.setLayoutManager(linearLayoutManager);// set form for recycleView truyentranh

        TruyenTranhAdapter truyenTranhAdapter = new TruyenTranhAdapter();
        truyenTranhAdapter.SetData(category.getTruyenTranhList());// set truyentranhList for adapter

        holder.rcvTruyenTranh.setAdapter(truyenTranhAdapter);
    }

    @Override
    public int getItemCount() {
        if(categoryList != null){
            return categoryList.size();
        }
        return 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder{
        TextView categoryName;
        RecyclerView rcvTruyenTranh;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.tv_name_category);
            rcvTruyenTranh = itemView.findViewById(R.id.rcv_truyentranh);
        }
    }
}
