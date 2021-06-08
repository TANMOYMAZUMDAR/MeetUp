package com.example.demo_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.demo_project.databinding.ActivityFeedBackBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class FeedBackActivity extends AppCompatActivity {
  ActivityFeedBackBinding binding;
  FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFeedBackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final String[] rate = {""};
        getSupportActionBar().hide();
        changeStatusBarColor();
        database=FirebaseDatabase.getInstance();
        binding.rbStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating==0)
                {
                    binding.tvFeedback.setText("Very Dissatisfied");
                    rate[0] ="Very Dissatisfied";
                }
                else if(rating==1)
                {
                    binding.tvFeedback.setText("Dissatisfied");
                    rate[0] ="Dissatisfied";
                }
                else if(rating==2)
                {
                    binding.tvFeedback.setText("OK");
                    rate[0] ="OK";
                }
                else if(rating==3)
                {
                    binding.tvFeedback.setText("Good");
                    rate[0] ="Good";
                }
                else if(rating==4)
                {
                    binding.tvFeedback.setText("Satisfied");
                    rate[0]="Satisfied";
                }
                else if(rating==5)
                {
                    binding.tvFeedback.setText("Very Satisfied");
                    rate[0]="Very Satisfied";
                }
                else{

                }

            }


        });

        String feedback=binding.feedback.getText().toString();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FeedBackActivity.this,"Feedback Submitted Suceesfully",Toast.LENGTH_SHORT).show();
                database.getReference().child("Feedback").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("message").setValue(feedback);
                database.getReference().child("Feedback").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("rating").setValue(rate[0]);
                binding.feedback.setText("");
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