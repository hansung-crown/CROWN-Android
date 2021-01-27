package com.example.crown_doorlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class GroupAdapter extends BaseAdapter {

    private Context context;
    private List<GroupData> myList;
    private SharedPreferences mPref;


    public GroupAdapter(Context context, List<GroupData> myList) {
        this.context = context;
        this.myList = myList;
        mPref = context.getSharedPreferences(ViewKnownGroup.mKnownFILENAME, Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return myList.size();    // 그리드뷰에 출력할 목록 수
    }

    @Override
    public GroupData getItem(int position) {
        return myList.get(position);    // 아이템을 호출할 때 사용하는 메소드
    }

    @Override
    public long getItemId(int position) {
        return position;    // 아이템의 아이디를 구할 때 사용하는 메소드
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.group_data, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.profileImage);
        image.setImageBitmap(myList.get(position).getImage());
        TextView name = (TextView) convertView.findViewById(R.id.profileName);
        name.setText(myList.get(position).getName());

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        return convertView;
    }
}