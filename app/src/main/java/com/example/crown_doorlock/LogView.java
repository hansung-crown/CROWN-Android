package com.example.crown_doorlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.ArrayList;
import java.util.Map;

public class LogView extends Fragment {

    private static final String mLogFILENAME = "myLog";
    private final int MAX = 30;
    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;

    private ArrayList<String> logDataList = new ArrayList<>();
    private ArrayList<LogData> LogList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LogViewAdapter mAdapter;
    private MqttAndroidClient mqttAndroidClient;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_view, container, false);

        mPref = getActivity().getSharedPreferences(mLogFILENAME, Context.MODE_PRIVATE);
        editor = mPref.edit();

//        /* LogView가 말썽일 때 */
//        editor.clear();
//        editor.commit();

        for (int i=0; i<MAX; i++) {
            String imageName = mPref.getString("imageName" + i, "");
            String success = mPref.getString("success" + i, "");
            if (imageName != "") {
                LogData data = makeLogData(imageName);
                data.setSuccess(success);

                LogList.add(data);
            }
        }

        mqttAndroidClient = ((StartActivity)getActivity()).getMqttAndroidClient();
        mqttAndroidClient.setCallback(new MqttCallback() {  //클라이언트의 콜백을 처리하는부분
            @Override
            public void connectionLost(Throwable cause) { }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String name = "";
                String time = "";
                String imageName = "";

                // topic 별로 나눠서 처리 가능
                String msg = new String(message.getPayload());
                imageName = msg;
                System.out.println("log : " + imageName);

                LogData data = makeLogData(imageName);

                if (topic.equals("crown/gesture/success")) {
                    data.setSuccess("success");
                }
                else if (topic.equals("crown/gesture/fail")) {
                    data.setSuccess("fail");
                }
                LogList.add(0, data);
                mAdapter.notifyItemInserted(0);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) { }
        });

        recyclerView = view.findViewById(R.id.recyclerview_main_list);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new LogViewAdapter(LogList, (StartActivity)getContext());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    public LogData makeLogData(String imageName) {
        String tmp[] = imageName.split("_");
        String name = tmp[2];
        String time = tmp[0];

        return new LogData(name, splitTime(time), imageName);
    }

    public String splitTime(String time) {
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String date = time.substring(6, 8);
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        String second = time.substring(12);

        String realTime = year + "." + month + "." + date + " " + hour + ":" + minute + ":" + second;

        return realTime;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        editor.clear();
        editor.commit();

        for (int i=0; i<MAX; i++) {
            editor.putString("imageName" + i, LogList.get(i).getImageName());
            editor.putString("success" + i, LogList.get(i).getSuccess());
        }
        editor.commit();
    }
}
