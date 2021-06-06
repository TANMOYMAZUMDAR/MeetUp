package com.example.demo_project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.demo_project.ChatAcitvity;
import com.example.demo_project.HomeActivity;
import com.example.demo_project.Models.Users;
import com.example.demo_project.R;
import com.example.demo_project.databinding.ActivityChatAcitvityBinding;
import com.example.demo_project.databinding.ShowUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static okhttp3.internal.http.HttpDate.format;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    ArrayList<Users> list;

    Context context;




    public UserAdapter(Context context, ArrayList<Users> list){
        this.list=list;
        this.context=context;

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.show_user,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
   Users users= list.get(position);
   Picasso.get().load(users.getProfilePicture()).placeholder(R.drawable.userpic).into(holder.binding.profileImage);


        final ImagePopup imagePopup = new ImagePopup(context);
        imagePopup.setWindowHeight(800); // Optional
        imagePopup.setWindowWidth(800); // Optional
        imagePopup.setBackgroundColor(Color.TRANSPARENT);  // Optional
        imagePopup.setFullScreen(false); // Optional
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional

        ImageView imageView = (ImageView) holder.binding.profileImage;
         if(users.getProfilePicture()!=null)
        imagePopup.initiatePopupWithPicasso(users.getProfilePicture());
         else
             imagePopup.initiatePopup(imageView.getDrawable());    // Load Image from Drawable


        holder.binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Initiate Popup view **/
                imagePopup.viewPopup();

            }
        });










   holder.binding.UserName.setText(users.getUserName());

   //Last Message
       String senderId= FirebaseAuth.getInstance().getUid();
        String senderRoom=senderId+users.getUserId();
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                      if(snapshot.exists()) {
                          String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                        long time;
                          if(snapshot.child("lastMsgTime").getValue(Long.class)!=null) {
                              time = snapshot.child("lastMsgTime").getValue(Long.class);
                                      SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                              holder.binding.Time.setText(dateFormat.format(new Date(time)));
                          }
                      holder.binding.LastMessage.setText(lastMsg);
                      }
                      else {
                          holder.binding.LastMessage.setText("Tap to chat");
                      }
                      }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatAcitvity.class);
                intent.putExtra("UserId",users.getUserId());
                intent.putExtra("ProfilePic",users.getProfilePicture());
                intent.putExtra("UserName",users.getUserName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

      ShowUserBinding binding;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
           binding=ShowUserBinding.bind(itemView);
        }
    }
}
