package recognitioncom.speech.myspeech.Retrofit;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Model.SendScore;

public interface CallbackSendScore {

    public void onResponse(SendScore sendScore);
    public void onBodyError(ResponseBody responseBodyError);
    public void onBodyErrorIsNull();
    public void onFailure(Throwable t);

}
