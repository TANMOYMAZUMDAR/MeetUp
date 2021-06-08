package com.example.demo_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.VerifiedInputEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.demo_project.Models.Users;
import com.example.demo_project.databinding.ActivitySettingsBinding;
import com.example.demo_project.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
ActivitySettingsBinding binding;
FirebaseAuth mauth;
FirebaseStorage storage;
FirebaseDatabase database;
ProgressDialog dialog;
ProgressDialog p;
Uri pr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        changeStatusBarColor();


       database= FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setTitle("Uploading profile picture....");
        dialog.setCancelable(false);

        p=new ProgressDialog(this);
        p.setTitle("Uploading Status....");
        p.setCancelable(false);


      binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(SettingsActivity.this,PrivacyPolicy.class));
              overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
          }
      });

      binding.aboutInfo.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(SettingsActivity.this,AboutActivity.class));
              overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
          }
      });

      binding.help.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(SettingsActivity.this,FeedBackActivity.class));
              overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
          }
      });

        binding.backarrow.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
               overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
           }
       });



        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.show();
               String status=binding.etAbout.getText().toString();
               String username=binding.etUserName.getText().toString();

                HashMap<String,Object> obj=new HashMap<>();
                obj.put("userName",username);
                obj.put("about",status);

                database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).updateChildren(obj);
                Toast.makeText(SettingsActivity.this,"Profile Updated",Toast.LENGTH_SHORT);
            p.dismiss();
            }
        });

        database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Users user=snapshot.getValue(Users.class);
                assert user != null;
                Picasso.get().load(user.getProfilePicture()).placeholder(R.drawable.userpic).into(binding.imageProfile);

                binding.etAbout.setText(user.getAbout());
                binding.etUserName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

       binding.plus.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent();
               intent.setAction(Intent.ACTION_GET_CONTENT);
               intent.setType("image/*");
               startActivityForResult(intent,33);
           }
       });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(data!=null)
       {
           if(data.getData()!=null)
           {
              dialog.show();
              Uri sFile= data.getData();
              pr=sFile;
              binding.imageProfile.setImageURI(sFile);

              final StorageReference reference=storage.getReference().child("ProfilePicture").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
             reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                         database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("ProfilePicture").setValue(uri.toString());
                         Toast.makeText(SettingsActivity.this,"Profile Picture Updated",Toast.LENGTH_SHORT).show();

                        }
                    });
                     dialog.dismiss();

                 }
             });
           }
       }


    }


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.colorDarkAccent));
        }
    }

}