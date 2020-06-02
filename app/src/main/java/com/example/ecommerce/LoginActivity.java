package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText InputPhoneNumber,InputPassword;
     private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView AdminLink,NotAdmin;
    private  String parentDbName="Users";
    private CheckBox checkBoxRemeberMe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        AdminLink=(TextView)findViewById(R.id.admin_panel_link);
        NotAdmin=(TextView)findViewById(R.id.not_admin_panel_link);
        LoginButton=(Button) findViewById(R.id.login_btn);

        InputPassword       =(EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber    =(EditText) findViewById(R.id.login_phone_number_input);

        loadingBar          = new ProgressDialog(this);

        checkBoxRemeberMe=(CheckBox)findViewById(R.id.remeber_me_chk);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginButton.setText("login As Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdmin.setVisibility(View.VISIBLE);
                parentDbName ="Admins";

            }
        });
        NotAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginButton.setText("login ");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdmin.setVisibility(View.INVISIBLE);
                parentDbName ="Users";

            }
        });
    }

    private void LoginUser()

    {
        String phone= InputPhoneNumber.getText().toString();
        String password= InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone))
         {

            Toast.makeText(this,"pkease write your phone Number",Toast.LENGTH_SHORT).show();
         }
          else if(TextUtils.isEmpty(password))
          {
              Toast.makeText(this,"pkease write your password",Toast.LENGTH_SHORT).show();
          }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            AllowAccessToAccount(phone, password);

        }


    }

    private void AllowAccessToAccount(final String phone, final String password) {

            if(checkBoxRemeberMe.isChecked())
            {
                Paper.book().write(Prevalent.UserphoneKey,phone);
                Paper.book().write(Prevalent.UserpasswordKey,password);
            }



        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData=dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            if(parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this,"welcome Admin You are logged successfull ",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent=new Intent(LoginActivity.this,AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this,"logged successfull ",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(intent);

                            }

                        }
                        else
                        {

                            Toast.makeText(LoginActivity.this,"password is not correct   ",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Account with this "+phone+"not exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this,"you need to create an account please ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }

}
