package recognitioncom.speech.myspeech.Retrofit;

import java.util.List;

import recognitioncom.speech.myspeech.Pojo.CategoriesRes;
import recognitioncom.speech.myspeech.Pojo.LoginRes;
import recognitioncom.speech.myspeech.Pojo.QuestionRes;
import recognitioncom.speech.myspeech.Pojo.RegisterRes;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {

//    @FormUrlEncoded
//    @POST("apigame/login/")
//    Call<LoginRes> logIn(@Field("username") String usr);
//
//    @FormUrlEncoded
//    @POST("apigame/register/")
//    Call<RegisterRes> register(@Field("name") String name,@Field("password") String email,@Field("countcheck") String pwd);
//
//
//    @GET("apigame/list-categories/")
//    Call<List<CategoriesRes>> getCategory();
    @FormUrlEncoded
    @POST("api/api-login.php")
    Call<LoginRes> logIn(@Field("username") String usr);

    @FormUrlEncoded
    @POST("api/api-register.php")
    Call<RegisterRes> register(@Field("name") String name,@Field("password") String email,@Field("countcheck") String pwd);


    @GET("api/api-list-categories.php")
    Call<List<CategoriesRes>> getCategory();

    @FormUrlEncoded
    @POST("api/api-list-questions.php")
    Call<List<QuestionRes>> justPlay(@Field("category_id") String cId);


}
