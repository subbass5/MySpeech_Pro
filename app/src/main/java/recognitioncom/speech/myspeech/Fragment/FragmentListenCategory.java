package recognitioncom.speech.myspeech.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Model.PlaySoundRes;
import recognitioncom.speech.myspeech.R;
import recognitioncom.speech.myspeech.Retrofit.CallbackPlaysound;
import recognitioncom.speech.myspeech.Retrofit.NetworkConnectionManager;
import recognitioncom.speech.myspeech.TTS.MyTTS;
import recognitioncom.speech.myspeech.Util.MyFer;

import static android.app.Activity.RESULT_OK;

public class FragmentListenCategory extends Fragment{

    private final int REQ_CODE_SPEECH_INPUT = 1001;
    TextView tv_header,tv_no1,tv_no2,tv_no3,tv_no4,tv_no5;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    FragmentManager fragmentManager;
    MediaPlayer mPlayer;
    String category_id = "",
    TAG = "<FragmentListenCategory>";


    List<String> listUrlSound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.listen_category_page,container,false);
        init(v);
        return v;
    }

    private void init(View v){

        context = getContext();
        tv_header = v.findViewById(R.id.tv_header);

        tv_no1 = v.findViewById(R.id.tv_no1);
        tv_no2 = v.findViewById(R.id.tv_no2);
        tv_no3 = v.findViewById(R.id.tv_no3);
        tv_no4 = v.findViewById(R.id.tv_no4);
        tv_no5 = v.findViewById(R.id.tv_no5);

        listUrlSound = new ArrayList<>();

        fragmentManager = getActivity().getSupportFragmentManager();

        sharedPreferences = getActivity().getSharedPreferences(FragmentLogin.MYFER, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        category_id = sharedPreferences.getString(MyFer.ID_CATE,"");

//        Toast.makeText(context, ""+category_id, Toast.LENGTH_SHORT).show();
        Log.d(TAG,""+category_id);

        if(!sharedPreferences.getString(FragmentLogin.KEY_HEADER_SOUND,"").isEmpty())
            tv_header.setText(sharedPreferences.getString(FragmentLogin.KEY_HEADER_SOUND,""));

        try {

            JSONArray jsonArray = new JSONArray(sharedPreferences.getString(FragmentLogin.KEY_NO,""));
            Log.e(TAG,jsonArray.toString());

            String name = "name";
            for (int i =0;i<jsonArray.length();i++){

                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                Log.e(TAG,jsonObject.getString(name)+" "+i);

                if(i == 0 )
                  tv_no1.setText("1."+jsonObject.getString(name));

                else if(i==1 )
                    tv_no2.setText("2."+jsonObject.getString(name));


                else if(i==2 )
                    tv_no3.setText("3."+jsonObject.getString(name));


                else if(i==3 )
                    tv_no4.setText("4."+jsonObject.getString(name));


                else if(i==4 )
                    tv_no5.setText("5."+jsonObject.getString(name));


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        int size = sharedPreferences.getInt("index",0);



        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try{
                    promptSpeechInput();
                    mPlayer.stop();
                    mPlayer.release();

                }catch (Exception e){

                }

                swipeRefreshLayout.setRefreshing(false);

            }
        });

        playSound(sharedPreferences.getString(MyFer.URL_CATE_PLAY,""));

        new NetworkConnectionManager().getUrlPlaysound(playsound,category_id);
    }

    private void playSound(String url){
        try {
//            Log.e(TAG,UrlCategory);
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(url);
            mPlayer.prepare();

            // Start playing audio from http url
            mPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                Toast.makeText(context,"End",Toast.LENGTH_SHORT).show();
                try {
                    promptSpeechInput();
                }catch (Exception e){

                }

            }
        });
    }

    CallbackPlaysound playsound  = new CallbackPlaysound() {
        @Override
        public void onResponse(List<PlaySoundRes> playSoundRes) {
            for (int i= 0;i<playSoundRes.size();i++){
                listUrlSound.add(playSoundRes.get(i).getUrlSound());
//                Toast.makeText(context, ""+listUrlSound.get(i), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onBodyError(ResponseBody responseBodyError) {
            Toast.makeText(context, ""+responseBodyError.source(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onBodyErrorIsNull() {
            Toast.makeText(context, "onBodyErrorIsNull", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(Throwable t) {
            Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));


        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context,
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void fragmentTran(Fragment fragment,Bundle bundle){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction frgTran = fragmentManager.beginTransaction();
        frgTran.replace(R.id.contentApp, fragment).addToBackStack(null).commit();
    }

    /**
     * Receiving speech input
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                try{
                    if (resultCode == RESULT_OK && null != data) {

                        ArrayList<String> result = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if(result.get(0).equals("1")){
                            playSound(listUrlSound.get(0));
                            Log.e(TAG,listUrlSound.get(0));

                        }else  if(result.get(0).equals("2")){
                            playSound(listUrlSound.get(1));
                            Log.e(TAG,listUrlSound.get(1));
                        }
                        else  if(result.get(0).equals("3")){
                            playSound(listUrlSound.get(2));
                            Log.e(TAG,listUrlSound.get(2));
                        }
                        else  if(result.get(0).equals("4")){
                            playSound(listUrlSound.get(3));
                            Log.e(TAG,listUrlSound.get(3));
                        }
                        else  if(result.get(0).equals("5")){
                            playSound(listUrlSound.get(4));
                            Log.e(TAG,listUrlSound.get(4));
                        }else  if(result.get(0).equals("กลับสู่หน้าหลัก")){
                            fragmentManager.popBackStack();
                        }
                        else {
                            MyTTS.getInstance(context).setLocale(new Locale("th")).speak("ไม่พบข้อมูลกรุณาดึงหน้าจอลงเพื่อสั่งงาน");
                        }
                    }
                }catch(Exception e){
                    Toast.makeText(context, "กำลัง download เสียง", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        try{
            mPlayer.stop();
        }catch (Exception e){

        }

    }
}
