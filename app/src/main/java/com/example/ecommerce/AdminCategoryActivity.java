package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView tshirts,sports,femalesglasess,sweathers;
    private ImageView glasses,hats,wallet,shoes;
    private ImageView headphones,laptop,watches,mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);


        tshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(AdminCategoryActivity.this,Admin_Add_ProductActivity.class);
                intent.putExtra("category","tshirts");
                startActivity(intent);

            }
        });


        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(AdminCategoryActivity.this,Admin_Add_ProductActivity.class);
                intent.putExtra("category","sports");
                startActivity(intent);

            }
        });





        femalesglasess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Intent intent= new Intent(AdminCategoryActivity.this,Admin_Add_ProductActivity.class);
                intent.putExtra("category","femalesglasess");
                startActivity(intent);

            }
        });




        tshirts=(ImageView) findViewById(R.id.t_shirts);
        sports=(ImageView) findViewById(R.id.sport_t_shirts);


        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(AdminCategoryActivity.this,Admin_Add_ProductActivity.class);
                intent.putExtra("category","glasses");
                startActivity(intent);

            }
        });


    }
}
