package com.example.gethttp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// https://cig-rdlab.gitbook.io/android/lectures/lecture-1/simple-app
// https://coderlessons.com/articles/mobilnaia-razrabotka-articles/http-klient-android-get-post-zagruzka-vygruzka-mnogochastnyi-zapros
// https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-scenario.html

public class MainActivity extends AppCompatActivity {
    TextView text;
    Button button;
    Button light;
    String scenID = "yourScenID";
    String AuthID = "Bearer yourTOKEN";
    String devID = "yourDeviceID";
    EditText edtResp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        light = findViewById(R.id.Light);
        text = findViewById(R.id.textView);
        edtResp = (EditText) findViewById(R.id.edtResp);

    }
    public void handleClickAllOff(View view) {

        text.setText("Goodbye Alex!");
        String name = "AllOff";
        SendHttpRequestTask t = new SendHttpRequestTask();

        String[] params = new String[]{"https://api.iot.yandex.net/v1.0/scenarios/"+scenID+"/actions", name};
        t.execute(params);
    }
    public void handleClickLight(View view) {

        text.setText("Light!");
        String name = "LightOn";
        SendHttpRequestTask t = new SendHttpRequestTask();

        String[] params = new String[]{"https://api.iot.yandex.net/v1.0/devices/actions", name};
        t.execute(params);
    }
    public void handleClickLightOff(View view) {

        text.setText("Light!");
        String name = "LightOff";
        SendHttpRequestTask t = new SendHttpRequestTask();

        String[] params = new String[]{"https://api.iot.yandex.net/v1.0/devices/actions", name};
        t.execute(params);
    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String name = params[1];

            String data = sendHttpRequest(url, name);
            System.out.println("Data ["+data+"]");
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            edtResp.setText(result);
        }
    }
    private String sendHttpRequest(String url, String name) {
        StringBuffer buffer = new StringBuffer();
        try {
            String strToSend ="";
            HttpURLConnection httpConn = (HttpURLConnection)(new URL(url)).openConnection();
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setRequestProperty("Authorization", AuthID);
            if (name == "AllOff") {
                httpConn.setRequestMethod("GET");
                strToSend = name;
            }
            else if (name == "LightOn"){
                httpConn.setRequestMethod("POST");
                httpConn.setRequestProperty("Content-Type", "application/json");
                strToSend = "{ \"devices\": [ { \"id\": \""+devID+"\", \"actions\": [ { \"type\": \"devices.capabilities.on_off\", \"state\": { \"instance\": \"on\", \"value\": true } } ] } ]}";
            }
            else if (name == "LightOff"){
                httpConn.setRequestMethod("POST");
                httpConn.setRequestProperty("Content-Type", "application/json");
                strToSend = "{ \"devices\": [ { \"id\": \""+devID+"\", \"actions\": [ { \"type\": \"devices.capabilities.on_off\", \"state\": { \"instance\": \"on\", \"value\": false } } ] } ]}";
            }
            httpConn.getOutputStream().write((strToSend).getBytes());
            InputStream is = httpConn.getInputStream();
            byte[] b = new byte[1024];

            while (is.read(b) != -1)
                buffer.append(new String(b));
            httpConn.disconnect();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }

        return buffer.toString();
    }
}