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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Model.SendScore;
import recognitioncom.speech.myspeech.R;
import recognitioncom.speech.myspeech.Retrofit.CallbackSendScore;
import recognitioncom.speech.myspeech.Retrofit.NetworkConnectionManager;
import recognitioncom.speech.myspeech.TTS.MyTTS;

import static android.app.Activity.RESULT_OK;

public class FragmentQuestion extends Fragment implements View.OnClickListener{

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    TextView tv_question;
    RadioButton ans1,ans2,ans3;
    SwipeRefreshLayout swipeRefreshLayout;
    private final int REQ_CODE_SPEECH_INPUT = 1001;
    FragmentManager fragmentManager;
    String dataAll,url;
    int ansTrue ,scoreTmp;
    int choice = 0;
    MediaPlayer mPlayer;
    public static final String KEY_SCORE = "score";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.question_page,container,false);
        init(v);
        return v;

    }

    private void init(View v) {

        fragmentManager = getActivity().getSupportFragmentManager();
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPlayer.stop();
                swipeRefreshLayout.setRefreshing(false);
                promptSpeechInput();

            }
        });

        sharedPreferences = getActivity().getSharedPreferences(FragmentLogin.MYFER, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        dataAll = sharedPreferences.getString(FragmentLogin.KEY_DATA,"");
        choice = 0;
        context = getContext();
        v.findViewById(R.id.btn_send_question).setOnClickListener(this);
        v.findViewById(R.id.btn_play).setOnClickListener(this);
        tv_question = v.findViewById(R.id.tv_question);
        ans1 = v.findViewById(R.id.radioAns1);
        ans2 = v.findViewById(R.id.radioAns2);
        ans3 = v.findViewById(R.id.radioAns3);

        set_choice(choice);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if(result.get(0).equals("1") || result.get(0).equals(ans1.getText().toString())){

                        ans1.setChecked(true);

                    }else if(result.get(0).equals("2") || result.get(0).equals(ans2.getText().toString())){

                        ans2.setChecked(true);

                    }else if(result.get(0).equals("3") || result.get(0).equals(ans3.getText().toString())){
                        ans3.setChecked(true);
                    }

                    if(ans1.isChecked() || ans2.isChecked() || ans3.isChecked()){
                        onClickNext();
                    }


                    if(result.get(0).equals("กลับสู่หน้าหลัก")){
                        fragmentManager.popBackStack();
                    }


                }
                break;
            }

        }
    }

    private void set_choice(int category_id){
//        Toast.makeText(context, "Set choice= "+category_id, Toast.LENGTH_SHORT).show();
        try {

            JSONObject jsonObject = new JSONObject(getQuestion(category_id));
            playSoundQuestion(jsonObject.getString(FragmentMainCategory.JSON_URL));
            url = jsonObject.getString(FragmentMainCategory.JSON_NAMES);
            tv_question.setText(url);

            ans1.setText(jsonObject.getString(FragmentMainCategory.JSON_ANS1));
            ans2.setText(jsonObject.getString(FragmentMainCategory.JSON_ANS2));
            ans3.setText(jsonObject.getString(FragmentMainCategory.JSON_ANS3));

            ansTrue = Integer.parseInt(jsonObject.getString(FragmentMainCategory.JSON_SCORE));

//            Toast.makeText(context, ""+ansTrue, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void Speak_for_simple(int category_id){
//        Toast.makeText(context, "Set choice= "+category_id, Toast.LENGTH_SHORT).show();
        try {

            JSONObject jsonObject = new JSONObject(getQuestion(category_id));
            url = jsonObject.getString(FragmentMainCategory.JSON_NAMES);
            MyTTS.getInstance(context).setLocale(new Locale("th"))
                    .speak(jsonObject.getString(FragmentMainCategory.JSON_NAMES)+"ตอบ  1"
                            +jsonObject.getString(FragmentMainCategory.JSON_ANS1)
                            +"ตอบ  2 "+jsonObject.getString(FragmentMainCategory.JSON_ANS2)
                            +"ตอบ  3 "+jsonObject.getString(FragmentMainCategory.JSON_ANS3)
                    );

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private int getIndexData(){
        try {
            JSONArray jsonArray = new JSONArray(dataAll);

            return jsonArray.length();

        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }


    private String  getQuestion(int index){
        String tmpStr = "";

        try {

            JSONArray jsonArray = new JSONArray(dataAll);
            tmpStr = ""+jsonArray.get(index);

        } catch (JSONException e) {

            Log.e("getQuestion "+index,e.getMessage());
        }
        return tmpStr;
    }



    private void playSoundQuestion(String url){


        try {

            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(url);
            mPlayer.prepare();

            // Start playing audio from http url
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
//                Toast.makeText(context,"End",Toast.LENGTH_SHORT).show();
                    try {

                        Speak_for_simple(choice);
                        promptSpeechInput();

                    }catch (Exception e){

                    }

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void fragmentTran(Fragment fragment,Bundle bundle){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction frgTran = fragmentManager.beginTransaction();
        frgTran.replace(R.id.contentApp, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onStop() {
        super.onStop();

        mPlayer.stop();
    }

    private void onClickNext(){



        int AnswerNow = 0;
//                Toast.makeText(context, ""+getIndexData(), Toast.LENGTH_SHORT).show();
        mPlayer.stop();

        choice +=1;

        if(ans1.isChecked()){
            AnswerNow = 1;

        }else if(ans2.isChecked()){
            AnswerNow = 2;


        }else if (ans3.isChecked()){
            AnswerNow = 3;

        }else {
            Toast.makeText(context, "กรุณาเลือกข้อสอบก่อนส่ง", Toast.LENGTH_SHORT).show();
        }

        if(choice < getIndexData()){

//            Log.e("เช็คคำตอบ",ansTrue+"  , "+AnswerNow);
            if(ansTrue == AnswerNow) {
                scoreTmp += 1;
                editor.putInt(KEY_SCORE, scoreTmp);
                editor.commit();
            }
            if(ans1.isChecked() || ans2.isChecked() || ans3.isChecked()){
                set_choice(choice);
            }else {
                Toast.makeText(context, "กรุณาเลือกข้อสอบก่อนส่ง", Toast.LENGTH_SHORT).show();
            }



        }else {

            if(ansTrue == AnswerNow) {
                scoreTmp += 1;
                editor.putInt(KEY_SCORE, scoreTmp);
                editor.commit();
            }
            String name = sharedPreferences.getString(FragmentLogin.KEY_NAME,"");
            String category_id = sharedPreferences.getString(FragmentLogin.KEY_CATEGORY_ID,"");
            String ScoreTmp = ""+scoreTmp;
//            Log.e("Befor save","name = "+name+" score = "+ScoreTmp+" Category = "+category_id);
            new NetworkConnectionManager().sendScore(sendScore,name,ScoreTmp,category_id);


        }
    }


    CallbackSendScore sendScore = new CallbackSendScore() {
        @Override
        public void onResponse(SendScore sendScore) {

//            Toast.makeText(context, ""+sendScore.getState(), Toast.LENGTH_SHORT).show();

            if(sendScore.getState().equals("success")){

                FragmentShowScore showScore = new FragmentShowScore();
                fragmentTran(showScore,null);

            }else {
                fragmentManager.popBackStack();
            }


        }

        @Override
        public void onBodyError(ResponseBody responseBodyError) {

        }

        @Override
        public void onBodyErrorIsNull() {

        }

        @Override
        public void onFailure(Throwable t) {

        }
    };


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_send_question:

                onClickNext();

                break;
            case R.id.btn_play:
                playSoundQuestion(url);
                break;
        }
    }
}
