package hk.edu.cuhk.ie.iems5722;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static int roomID;

    private TextView title;
    private TextView chatroom;
    private ListView roomview;
    private String[] arrID;
    //private List<Room> rooms;
    private List<String> rooms;
    private ArrayAdapter<String> adapter;
    private ConnectionResult ConnectionResult;
    private Context context;
    private static final String TAG="chek";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //final LinearLayout layout2 = new LinearLayout(this);
        //layout2.setOrientation(LinearLayout.VERTICAL);
        context = getApplicationContext();

        //check device for Play Services APK.
        if(checkPlayServices()) {
            rooms = new ArrayList<String>();
            roomview = (ListView) findViewById(R.id.listView2);
            //异步请求聊天室信息
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rooms);
            //rooms.add("1");rooms.add("2");
            roomview.setAdapter(adapter);
            roomview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    roomID = Integer.parseInt(arrID[position]);
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ChatActivity.class);
                    startActivity(intent);

                }
            });
            Mytask mtask = new Mytask();
            mtask.execute();
        }
      //  chatroom =  findViewById(R.id.imageButton2);
       /* title= new TextView(this);
        title.setText("IEMS5722");
        title.setTextSize(30);
        title.setTextColor(Color.rgb(255, 255, 255));
        title.setBackgroundColor(Color.rgb(51 ,161 ,201));
        title.setGravity(Gravity.CENTER);
        layout2.addView(title);
        bts=new Button[10];
        for(int i=0;i<10;i++){
            bts[i] = new Button(this);
            bts[i].setBackgroundColor(Color.rgb(255 - i * 20, 215  , 0  ));
            bts[i].setText("a"+i);
            layout2.addView(bts[i]);
            bts[i].setVisibility(View.GONE);
        }
        setContentView(layout2);*/

        //跳转聊天界面

       /* for(int i=0;i<10;i++) {

            bts[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    roomID= v.getId();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
            });
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    private class Mytask extends AsyncTask<String,Integer,String>{

        String[] id;
        String[] name;
        @Override
        protected String doInBackground(String... params) {
            //后台获取聊天室信息
            Log.i("haohang","bbbbbbbbbbbbb");
            String chatinfoURL = "http://104.155.195.255/iems5722/get_chatrooms";
            String test = null;
            StringBuilder builder =new StringBuilder();
            try {
                URL url = new URL(chatinfoURL);
                HttpURLConnection connection =(HttpURLConnection)url.openConnection();
                connection.setConnectTimeout(6*1000);
                if (connection.getResponseCode() != 200)
                    throw new RuntimeException("请求url失败");

                InputStream input = connection.getInputStream();
                BufferedReader buff = new BufferedReader(new InputStreamReader(input));
                for(String s=buff.readLine();s!=null;s=buff.readLine())
                    builder.append(s);
                Log.i("JSON",builder.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject json= null;
            try {
                json = new JSONObject(builder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray array = null;
            try {
                array = json.getJSONArray("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int len = array.length();
            id=new String[len];
            name=new String[len];
            for(int i=0;i<len;i++){
                id[i]=array.optJSONObject(i).optString("id");
                name[i]=array.optJSONObject(i).optString("name");
            }
            arrID=id;
            return builder.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            int num = name.length;
            for(int i=0;i<num;i++){
                rooms.add(name[i]);
            }
            adapter.notifyDataSetChanged();//通知adapter更新数据



        }
    }

}
