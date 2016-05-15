package hk.edu.cuhk.ie.iems5722;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.app.ActionBar;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private SimpleDateFormat simpleDateFormat;
    private ListView msgListView;
    private EditText inputText;
    private ImageButton send;
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<Msg>();
    private GetMsg oldmsg;
    private Msg message;
    private int currentpage=1;
    private int totalpage=1;
    private SentMsg sent;
    private String whatIsay;
    private Button refresh;
    GetMsg getmsg2;
    private int lastVisibleIndex;
    private ListView moreView;
    int count=0;
    ActionBar actionBar;



    protected   MenuItem refreshItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        actionBar=getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i("roomID", "id=" + MainActivity.roomID);
        //获取聊天室原有信息
        oldmsg = new GetMsg();
        oldmsg.execute();
        //currentpage = 1;//显示第一页
       // ImageButton btn1 = (ImageButton) findViewById(R.id.imageButton2);
       // btn1.setOnClickListener(new View.OnClickListener() {

           // @Override
           // public void onClick(View v) {
             //   Intent intent = new Intent();
              //  intent.setClass(ChatActivity.this, MainActivity.class);
               // startActivity(intent);
          //  }
      //   });
       // refresh = (Button) findViewById(R.id.button2);
        //刷新数据
        //refresh.setOnClickListener(new View.OnClickListener() {

            //@Override
            //public void onClick(View v) {
               // adapter.clear();
                //msgListView.setAdapter(adapter);
               // currentpage=1;
               // GetMsg firstmsg2 = new GetMsg();
                //firstmsg2.execute();
            //}
        //});
        adapter = new MsgAdapter(ChatActivity.this, R.layout.msg_item, msgList);
        inputText = (EditText) findViewById(R.id.input_text);

        send = (ImageButton) findViewById(R.id.imageButton);
        msgListView = (ListView) findViewById(R.id.listView);
       // moreView = getLayoutInflater().inflate(R.layout.moredate, null);
        msgListView.setAdapter(adapter);
        //getmsg2 = new GetMsg();
        //滚动至底刷新数据
        msgListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean istop=false;//
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
// 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

// 判断滚动到顶部
                        if(msgListView.getFirstVisiblePosition() == 0){
                            Log.i("top","TOP");
                            //count++;
                             //if(count>100&&currentpage<totalpage){
                        currentpage++;
                            Log.i("currentpage",currentpage+"");
                        getmsg2 = new GetMsg();
                        getmsg2.execute();

                        }

                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });


        //发送数据
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                whatIsay = content;
                String name="test";


                long data = System.currentTimeMillis();
                simpleDateFormat = new SimpleDateFormat("HH:MM");
                String time = simpleDateFormat.format(data);
                if (content.equals("")||content.equals(null)) {
                }
                else{
                    content="User: "+name+"\r\n"+content;
                    Msg msg = new Msg(content, time, Msg.TYPE_SEND);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                    inputText.setText("");
                    sent = new SentMsg();
                    sent.execute();
                }
            }
        });
    }


    private class GetMsg extends AsyncTask<String, Integer, String> {
        private JSONArray jsonArray;

        @Override
        protected String doInBackground(String... params) {
            if(currentpage<=totalpage) {
                int temppage = currentpage;
                StringBuilder builder = new StringBuilder();
                String urlstr = "http://104.155.195.255/iems5722/get_messages?chatroom_id=";
                String chatroomid = MainActivity.roomID + "";
                builder.append(urlstr);
                builder.append(chatroomid);
                builder.append("&page="+temppage+"");
                Log.i("wwl",builder.toString());
                String realurl = builder.toString();
                try {
                    URL url = new URL(realurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6 * 1000);
                    if (conn.getResponseCode() != 200)
                        throw new RuntimeException("请求url失败");
                    InputStream input = conn.getInputStream();
                    BufferedReader buff = new BufferedReader(new InputStreamReader(input));
                    StringBuilder builder2 = new StringBuilder();
                    for (String s = buff.readLine(); s != null; s = buff.readLine())
                        builder2.append(s);
                    JSONObject json = new JSONObject(builder2.toString());
                    //检查json数据
                    Log.i("json",builder2.toString());
                    json = json.optJSONObject("data");
                    totalpage = json.optInt("total_pages");
                    Log.i("totalpage",totalpage+"");
                    JSONArray jsonarray1;
                    jsonarray1 = json.optJSONArray("messages");
                    jsonArray = jsonarray1;
                    Log.i("mmm", jsonarray1.optJSONObject(0).optString("message"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        protected void onPostExecute(String result) {
            //Log.i("message3", jsonArray.optJSONObject(0).optString("name"));

            for (int i = 0; i < this.jsonArray.length(); i++) {
                message = new Msg("User:" + this.jsonArray.optJSONObject(i).optString("name") + "\r\n" + this.jsonArray.optJSONObject(i).optString("message"),
                        this.jsonArray.optJSONObject(i).optString("timestamp"), Msg.TYPE_RECEIVED);
                msgList.add(message);
                //msgList
                adapter.notifyDataSetChanged();
                msgListView.setSelection(msgList.size());
            }
        }
    }
    //后台发送消息
    private class SentMsg extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            String request = "chatroom_id=" + MainActivity.roomID + "&user_id=1155073573&name=test&message=" + whatIsay;
            try {
                URL url = new URL("http://104.155.195.255/iems5722/send_message");
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setDoOutput(true);//使用 URL 连接进行输出
                httpConn.setDoInput(true);//使用 URL 连接进行输入
                httpConn.setUseCaches(false);//忽略缓存
                httpConn.setRequestMethod("POST");//设置URL请求方法
                byte[] requestStringBytes = request.getBytes();
                //建立输出流
                OutputStream outputStream = httpConn.getOutputStream();
                outputStream.write(requestStringBytes);
                outputStream.close();
                //获得响应状态
                int responseCode = httpConn.getResponseCode();
                if (HttpURLConnection.HTTP_OK == responseCode) {//连接成功

                    //当正确响应时处理数据
                    StringBuffer sb = new StringBuffer();

                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    for (String s = responseReader.readLine(); s != null; s = responseReader.readLine()) {
                        sb.append(s);
                    }
                    responseReader.close();
                    httpConn.disconnect();
                    Log.i("ans", sb.toString());
                }
            }
             catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.refresh_button:
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}