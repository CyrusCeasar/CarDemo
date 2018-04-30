package com.csxd.cardemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

/**
 * 描述:
 * author:41264
 * createtime:04/30/18
 */


public class AIUIService implements AIUIListener {
    private static final String TAG = AIUIService.class.getSimpleName();

    private final AIUIAgent mAIUIAgent;
    private Context mContext;
    private HandleListener handleListener;



    private int mAIUIState;

    public AIUIService(Context context,HandleListener handleListener) {
        mContext = context;
        mAIUIAgent = AIUIAgent.createAgent(context, getAIUIParams(), this);
        AIUIMessage startMsg = new AIUIMessage(AIUIConstant.CMD_START
                , 0
                , 0
                , null
                , null);
        mAIUIAgent.sendMessage(startMsg);
        this.handleListener = handleListener;
    }

    public interface HandleListener{
        void onHandleResult(String result);
    }







    private void showTip(String str){
        Log.d(TAG,str);
    }

    @Override
    public void onEvent(AIUIEvent aiuiEvent) {
        Log.d(TAG, aiuiEvent.info + "--AIUI_EVENT--" + aiuiEvent.arg1);
        switch (aiuiEvent.eventType) {
            case AIUIConstant.EVENT_CONNECTED_TO_SERVER:
                showTip("已连接服务器");
                break;

            case AIUIConstant.EVENT_SERVER_DISCONNECTED:
                showTip("与服务器断连");
                break;

            case AIUIConstant.EVENT_WAKEUP:
                showTip( "进入识别状态" );
                break;

            case AIUIConstant.EVENT_RESULT: {
                try {
                    JSONObject bizParamJson = new JSONObject(aiuiEvent.info);
                    JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
                    JSONObject params = data.getJSONObject("params");
                    JSONObject content = data.getJSONArray("content").getJSONObject(0);

                    if (content.has("cnt_id")) {
                        String cnt_id = content.getString("cnt_id");
                        String cntStr = new String(aiuiEvent.data.getByteArray(cnt_id), "utf-8");

                        // 获取该路会话的id，将其提供给支持人员，有助于问题排查
                        // 也可以从Json结果中看到
                        String sid = aiuiEvent.data.getString("sid");
                        String tag = aiuiEvent.data.getString("tag");

                        showTip("tag=" + tag);

                        // 获取从数据发送完到获取结果的耗时，单位：ms
                        // 也可以通过键名"bos_rslt"获取从开始发送数据到获取结果的耗时
                        long eosRsltTime = aiuiEvent.data.getLong("eos_rslt", -1);
                     //   mTimeSpentText.setText(eosRsltTime + "ms");

                        if (TextUtils.isEmpty(cntStr)) {
                            return;
                        }

                        JSONObject cntJson = new JSONObject(cntStr);


                        String sub = params.optString("sub");
                        if ("nlp".equals(sub)) {
                            // 解析得到语义结果
                            String resultStr = cntJson.optString("intent");
                            Log.i( TAG, resultStr );
                            stopVoiceNlp();
                            handleListener.onHandleResult(resultStr);
                 //           mIvw.startListening(this);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            } break;
            case AIUIConstant.EVENT_ERROR: {
            } break;
            case AIUIConstant.EVENT_VAD: {
                if (AIUIConstant.VAD_BOS == aiuiEvent.arg1) {
                    showTip("找到vad_bos");
                } else if (AIUIConstant.VAD_EOS == aiuiEvent.arg1) {
                    showTip("找到vad_eos");
                } else {
                    showTip("" + aiuiEvent.arg2);
                }
            } break;

            case AIUIConstant.EVENT_START_RECORD: {
                showTip("已开始录音");
            } break;

            case AIUIConstant.EVENT_STOP_RECORD: {
                showTip("已停止录音");
            } break;

            case AIUIConstant.EVENT_STATE: {	// 状态事件
                mAIUIState = aiuiEvent.arg1;

                if (AIUIConstant.STATE_IDLE == mAIUIState) {
                    // 闲置状态，AIUI未开启
                    showTip("STATE_IDLE");
                } else if (AIUIConstant.STATE_READY == mAIUIState) {
                    // AIUI已就绪，等待唤醒
                    showTip("STATE_READY");
                } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                    // AIUI工作中，可进行交互
                    showTip("STATE_WORKING");
                }
            } break;

            case AIUIConstant.EVENT_CMD_RETURN: {
                if (AIUIConstant.CMD_SYNC == aiuiEvent.arg1) {	// 数据同步的返回
                    int dtype = aiuiEvent.data.getInt("sync_dtype", -1);
                    int retCode = aiuiEvent.arg2;

                    switch (dtype) {
                        case AIUIConstant.SYNC_DATA_SCHEMA: {
                            if (AIUIConstant.SUCCESS == retCode) {
                                // 上传成功，记录上传会话的sid，以用于查询数据打包状态
                                // 注：上传成功并不表示数据打包成功，打包成功与否应以同步状态查询结果为准，数据只有打包成功后才能正常使用
                                String mSyncSid = aiuiEvent.data.getString("sid");

                                // 获取上传调用时设置的自定义tag
                                String tag = aiuiEvent.data.getString("tag");

                                // 获取上传调用耗时，单位：ms
                                long timeSpent = aiuiEvent.data.getLong("time_spent", -1);
                                if (-1 != timeSpent) {
                                }

                                showTip("上传成功，sid=" + mSyncSid + "，tag=" + tag + "，你可以试着说“打电话给刘德华”");
                            } else {
                                showTip("上传失败，错误码：" + retCode);
                            }
                        } break;
                    }
                } else if (AIUIConstant.CMD_QUERY_SYNC_STATUS == aiuiEvent.arg1) {	// 数据同步状态查询的返回
                    // 获取同步类型
                    int syncType = aiuiEvent.data.getInt("sync_dtype", -1);
                    if (AIUIConstant.SYNC_DATA_QUERY == syncType) {
                        // 若是同步数据查询，则获取查询结果，结果中error字段为0则表示上传数据打包成功，否则为错误码
                        String result = aiuiEvent.data.getString("result");

                    }
                }
            } break;

            default:
                break;
        }
      /*  stopVoiceNlp();
        mIvw.startListening(this);*/
    }





    private String getAIUIParams() {
        String params = "";

        AssetManager assetManager = mContext.getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);

            JSONObject paramsJson = new JSONObject(params);

            params = paramsJson.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, params);
        return params;
    }





    public void startVoiceNlp(){
        if (null == mAIUIAgent) {
            return;
        }

        Log.i( TAG, "start voice nlp" );

        // 先发送唤醒消息，改变AIUI内部状态，只有唤醒状态才能接收语音输入
        // 默认为oneshot模式，即一次唤醒后就进入休眠。可以修改aiui_phone.cfg中speech参数的interact_mode为continuous以支持持续交互
//		if (AIUIConstant.STATE_WORKING != mAIUIState)
        {
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            mAIUIAgent.sendMessage(wakeupMsg);
        }

        // 打开AIUI内部录音机，开始录音。若要使用上传的个性化资源增强识别效果，则在参数中添加pers_param设置
        // 个性化资源使用方法可参见http://doc.xfyun.cn/aiui_mobile/的用户个性化章节
        // 在输入参数中设置tag，则对应结果中也将携带该tag，可用于关联输入输出
        String params = "sample_rate=16000,data_type=audio,pers_param={\"uid\":\"\"},tag=audio-tag";
        AIUIMessage startRecord = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null);

        mAIUIAgent.sendMessage(startRecord);
    }

    private void stopVoiceNlp(){
        if (null == mAIUIAgent) {
            return;
        }

        Log.i( TAG, "stop voice nlp" );
        // 停止录音
        String params = "sample_rate=16000,data_type=audio";
        AIUIMessage stopRecord = new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, params, null);

        mAIUIAgent.sendMessage(stopRecord);
    }
}
