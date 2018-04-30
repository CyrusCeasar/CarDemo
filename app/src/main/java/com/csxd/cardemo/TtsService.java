package com.csxd.cardemo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.json.JSONObject;

/**
 * 描述:
 * author:41264
 * createtime:04/30/18
 */


public class TtsService implements InitListener,SynthesizerListener{
    private static final String TAG = TtsService.class.getSimpleName();
    private SpeechSynthesizer ssth;

    public TtsService(Context context){
        ssth =  SpeechSynthesizer.createSynthesizer(context,this);
    }

    @Override
    public void onInit(int i) {
        Log.d(TAG,"tts init reuslt:"+i);
        speak("语音合成引擎初始化完毕");
    }

    public void speak(String txt){
        ssth.startSpeaking(txt,this);
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {
        Log.d(TAG,speechError.getErrorDescription());
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }
}
