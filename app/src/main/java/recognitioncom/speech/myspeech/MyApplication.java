package recognitioncom.speech.myspeech;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        initfont();
    }
    public void initfont(){
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Kanit-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
