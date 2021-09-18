package com.example.demo_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demo_project.Models.Users;
import com.example.demo_project.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class SignUpActivity extends AppCompatActivity {



  ActivitySignupBinding binding;
  private FirebaseAuth mauth;
  FirebaseDatabase database;
  DatabaseReference reference;
  ProgressDialog progreesDialog;
  SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

         changeStatusBarColor();
        mauth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
          reference=FirebaseDatabase.getInstance().getReference();
          reference.keepSynced(true);
        progreesDialog=new ProgressDialog(SignUpActivity.this);
        progreesDialog.setTitle("Creating Account");
        progreesDialog.setMessage("We are creating your account");
binding.accexist.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                            overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
                                        }
                                    });



binding.backKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });



binding.cirRegisterButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(binding.editTextUserName.getText().toString().isEmpty()){
            binding.editTextUserName.setError("Enter an username");
            return;
        }
        if(binding.editTextEmail.getText().toString().isEmpty()){
            binding.editTextEmail.setError("Enter email id");
            return;
        }
        if(binding.editTextPassword.getText().toString().isEmpty())
        {
            binding.editTextPassword.setError("Enter a password");
            return;
        }
      //  progreesDialog.show();
        mauth.createUserWithEmailAndPassword(binding.editTextEmail.getText().toString(), binding.editTextPassword.getText().toString()).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      // progreesDialog.dismiss();

                        if (task.isSuccessful()) {


                            Users user=new Users(binding.editTextUserName.getText().toString(),
                                    binding.editTextEmail.getText().toString(),binding.editTextPassword.getText().toString());


                            String id=task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(user);


                            startActivity(new Intent(SignUpActivity.this,AfterSignUpActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                            Toast.makeText(SignUpActivity.this, "Congratulations! Account Created.", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(SignUpActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
 );
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
