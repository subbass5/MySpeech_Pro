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
import android.support.v4.widget.SwipeRefreshLayout;
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

import recognitioncom.speech.myspeech.R;
import recognitioncom.speech.myspeech.Retrofit.NetworkConnectionManager;

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
    int choice = 0;
    MediaPlayer mPlayer;

    public static final String KEY_SCORE_TMP = "score_tmp";

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

                swipeRefreshLayout.setRefreshing(false);
                promptSpeechInput();

            }
        });

        sharedPreferences = getActivity().getSharedPreferences(FragmentLogin.MYFER, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        dataAll = sharedPreferences.getString(FragmentLogin.KEY_DATA,"");
        choice = sharedPreferences.getInt(FragmentLogin.KEY_CHOICE_NUM,0);
        context = getContext();
        v.findViewById(R.id.btn_send_question).setOnClickListener(this);
        v.findViewById(R.id.btn_play).setOnClickListener(this);
        tv_question = v.findViewById(R.id.tv_question);
        ans1 = v.findViewById(R.id.radioAns1);
        ans2 = v.findViewById(R.id.radioAns2);
        ans3 = v.findViewById(R.id.radioAns3);

        set_choice(choice);

    }

    private void set_choice(int category_id){

        try {

            JSONObject jsonObject = new JSONObject(getQuestion(category_id));
            playSoundQuestion(jsonObject.getString(FragmentMainCategory.JSON_URL));
            url = jsonObject.getString(FragmentMainCategory.JSON_NAMES);
            tv_question.setText(url);
            ans1.setText(jsonObject.getString(FragmentMainCategory.JSON_ANS1));
            ans2.setText(jsonObject.getString(FragmentMainCategory.JSON_ANS2));
            ans3.setText(jsonObject.getString(FragmentMainCategory.JSON_ANS3));

        } catch (JSONException e) {
            e.printStackTrace();
        }

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



                    if(result.get(0).equals("กลับสู่หน้าหลัก")){

                    }


                }
                break;
            }

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
            e.printStackTrace();
        }
        return tmpStr;
    }

    private void sendQuestion(String id,String choice){
//        new NetworkConnectionManager().callQuestion();

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
                        promptSpeechInput();
                    }catch (Exception e){

                    }

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStop() {
        super.onStop();

        mPlayer.stop();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_question:

                if(choice < getIndexData()){
                    set_choice(choice+=1);
                }

                break;
            case R.id.btn_play:
                playSoundQuestion(url);
                break;
        }
    }
}
