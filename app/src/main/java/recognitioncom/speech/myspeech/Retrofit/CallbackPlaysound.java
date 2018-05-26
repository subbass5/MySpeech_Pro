package recognitioncom.speech.myspeech.Retrofit;

import java.util.List;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Model.PlaySoundRes;

public interface CallbackPlaysound {
    public void onResponse(List<PlaySoundRes> playSoundRes);
    public void onBodyError(ResponseBody responseBodyError);
    public void onBodyErrorIsNull();
    public void onFailure(Throwable t);
}
