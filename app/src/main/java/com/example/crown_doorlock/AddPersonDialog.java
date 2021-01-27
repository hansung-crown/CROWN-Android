package com.example.crown_doorlock;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AddPersonDialog extends DialogFragment {

    public static final String TAG_EVENT_DIALOG = "dialog_event";
    private RadioGroup radioGroup;
    private String group;
    private EditText who;
    private Button okBtn;
    private Button cancelBtn;
    OnMyDialogResult mDialogResult;

    public AddPersonDialog() {}

    public static AddPersonDialog getInstance() {
        AddPersonDialog addPersonDialog = new AddPersonDialog();

        return addPersonDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_person_dialog, container);

        okBtn = (Button) view.findViewById(R.id.okBtn);
        cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        who = (EditText) view.findViewById(R.id.who);

        radioGroup = (RadioGroup) view.findViewById(R.id.group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.knownGroup)
                    group = "Known";
                else if (i == R.id.dangerGroup)
                    group = "Danger";
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialogResult != null) {
                    mDialogResult.finish(group + "/" + who.getText().toString());
                }
                dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setCancelable(false);
        return view;
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(String result);
    }
}