package recognitioncom.speech.myspeech.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Pojo.RegisterRes;
import recognitioncom.speech.myspeech.R;
import recognitioncom.speech.myspeech.Retrofit.NetworkConnectionManager;
import recognitioncom.speech.myspeech.Retrofit.CallbackRegisterListener;

public class FragmentRegister extends Fragment implements View.OnClickListener{

    Context context;
    ProgressDialog progress;
    EditText etName,etPassword,et_numCount;
    private String TAG = "FragmentRegister";
    FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.register_page,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide(); // hide tools bar

        init(v);

        return v;
    }

    private void init(View v){

        v.findViewById(R.id.btnRegister).setOnClickListener(this);
        v.findViewById(R.id.btnReset).setOnClickListener(this);

        etName = v.findViewById(R.id.et_name);
        etPassword = v.findViewById(R.id.et_password);
        et_numCount = v.findViewById(R.id.et_numLogin);
        context = getContext();
        fragmentManager = getActivity().getSupportFragmentManager();


    }

    private void register(){
        try{

           String name =  etName.getText().toString().trim();
           String password = etPassword.getText().toString().trim();
           String numcount = et_numCount.getText().toString().trim();

            if(!name.isEmpty() && !numcount.isEmpty() &&  !password.isEmpty()){
                new NetworkConnectionManager().callRegister(listener,name,password,numcount);
                progress = new ProgressDialog(context);
                progress.setMessage(getString(R.string.progressLoading));
                progress.show();
            }else{
                Toast.makeText(context, "กรุณากรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }



    }

    private void reset(){

        etPassword.setText("");
        etPassword.setText("");
        et_numCount.setText("");

    }

    public void fragmentTran(Fragment fragment,Bundle bundle){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction frgTran = fragmentManager.beginTransaction();
        frgTran.replace(R.id.contentApp, fragment).addToBackStack(null).commit();
    }

    CallbackRegisterListener listener = new CallbackRegisterListener() {
        @Override
        public void onResponse(RegisterRes registerRes) {
            if(progress.isShowing()){
                progress.dismiss();

            }

//            fragmentManager.popBackStack();
            Log.e(TAG,registerRes.getStatus());
            if(registerRes.getStatus().equals("Sorry this name is already")){
                etName.setText("");
                et_numCount.setText("");
                etPassword.setText("");
                Toast.makeText(context, "ชื่อที่ใช้ในการสมัครซ้ำ!", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(context, "สมัครสมาชิกเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                fragmentManager.popBackStack();
            }



        }

        @Override
        public void onBodyError(ResponseBody responseBodyError) {
            if(progress.isShowing()){
                progress.dismiss();
                Toast.makeText(context, "สมัครสมาชิก ล้มเหลว", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onBodyErrorIsNull() {
            if(progress.isShowing()){
                progress.dismiss();
                Toast.makeText(context, "สมัครสมาชิก ล้มเหลว", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onFailure(Throwable t) {
            if(progress.isShowing()){
                progress.dismiss();
                Toast.makeText(context, "สมัครสมาชิก ล้มเหลว", Toast.LENGTH_SHORT).show();

            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegister:
                    register();
                break;
            case R.id.btnReset:
                reset();
                break;
        }
    }
}
