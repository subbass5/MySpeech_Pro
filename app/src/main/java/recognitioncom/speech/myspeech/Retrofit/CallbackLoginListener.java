package recognitioncom.speech.myspeech.Retrofit;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Pojo.LoginRes;
import retrofit2.Retrofit;

public interface CallbackLoginListener {

    public void onResponse(LoginRes user);
    public void onBodyError(ResponseBody responseBodyError);
    public void onBodyErrorIsNull();
    public void onFailure(Throwable t);

}
