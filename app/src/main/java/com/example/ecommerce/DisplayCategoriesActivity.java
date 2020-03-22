package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import com.example.ecommerce.databinding.ActivityDisplayCategoriesBinding;


public class DisplayCategoriesActivity extends AppCompatActivity {
    //public String category;
    private ClickHandler clickHandler;
    ActivityDisplayCategoriesBinding activityDisplayCategoriesBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDisplayCategoriesBinding = DataBindingUtil.setContentView(this,R.layout.activity_display_categories);
        clickHandler = new ClickHandler(this);
        activityDisplayCategoriesBinding.setClickHandler(clickHandler);
    }
    public class ClickHandler{
        private Context context;
        public String category = getIntent().getStringExtra("category");
        public ClickHandler(Context context){
            this.context = context;
        }

    }
}
