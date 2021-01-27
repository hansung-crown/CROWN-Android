package com.example.crown_doorlock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MonitoringControl extends Fragment {

    private MqttAndroidClient mqttAndroidClient;
    private WebView moniteringView;
    private Switch doorlock;
    private Switch speaker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_monitoring_control, container, false);

        doorlock = (Switch) view.findViewById(R.id.doorlockSwitch);
        doorlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (doorlock.isChecked()) {
                    try {
                        mqttAndroidClient.connect();
                        mqttAndroidClient.publish("crown/app/doorlock", new MqttMessage("open".getBytes()));
                    } catch (MqttException e) {
                    }
                }
            }
        });

        speaker = (Switch) view.findViewById(R.id.speakerSwitch);
        speaker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (speaker.isChecked()) {
                    try {
                        mqttAndroidClient.connect();
                        mqttAndroidClient.publish("crown/app/speaker", new MqttMessage("start".getBytes()));
                    } catch (MqttException e){
                    }
                }
            }
        });

        mqttAndroidClient = ((StartActivity)getActivity()).getMqttAndroidClient();
        mqttAndroidClient.setCallback(new MqttCallback() {  //클라이언트의 콜백을 처리하는부분
            @Override
            public void connectionLost(Throwable cause) { }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // topic 별로 나눠서 처리 가능
                if (topic.equals("crown/state/doorlock")) {
                    System.out.println("도어락 closed 받았다........");
                    String doorlockState = new String(message.getPayload());

                    if (doorlockState.equals("close")) {
                        doorlock.setChecked(false);
                    }
                }

                if (topic.equals("crown/state/speaker")) {
                    String speakerState = new String(message.getPayload());

                    if (speakerState.equals("end")) {
                        speaker.setChecked(false);
                    }
                }

            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) { }
        });

        moniteringView = (WebView)view.findViewById(R.id.webView);
        moniteringView.setPadding(0,0,0,0);
        moniteringView.getSettings().setBuiltInZoomControls(false);
        moniteringView.getSettings().setJavaScriptEnabled(true);
        moniteringView.getSettings().setLoadWithOverviewMode(true);
        moniteringView.getSettings().setUseWideViewPort(true);

        String url ="http://192.168.0.40:8090/stream/video.mjpeg";
        moniteringView.loadUrl(url);

        return view;
    }
}
