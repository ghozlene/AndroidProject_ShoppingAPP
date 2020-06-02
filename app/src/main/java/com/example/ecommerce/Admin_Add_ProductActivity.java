package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.SimpleTimeZone;

public class Admin_Add_ProductActivity extends AppCompatActivity {

    private  String CategoryName,Description,Price,Pname,saveCdate,saveCTime;
    private Button addnewprodbtn;
    private ImageView imageProduct;
    private EditText ProductName,ProductDisc,ProductPrice;
    private Uri imageUri;
    private  static  final int gallerypick=1;
    private  String prodRandomKey ,downloadImageURL;
    private StorageReference Prodc_image_REF;
    private DatabaseReference ProductRef;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__add__product);

        CategoryName=getIntent().getExtras().get("category").toString();
        Prodc_image_REF= FirebaseStorage.getInstance().getReference().child("Product Image");
        ProductRef=FirebaseDatabase.getInstance().getReference().child("products");
        addnewprodbtn=(Button) findViewById(R.id.add_new_prod);
        imageProduct=(ImageView)findViewById(R.id.select_prod_image);
        ProductName=(EditText)findViewById(R.id.prod_name);
        ProductDisc=(EditText)findViewById(R.id.prod_discription);
        ProductPrice=(EditText)findViewById(R.id.prod_price);

        loadingBar          = new ProgressDialog(this);


        imageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openGallery();
            }
        });
        addnewprodbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                validateProdData();
            }
        });
    }


    private void validateProdData()
    {

        Description=ProductDisc.getText().toString();
        Price=ProductPrice.getText().toString();
        Pname=ProductName.getText().toString();


        if(imageUri==null)
        {

            Toast.makeText(this,"Product needs Photo",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this,"write a product Desc",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this,"write a product Price",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this,"write a product Name",Toast.LENGTH_SHORT).show();

        }
        else
            StoreProdcInfo();
    }

    private void StoreProdcInfo() {



        loadingBar.setTitle("Adding New Products");
        loadingBar.setMessage("Please wait, while we are adding the new items.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar Calander=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("mmm,dd,yyyy");
        saveCdate=currentDate.format(Calander.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCTime=currentTime.format(Calander.getTime());
        prodRandomKey=saveCdate+saveCTime;
        final StorageReference filepath=Prodc_image_REF.child(imageUri.getLastPathSegment()+ prodRandomKey+".jpg");

        final UploadTask uploadtask=filepath.putFile(imageUri);
        uploadtask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message=e.toString();
                Toast.makeText(Admin_Add_ProductActivity.this,"Error"+message,Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(Admin_Add_ProductActivity.this,"Product Image upload with Success",Toast.LENGTH_SHORT).show();
                Task<Uri> urltask=uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful() )
                        {
                            throw task.getException();

                        }
                        downloadImageURL=filepath.getDownloadUrl().toString();
                        return  filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadImageURL=task.getResult().toString();
                            Toast.makeText(Admin_Add_ProductActivity.this,"Producte Image is saved in DataBase with success ",Toast.LENGTH_SHORT).show();
                            saveProdcINfoDatabase();
                        }
                    }
                });
            }
        });
    }

    private void saveProdcINfoDatabase() {


        HashMap<String,Object> porductMap =new HashMap<>();
        porductMap.put("pid",saveCdate);
        porductMap.put("Time",saveCTime);
        porductMap.put("Descr",Description);
        porductMap.put("ImageProd",downloadImageURL);
        porductMap.put("catagory",CategoryName);
        porductMap.put("Price",Price);
        porductMap.put("ProdName",Pname);

        ProductRef.child(prodRandomKey).updateChildren(porductMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            Intent intent=new Intent(Admin_Add_ProductActivity.this,AdminCategoryActivity.class);
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(Admin_Add_ProductActivity.this,"Product is already success ",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String mesg=task.getException().toString();
                            Toast.makeText(Admin_Add_ProductActivity.this,"Error "+mesg,Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }



    private void openGallery() {

        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,gallerypick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallerypick && resultCode==RESULT_OK && data !=null)
        {
            imageUri=data.getData();
            imageProduct.setImageURI(imageUri);

        }
    }
}
