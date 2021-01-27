package com.example.crown_doorlock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String mFILENAME = "mySettings";
    private SharedPreferences mPref;
    private TabPageAdapter tabPagerAdapter;

    public static Context mContext;

    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private Switch drawerSwitch;
    private Spinner drawerSpinner;
    private String[] spinnerArray;

    // Mqtt
    private MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mPref = getSharedPreferences(mFILENAME, Context.MODE_PRIVATE);


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.e("kkankkan", token);
                    }
                });


        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        ViewPager pager = (ViewPager)findViewById(R.id.viewpager);

        tabPagerAdapter = new TabPageAdapter(getSupportFragmentManager());
        pager.setAdapter(tabPagerAdapter);
        tabLayout.setupWithViewPager(pager);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final String fToken = FirebaseInstanceId.getInstance().getToken(); // fcm 받을 때 필요한 device id

        mqttAndroidClient = new MqttAndroidClient(this, "tcp://192.168.0.40:1883", MqttClient.generateClientId());
        try {
            IMqttToken token = mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // when connect success
                    Log.e("Connect_success", "Success");
                    try {
                        mqttAndroidClient.subscribe("crown/gesture/success", 0);
                        mqttAndroidClient.subscribe("crown/gesture/fail", 0);
                        mqttAndroidClient.subscribe("crown/warning/danger", 0);
                        mqttAndroidClient.subscribe("crown/warning/unknown", 0);
                        mqttAndroidClient.subscribe("crown/state/doorlock", 0);
                        mqttAndroidClient.subscribe("crown/state/speaker", 0);
                        mqttAndroidClient.publish("crown/token", new MqttMessage(fToken.getBytes()));
                    } catch (MqttException e) { e.printStackTrace(); }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {   //연결에 실패한경우
                    Log.e("connect_fail", "Failure " + exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        drawerSwitch = (Switch) navigationView.getMenu().findItem(R.id.nav_alarm).getActionView().findViewById(R.id.alarmSwitch);
        drawerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MyFirebaseMessagingService.setFlag("on");
                } else {
                    MyFirebaseMessagingService.setFlag("off");
                }
            }
        });

        Button drawerButton112 = (Button) navigationView.getMenu().findItem(R.id.nav_report).getActionView().findViewById(R.id.report112);
        drawerButton112.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:112")));
            }
        });

        Button drawerButton119 = (Button) navigationView.getMenu().findItem(R.id.nav_report).getActionView().findViewById(R.id.report119);
        drawerButton119.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:119")));
            }
        });

        drawerSpinner = (Spinner) navigationView.getMenu().findItem(R.id.nav_gesture).getActionView().findViewById(R.id.gestureSpinner);
        spinnerArray = getResources().getStringArray(R.array.gestureArray);

        displayContacts();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_alarm) {}
        else if (id == R.id.nav_report) {}
        else if (id == R.id.nav_gesture) {}

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewpager, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public MqttAndroidClient getMqttAndroidClient() {
        return mqttAndroidClient;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        boolean switchState = drawerSwitch.isChecked();
        int spinnerState = drawerSpinner.getSelectedItemPosition();

        SharedPreferences.Editor editor = mPref.edit();

        editor.putBoolean("drawerSwitchState", switchState);
        editor.putInt("drawerSpinnerState", spinnerState);

        editor.commit();
    }

    public void displayContacts() {

        boolean switchState = mPref.getBoolean("drawerSwitchState", false);
        drawerSwitch.setChecked(switchState);

        int spinnerState = mPref.getInt("drawerSpinnerState", 0);
        drawerSpinner.setSelection(spinnerState);
    }

    public void mOnClick(View view) {
        if (view.getId() == R.id.spinnerBtn) {
            int position = drawerSpinner.getSelectedItemPosition();
            String gesture = spinnerArray[position];

            Toast.makeText(this, gesture + " selected !", Toast.LENGTH_SHORT).show();

            // 서버한테 MQTT 보내기
            try {
                mqttAndroidClient.connect();
                mqttAndroidClient.publish("crown/gesture/key", new MqttMessage(gesture.getBytes()));
            } catch (MqttException e) {}
        }
    }
}
