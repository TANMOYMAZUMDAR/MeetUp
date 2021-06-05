package com.example.demo_project.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo_project.Models.MessagesModel;
import com.example.demo_project.R;
import com.example.demo_project.databinding.DeleteDialogBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<MessagesModel> messages;
    Context context;

    
    String senderoom;
    String recieverroom;
    int SENDER_VIEW_TYPE=1;
    int RECIEVER_VIEW_TYPE=2;

    public ChatAdapter(ArrayList<MessagesModel> messages, Context context,String senderoom,String recieverroom) {
        this.messages = messages;
        this.context = context;
        this.senderoom=senderoom;
        this.recieverroom=recieverroom;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciever, parent, false);
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
            FirebaseDatabase.getInstance().getReference().child("chats").child(senderoom).child("messages").child(message.getMessageId()).setValue(message);


            FirebaseDatabase.getInstance().getReference().child("chats").child(recieverroom).child("messages").child(message.getMessageId()).setValue(message);
                return true; // true is closing popup, false is requesting a new selection
        });








        if(holder.getClass()==SenderViewHolder.class) {
            ((SenderViewHolder) holder).SenderText.setText(message.getMessage());
            //set time using time strap

            if(message.getMessage().equals("photo"))
            {
                ((SenderViewHolder) holder).SenderImage.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).SenderText.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.avatar).into(((SenderViewHolder) holder).SenderImage);
            }

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
                                    .child("chats")
                                    .child(senderoom)
                                    .child("messages")
                                    .child(message.getMessageId()).setValue(message);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(recieverroom)
                                    .child("messages")
                                    .child(message.getMessageId()).setValue(message);
                            dialog.dismiss();
                            Toast.makeText(context,"Message Deleted for Everyone",Toast.LENGTH_SHORT).show();
                        }
                    });

                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderoom)
                                    .child("messages")
                                    .child(message.getMessageId()).setValue(null);
                            dialog.dismiss();
                            Toast.makeText(context,"Message Deleted",Toast.LENGTH_SHORT).show();
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



            if(message.getMessage().equals("photo"))
            {
                ((RecieverViewHolder) holder).RecieverImage.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).RecieverText.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.avatar).into(((RecieverViewHolder) holder).RecieverImage);
            }

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
                                    .child("chats")
                                    .child(senderoom)
                                    .child("messages")
                                    .child(message.getMessageId()).setValue(message);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(recieverroom)
                                    .child("messages")
                                    .child(message.getMessageId()).setValue(message);
                            dialog.dismiss();
                            Toast.makeText(context,"Message Deleted for Everyone",Toast.LENGTH_SHORT).show();
                        }
                    });

                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderoom)
                                    .child("messages")
                                    .child(message.getMessageId()).setValue(null);
                            dialog.dismiss();
                            Toast.makeText(context,"Message Deleted",Toast.LENGTH_SHORT).show();
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
        TextView RecieverText,RecieverTime;
        ImageView RecieverFeeling,RecieverImage;


        public RecieverViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        RecieverText=itemView.findViewById(R.id.recieverText);
            RecieverTime=itemView.findViewById(R.id.recieverTime);
        RecieverFeeling=itemView.findViewById(R.id.RecieverReactionImage);
        RecieverImage=itemView.findViewById(R.id.recieverImage);
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView SenderText, SenderTime;
        ImageView SenderFeeling,SenderImage;


        public SenderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            SenderText = itemView.findViewById(R.id.senderText);
            SenderTime = itemView.findViewById(R.id.senderTime);
            SenderFeeling=itemView.findViewById(R.id.SenderReactionImage);
           SenderImage=itemView.findViewById(R.id.senderImage);
        }
    }

}
