package com.csxd.cardemo;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * 描述:
 * author:41264
 * createtime:04/30/18
 */


public class CarDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn
// 请勿在“=”与appid之间添加任何空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5ae6ba9d");
        AICenter aiCenter = new AICenter(this);
    }
}
