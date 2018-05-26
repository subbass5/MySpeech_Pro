package recognitioncom.speech.myspeech.Model;

public class DataModel {


    public DataModel(){

    }

    public String getUrl(String inStr){

        if(inStr.equals("หมวดเตือนภัย"))
            return "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/D.wav?alt=media&token=25e2de0a-39cb-4c75-9da9-24389d10c05f";
        else if(inStr.equals("หมวดยานพาหนะ"))
            return "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/E.wav?alt=media&token=01c1f7c7-94b1-4878-b121-c49c199dfa19";
        else if(inStr.equals("หมวดสัตว์อันตราย"))
            return "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/C.wav?alt=media&token=19ff261a-e63f-4005-9894-fa17924d90f4";
        else if(inStr.equals("หมวดสัตว์เลี้ยง"))
            return "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/B.wav?alt=media&token=df42709f-ac5d-4907-8163-ee52a0a729cc";
        else if(inStr.equals("หมวดอวัยวะในร่างกาย"))
            //return "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/F.wav?alt=media&token=cff65da3-dbc2-4f87-a890-2e0377eac4b3";
            return "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/F.wav?alt=media&token=8217fb71-f1f1-4e61-9173-290a3178bff0";
        else if(inStr.equals("หมวดการช่วยเหลือตนเอง"))
            return "https://firebasestorage.googleapis.com/v0/b/project1-98b7f.appspot.com/o/G.wav?alt=media&token=8c88adf5-147c-4567-a66f-1a3d787c47c1";
        else
            return "null";
    }

}
