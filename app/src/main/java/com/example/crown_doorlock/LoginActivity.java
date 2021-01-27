package com.example.crown_doorlock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private final String rootID = "crown";
    private final String rootPW = "1234";

    private EditText loginId;
    private EditText loginPW;
    private String id;
    private String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginId = (EditText)findViewById(R.id.loginID);
        loginPW = (EditText)findViewById(R.id.loginPW);
    }

    public void loginClick(View view) {
        id = loginId.getText().toString();
        pw = loginPW.getText().toString();

        if (id.equals(rootID) && pw.equals(rootPW)) {
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);

            startActivity(intent);
            finish();
        }
        else {
            loginId.setText("");
            loginPW.setText("");

            Toast.makeText(this, "틀렸습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
