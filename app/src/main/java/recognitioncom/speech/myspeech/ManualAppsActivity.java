package recognitioncom.speech.myspeech;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.util.Locale;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Model.FirstSoundModel;
import recognitioncom.speech.myspeech.Retrofit.CallbackFirstSoundListener;
import recognitioncom.speech.myspeech.Retrofit.NetworkConnectionManager;
import recognitioncom.speech.myspeech.TTS.MyTTS;

public class ManualAppsActivity extends AppCompatActivity{
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    MediaPlayer mPlayer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initialize_app);
        getSupportActionBar().hide();
        initInstance();

    }

    private void initInstance(){

     context = getApplicationContext();
     mPlayer = new MediaPlayer();
     new NetworkConnectionManager().callFirstSound(listener);
//     MyTTS.getInstance(context).setLocale(new Locale("th-TH")).speak(getString(R.string.initStr)+getString(R.string.tv_bottom));
     swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
     swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
             swipeRefreshLayout.setRefreshing(false);
             try{

                 mPlayer.stop();
                 MyTTS.getInstance(context).clear();
                 Intent goMain = new Intent(ManualAppsActivity.this,MainActivity.class);
                 startActivity(goMain);
                 finish();

             }catch (Exception e){

             }

         }
     });
    }
    private void playMainSound(){

        // Set the media player audio stream type
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //Try to play music/audio from url
        try{

            // Prepare the media player
            mPlayer.prepare();

            // Start playing audio from http url
            mPlayer.start();

            // Inform user for audio streaming
//            Toast.makeText(context,"Playing",Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            // Catch the exception
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }

//        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
////                Toast.makeText(context,"End",Toast.LENGTH_SHORT).show();
//                try {
//                    promptSpeechInput();
//                }catch (Exception e){
//
//                }
//
//            }
//        });
    }

    CallbackFirstSoundListener  listener = new CallbackFirstSoundListener() {
        @Override
        public void onResponse(FirstSoundModel firstSoundModel) {
            Log.e("url",firstSoundModel.getPath());
            try {

                mPlayer.setDataSource(firstSoundModel.getPath());
                playMainSound();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBodyError(ResponseBody responseBodyError) {
            Log.e("url",responseBodyError.source().toString());
        }

        @Override
        public void onBodyErrorIsNull() {
            Log.e("url","null");
        }

        @Override
        public void onFailure(Throwable t) {
            t.printStackTrace();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        MyTTS.getInstance(context).clear();
    }
}
