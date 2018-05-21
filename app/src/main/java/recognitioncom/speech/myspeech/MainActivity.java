package recognitioncom.speech.myspeech;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;
import recognitioncom.speech.myspeech.Fragment.FragmentLogin;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    FragmentLogin loginFrg;
    public static final String BASE_URL = "http://profile2.chiangraisoftware.com/";    //"http://mumei-system.ml/";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginFrg = new FragmentLogin();
        fragmentTran(loginFrg,null);

        sharedPreferences = getSharedPreferences(FragmentLogin.MYFER,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        fragmentManager = getSupportFragmentManager();

    }

    public void fragmentTran(Fragment fragment,Bundle bundle){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction frgTran = fragmentManager.beginTransaction();
        frgTran.replace(R.id.contentApp, fragment).addToBackStack(null).commit();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    private void onLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.logout));
        builder.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {

                editor.clear();
                editor.commit();
                fragmentManager.popBackStack();

            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builder.show();


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        boolean doRegister = sharedPreferences.getBoolean(FragmentLogin.KEY_DO_REGISTER,false);

        int co = fragmentManager.getBackStackEntryCount();
//        Toast.makeText(this, ""+co, Toast.LENGTH_SHORT).show();
        if(co > 2 || doRegister){
            fragmentManager.popBackStack();
        }

        if(co == 2 && !doRegister){

            onLogout();
//            Toast.makeText(this, "Logout ", Toast.LENGTH_SHORT).show();
        }


    }

}
