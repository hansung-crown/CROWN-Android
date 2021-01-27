package com.example.crown_doorlock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UploadImage extends AppCompatActivity {

    private static final int MAX = 40;

    private ProgressDialog progressDialog; // 사진 업로딩 보여줌
    private ArrayList<Bitmap> ImageList;
    private String userName;
    private String userGroupName;
    private Button addBtn;
    private Button uploadBtn;
    private GridView gridView;
    private StorageReference mStorageRef;
    private ImageAdapter mAdapter;
    private MqttAndroidClient mqttAndroidClient;
    private int extraCnt;
    private int uploadCnt = 0;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        intent = getIntent();
//        ImageList = intent.getParcelableArrayListExtra("imageList");
        ImageList = ((FaceDetect)FaceDetect.mContext).getImageList();
        userName = intent.getStringExtra("name");
        userGroupName = intent.getStringExtra("groupName");

        progressDialog = new ProgressDialog(UploadImage.this);
        progressDialog.setMessage("Image Uploading ...");

        mqttAndroidClient = ((StartActivity)StartActivity.mContext).getMqttAndroidClient();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImageList.size() == MAX) {
                    Toast.makeText(getApplicationContext(), "사진을 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    extraCnt = MAX - ImageList.size();
                    // 추가로 사진 찍을 Activity로 이동
                    ((FaceDetect)FaceDetect.mContext).setImageList(ImageList);

                    Intent intent = new Intent(getApplicationContext(), Extra.class);
                    intent.putExtra("extraCnt", extraCnt);
                    intent.putExtra("userName", userName);
                    intent.putExtra("userGroupName", userGroupName);

                    startActivityForResult(intent, 1);
                }
            }
        });

        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImageList.size() == MAX) {
                    if (mAdapter.getProfileFlag()) {
                        progressDialog.show();
                        progressDialog.setCanceledOnTouchOutside(false);

                        StorageReference ImageFolder = mStorageRef.child(userName);

                        for (int i = 0; i < MAX; i++) {
                            Bitmap bitmap = ImageList.get(i);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                            byte[] data = baos.toByteArray();

                            StorageReference mImageRef = ImageFolder.child(userName + (i + 1) + ".jpg");
                            UploadTask uploadTask = mImageRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("kkankkan", "Firebase upload failed !");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    uploadCnt++;
                                    if (uploadCnt == MAX) {
                                        try {
                                            mqttAndroidClient.connect();
                                            mqttAndroidClient.publish("crown/addgroup", new MqttMessage((userGroupName + "/" + userName).getBytes()));
                                        } catch (MqttException e) {
                                        }
                                        uploadCnt = 0;
                                        progressDialog.dismiss();
                                        setResult(1);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "대표사진을 설정하세요.", Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    extraCnt = MAX-ImageList.size();
                    Toast.makeText(getApplicationContext(), "사진 " + Integer.toString(extraCnt) + "장이 부족합니다. '사진 추가' 버튼을 눌러 사진을 추가해주세요.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        int displayWidth = display.getWidth();

        gridView = (GridView) findViewById(R.id.gridView);
        mAdapter = new ImageAdapter(this, userName, userGroupName, displayWidth);
        gridView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            ImageList = ((FaceDetect)FaceDetect.mContext).getImageList();
            Log.e("kkankkan", Integer.toString(ImageList.size()));
            mAdapter.notifyDataSetChanged();
        }

    }
}
