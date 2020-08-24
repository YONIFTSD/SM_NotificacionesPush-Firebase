package com.example.notificacionespushfirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.notificacionespushfirebase.util.Adapter;
import com.example.notificacionespushfirebase.util.Config;
import com.example.notificacionespushfirebase.model.Noticia;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    BroadcastReceiver broadcastReceiver;

    RecyclerView recyclerView;
    private ArrayList<Noticia> news;

    private String messageFB = "";
    private String regID = "";
    private String TOKEN_TO = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String nuevoToken = instanceIdResult.getToken();


                almacenarPreferencias(nuevoToken);
            }
        });

        setNews();
        initRecyclerView();

        broadcastReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Config.REGISTRATION_COMPLETE)){
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    //mostrar ID
                    mostrarFirebaseId();
                }else if(intent.getAction().equals(Config.PUSH_NOTIFICATION)){
                    String mensaje = intent.getStringExtra("mensaje");
                    
                    messageFB = mensaje;
                }
            }
        };
        mostrarFirebaseId();
    }

    private void almacenarPreferencias(String token){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("REGID", token);
        editor.commit();
        TOKEN_TO = token;
    }

    private void mostrarFirebaseId(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = sharedPreferences.getString("REGID", null);
        Log.d(TAG, " Firebase Id: " + regId);
        if(!TextUtils.isEmpty(regId)){
            regID = "Firebase ID: " + regId;
        }else{
            regID = "No existe una respuesta de Firebase aún";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        //clearNotifycation
        clearNotification();

    }

    public void clearNotification(){
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }


    private void initRecyclerView() {
        recyclerView = findViewById(R.id.news_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Adapter newsAdapter = new Adapter(news);
        newsAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onSendNotificationClick(int position) {
                Noticia obj = news.get(position);
                sendNotification(obj);
            }
        });
        recyclerView.setAdapter(newsAdapter);
    }

    private void setNews(){

        news = new ArrayList<>();
        news.add(new Noticia(1,
                "Asciende a 27.663 la cifra de muertes y 594.326 los contagios de COVID-19 a nivel nacional",
                "El Ministerio de Salud confirmó que en las últimas 24 horas se registraron un total de 210 muertes causadas por el nuevo coronavirus. Esta semana inició investigación de al menos 10.000 fallecimientos más por COVID-19.",
                "https://larepublica.pe/resizer/GMNOy5DvVMQk5rO9556Gy5rP7dw=/646x380/top/smart/cloudfront-us-east-1.images.arcpublishing.com/gruporepublica/FZPFD5RSABERPG3SSEROEUYF64.JPG" ));


        news.add(new Noticia(2,
                "Tragedia en Los Olivos: 13 personas fallecieron en discoteca durante intervención policial",
                "23 jóvenes quedaron detenidos y 4 heridos fueron trasladados a una clínica local. Dueños del local organizaron evento pese al horario de toque de queda."
                ,"https://larepublica.pe/resizer/6iiDTBYGJoGMNj5IQPKTbK3sssQ=/646x380/top/smart/cloudfront-us-east-1.images.arcpublishing.com/gruporepublica/ZW7U5QHHUZAH5JT67S3Z3XPWWQ.png" ));


        news.add(new Noticia(3,
                "Bayern Múnich se proclamó campeón de la Champions League frente al PSG",
                "PSG perdió su primera final de Champions League ante el Bayern Múnich en el Estadio Da Luz de Lisboa. El exjugador parisino Kingsley Coman fue el gran verdugo del encuentro."
                , "https://larepublica.pe/resizer/59de4wJ0jMASIYJkR_bw9ldWH6A=/646x380/top/smart/cloudfront-us-east-1.images.arcpublishing.com/gruporepublica/YKSCGP3JGREIFIVAKDK6IJ7WQA.png" ));


        news.add(new Noticia(4,
                "Los Olivos: periodista presente en el operativo policial cuenta su testimonio",
                "El reportero Gerson Taype de Latina llegó con los agentes hasta las instalaciones de la discoteca Thomas, ubicada en Los Olivos, donde se realizo la intervención que ha dejado 13 fallecidos."
                , "https://larepublica.pe/resizer/xReP0EgkgU3iuBKhkSGwbVIPlGw=/646x380/top/smart/cloudfront-us-east-1.images.arcpublishing.com/gruporepublica/Y6EC7DLQNFACNHM4HITS5HONEU.jpg" ));


    }


    private void sendNotification(final Noticia news) {
        if(TOKEN_TO == null) return;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage("Enviando la Notificación");
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        String URL_FCM_SEND = "https://fcm.googleapis.com/fcm/send";
        final String SERVER_KEY = "key=AAAAM6xFA_M:APA91bEgsDdxhFTfX9QwzHfzsSwIjN019AuuIYfafxZzJIh79knXkbItwGfGp24RrpZX92H2U4kbh53y5PJc6LrV87ldLhNIwGLjgsNJf0drSZOeCntpLBLzMxIaGFNJrYLny_cAi_Ux";
        final String DEVICE_TOKEN = TOKEN_TO;

        JSONObject jsonObject = getBodyFCM(DEVICE_TOKEN, news);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_FCM_SEND, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                progressDialog.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", SERVER_KEY);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject getBodyFCM(String to, Noticia news){
        JSONObject json = new JSONObject();
        JSONObject notificationJson = new JSONObject();
        JSONObject dataJson = new JSONObject();
        try {
            notificationJson.put("title", news.getTitle());
            notificationJson.put("body", news.getSummary());
            notificationJson.put("image", news.getUrlImage());
            dataJson.put("title", news.getTitle());
            dataJson.put("description", news.getSummary());
            json.put("to", to);
            json.put("priority", "high");
            json.put("notification", notificationJson);
            json.put("data", dataJson);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return json;
    }
}