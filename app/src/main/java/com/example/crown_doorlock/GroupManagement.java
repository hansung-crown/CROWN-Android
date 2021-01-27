package com.example.crown_doorlock;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GroupManagement extends Fragment {

    private Button knownView;
    private Button dangerView;
    private Button addGroup;

    public GroupManagement() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_management, container, false);

        knownView = (Button) view.findViewById(R.id.known_view);
        knownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewKnownGroup.class);
                startActivity(intent);
            }
        });

        dangerView = (Button) view.findViewById(R.id.danger_view);
        dangerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewDangerGroup.class);
                startActivity(intent);
            }
        });

        addGroup = (Button) view.findViewById(R.id.add_group);
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPersonDialog myDialog = AddPersonDialog.getInstance();
                myDialog.show(getActivity().getSupportFragmentManager(), AddPersonDialog.TAG_EVENT_DIALOG);
                myDialog.setDialogResult(new AddPersonDialog.OnMyDialogResult() {
                    @Override
                    public void finish(String result) {
                        String group = result.split("/")[0];
                        String name = result.split("/")[1];

                        Intent intent = new Intent(getActivity(), FaceDetect.class);
                        intent.putExtra("groupName", group);
                        intent.putExtra("name", name);

                        startActivity(intent);
                    }
                });
            }
        });
        return view;
    }

}
