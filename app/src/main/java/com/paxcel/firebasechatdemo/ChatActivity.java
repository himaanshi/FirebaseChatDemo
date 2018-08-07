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
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    ImageView iv_image, iv_video;
    VideoView vv;
    String userID;
    ProgressBar progressBar;
    RelativeLayout relativeLayoutImage, relativeLayoutVideo;
    ImageView imageView;
    private StorageReference riversRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        iv_image = findViewById(R.id.iv_image);
        iv_video = findViewById(R.id.iv_video);

        vv=findViewById(R.id.vv);

        imageView = findViewById(R.id.iv);
        progressBar = findViewById(R.id.progress_bar);
        relativeLayoutImage = findViewById(R.id.rl_image);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userID = bundle.getString("userId");
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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


    }

    private void uploadFile(String path, final boolean isImage) {

        File file3 = new File(path);
        String strFileName = file3.getName();
        Uri file = Uri.fromFile(file3);

        if (isImage) {
            riversRef = storageRef.child("Images").child(userID).child(strFileName);

        } else {
            riversRef = storageRef.child("Videos").child(userID).child(strFileName);

        }

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        if (isImage) {
                            Log.e("uploadFile", "onSuccess" + downloadUrl);
                            Glide.with(ChatActivity.this).load(downloadUrl).into(imageView);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ChatActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                        } else {

                            Log.e("uploadFile", "onSuccess" + downloadUrl);

                            imageView.setVisibility(View.GONE);
                            vv.setVisibility(View.VISIBLE);


                            MediaController mc = new MediaController(ChatActivity.this);
                            mc.setAnchorView(vv);
                            mc.setMediaPlayer(vv);
                            vv.setMediaController(mc);
                            vv.setVideoURI(downloadUrl);
                            vv.start();

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ChatActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();


                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ChatActivity.this, "Sorry try again", Toast.LENGTH_SHORT).show();

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
        int result = ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

        } else {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
                    progressBar.setVisibility(View.VISIBLE);
                    uploadFile(filePath, true);

                }
                break;

            case 12:
                if (resultCode == RESULT_OK) {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


//                    Uri uri = Uri.parse(filePath);
//
//                    MediaController mc = new MediaController(this);
//                    mc.setAnchorView(vv);
//                    mc.setMediaPlayer(vv);
//                    vv.setMediaController(mc);
//                    vv.setVideoURI(uri);
//                    vv.start();

                    Log.e(TAG, filePath);
                    progressBar.setVisibility(View.VISIBLE);
                    uploadFile(filePath, false);

                }
        }

    }


}
