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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import recognitioncom.speech.myspeech.R;

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

        fragmentManager = getActivity().getSupportFragmentManager();

        sharedPreferences = getActivity().getSharedPreferences(FragmentLogin.MYFER, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString(FragmentLogin.KEY_HEADER_SOUND,"").isEmpty())
        tv_header.setText(sharedPreferences.getString(FragmentLogin.KEY_HEADER_SOUND,""));
        if(!sharedPreferences.getString(FragmentLogin.KEY_NO1,"").isEmpty())
        tv_no1.setText("1."+sharedPreferences.getString(FragmentLogin.KEY_NO1,""));
        if(!sharedPreferences.getString(FragmentLogin.KEY_NO2,"").isEmpty())
        tv_no2.setText("2."+sharedPreferences.getString(FragmentLogin.KEY_NO2,""));
        if(!sharedPreferences.getString(FragmentLogin.KEY_NO3,"").isEmpty())
        tv_no3.setText("3."+sharedPreferences.getString(FragmentLogin.KEY_NO3,""));
        if(!sharedPreferences.getString(FragmentLogin.KEY_NO4,"").isEmpty())
        tv_no4.setText("4."+sharedPreferences.getString(FragmentLogin.KEY_NO4,""));
        if(!sharedPreferences.getString(FragmentLogin.KEY_NO5,"").isEmpty())
        tv_no5.setText("5."+sharedPreferences.getString(FragmentLogin.KEY_NO5,""));
        else
            tv_no5.setText("");


        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                promptSpeechInput();
                mPlayer.stop();
                mPlayer.release();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        try {
//            Log.e(TAG,UrlCategory);
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(sharedPreferences.getString(FragmentLogin.KEY_URL_SOUND_MAIN,""));
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
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if(result.get(0).equals("กลับสู่หน้าหลัก")){
                        fragmentManager.popBackStack();
                    }


                }
                break;
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlayer.stop();
    }
}
