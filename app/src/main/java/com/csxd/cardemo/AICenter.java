package com.csxd.cardemo;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述:
 * author:41264
 * createtime:04/30/18
 */


public class AICenter {

    private Context context;
    private AIUIService aiuiService;
    private TtsService ttsService;
    private WakeUpService wakeUpService;
    public AICenter(Context context){
        this.context = context;
        aiuiService = new AIUIService(context, new AIUIService.HandleListener() {
            @Override
            public void onHandleResult(String result) {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONObject answer = (JSONObject) jsonObject.get("answer");
                    String content = answer.getString("text");
                    ttsService.speak(content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                wakeUpService.startListen();
            }
        });
         ttsService= new TtsService(context);
         wakeUpService = new WakeUpService(context, new WakeUpService.WakeListener() {
            @Override
            public void onWakeUp() {
                aiuiService.startVoiceNlp();
            }
        });
    }

}
