package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
 private Button joinNowButton,loginButton;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");







        joinNowButton=(Button) findViewById(R.id.main_join_now_btn);
        loginButton=(Button) findViewById(R.id.main_login_btn);
        loadingBar=new ProgressDialog(this);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });



        String UserPhoneKey= Paper.book().read(Prevalent.UserphoneKey);
        String UserpasswordKey= Paper.book().read(Prevalent.UserpasswordKey);
        if(UserPhoneKey !="" && UserpasswordKey !="")
        {
            if(!TextUtils.isEmpty(UserPhoneKey) &&!TextUtils.isEmpty(UserpasswordKey)  )
            {
                AllowAccess(UserPhoneKey,UserpasswordKey);
                loadingBar.setTitle("Alrady logged In");
                loadingBar.setMessage("Please wait............");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        }
    }

    private void AllowAccess(final String phone, final String password)
    {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("Users").child(phone).exists())
                {
                    Users usersData=dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {

                            Toast.makeText(MainActivity.this,"logged successfull ",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {

                            Toast.makeText(MainActivity.this,"password is not correct   ",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Account with this "+phone+"not exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this,"you need to create an account please ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });


    }
}
