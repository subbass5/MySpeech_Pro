package recognitioncom.speech.myspeech.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaySoundRes {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("url_sound")
    @Expose
    private String urlSound;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlSound() {
        return urlSound;
    }

    public void setUrlSound(String urlSound) {
        this.urlSound = urlSound;
    }
}
