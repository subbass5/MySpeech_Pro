package recognitioncom.speech.myspeech.Retrofit;

import java.util.List;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Pojo.QuestionRes;

public interface CallbackQuestionListenner {
    public void onResponse(List<QuestionRes> questionRes);
    public void onBodyError(ResponseBody responseBodyError);
    public void onBodyErrorIsNull();
    public void onFailure(Throwable t);

}
