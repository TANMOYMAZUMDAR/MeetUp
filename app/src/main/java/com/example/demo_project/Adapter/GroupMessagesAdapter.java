package com.example.demo_project.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo_project.Models.MessagesModel;
import com.example.demo_project.Models.Users;
import com.example.demo_project.R;
import com.example.demo_project.databinding.DeleteDialogBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GroupMessagesAdapter extends RecyclerView.Adapter {

    ArrayList<MessagesModel> messages;
    Context context;



    int SENDER_VIEW_TYPE=1;
    int RECIEVER_VIEW_TYPE=2;

    public GroupMessagesAdapter(ArrayList<MessagesModel> messages, Context context) {
        this.messages = messages;
        this.context = context;

    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_send_group, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciever_group, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessagesModel message=messages.get(position);
   if(message.getSenderId().equals(FirebaseAuth.getInstance().getUid()))
   {
       return SENDER_VIEW_TYPE;
   }
   else
   {
       return RECIEVER_VIEW_TYPE;
   }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        MessagesModel message=messages.get(position);

       //Reactions
        int[] reactions=new int[]{R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry};
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();



        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass()==SenderViewHolder.class) {
                ((SenderViewHolder) holder).SenderFeeling.setImageResource(reactions[pos]);
                ((SenderViewHolder) holder).SenderFeeling.setVisibility(View.VISIBLE);
            }
            else
            {
                    ((RecieverViewHolder) holder).RecieverFeeling.setImageResource(reactions[pos]);
                ((RecieverViewHolder) holder).RecieverFeeling.setVisibility(View.VISIBLE);
            }
            message.setFeeling(pos);
            FirebaseDatabase.getInstance().getReference().child("public").child(message.getMessageId()).setValue(message);



                return true; // true is closing popup, false is requesting a new selection
        });








        if(holder.getClass()==SenderViewHolder.class) {
            ((SenderViewHolder) holder).SenderText.setText(message.getMessage());
            //set time using time strap
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            ((SenderViewHolder)holder).SenderTime.setText(dateFormat.format(new Date(message.getTimestamp())));

            if(message.getMessage().equals("photo"))
            {
                ((SenderViewHolder) holder).SenderImage.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).SenderText.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.avatar).into(((SenderViewHolder) holder).SenderImage);

            }
            FirebaseDatabase.getInstance().getReference().child("Users").child(message.getSenderId())
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        Users user=snapshot.getValue(Users.class);
                        ((SenderViewHolder) holder).SenderName.setText("@"+user.getUserName());
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            if(message.getFeeling()>=0)
            {
              //  messagesModel.setFeeling(reactions[(int)messagesModel.getFeeling()]);
                ((SenderViewHolder) holder).SenderFeeling.setImageResource(reactions[(int)message.getFeeling()]);
                ((SenderViewHolder) holder).SenderFeeling.setVisibility(View.VISIBLE);
            }
            else
            {
                ((SenderViewHolder) holder).SenderFeeling.setVisibility(View.GONE);
            }



            ((SenderViewHolder) holder).SenderText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
               popup.onTouch(v,event);
                    return false;
                }
            });
            ((SenderViewHolder) holder).SenderImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });



            ((SenderViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
                    DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Delete Message")
                            .setView(binding.getRoot())
                            .create();

                    binding.everyone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            message.setMessage("This message is removed.");
                            message.setFeeling(-1);
                            FirebaseDatabase.getInstance().getReference()
                                    .child("public")
                                    .child(message.getMessageId()).setValue(message);


                            dialog.dismiss();
                        }
                    });

                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("public")
                                    .child(message.getMessageId()).setValue(null);
                            dialog.dismiss();
                        }
                    });

                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    return false;
                }
            });


        }
else
        {
            ((RecieverViewHolder)holder).RecieverText.setText(message.getMessage());
        //set time using time strap
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            ((RecieverViewHolder)holder).RecieverTime.setText(dateFormat.format(new Date(message.getTimestamp())));

            if(message.getMessage().equals("photo"))
            {
                ((RecieverViewHolder) holder).RecieverImage.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).RecieverText.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.avatar).into(((RecieverViewHolder) holder).RecieverImage);
            }

            FirebaseDatabase.getInstance().getReference().child("Users").child(message.getSenderId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                Users user=snapshot.getValue(Users.class);
                                ((RecieverViewHolder) holder).RecieverName.setText("@"+user.getUserName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });







            if(message.getFeeling()>=0)
            {
              //  messagesModel.setFeeling(reactions[(int)messagesModel.getFeeling()]);
                ((RecieverViewHolder) holder).RecieverFeeling.setImageResource(reactions[(int)message.getFeeling()]);
                ((RecieverViewHolder) holder).RecieverFeeling.setVisibility(View.VISIBLE);
            }
            else
            {
                ((RecieverViewHolder) holder).RecieverFeeling.setVisibility(View.GONE);
            }



            ((RecieverViewHolder) holder).RecieverText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });


            ((RecieverViewHolder) holder).RecieverImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });

            ((RecieverViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
                    DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Delete Message")
                            .setView(binding.getRoot())
                            .create();

                    binding.everyone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            message.setMessage("This message is removed.");
                            message.setFeeling(-1);
                            FirebaseDatabase.getInstance().getReference()
                                    .child("public")
                                    .child(message.getMessageId()).setValue(message);


                            dialog.dismiss();
                        }
                    });

                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("public")
                                    .child(message.getMessageId()).setValue(null);
                            dialog.dismiss();
                        }
                    });

                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    return false;
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        TextView RecieverText,RecieverTime,RecieverName;
        ImageView RecieverFeeling,RecieverImage;



        public RecieverViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        RecieverText=itemView.findViewById(R.id.recieverText);
            RecieverTime=itemView.findViewById(R.id.recieverTime);
        RecieverFeeling=itemView.findViewById(R.id.RecieverReactionImage);
        RecieverImage=itemView.findViewById(R.id.recieverImage);
        RecieverName=itemView.findViewById(R.id.recieverName);
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView SenderText, SenderTime,SenderName;
        ImageView SenderFeeling,SenderImage;


        public SenderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            SenderText = itemView.findViewById(R.id.senderText);
            SenderTime = itemView.findViewById(R.id.senderTime);
            SenderFeeling=itemView.findViewById(R.id.SenderReactionImage);
           SenderImage=itemView.findViewById(R.id.senderImage);
           SenderName=itemView.findViewById(R.id.SenderName);
        }
    }

}
