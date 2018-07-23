package recognitioncom.speech.myspeech.Retrofit;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Model.FirstSoundModel;
import recognitioncom.speech.myspeech.Model.LoginRes;

public interface CallbackFirstSoundListener {

    public void onResponse(FirstSoundModel firstSoundModel);
    public void onBodyError(ResponseBody responseBodyError);
    public void onBodyErrorIsNull();
    public void onFailure(Throwable t);
}
