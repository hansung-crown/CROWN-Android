package com.example.crown_doorlock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static org.opencv.imgproc.Imgproc.INTER_AREA;
import static org.opencv.imgproc.Imgproc.resize;

public class ImageAdapter extends BaseAdapter {

    private static final String mDangerFILENAME = "myDangerProfile";
    private SharedPreferences mPref;

    private Activity activity;
    private ArrayList<Bitmap> ImageList;
    int size;
    private String userName;
    private String userGroupName;
    private boolean profileFlag = false;


    public ImageAdapter(Activity activity,  String userName, String userGroupName, int width) {
        this.activity = activity;
        ImageList = ((FaceDetect)FaceDetect.mContext).getImageList();
        this.userName = userName;
        this.userGroupName = userGroupName;
        size = width/3;

        if (userGroupName.equals("Known"))
            mPref = activity.getSharedPreferences(ViewKnownGroup.mKnownFILENAME, Context.MODE_PRIVATE);
        else if (userGroupName.equals("Danger"))
            mPref = activity.getSharedPreferences(mDangerFILENAME, Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return ImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return ImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageButton imageButton;

        if (convertView == null) {
            imageButton = new ImageButton(activity);
        } else {
            imageButton = (ImageButton)convertView;
        }

        imageButton.setLayoutParams(new GridView.LayoutParams(size-5, size-5));
        imageButton.setImageBitmap(ImageList.get(position));
        imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
        imageButton.setBackgroundColor(Color.TRANSPARENT);

        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final View dialogView = activity.getLayoutInflater().inflate(R.layout.image_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder((activity));
                builder.setTitle(" ");
                builder.setView(dialogView);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.imageRadioGroup);
                        int checkedId = radioGroup.getCheckedRadioButtonId();

                        if (checkedId == R.id.profile) {
                            saveProfile(ImageList.get(position));
                            Toast.makeText(activity.getApplicationContext(), "대표 사진으로 설정하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if (checkedId == R.id.delete) {
                            ImageList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(activity, "사진이 삭제되었습니다.", Toast.LENGTH_SHORT);
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                return true;
            }
        });

        return imageButton;
    }

    public void saveProfile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] bytes = baos.toByteArray();

        String image = Base64.encodeToString(bytes, Base64.DEFAULT);

        SharedPreferences.Editor editor = mPref.edit();

        editor.putString(userName, image);
        profileFlag = true;

        editor.commit();
    }

    public boolean getProfileFlag() {
        return profileFlag;
    }
}
