package com.example.crown_doorlock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

public class ViewLogImage extends AppCompatActivity {

    private Bitmap bitmap;
    private String image;
    private Intent intent;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log_image);

        intent = getIntent();

        image = intent.getStringExtra("imagePreview");
        byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        imageView = (ImageView) findViewById(R.id.imagePreview);
        imageView.setImageBitmap(bitmap);
    }
}
