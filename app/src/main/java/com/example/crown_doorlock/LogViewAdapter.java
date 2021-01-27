package com.example.crown_doorlock;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
/*
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;*/

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class LogViewAdapter extends RecyclerView.Adapter<LogViewAdapter.ItemViewHolder> {

    private List<LogData> myList;
    private Context context;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference ref;

    public LogViewAdapter(List<LogData> myList, Context context) {
        this.myList = myList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_list, parent, false);

        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        holder.onBind(myList.get(position));

        final String imageName = myList.get(position).getImageName();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(Integer.toString(position) + " " + imageName);
                ref = storageRef.child("log/" + imageName + ".jpg");

                ref.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                        byte[] imageBytes = baos.toByteArray();
                        String image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                        Intent intent = new Intent(context, ViewLogImage.class);
                        intent.putExtra("imagePreview", image);

                        context.startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView nameView;
        private TextView timeView;
        private TextView successView;
        private ImageView imageView;
        private String imageName;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            timeView = itemView.findViewById(R.id.time);
            successView = itemView.findViewById(R.id.success);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void onBind(LogData data) {
            nameView.setText(data.getName());
            timeView.setText(data.getTime());
            successView.setText(data.getSuccess());
            imageName = data.getImageName();
            String tmp[] = imageName.split("_");
            if (tmp[1].equals("Known"))
                imageView.setBackgroundColor(Color.rgb(84, 191, 62));
            else if (tmp[1].equals("Danger"))
                imageView.setBackgroundColor(Color.rgb(197, 30, 30));
            else if (tmp[1].equals("Unknown"))
                imageView.setBackgroundColor(Color.rgb(97, 120, 211));
        }
    }
}
