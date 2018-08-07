package com.paxcel.firebasechatdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class Chat extends AppCompatActivity {
    private static final String TAG = "Chat";
    private static final int PERMISSION_REQUEST_CODE = 1;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton, iv_send;
    EditText messageArea;
    ScrollView scrollView;
    ProgressBar progressBar;
    ImageView iv_image, iv_video;
    String userNumber, userId, myNumber, myId;
    DatabaseReference reference1, reference2;
    LinearLayout linearLayout;
    private StorageReference riversRef;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        iv_send = findViewById(R.id.iv_send);
        //  progressBar = findViewById(R.id.progress_bar);
        messageArea = (EditText) findViewById(R.id.messageArea);
        linearLayout = findViewById(R.id.ll);

        iv_image = findViewById(R.id.iv_image);
        iv_video = findViewById(R.id.iv_video);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        myId = Constants.MY_ID;
        myNumber = Constants.MY_NUMBER;

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            userId = bundle.getString("userId");
            userNumber = bundle.getString("number");
        }
        Log.e("number", myNumber);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        reference1 = FirebaseDatabase.getInstance().getReference().child("Chats/" + myId + "_" + userId);
        reference2 = FirebaseDatabase.getInstance().getReference().child("Chats/" + userId + "_" + myId);

        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("number", myNumber);
                    map.put("type", "text");
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        Intent i = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);

                        final int ACTIVITY_SELECT_IMAGE = 1234;
                        startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);

                    } else {
                        requestPermission(); // Code for permission
                    }
                }
            }
        });

        iv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        Intent i = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                        Intent intent = new Intent();
                        intent.setType("video/*");
                        intent.setAction(Intent.ACTION_PICK);


                        final int ACTIVITY_SELECT_VIDEO = 12;
                        startActivityForResult(intent, ACTIVITY_SELECT_VIDEO);

                    } else {
                        requestPermission(); // Code for permission
                    }
                }
            }
        });


        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, String> map = (HashMap) dataSnapshot.getValue();
                String message = map.get("message");
                String number = map.get("number");
                String type = map.get("type");

                Log.e(TAG, message + "number" + number + "type" + type);

                if (type.equalsIgnoreCase("text")) {

                    if (number.equals(Constants.MY_NUMBER)) {
                        addMessageBox("You\n" + message, 2);
                    } else {
                        addMessageBox(userNumber + "\n" + message, 1);
                    }
                } else if (type.equalsIgnoreCase("image")) {
                    if (number.equals(Constants.MY_NUMBER)) {
                        addImageView(message, 2);
                    } else {
                        addImageView(message, 1);
                    }
                } else {
                    if (number.equals(Constants.MY_NUMBER)) {
                        addVideoView(message, 2);
                    } else {
                        addVideoView(message, 1);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        } else {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void addImageView(String downloadUrl, int type) {


        ImageView imageView = new ImageView(Chat.this);
        LinearLayout.LayoutParams lp2 =
                new LinearLayout.LayoutParams(400, 300);
        lp2.weight = 1.0f;
        imageView.setLayoutParams(lp2);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setPadding(8,8,8,8);

        if (type == 1) {
            lp2.gravity = Gravity.LEFT;
        } else {
            lp2.gravity = Gravity.RIGHT;
        }

        layout.addView(imageView);
        Glide.with(Chat.this).load(downloadUrl).into(imageView);
        scrollView.fullScroll(View.FOCUS_DOWN);

    }

    private void addVideoView(String downloadUrl, int type) {

        Uri uri = Uri.parse(downloadUrl);

        VideoView videoView = new VideoView(Chat.this);
        LinearLayout.LayoutParams lp2 =
                new LinearLayout.LayoutParams(400, 300);
        lp2.weight = 1.0f;
        videoView.setLayoutParams(lp2);

        MediaController mc = new MediaController(Chat.this);
        mc.setAnchorView(videoView);
        mc.setMediaPlayer(videoView);
        videoView.setMediaController(mc);
        videoView.setVideoURI(uri);
        videoView.start();

        if (type == 1) {
            lp2.gravity = Gravity.LEFT;
        } else {
            lp2.gravity = Gravity.RIGHT;
        }
        layout.addView(videoView);
        scrollView.fullScroll(View.FOCUS_DOWN);

    }

    private void uploadFile(String path, final boolean isImage) {

        File file = new File(path);
        String strFileName = file.getName();
        Uri uriFile = Uri.fromFile(file);

        if (isImage) {
            riversRef = storageRef.child("Images").child(myId + "_" + userId).child(strFileName);

        } else {
            riversRef = storageRef.child("Videos").child(myId + "_" + userId).child(strFileName);

        }

        riversRef.putFile(uriFile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        if (isImage) {

                            Log.e("uploadFile", "onSuccess" + downloadUrl);

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("message", "" + downloadUrl);
                            map.put("number", myNumber);
                            map.put("type", "image");

                            reference1.push().setValue(map);
                            reference2.push().setValue(map);

                            //addImageView("" + downloadUrl,2);
                            Toast.makeText(Chat.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                        } else {

                            Log.e("uploadFile", "onSuccess" + downloadUrl);

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("message", "" + downloadUrl);
                            map.put("number", myNumber);
                            map.put("type", "video");

                            reference1.push().setValue(map);
                            reference2.push().setValue(map);

                            // addVideoView(""+downloadUrl, 2);
                            Toast.makeText(Chat.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();


                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Toast.makeText(Chat.this, "Sorry try again", Toast.LENGTH_SHORT).show();
                        Log.e("uploadFile", "onFailure" + exception);

                    }
                });
    }

    private void downloadFile() {
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");

            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            Log.e("downloadFile", "onSuccess");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Log.e("downloadFile", "onFailure");

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Chat.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Chat.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(Chat.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

        } else {
            ActivityCompat.requestPermissions(Chat.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");

                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    final int ACTIVITY_SELECT_IMAGE = 1234;
                    startActivityForResult(i, ACTIVITY_SELECT_IMAGE);

                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1234:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    Log.e(TAG, filePath);

                    uploadFile(filePath, true);

                }
                break;

            case 12:
                if (resultCode == RESULT_OK) {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Log.e(TAG, "" + selectedImage);
                    Cursor cursor2 = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                    if (cursor2 != null) {

                        cursor2.moveToFirst();

                        int columnIndex = cursor2.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor2.getString(columnIndex);
                        cursor2.close();

                        Log.e(TAG, filePath);
                        uploadFile(filePath, false);
                    }

                }
        }

    }

}