package recognitioncom.speech.myspeech.Retrofit;

import java.util.List;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Model.PlayListModel;
import recognitioncom.speech.myspeech.Model.QuestionRes;

public interface CallbackPlaylistListenner {

    public void onResponse(List<PlayListModel> playListModels);
    public void onBodyError(ResponseBody responseBodyError);
    public void onBodyErrorIsNull();
    public void onFailure(Throwable t);


}
