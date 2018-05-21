package recognitioncom.speech.myspeech.Retrofit;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Pojo.LoginRes;
import recognitioncom.speech.myspeech.Pojo.RegisterRes;

public interface CallbackRegisterListener {

    public void onResponse(RegisterRes registerRes);
    public void onBodyError(ResponseBody responseBodyError);
    public void onBodyErrorIsNull();
    public void onFailure(Throwable t);

}
