package recognitioncom.speech.myspeech.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import java.util.Locale;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Model.LoginRes;
import recognitioncom.speech.myspeech.R;
import recognitioncom.speech.myspeech.Retrofit.NetworkConnectionManager;
import recognitioncom.speech.myspeech.Retrofit.CallbackLoginListener;
import recognitioncom.speech.myspeech.TTS.MyTTS;

public class FragmentLogin extends Fragment implements View.OnClickListener{

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private EditText et_user;


    public static final String MYFER = "myFer";

    public static final String KEY_ID = "idUser";
    public static final String KEY_NAME ="nameUser";
    public static final String KEY_COUNTCHECK = "countCheck";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_URL_MAIN_CATEGORY = "mainCategories";
    public static final String KEY_DO_REGISTER = "doRegister";

    public static final String KEY_HEADER_SOUND = "header_sound";
    public static final String KEY_NO = "no";
    public static final String KEY_URL_SOUND_MAIN = "urlMain";
    public static final String KEY_CATEGORY_ID = "Cate_id";


    //qu
    public static final String KEY_CHOICE_NUM = "KEY_CHOICE_NUM";
    public static final String KEY_DATA = "DATA";
    public static final String KEY_SIZE = "KEY_SIZE";


    private String usr = "";

    private String TAG = "FragmentLogin";
    private ProgressDialog progress;
    int Count = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_page,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide(); // hide tools bar

        init(v);

        return v;
    }

    private void init(View v){

        //init instance
        context = getContext();


        v.findViewById(R.id.btn_login).setOnClickListener(this);
        v.findViewById(R.id.tv_register).setOnClickListener(this);

//        checkBox = v.findViewById(R.id.chkRemeber);

        et_user = v.findViewById(R.id.et_username);

//        et_user.setText("test");


        sharedPreferences = getActivity().getSharedPreferences(MYFER,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putBoolean(KEY_DO_REGISTER,false);
        editor.commit();


    }

    private void login(){


        usr = et_user.getText().toString().trim();


        if(usr.length() > 0){

            // popup loading
            progress = new ProgressDialog(context);
            progress.setMessage(getString(R.string.progressLoading));
            progress.show();

            //call api login
            new NetworkConnectionManager().callLogin(listener,usr);

        }else{

            Toast.makeText(context, "กรุณากรอกข้อมูล ชื่อ", Toast.LENGTH_SHORT).show();

        }


    }

    private void  register(){

        editor.putBoolean(KEY_DO_REGISTER,true);
        editor.commit();

        FragmentRegister fragmentMainApp = new FragmentRegister();
        fragmentTran(fragmentMainApp,null);

//        Toast.makeText(context, "Register", Toast.LENGTH_SHORT).show();

    }

    public void fragmentTran(Fragment fragment,Bundle bundle){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction frgTran = fragmentManager.beginTransaction();
        frgTran.replace(R.id.contentApp, fragment).addToBackStack(null).commit();
    }

    private void checkTuch(final int input){

        LayoutInflater layoutInflater = getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.custom_tuch_dialog,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(view);

        Button btn = view.findViewById(R.id.button);

        final AlertDialog dialog = builder.create();


       btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Count == input){

                   FragmentMainApp fragmentMainApp = new FragmentMainApp();
                    fragmentTran(fragmentMainApp,null);
                    dialog.dismiss();

                }else {

                    Count+=1;
                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(1024,425);

//

    }


    //callback จาก server
    CallbackLoginListener listener = new CallbackLoginListener() {
        @Override
        public void onResponse(LoginRes loginRes) {
//            Log.e(TAG,""+loginRes.getName());

            if(loginRes.getName().equals("null")){
                Toast.makeText(context, "ไม่พบชื่อผู้ใช้กรุณาสมัครสมาชิก", Toast.LENGTH_SHORT).show();
//                MyTTS.getInstance(context).setLocale(new Locale("th")).speak("กรุณากรอกข้อมูลชื่อ");

            }

            //ปิด alert loading
            if(progress.isShowing()){
                progress.dismiss();

            }

            editor.putString(KEY_ID,loginRes.getId());
            editor.putString(KEY_NAME,loginRes.getName());
            editor.putString(KEY_COUNTCHECK,loginRes.getCountchack());
            editor.commit();
            Count  = 1;

            checkTuch(Integer.parseInt(loginRes.getCountchack()));

//            Toast.makeText(context, ""+loginRes.getCountchack(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onBodyError(ResponseBody responseBodyError) {
            Log.e(TAG,"responseBodyError ");
            if(progress.isShowing()){
                progress.dismiss();
                Toast.makeText(context, "เข้าสู่ระบบไม่สำเร็จ", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        public void onBodyErrorIsNull() {
            Log.e(TAG,"onBodyErrorIsNull");
            if(progress.isShowing()){
                progress.dismiss();
                Toast.makeText(context, "เข้าสู่ระบบไม่สำเร็จ", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        public void onFailure(Throwable t) {
//            Log.e(TAG,""+t.getMessage());

            t.printStackTrace();
            if(progress.isShowing()){
                progress.dismiss();
                Toast.makeText(context, "เข้าสู่ระบบไม่สำเร็จ", Toast.LENGTH_SHORT).show();

            }


        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:  //event onClick button login
                login();
                break;
            case R.id.tv_register: //event onClick textview register
                register();
                break;
        }
    }


}
