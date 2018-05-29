package recognitioncom.speech.myspeech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

import recognitioncom.speech.myspeech.TTS.MyTTS;

public class ManualAppsActivity extends AppCompatActivity{
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initialize_app);
        getSupportActionBar().hide();
        initInstance();

    }

    private void initInstance(){

     context = getApplicationContext();
     MyTTS.getInstance(context).setLocale(new Locale("th-TH")).speak(getString(R.string.initStr)+getString(R.string.tv_bottom));
     swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
     swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
             swipeRefreshLayout.setRefreshing(false);
             MyTTS.getInstance(context).clear();
             Intent goMain = new Intent(ManualAppsActivity.this,MainActivity.class);
             startActivity(goMain);
             finish();

         }
     });
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyTTS.getInstance(context).clear();
    }
}
