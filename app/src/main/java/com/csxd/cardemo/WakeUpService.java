package com.csxd.cardemo;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述:
 * author:41264
 * createtime:04/30/18
 */


public class WakeUpService implements WakeuperListener {
    private static final String TAG = WakeUpService.class.getSimpleName();
    private VoiceWakeuper mIvw;
    private String threshStr = "门限值：";
    private int curThresh = 10;
    private String keep_alive = "1";
    private String ivwNetMode = "0";

    private WakeListener wakeListener;

    public WakeUpService(Context context,WakeListener wakeListener){
        mIvw = VoiceWakeuper.createWakeuper(context, new InitListener() {
            @Override
            public void onInit(int i) {
                Log.i(TAG, "wakeup:" + i);
            }
        });
        initWakeUpService(context);
        this.wakeListener = wakeListener;
    }

    public interface WakeListener{
        void onWakeUp();
    }

    @Override
    public void onBeginOfSpeech() {
        Log.d(TAG, "begin");
    }

    @Override
    public void onResult(WakeuperResult wakeuperResult) {
        String resultString;
        try {
            String text = wakeuperResult.getResultString();
            JSONObject object;
            object = new JSONObject(text);
            StringBuffer buffer = new StringBuffer();
            buffer.append("【RAW】 " + text);
            buffer.append("\n");
            buffer.append("【操作类型】" + object.optString("sst"));
            buffer.append("\n");
            buffer.append("【唤醒词id】" + object.optString("id"));
            buffer.append("\n");
            buffer.append("【得分】" + object.optString("score"));
            buffer.append("\n");
            buffer.append("【前端点】" + object.optString("bos"));
            buffer.append("\n");
            buffer.append("【尾端点】" + object.optString("eos"));
            resultString = buffer.toString();
            mIvw.stopListening();
            wakeListener.onWakeUp();
        } catch (JSONException e) {
            resultString = "结果解析出错";
            e.printStackTrace();
        }
        Log.d(TAG, resultString);
    }

    @Override
    public void onError(SpeechError speechError) {
        Log.d(TAG, speechError.getErrorDescription());
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {
  /*      stopVoiceNlp();
        mIvw.startListening(this);*/
    }


    @Override
    public void onVolumeChanged(int i) {

    }

    private void initWakeUpService(Context context) {
        // 清空参数
        mIvw.setParameter(SpeechConstant.PARAMS, null);
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
        // 设置唤醒模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
        // 设置持续进行唤醒
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
        // 设置闭环优化网络模式
        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
        mIvw.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置本地识别资源

        // 设置唤醒录音保存路径，保存最近一分钟的音频
        mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath() + "/msc/ivw.wav");
//        mIvw.setParameter(ResourceUtil.GRM_BUILD_PATH, localGrammId);
        // 设置唤醒资源路径
        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, OthersUtil.getResource(context, "ivw/5ae6ba9d.jet"));
        // 设置唤醒录音保存路径，保存最近一分钟的音频
//        mIvw.setParameter( SpeechConstant.IVW_AUDIO_PATH, getResource("msc/ivw.wav") );
        mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
        //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );

        // 启动唤醒
        startListen();

    }

    public void startListen(){
        mIvw.startListening(this);
    }
}
