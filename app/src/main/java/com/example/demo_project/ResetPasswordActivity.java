package com.example.demo_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.demo_project.databinding.ActivityResetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
ActivityResetPasswordBinding binding;
FirebaseAuth mauth;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        changeStatusBarColor();
        Objects.requireNonNull(getSupportActionBar()).hide();


        mauth=FirebaseAuth.getInstance();
        binding.ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= Objects.requireNonNull(binding.sendMail.getText()).toString();
                if (binding.sendMail.getText().toString().isEmpty()) {
                    binding.sendMail.setError("Enter your email");
                }
                else
                {
                 mauth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull @NotNull Task<Void> task) {
                         if(task.isSuccessful())
                         {
                             Toast.makeText(ResetPasswordActivity.this,"We have sent you an Email.Please check",Toast.LENGTH_SHORT).show();
                             finish();
                             overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
                         }
                         else
                         {
                             String error= Objects.requireNonNull(task.getException()).getMessage();
                             Toast.makeText(ResetPasswordActivity.this,error,Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
                }
            }
        });
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