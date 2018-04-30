package com.csxd.cardemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import java.io.InputStream;

/**
 * 描述:
 * author:41264
 * createtime:04/30/18
 */


public class AsrService  implements RecognizerListener {

    private static final String TAG = AsrService.class.getSimpleName();
    private SpeechRecognizer mAsr;
    private String grmPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/msc/test";
    public AsrService(Context context){
        initAsr(context);
    }

    private void initAsr(Context mContext) {
        mAsr = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
                Log.d(TAG, "asr init result:" + i);
                if (i == 0) {

                }
            }
        });
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // 设置引擎类型
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        // 设置资源路径
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, OthersUtil.getResource(mContext, "asr/common.jet"));
        int ret = mAsr.buildGrammar("abnf", readFile(mContext, "wake_grammar_sample.abnf", "utf-8"), new GrammarListener() {
            @Override
            public void onBuildFinish(String s, SpeechError speechError) {
                if (speechError == null) {
                   // initWakeUpService(s);
                } else {
                    Log.w(TAG, "语法构建失败,错误码：" + speechError.getErrorCode());
                }

            }
        });
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG, "语法构建失败,错误码：" + ret);
        }

    }
    @Override
    public void onVolumeChanged(int i, byte[] bytes) {

    }

    @Override
    public void onBeginOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean b) {

    }

    @Override
    public void onError(SpeechError speechError) {

    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }
    /**
     * 读取asset目录下文件。
     *
     * @return content
     */
    public static String readFile(Context mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        AssetManager assetManager = mContext.getResources().getAssets();
        try {
            InputStream in = assetManager.open("bnf/" + file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
