package com.example.crown_doorlock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Map;

public class ViewDangerGroup extends AppCompatActivity {

    public static final String mDangerFILENAME = "myDangerProfile";
    private SharedPreferences mPref;
    private Context context;

    private ArrayList<GroupData> mList = new ArrayList<GroupData>();
    private GridView gridView;
    private GroupAdapter mAdapter;

    private int position;

    public ViewDangerGroup() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_danger_group);
        context = this;

        mPref = getSharedPreferences(mDangerFILENAME, Context.MODE_PRIVATE);

        displayContacts();

        gridView = (GridView) findViewById(R.id.gridDanger);
        mAdapter = new GroupAdapter(this, mList);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                onStop();
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context);
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setPosition(position);
                        onRestart();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create().show();

                return true;
            }
        });

    }

    public void setPosition(int position){
        this.position = position;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        SharedPreferences.Editor editor = mPref.edit();
        String name = mAdapter.getItem(position).getName();
        Log.e("kkankkan",name);
        editor.remove(name);
        editor.commit();
        mList.clear();
        displayContacts();
        mAdapter.notifyDataSetChanged();
    }



    public void displayContacts() {

        Map<String, ?> keys = mPref.getAll();
        String names[] = new String[keys.size()];

        for (Map.Entry<String, ?> entry: keys.entrySet()) {
            String image = mPref.getString(entry.getKey(), "");
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Bitmap bitmapResize = Bitmap.createScaledBitmap(bitmap, 250,250, true);
            mList.add(new GroupData(bitmapResize, entry.getKey()));
        }

        Log.e("kkankkan", "복구 완료!");
    }
}
