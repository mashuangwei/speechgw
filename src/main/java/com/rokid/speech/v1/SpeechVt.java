package com.rokid.speech.v1;


import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rokid.common.SpeechSign;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import rokid.open.speech.Auth;
import rokid.open.speech.v1.SpeechTypes;
import rokid.open.speech.v1.SpeechV1;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 作者: mashuangwei
 * 日期: 2017/9/18
 * 功能： speech demo
 */
@Slf4j
public class SpeechVt extends WebSocketClient {

    SpeechV1.SpeechRequest speechRequestStart;
    SpeechV1.SpeechRequest speechRequestVoi;
    SpeechV1.SpeechRequest speechRequestEnd;
    SpeechV1.SpeechRequest speechRequestText;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    //每次发送都是以这个值来表示建立的连接标识
    private int sendId = 0;

    public SpeechVt(URI serverURI) {
        super(serverURI);
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLSocketFactory factory = sslContext.getSocketFactory();
        try {
            this.setSocket(factory.createSocket());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 认证登录
     *
     * @param key
     * @param deviceTypeId
     * @param version
     * @param secret
     * @param deviceId
     */
    public void init(String key, String deviceTypeId, String version, String secret, String deviceId) {
        long time = System.currentTimeMillis();
        String src = "key=" + key + "&device_type_id=" + deviceTypeId + "&device_id=" + deviceId + "&service=speech&version=" + version + "&time=" + time + "&secret=" + secret;
        String sign = SpeechSign.getMD5(src);
        this.connect();
        try {
            // 建立连接超时时间自己定义
            boolean connectFlag = countDownLatch.await(10, TimeUnit.SECONDS);
            if (!connectFlag) {
                log.error("Speech websocket建立连接失败");
                // 根据自己需求做逻辑处理
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Auth.AuthRequest authRequest = Auth.AuthRequest.newBuilder().setDeviceId(deviceId).setDeviceTypeId(deviceTypeId).setKey(key).setService("speech").setVersion(version).setTimestamp("" + time).setSign(sign).build();
        this.send(authRequest.toByteArray());
        try {
            // 登录超时时间自己定义
            countDownLatch = new CountDownLatch(1);
            boolean authFlag = countDownLatch.await(10, TimeUnit.SECONDS);
            if (!authFlag) {
                log.error("Speech auth failed");
                // 根据自己需求做逻辑处理
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟设备端不断的向服务器发送语音
     *
     * @param codec
     * @param language
     * @param fileurl
     * @param activeWords
     * @param voice_power
     * @param intermediate_asr
     * @param no_nlp
     */
    public void sendDataByTime(String codec, String language, String fileurl, String activeWords, String voice_power, int intermediate_asr, int no_nlp) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("voice_power", voice_power);
        //用户使用时一般也是为0
        jsonObject.put("trigger_start", "0");
        //用户使用时一般也是为9600, 这个长度是指语音数据的长度要把激活词包括进来
        jsonObject.put("trigger_length", "9600");
        jsonObject.put("voice_trigger", activeWords);
        jsonObject.put("intermediate_asr", intermediate_asr);
        jsonObject.put("no_nlp", no_nlp);

        log.info("speech client send params: {}", jsonObject.toString());
        Random random = new Random();
        sendId = random.nextInt();
        speechRequestStart = SpeechV1.SpeechRequest.newBuilder().setId(sendId).setCodec(codec).setLang(language).setType(SpeechTypes.ReqType.START).setFrameworkOptions(jsonObject.toString()).build();
        this.send(speechRequestStart.toByteArray());

        FileInputStream fileInput = null;
        try {
            File file = new File(fileurl);
            byte[] buffer = new byte[9600];
            fileInput = new FileInputStream(file);
            int byteread = 0;
            // byteread表示一次读取到buffers中的数量。
            while ((byteread = fileInput.read(buffer)) != -1) {
                speechRequestVoi = SpeechV1.SpeechRequest.newBuilder().setId(sendId).setCodec(codec).setLang(language).setType(SpeechTypes.ReqType.VOICE).setVoice(ByteString.copyFrom(buffer)).build();
                this.send(speechRequestVoi.toByteArray());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInput != null) {
                    fileInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        speechRequestEnd = SpeechV1.SpeechRequest.newBuilder().setId(sendId).setCodec(codec).setLang(language).setType(SpeechTypes.ReqType.END).build();
        this.send(speechRequestEnd.toByteArray());
        try {
            countDownLatch = new CountDownLatch(1);
            countDownLatch.await(200, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("send data finished!");

    }

    /**
     * 发送文本内容
     *
     * @param tts
     */
    public void sendSpeechText(String tts) {
        speechRequestText = SpeechV1.SpeechRequest.newBuilder().setId(new Random().nextInt()).setLang("zh").setAsr(tts).setType(SpeechTypes.ReqType.TEXT).build();
        this.send(speechRequestText.toByteArray());
        try {
            countDownLatch = new CountDownLatch(1);
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        countDownLatch.countDown();
        log.info("Connected");
    }

    @Override
    public void onMessage(String message) {
        log.info("got: {}", message);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        byte[] byteMessage = message.array();
        Auth.AuthResponse authResponse = null;
        SpeechV1.SpeechResponse spResponse;

        //第一次接收到的消息其实都是登录，这边只是巧合可以通过长度可以判断是不是登录。也可以用第一次建立连接后第一次接收消息做为判断auth
        if (byteMessage.length == 2) {
            countDownLatch.countDown();
            try {
                authResponse = Auth.AuthResponse.parseFrom(byteMessage);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

            log.info("Auth Result is " + authResponse.getResult());
            if (authResponse.getResult().equals(Auth.AuthErrorCode.AUTH_FAILED)) {
                log.error("Auth Result is {}", authResponse.getResult());
                this.onClose(1006, "AUTH_FAILED", true);
            }
        } else {
            try {
                spResponse = SpeechV1.SpeechResponse.parseFrom(byteMessage);
                log.info("getAction: {}", spResponse.getAction());
                log.info("getAsr: {}", spResponse.getAsr());
                log.info("getExtra: {}", spResponse.getExtra());
                log.info("getNlp: {}", spResponse.getNlp());
                log.info("getResult: {}", spResponse.getResult());
                log.info("getResult: {}", spResponse.getResult());
                log.info("getFinish: {}", spResponse.getFinish());
                if (spResponse.getFinish()) {
                    countDownLatch.countDown();
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Disconnected,reason: {}", reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

}
