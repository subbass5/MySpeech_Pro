package recognitioncom.speech.myspeech.Fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.MainActivity;
import recognitioncom.speech.myspeech.Model.CategoriesRes;
import recognitioncom.speech.myspeech.Model.DataModel;
import recognitioncom.speech.myspeech.Model.FirstSoundModel;
import recognitioncom.speech.myspeech.R;
import recognitioncom.speech.myspeech.Recycleview.MainappRecycleAdp;
import recognitioncom.speech.myspeech.Retrofit.CallbackCategoriesListener;
import recognitioncom.speech.myspeech.Retrofit.CallbackFirstSoundListener;
import recognitioncom.speech.myspeech.Retrofit.NetworkConnectionManager;
import recognitioncom.speech.myspeech.TTS.MyTTS;
import recognitioncom.speech.myspeech.Util.MyFer;

import static android.app.Activity.RESULT_OK;

public class FragmentMainApp extends Fragment {

    private final int REQ_CODE_SPEECH_INPUT = 1001;
    Context context;
    RecyclerView recyclerView;
    MainappRecycleAdp adp;
    ProgressDialog progressDialog;
    String TAG = "<FragmentMainApp>";
    List<String> categories;
    List<String>  urlList;
    List<String> id;
    List<Map<String,Object>> val;

    SwipeRefreshLayout mSwipeRefreshLayout;

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;
    FragmentManager fragmentManager;

    //play sound
    String url = MainActivity.BASE_URL+"/Sound/A.wav";
    MediaPlayer mPlayer;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layoutmainapp,container,false);
            initInstance(v);
        return v;
    }


    private void initInstance(View v){

            ((AppCompatActivity) getActivity()).getSupportActionBar().hide(); // hide tools bar
            fragmentManager = getActivity().getSupportFragmentManager();
            context = getContext();
            recyclerView = v.findViewById(R.id.mainApprecycle);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            adp = new MainappRecycleAdp(context);

            categories = new ArrayList<>();
            urlList = new ArrayList<>();
            id = new ArrayList<>();
            val = new ArrayList<>();

            mSwipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if(mPlayer.isPlaying())
                        mPlayer.stop();

                    promptSpeechInput();
                    mSwipeRefreshLayout.setRefreshing(false);

                }
            });

        sharedPreferences = getActivity().getSharedPreferences(FragmentLogin.MYFER,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        new NetworkConnectionManager().callUrlmainapp(callbackURl);


    }

    CallbackFirstSoundListener callbackURl = new CallbackFirstSoundListener() {
        @Override
        public void onResponse(FirstSoundModel firstSoundModel) {

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getString(R.string.progressLoading));
            progressDialog.show();

            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(firstSoundModel.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            new NetworkConnectionManager().callCategories(listener);

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


    CallbackCategoriesListener listener = new CallbackCategoriesListener() {
        @Override
        public void onResponse(List<CategoriesRes> res) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();



            for (int i  = 0; i<res.size();i++){

                Map<String,Object> result = new HashMap<>();
                result.put(MyFer.ID_CATE,res.get(i).getId());
                result.put(MyFer.CATE,res.get(i).getCategoryName());
                result.put(MyFer.URL_CATE_MAIN,res.get(i).getMainUrl());
                result.put(MyFer.URL_CATE_PLAY,res.get(i).getPlaySound());
                val.add(result);
//                categories.add("หมวด"+res.get(i).getCategoryName());
//                id.add(res.get(i).getId());
            }
//            Log.e(TAG,new Gson().toJson(val));
            adp.UpdateData(val);
            recyclerView.setAdapter(adp);
            playMainSound();

        }

        @Override
        public void onBodyError(ResponseBody responseBodyError) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            Toast.makeText(context, "responseBodyError"+responseBodyError.source(), Toast.LENGTH_SHORT).show();
            Log.d(TAG,""+responseBodyError.source());

        }

        @Override
        public void onBodyErrorIsNull() {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(Throwable t) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            t.printStackTrace();
            Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    private void playMainSound(){

        // Initialize a new media player instance


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

    private void promptSpeechInput() {



        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.speech_prompt));

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context,
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void setGoFragment(String id,String input){

        DataModel dataModel = new DataModel();
        String getUrl = dataModel.getUrl(input);

        editor.putString(FragmentLogin.KEY_CATEGORY_ID,id);
        editor.putString(FragmentLogin.KEY_CATEGORY,input);
        editor.putString(FragmentLogin.KEY_URL_MAIN_CATEGORY,getUrl);
        editor.commit();
        Log.e(TAG,getUrl);
//
        FragmentMainCategory mainCategory = new FragmentMainCategory();
        fragmentTran(mainCategory,null);


    }

    public void fragmentTran(Fragment fragment, Bundle bundle){

        FragmentManager fragmentManager =((AppCompatActivity) context).getSupportFragmentManager();
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

//                    Toast.makeText(context, result.get(0), Toast.LENGTH_SHORT).show();

                    for (int i=0;i<val.size();i++){

                        if(val.get(i).get(MyFer.CATE).equals(result.get(0)) ||
                                val.get(i).get(MyFer.ID_CATE).equals(""+result.get(0))){
                            setGoFragment(val.get(i).get(MyFer.ID_CATE).toString(),val.get(i).get(MyFer.CATE).toString());

                        }
                    }

                    if(result.get(0).equals("ออกจากระบบ")){
                        editor.clear();
                        editor.commit();
                        fragmentManager.popBackStack();
//                        Toast.makeText(context, "ออกจากระบบ", Toast.LENGTH_SHORT).show();
                        MyTTS.getInstance(context).setLocale(new Locale("th")).speak("ตุณได้ออกจากระบบเรียบร้อยแล้ว");

                    }

                }
                break;
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayer.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlayer.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }
}
