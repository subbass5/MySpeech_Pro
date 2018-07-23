package recognitioncom.speech.myspeech.Fragment;


import android.app.ProgressDialog;
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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Model.PlayListModel;
import recognitioncom.speech.myspeech.Model.QuestionRes;
import recognitioncom.speech.myspeech.R;
import recognitioncom.speech.myspeech.Retrofit.CallbackPlaylistListenner;
import recognitioncom.speech.myspeech.Retrofit.CallbackQuestionListenner;
import recognitioncom.speech.myspeech.Retrofit.NetworkConnectionManager;
import recognitioncom.speech.myspeech.Util.MyFer;

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

    public static final String JSON_ID = "id_";
    public static final String JSON_NAMES = "question_name";
    public static final String JSON_ANS1 = "ans1";
    public static final String JSON_ANS2 = "ans2";
    public static final String JSON_ANS3 = "ans3";
    public static final String JSON_URL = "url_sund";
    public static final String JSON_SCORE = "score";
    public static final String JSON_ANS = "ans";




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

        Category = sharedPreferences.getString(MyFer.CATE,"");  //get Category name
        UrlCategory = sharedPreferences.getString(MyFer.URL_CATE_MAIN,"");  // get url play sound
        id_ = sharedPreferences.getString(MyFer.ID_CATE,"");  //get id


        try {
//            Log.e(TAG,UrlCategory+"   "+id_);
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

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getString(R.string.progressLoading));
        progressDialog.show();

//        dataCategory(Category);
        //call api
        new NetworkConnectionManager().getPlaylist(playlistListenner,id_);



    }


    private void btnPlayChoice(){

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getString(R.string.progressLoading));
        progressDialog.show();

        // set choice  0
        editor.putInt(FragmentLogin.KEY_CHOICE_NUM,0);
        editor.commit();
//        Toast.makeText(context, ""+id_, Toast.LENGTH_SHORT).show();

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
                        btnPlayChoice();

                    if(result.get(0).equals("กลับสู่หน้าหลัก")){
                        fragmentManager.popBackStack();
                    }


                }
                break;
            }

        }
    }

    CallbackPlaylistListenner playlistListenner = new CallbackPlaylistListenner() {

        @Override
        public void onResponse(List<PlayListModel> playListModels) {

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            editor.putString(FragmentLogin.KEY_HEADER_SOUND,Category);
            editor.putString(FragmentLogin.KEY_NO, new Gson().toJson(playListModels));
            editor.putString(FragmentLogin.KEY_URL_SOUND_MAIN, sharedPreferences.getString(MyFer.URL_CATE_PLAY,""));
            editor.putInt("index",playListModels.size());
            editor.commit();

            FragmentListenCategory listenCategory = new FragmentListenCategory();
            fragmentTran(listenCategory,null);

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
            t.printStackTrace();
        }
    };


    CallbackQuestionListenner listenner = new CallbackQuestionListenner() {
        @Override
        public void onResponse(List<QuestionRes> questionRes) {

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            try {

            JSONArray jsonArray = new JSONArray();

            for(int i = 0 ;i<questionRes.size();i++){
                Log.e(TAG,"index "+i+"= "+questionRes.get(i).getId());
                   JSONObject jsonObject = new JSONObject();

                    jsonObject.put(JSON_ID,questionRes.get(i).getId());
                    jsonObject.put(JSON_NAMES,questionRes.get(i).getQuestionName());
                    jsonObject.put(JSON_ANS1,questionRes.get(i).getAnswer1());
                    jsonObject.put(JSON_ANS2,questionRes.get(i).getAnswer2());
                    jsonObject.put(JSON_ANS3,questionRes.get(i).getAnswer3());
                    jsonObject.put(JSON_URL,questionRes.get(i).getAnswer4());
                    jsonObject.put(JSON_SCORE,questionRes.get(i).getAnswer());
                    jsonArray.put(jsonObject);


                }

                editor.putInt(FragmentLogin.KEY_SIZE,questionRes.size());
                editor.putString(FragmentLogin.KEY_DATA,jsonArray.toString());
                Log.e(TAG,jsonArray.toString());
                editor.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }



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
