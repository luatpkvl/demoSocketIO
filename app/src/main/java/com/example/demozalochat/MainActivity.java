package com.example.demozalochat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Gson gson = new Gson();
    TextView tvtest;
    ArrayAdapter adapter;
    ArrayAdapter chatAdapter;
    EditText edtusername;
    Button btnok;
    Button btndis;
    Button btnChat;
    ListView listUser;
    List<String> mangusername;
    List<String> mangChat;
    String state = "";
    private Socket mSocket;
    {
        try{
            mSocket = IO.socket("http://192.168.1.4:3000");
        }catch (URISyntaxException e){}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.connect();
        anhXa();
        btnok.setOnClickListener(this);
        mSocket.on("server-send-state-ok",onNewMessage);
    }

    private void anhXa() {
        tvtest = findViewById(R.id.tvtest);
        edtusername = findViewById(R.id.edtusername);
        btnok = findViewById(R.id.btnsend);
        btnChat = findViewById(R.id.btnchat);
        btndis = findViewById(R.id.btndisconnect);
        listUser = findViewById(R.id.listuser);
        mangusername = new ArrayList<>();
        mangChat = new ArrayList<>();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnchat:
                edtusername.setText("");
                break;
                case R.id.btnsend:
                    state = "server-response-check";
                    mSocket.emit("user-nhap-username",edtusername.getText().toString());
                    edtusername.setText("");
                    break;
        }
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                        switch (state){
                            case "server-response-check":
                                receive_state(data);
                                break;
                        }
                }
            });
        }
    };

    private void receive_state(JSONObject data) {
        Receive receive = gson.fromJson(String.valueOf(data),Receive.class);
        String tester = gson.toJson(receive);
        Log.e("ok",receive.getState());
        int i = Integer.parseInt(receive.getState());
        tvtest.setText(i+"");
        if(i == 1){
            Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_SHORT).show();
        }else if(i == 0){
            Toast.makeText(getApplicationContext(),"nôô",Toast.LENGTH_SHORT).show();
        }
    }
}
