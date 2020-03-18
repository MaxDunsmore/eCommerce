package com.example.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Interface.ItemClickListener;
import com.example.ecommerce.R;

public class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView textViewCategoryName;
    public ImageView imageViewCategory;
    public ItemClickListener listener;

    public CategoriesViewHolder(@NonNull View itemView) {
        super(itemView);

        imageViewCategory = itemView.findViewById(R.id.category_image);
        textViewCategoryName = itemView.findViewById(R.id.text_view_category);


    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(),true);
    }
}
