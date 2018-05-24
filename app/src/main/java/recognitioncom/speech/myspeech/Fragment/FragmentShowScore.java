package recognitioncom.speech.myspeech.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import recognitioncom.speech.myspeech.R;
import recognitioncom.speech.myspeech.TTS.MyTTS;

import static android.app.Activity.RESULT_OK;

public class FragmentShowScore extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int score=0;
    TextView tv_score;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    private final int REQ_CODE_SPEECH_INPUT = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.show_score_page,container,false);
        init(v);
        return v;

    }

    private void init(View v){

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        tv_score = v.findViewById(R.id.tv_score);
        context = getContext();
        sharedPreferences = getActivity().getSharedPreferences(FragmentLogin.MYFER, Context.MODE_PRIVATE);
        score = sharedPreferences.getInt(FragmentQuestion.KEY_SCORE,0);


        MyTTS.getInstance(context).setLocale(new Locale("th")).speak("คุณได้คะแนนรวมทั้งหมด"+ score+"  คะแนน");
        tv_score.setText(""+score +" คะแนน ");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                promptSpeechInput();

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

}
