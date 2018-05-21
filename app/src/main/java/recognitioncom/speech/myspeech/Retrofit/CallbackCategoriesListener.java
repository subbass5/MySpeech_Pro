package recognitioncom.speech.myspeech.Retrofit;

import java.util.List;

import okhttp3.ResponseBody;
import recognitioncom.speech.myspeech.Pojo.CategoriesRes;

public interface CallbackCategoriesListener {
    public void onResponse(List<CategoriesRes> res);

    public void onBodyError(ResponseBody responseBodyError);

    public void onBodyErrorIsNull();

    public void onFailure(Throwable t);
}