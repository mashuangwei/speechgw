package com.rokid.speech.v1;

import com.rokid.speech.v1.SpeechVt;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 作者: mashuangwei
 * 日期: 2017/8/22
 */

public class SpeechVTDemo {

    // 以下几个值从rokid开放平台获取或者rokid提供

    String key = "9C4D6BEB6448468FB73E75A2C33E6ADE";
    String deviceTypeId = "98EA4B548AEB4A329D21615B9ED060E5";
    String version = "1.0";
    String secret = "32F7A304CE8740C5BD61F587F7DD7B88";
    String deviceId = "0201021711000210";
    String url = "wss://apigwws-daily.open.rokid.com/api";

    /**
     * 直接发送文本（对比直接发送语音数据，服务端少了一个asr识别语音为文字过程）
     */
    @Test
    public void testSpeechText() {
        SpeechVt webSock = null;
        try {
            webSock = new SpeechVt(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        webSock.init(key, deviceTypeId, version, secret, deviceId);
        webSock.sendSpeechText("若琪今天天气怎么样");
        webSock.close();

    }

    /**
     * 发送语音数据
     */
    @Test
    public void testSpeechVoice() {
        SpeechVt webSock = null;
        try {
            webSock = new SpeechVt(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        webSock.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        webSock.sendDataByTime("pcm", "zh", path, "", "100", 1, 1);
        webSock.close();
    }

}
