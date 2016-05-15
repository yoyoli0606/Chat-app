package hk.edu.cuhk.ie.iems5722;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegistrationIntentService extends IntentService{

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private static String sendToken;

    public RegistrationIntentService() {
        super(TAG);
    }
    @Override
    public void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("9953213776",
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);
            sendRegistrationToServer(token);
            subscribeTopics(token);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        }catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        sendToken = token;
        SentMsg sentMsg = new SentMsg();
        sentMsg.execute();
    }

    private class SentMsg extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String request = "user_id=1155073573&message=" + sendToken;
            try {
                URL url = new URL("http://52.193.255.39/iems5722/submit_push_token");
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

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }


}
