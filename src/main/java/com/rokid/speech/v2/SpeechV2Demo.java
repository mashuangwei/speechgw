package com.rokid.speech.v2;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.testng.annotations.Test;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 作者: mashuangwei
 * 日期: 2017/9/6
 */
@Slf4j
public class SpeechV2Demo {
    String key = "rokid_test_key";
    String deviceTypeId = "rokid_test_device_type_id";
    String version = "2.0";
    String secret = "rokid_test_secret";
    String deviceId = "rokid_test_device_id";
    String url = "wss://apigwws.open.rokid.com/api";

    /**
     * asr为"若琪打开车门"，预期结果正常返回
     * no_nlp: false
     * vadmodleFlag: true
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     *
     */
    @Test
    public void case1() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/静音11秒.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh", path,"",160,true,false,true,500, "", 0);
        SpeechV2WebSocket.close();
        System.err.println(System.getProperty("user.dir"));
    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: false
     * vadmodleFlag: true
     * intermediate_asr: false
     * timeout: 500
     * voice_power: 160
     * 测试vad不上云
     */
    @Test
    public void case2() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/111.wav";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh", path,"若琪",160,false,true,true,500,"", 0);
        SpeechV2WebSocket.close();
    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: false
     * vadmodleFlag: false
     * intermediate_asr: false
     * timeout: 500
     * voice_power: 160
     * 测试vad不上云
     */
    @Test
    public void case3() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh", path,"若琪",160,false,false,false,500,"10", 1);
        SpeechV2WebSocket.close();

    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: false
     * vadmodleFlag: true
     * intermediate_asr: false
     * timeout: 500
     * voice_power: 160
     * activeWords: 的若琪
     * 测试激活词不正确的情况下返回是否为fake
     */
    @Test
    public void case4() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh", path,"的若琪",160,false,false,true,500,"10", 1);
        SpeechV2WebSocket.close();

    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: false
     * vadmodleFlag: false
     * intermediate_asr: false
     * timeout: 500
     * voice_power: 160
     * activeWords: 的若琪
     * 测试激活词不正确的情况下返回是否为fake
     */
    @Test
    public void case5() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh", path,"的若琪",160,false,false,false,500,"10", 1);
        SpeechV2WebSocket.close();

    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: false
     * vadmodleFlag: false
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: 的若琪
     * 测试激活词不正确的情况下返回是否为fake
     */
    @Test
    public void case6() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh", path,"的若琪",160,true,false,false,500,"10", 1);
        SpeechV2WebSocket.close();
    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: false
     * vadmodleFlag: true
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: 的若琪
     * 测试激活词不正确的情况下返回是否为fake
     */
    @Test
    public void case7() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh", path,"的若琪",160,true,false,true,500,"10", 1);
        SpeechV2WebSocket.close();
    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: true
     * vadmodleFlag: true
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: 的若琪
     * 测试激活词不正确的情况下返回是否为fake
     */
    @Test
    public void case8() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh", path,"的若琪",160,true,true,true,500,"10", 1);
        SpeechV2WebSocket.close();

    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: true
     * vadmodleFlag: true
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: null
     * 测试激活词不正确的情况下返回是否为none
     */
    @Test
    public void case9() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh", path,"",160,true,true,true,500,"10", 1);
        SpeechV2WebSocket.close();

    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: true
     * vadmodleFlag: false
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: null
     * 测试激活词为空的情况下返回none
     */
    @Test
    public void case10() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh", path,"",160,true,true,false,500,"10", 1);
        SpeechV2WebSocket.close();

    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: true
     * vadmodleFlag: false
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: 若琪abcde
     * 测试激活词不正确的情况下返回是否为none
     * 激活词内容一定要和语音文件的前x个字符相同,相同的话才会accept，不相同的话会fake
     */
    @Test
    public void case11() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh",path,"若琪abcd" ,160,true,true,false,500,"10", 1);
        SpeechV2WebSocket.close();
    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: true
     * vadmodleFlag: true
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: 若琪abcd
     * 测试激活词不正确的情况下返回是否为none
     * 激活词内容一定要和语音文件的前x个字符相同,相同的话才会accept，不相同的话会fake
     */
    @Test
    public void case12() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh",path,"若琪abcd" ,160,true,true,true,500,"10", 1);
        SpeechV2WebSocket.close();
    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: true
     * vadmodleFlag: true
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: 若琪abcd
     * 测试激活词不正确的情况下返回是否为fake
     * 激活词内容一定要和语音文件的前x个字符相同,相同的话才会accept，不相同的话会fake
     * codesc：22pcm  测试codesc不正确的情况
     *
     */
    @Test
    public void case13() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("22pcm", "zh",path,"若琪abcd" ,160,true,true,true,500,"10", 1);
        SpeechV2WebSocket.close();
    }

    /**
     * asr文件为"若琪打开车门"，预期结果正常返回
     * no_nlp: true
     * vadmodleFlag: true
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: 若琪
     * 测试激活词正确的情况下
     * 激活词内容一定要和语音文件的前x个字符相同,相同的话才会accept，不相同的话会fake
     * codesc：22pcm  测试codesc不正确的情况下
     */
    @Test
    public void case14() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        SpeechV2WebSocket.sendDataByTime("22pcm", "zh",path,"若琪" ,160,true,true,true,500,"10", 1);
        SpeechV2WebSocket.close();
    }

    /**
     * asr文件为"若琪,静音6秒"，预期结果正常返回
     * no_nlp: false
     * vadmodleFlag: true
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: 若琪
     * 测试激活词正确的情况下
     * 激活词内容一定要和语音文件的前x个字符相同,相同的话才会accept，不相同的话会fake
     * codesc：pcm
     * language: zh  为英文的暂不测试，功能不支持
     */
    @Test
    public void case15() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/blank.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh",path,"若琪" ,160,true,false,true,500,"10", 1);
        SpeechV2WebSocket.close();

    }

    /**
     * asr文件为"若琪,静音6秒"，预期结果正常返回
     * no_nlp: false
     * vadmodleFlag: true
     * intermediate_asr: true
     * timeout: 500
     * voice_power: 160
     * activeWords: 若琪
     * 测试激活词正确的情况下
     * 激活词内容一定要和语音文件的前x个字符相同,相同的话才会accept，不相同的话会fake
     * codesc：pcm
     * language: zh  为英文的暂不测试，功能不支持
     */
    @Test
    public void case16() {
        SpeechV2WebSocket SpeechV2WebSocket = null;
        try {
            SpeechV2WebSocket = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        SpeechV2WebSocket.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/blank.pcm";
        SpeechV2WebSocket.sendDataByTime("pcm", "zh",path,"若琪" ,160,true,true,true,500,"10", 1);

        SpeechV2WebSocket.close();

    }

    /**
     * 语音文件为，"若琪，静音6秒"
     */
    @Test
    public void testSpeechTimeout() {
        SpeechV2WebSocket webSock = null;
        try {
            webSock = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        webSock.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/blank.pcm";
        webSock.sendDataByTime("pcm", "zh", path,"若琪",160,false,false,true,8000,"10", 1);
        webSock.close();
    }

    /**
     * 语音文件为，"若琪，打开车门"。
     */
    @Test(enabled = false)
    public void testSpeechOnlyWithActiveWords() {
        SpeechV2WebSocket webSock = null;
        try {
            webSock = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        webSock.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        webSock.sendDataByTime("pcm", "zh", path,"若琪",160,false,false,true,1000,"10", 1);
        webSock.close();
    }

    /**
     * 测试立即返回，语音文件实时传送
     */
    @Test
    public void testSpeechvWithContinueIntermediateAsr() {
        SpeechV2WebSocket webSock = null;
        try {
            webSock = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        webSock.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/time.pcm";
        webSock.sendDataByTime("pcm", "zh", path,"若琪",100,true,true,true,500,"10", 1);
        webSock.close();
    }

    /**
     * 测试立即返回，语音文件实时传送
     */
    @Test
    public void testSpeechvWithContinueIntermediateAsrNoNlp() {
        SpeechV2WebSocket webSock = null;
        try {
            webSock = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        webSock.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/time.pcm";
        webSock.sendDataByTime("pcm", "zh", path,"若琪",100,false,false,true,500,"10", 1);
        webSock.close();
    }


    /**
     *
     */
    public void testSpeechvWithNoTriggerContinue( float power) {
        SpeechV2WebSocket webSock = null;
        try {
            webSock = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        webSock.init(key, deviceTypeId, version, secret,deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        webSock.sendDataByTime("pcm", "zh", path,"若琪",power,false,true,true,500,"10", 1);
        webSock.close();
    }

    /**
     *
     */
    @Test
    public void testSpeechvWithNoTriggerContinueWithNoNlp() {
        SpeechV2WebSocket webSock = null;
        try {
            webSock = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        webSock.init(key, deviceTypeId, version, secret,deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        webSock.sendDataByTime("pcm", "zh", path,"若琪",100,false,false,false,500,"10", 1);
        webSock.close();
    }


    /**
     * 有时{"activation":"fake"}，有时{"activation":"accept"}，什么原因？
     */
    @Test
    public void testSpeechvWithLargePower() {
        SpeechV2WebSocket webSock = null;
        try {
            webSock = new SpeechV2WebSocket(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        webSock.init(key, deviceTypeId, version, secret, deviceId);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/car.pcm";
        webSock.sendDataByTime("pcm", "zh", path,"若琪",1000,true,false,true,500, "10",1);
        webSock.close();
    }

}
