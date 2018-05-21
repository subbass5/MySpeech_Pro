package recognitioncom.speech.myspeech.Fragment;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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
import recognitioncom.speech.myspeech.Pojo.LoginRes;
import recognitioncom.speech.myspeech.Pojo.QuestionRes;
import recognitioncom.speech.myspeech.R;
import recognitioncom.speech.myspeech.Retrofit.CallbackQuestionListenner;
import recognitioncom.speech.myspeech.Retrofit.NetworkConnectionManager;
import recognitioncom.speech.myspeech.TTS.MyTTS;

import static android.app.Activity.RESULT_OK;

public class FragmentMainCategory extends Fragment implements View.OnClickListener{
    private final int REQ_CODE_SPEECH_INPUT = 1001;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String Category = "";
    String UrlCategory="";
    String TAG = "<FragmentMainCategory>";
    TextView tv_header;
    SwipeRefreshLayout swipeRefreshLayout;
    Context context;
    FragmentManager fragmentManager;
    ProgressDialog progressDialog;
    String id_;
//jsonObject.put("question_name",""+questionRes.get(i).getQuestionName());
//                    jsonObject.put("ans1",""+questionRes.get(i).getAnswer1());
//                    jsonObject.put("ans2",""+questionRes.get(i).getAnswer2());
//                    jsonObject.put("ans3",""+questionRes.get(i).getAnswer3());
//                    jsonObject.put("url_sund",""+questionRes.get(i).getAnswer4());

    public static final String JSON_ID = "id_";
    public static final String JSON_NAMES = "question_name";
    public static final String JSON_ANS1 = "ans1";
    public static final String JSON_ANS2 = "ans2";
    public static final String JSON_ANS3 = "ans3";
    public static final String JSON_URL = "url_sund";


    MediaPlayer mPlayer;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_main_category,container,false);
        init(v);
        return v;
    }

    private void init(View v){

        fragmentManager = getActivity().getSupportFragmentManager();

        context = getContext();
        v.findViewById(R.id.btn_playsound).setOnClickListener(this);
        v.findViewById(R.id.btn_playch).setOnClickListener(this);
        tv_header = v.findViewById(R.id.tv_header_category);
        sharedPreferences = getActivity().getSharedPreferences(FragmentLogin.MYFER, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Category = sharedPreferences.getString(FragmentLogin.KEY_CATEGORY,"");
        UrlCategory = sharedPreferences.getString(FragmentLogin.KEY_URL_MAIN_CATEGORY,"");
        id_ = sharedPreferences.getString(FragmentLogin.KEY_CATEGORY_ID,"");
        MyTTS.getInstance(context).speak("id  = "+ id_);

        try {
//            Log.e(TAG,UrlCategory);
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(UrlCategory);
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

        tv_header.setText(Category);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                promptSpeechInput();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

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


    private void btnPlaySound(){


        dataCategory(Category);
        FragmentListenCategory listenCategory = new FragmentListenCategory();
        fragmentTran(listenCategory,null);


    }

    private void dataCategory(String category){

        editor.putString(FragmentLogin.KEY_HEADER_SOUND,category);

            if(category.equals("หมวดสัตว์เลี้ยง"))
            {
                    editor.putString(FragmentLogin.KEY_NO1,"เสียงแมว");
                    editor.putString(FragmentLogin.KEY_NO2,"เสียงสุนัข");
                    editor.putString(FragmentLogin.KEY_NO3,"เสียงไก่");
                    editor.putString(FragmentLogin.KEY_NO4,"เสียงนก");
                    editor.putString(FragmentLogin.KEY_NO5,"เสียงกระต่าย");
                    editor.putString(FragmentLogin.KEY_URL_SOUND_MAIN,
                            "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/H.wav?alt=media&token=d7b8dae9-3cce-4114-abfc-27a7e2cb1a92");
            }
          else if(category.equals("หมวดสัตว์อันตราย"))
          {
                  editor.putString(FragmentLogin.KEY_NO1,"เสียงเสือ");
                  editor.putString(FragmentLogin.KEY_NO2,"เสียงจระเข้");
                  editor.putString(FragmentLogin.KEY_NO3,"เสียงสิงโต");
                  editor.putString(FragmentLogin.KEY_NO4,"เสียงปลาฉลาม");
                  editor.putString(FragmentLogin.KEY_NO5,"เสียงช้าง");
                  editor.putString(FragmentLogin.KEY_URL_SOUND_MAIN,
                          "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/I.wav?alt=media&token=290fc3ba-e4a2-4399-a7ed-8a21ef018c5d");
          }
          else if(category.equals("หมวดเตือนภัย")){
                    editor.putString(FragmentLogin.KEY_NO1,"สัญญาณไฟไหม้");
                    editor.putString(FragmentLogin.KEY_NO2,"สัญญาณน้ำท่วม");
                    editor.putString(FragmentLogin.KEY_NO3,"สัญญาณรถพยาบาลฉุกเฉิน");
                    editor.putString(FragmentLogin.KEY_NO4,"สัญญาณรถดับเพลิง");
                    editor.putString(FragmentLogin.KEY_NO5,"สัญญาณรถตำรวจ");
                editor.putString(FragmentLogin.KEY_URL_SOUND_MAIN,
                        "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/J.wav?alt=media&token=062a8ac1-4f32-43de-b587-92c03f7308c8");
        }
         else if(category.equals("หมวดยานพาหนะ"))
         {
             editor.putString(FragmentLogin.KEY_NO1,"เสียงรถยนต์");
             editor.putString(FragmentLogin.KEY_NO2,"เสียงเรือ");
             editor.putString(FragmentLogin.KEY_NO3,"เสียงรถมอเตอร์ไซต์");
             editor.putString(FragmentLogin.KEY_NO4,"เสียงเครื่องบิน");
             editor.putString(FragmentLogin.KEY_NO5,"เสียงรถไฟ");
             editor.putString(FragmentLogin.KEY_URL_SOUND_MAIN,
                     "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/K.wav?alt=media&token=ff43faab-6a39-4d97-b4d0-11137e79bcfc");
        }


          else if(category.equals("หมวดอวัยวะในร่างกาย"))
          {
              editor.putString(FragmentLogin.KEY_NO1,"เสียงหู");
              editor.putString(FragmentLogin.KEY_NO2,"เสียงเท้า");
              editor.putString(FragmentLogin.KEY_NO3,"เสียงจมูก");
              editor.putString(FragmentLogin.KEY_NO4,"เสียงนิ้วมือ");
              editor.putString(FragmentLogin.KEY_NO5,"เสียงลิ้น");
              editor.putString(FragmentLogin.KEY_URL_SOUND_MAIN,
                      "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/L.wav?alt=media&token=50db637d-bfbf-436c-aaab-8d61808e6ce0");
        }
        else if(category.equals("หมวดการช่วยเหลือตนเอง"))
        {
            editor.putString(FragmentLogin.KEY_NO1,"การเข้าห้องน้ำ");
            editor.putString(FragmentLogin.KEY_NO2,"การเดินข้ามถนน");
            editor.putString(FragmentLogin.KEY_NO3,"การอาบน้ำ");
            editor.putString(FragmentLogin.KEY_NO4,"การรับประทานอาหาร");
            editor.putString(FragmentLogin.KEY_NO5,"");
            editor.putString(FragmentLogin.KEY_URL_SOUND_MAIN,
                    "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/M.wav?alt=media&token=519c9b72-351f-41b1-b234-de4a25186c8e");
        }

        editor.commit();

    }

    private void btnPlayChoice(){

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getString(R.string.progressLoading));
        progressDialog.show();

        // set choice  0
        editor.putInt(FragmentLogin.KEY_CHOICE_NUM,0);
        editor.commit();
        Toast.makeText(context, ""+id_, Toast.LENGTH_SHORT).show();

        new NetworkConnectionManager().callQuestion(listenner,id_);

    }



    /**
     * Receiving speech input
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if (result.get(0).equals("1") || result.get(0).equals("ฟังเสียง"))
                        btnPlaySound();
                    if (result.get(0).equals("2") || result.get(0).equals("ทำแบบทดสอบ"))
                        btnPlaySound();

                    if(result.get(0).equals("กลับสู่หน้าหลัก")){
                        fragmentManager.popBackStack();
                    }


                }
                break;
            }

        }
    }

    CallbackQuestionListenner listenner = new CallbackQuestionListenner() {
        @Override
        public void onResponse(List<QuestionRes> questionRes) {

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for(int i = 0 ;i< questionRes.size();i++){
                Log.e(TAG,"index "+i+"= "+questionRes.get(i).getId());
                try {
                    jsonObject.put(JSON_ID,""+questionRes.get(i).getId());
                    jsonObject.put(JSON_NAMES,""+questionRes.get(i).getQuestionName());
                    jsonObject.put(JSON_ANS1,""+questionRes.get(i).getAnswer1());
                    jsonObject.put(JSON_ANS2,""+questionRes.get(i).getAnswer2());
                    jsonObject.put(JSON_ANS3,""+questionRes.get(i).getAnswer3());
                    jsonObject.put(JSON_URL,""+questionRes.get(i).getAnswer4());
                    jsonArray.put(jsonObject);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            editor.putInt(FragmentLogin.KEY_SIZE,questionRes.size());
            editor.putString(FragmentLogin.KEY_DATA,jsonArray.toString());
            editor.commit();

            //go to frg question
            FragmentQuestion fragmentQuestion = new FragmentQuestion();
            fragmentTran(fragmentQuestion,null);


        }

        @Override
        public void onBodyError(ResponseBody responseBodyError) {

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }

        @Override
        public void onBodyErrorIsNull() {

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

        }

        @Override
        public void onFailure(Throwable t) {

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

        }
    };

    public void fragmentTran(Fragment fragment,Bundle bundle){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction frgTran = fragmentManager.beginTransaction();
        frgTran.replace(R.id.contentApp, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_playsound:
                btnPlaySound();
                break;
            case R.id.btn_playch:
                btnPlayChoice();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayer.stop();
    }
}
