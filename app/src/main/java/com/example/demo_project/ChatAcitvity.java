package com.example.demo_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.demo_project.Adapter.ChatAdapter;
import com.example.demo_project.Models.MessagesModel;
import com.example.demo_project.databinding.ActivityChatAcitvityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ChatAcitvity extends AppCompatActivity {
ActivityChatAcitvityBinding binding;
ChatAdapter adapter;
ArrayList<MessagesModel> messages;
FirebaseDatabase database;
DatabaseReference reference;
   String senderRoom,recieverRoom;
FirebaseAuth mauth;
FirebaseStorage storage;
ProgressDialog dialog;
    String senderUid;
    String recieverUid;
    private byte encryptionKey[]={9,115,51,86,105,4,-31,-23,-68,88,17,20,3,-105,119,-53};
private Cipher cipher,decipher;
private SecretKeySpec secretKeySpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatAcitvityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        changeStatusBarColor();
   database = FirebaseDatabase.getInstance();
   reference = FirebaseDatabase.getInstance().getReference();
     storage=FirebaseStorage.getInstance();
     dialog=new ProgressDialog(this);
     dialog.setMessage("Uploading image....");
     dialog.setCancelable(false);
reference.keepSynced(true);


        getSupportActionBar().hide();

   senderUid= FirebaseAuth.getInstance().getUid();
   recieverUid=getIntent().getStringExtra("UserId");
        String userName=getIntent().getStringExtra("UserName");
        String ProfilePic=getIntent().getStringExtra("ProfilePic");

        try {
            cipher=Cipher.getInstance("AES");
            decipher=Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec=new SecretKeySpec(encryptionKey,"AES");


           senderRoom=senderUid+recieverUid;
           recieverRoom=recieverUid+senderUid;



        binding.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatAcitvity.this, "This featured would be Enabled soon", Toast.LENGTH_SHORT).show();
            }
        });

        binding.videCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatAcitvity.this, "This featured would be Enabled soon", Toast.LENGTH_SHORT).show();

            }
        });

        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatAcitvity.this, "This feature would be Enabled soon", Toast.LENGTH_SHORT).show();
            }
        });



       binding.name.setText(userName);
      Picasso.get().load(ProfilePic).placeholder(R.drawable.avatar).into(binding.profile);

      binding.backArrow.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             finish();
          }
      });

        database.getReference().child("presence").child(recieverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String status=snapshot.getValue(String.class);
                    if(!status.isEmpty())
                    {
                        if(status.equals("Offline")){
                            binding.status.setVisibility(View.GONE);
                        }
                        else
                        {
                            binding.status.setText(status);
                            binding.status.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });




      //Typing status


      binding.attachment.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             Intent intent=new Intent();
             intent.setAction(Intent.ACTION_GET_CONTENT);
             intent.setType("image/*");
             startActivityForResult(intent,25) ;
          }
      });

//Typing status
        final Handler handler=new Handler();
binding.messageBox.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }


    @Override
    public void afterTextChanged(Editable s) {
        database.getReference().child("presence").child(senderUid).setValue("typing..");
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(userStoppedTyping,1000);
    }
    Runnable userStoppedTyping =new Runnable(){

        @Override
        public void run() {
            database.getReference().child("presence").child(senderUid).setValue("Online");
        }
    };
});

      messages=new ArrayList<>();
      adapter=new ChatAdapter(messages,this,senderRoom,recieverRoom);
      binding.recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);



        database.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    MessagesModel message=dataSnapshot.getValue(MessagesModel.class);
                  message.setMessageId(dataSnapshot.getKey());
                    messages.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });





        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String msg= binding.messageBox.getText().toString();

               msg=AESEncryptionMethod(msg);

               Date date=new Date();
               MessagesModel message=new MessagesModel(msg,senderUid,date.getTime());

               binding.messageBox.setText("");

               String randomKey=database.getReference().push().getKey();

                HashMap<String,Object> lastMsgObj=new HashMap<>();
                try {
                    lastMsgObj.put("lastMsg",AESDecryptionMethod(message.getMessage()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                lastMsgObj.put("lastMsgTime",date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(recieverRoom).updateChildren(lastMsgObj);


                database.getReference().child("chats")
                       .child(senderRoom)
                       .child("messages")
                       .child(randomKey)
                       .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {

                       database.getReference().child("chats")
                       .child(recieverRoom)
                               .child("messages")
                               .child(randomKey)
                               .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {

                           }
                       });
                   }
               });
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==25)
        {
            if(data!=null)
            {
                if(data.getData()!=null)
                {
                    Uri selectedImage=data.getData();
                    Calendar calender=Calendar.getInstance();
                    StorageReference reference=storage.getReference().child("chats").child(calender.getTimeInMillis()+"");
                    dialog.show();
                   reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful())
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                     String filePath=uri.toString();
                                      String msg= binding.messageBox.getText().toString();

                                        msg=AESEncryptionMethod(msg);

             Date date=new Date();
               MessagesModel message=new MessagesModel(msg,senderUid,date.getTime());
               message.setMessage("photo");
               message.setImageUrl(filePath);
               binding.messageBox.setText("");

               String randomKey=database.getReference().push().getKey();

                                        HashMap<String,Object> lastMsgObj=new HashMap<>();
                                        try {
                                            lastMsgObj.put("lastMsg",AESDecryptionMethod(message.getMessage())); //decryption karna hai
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        lastMsgObj.put("lastMsgTime",date.getTime());

                                       database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(recieverRoom).updateChildren(lastMsgObj);


               database.getReference().child("chats")
                       .child(senderRoom)
                       .child("messages")
                       .child(randomKey)
                       .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       database.getReference().child("chats")
                       .child(recieverRoom)
                               .child("messages")
                               .child(randomKey)
                               .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {

                           }
                       });


                   }
               });
            }
        });

                                    }
                                });
                        }

                }
            }
        }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId=FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }




    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.colorDarkAccent));
        }
    }


public String AESEncryptionMethod(String string)
{
  byte[] stringByte=string.getBytes();
  byte[] encryptedByte=new byte[stringByte.length];

    try {
        cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        encryptedByte=cipher.doFinal(stringByte);

    } catch (InvalidKeyException e) {
        e.printStackTrace();
    } catch (BadPaddingException e) {
        e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
        e.printStackTrace();
    }

    String returnString=null;
    try {
        returnString=new String(encryptedByte,"ISO-8859-1");
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
    return returnString;

}

private String AESDecryptionMethod(String string) throws UnsupportedEncodingException
{

        byte[] EncryptedByte=string.getBytes("ISO-8859-1");
        String decryptedString=string;

        byte[] decryption;

    try {
        decipher.init(cipher.DECRYPT_MODE,secretKeySpec);
        decryption=decipher.doFinal(EncryptedByte);
        decryptedString=new String(decryption);

    } catch (InvalidKeyException e) {
        e.printStackTrace();
    } catch (BadPaddingException e) {
        e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
        e.printStackTrace();
    }

    return decryptedString;

}



}