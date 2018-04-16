package com.rokid.speech.v2;


import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rokid.common.SpeechSign;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import rokid.open.speech.Auth;
import rokid.open.speech.v1.SpeechTypes;
import rokid.open.speech.v2.SpeechV2;

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
 * 日期: 2017/9/6
 */
@Data
@Slf4j
public class SpeechV2WebSocket extends WebSocketClient {

    SpeechV2.SpeechRequest speechRequestStart;
    SpeechV2.SpeechRequest speechRequestVoi;
    SpeechV2.SpeechRequest speechRequestEnd;
    SpeechV2.SpeechRequest speechRequestT;
    JSONObject resultJson = new JSONObject();


    protected int sendId = 0;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public SpeechV2WebSocket(URI serverURI) {
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

    public void init(String key, String deviceTypeId, String version, String secret, String deviceId) {
        long time = System.currentTimeMillis();
        String beforeSign = "key=" + key + "&device_type_id=" + deviceTypeId + "&device_id=" + deviceId + "&service=speech&version=" + version + "&time=" + time + "&secret=" + secret;
        String sign = SpeechSign.getMD5(beforeSign);

        this.connect();
        try {
            if (!countDownLatch.await(10, TimeUnit.SECONDS)) {
                log.info("connect timeout");
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Auth.AuthRequest authRequest = Auth.AuthRequest.newBuilder().setDeviceId(deviceId).setDeviceTypeId(deviceTypeId).setKey(key).setService("speech").setVersion(version).setTimestamp("" + time).setSign(sign).build();
        this.send(authRequest.toByteArray());
        countDownLatch = new CountDownLatch(1);
        try {
            if (!countDownLatch.await(10, TimeUnit.SECONDS)) {
                log.info("auth timeout");
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void sendSpeechT(String tts) {
        speechRequestT = SpeechV2.SpeechRequest.newBuilder().setId(new Random().nextInt()).setAsr(tts).setType(SpeechTypes.ReqType.TEXT).build();
        this.send(speechRequestT.toByteArray());
        countDownLatch = new CountDownLatch(1);
        try {
            if (!countDownLatch.await(10, TimeUnit.SECONDS)) {
                log.info("sent speecht timeout");
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendDataByTime(String codec, String language, String fileurl, String activeWords,
                               float voice_power, boolean intermediate_asr, boolean no_nlp,
                               boolean vadmodleFlag, int timeout, String voice_extra, int vad_begin) {
        countDownLatch = new CountDownLatch(1);

        Random random = new Random();
        sendId = random.nextInt();
        SpeechV2.VadMode vadmodle = null;
        SpeechV2.Lang lang = null;
        SpeechTypes.Codec codec1 = null;
        if (codec.equalsIgnoreCase("pcm")) {
            codec1 = SpeechTypes.Codec.PCM;
        } else if (codec.equalsIgnoreCase("opu")) {
            codec1 = SpeechTypes.Codec.OPU;
        } else {
            codec1 = SpeechTypes.Codec.OPU2;
        }
        if (language.equalsIgnoreCase("zh")) {
            lang = SpeechV2.Lang.ZH;
        } else {
            lang = SpeechV2.Lang.EN;
        }
        if (vadmodleFlag) {
            vadmodle = SpeechV2.VadMode.CLOUD;
        } else {
            vadmodle = SpeechV2.VadMode.LOCAL;
        }
        JSONObject threholdJson = new JSONObject();
        threholdJson.put("threhold_energy", voice_extra);
        speechRequestStart = SpeechV2.SpeechRequest.newBuilder().setId(sendId).setType(SpeechTypes.ReqType.START)
                .setOptions(SpeechV2.SpeechOptions.newBuilder()
                        .setCodec(codec1).setLang(lang).setNoIntermediateAsr(intermediate_asr).setVoiceExtra(threholdJson.toString()).setVadBegin(vad_begin)
                        .setNoNlp(no_nlp).setVadMode(vadmodle).setVendTimeout(timeout).setVoicePower(voice_power).setVoiceTrigger(activeWords)
                        .setTriggerStart(0).setTriggerLength(9600)).build();
        this.send(speechRequestStart.toByteArray());
        FileInputStream fileInput = null;
        byte[] asrContent = null;
        try {
            File file = new File(fileurl);
            byte[] buffer = new byte[1280];
            fileInput = new FileInputStream(file);
            int byteread = 0;
            // byteread表示一次读取到buffers中的数量。
            int i = 0;
            while ((byteread = fileInput.read(buffer)) != -1) {
                asrContent = buffer;
                speechRequestVoi = SpeechV2.SpeechRequest.newBuilder().setId(sendId).setType(SpeechTypes
                        .ReqType.VOICE).setVoice(ByteString.copyFrom(asrContent)).build();
                this.send(speechRequestVoi.toByteArray());
                // 可以添加sleep时间，表示多久发一次语音数据，主要是为看来模拟实时语音发送，逻辑用户根据需要自己处理
                Thread.sleep(40);
//                System.err.println(++i);
            }
        } catch (Exception e) {
            log.info("读语音文件出错");
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
        if (!vadmodleFlag) {
            speechRequestEnd = SpeechV2.SpeechRequest.newBuilder()
                    .setId(sendId)
                    .setType(SpeechTypes.ReqType.END)
                    .build();
            log.info("send end");
            this.send(speechRequestEnd.toByteArray());
        }

        try {
            if (!countDownLatch.await(20, TimeUnit.SECONDS)) {
                log.info("send speechv timeout");
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("send data finished!");

    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Connected");
        countDownLatch.countDown();
    }

    @Override
    public void onMessage(String message) {
        log.info("got: " + message);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        byte[] byteMessage = message.array();
        Auth.AuthResponse authResponse = null;
        SpeechV2.SpeechResponse spResponse = null;

        if (byteMessage.length == 2) {
            try {
                authResponse = Auth.AuthResponse.parseFrom(byteMessage);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

            log.info("Auth Result is " + authResponse.getResult());
            if (authResponse.getResult().equals(Auth.AuthErrorCode.AUTH_FAILED)) {
                log.info("Auth Result is " + authResponse.getResult());
                this.onClose(1006, "AUTH_FAILED", true);
            }
            countDownLatch.countDown();
        } else {
            try {
                log.info("bytemessage: {}", byteMessage);
                spResponse = SpeechV2.SpeechResponse.parseFrom(byteMessage);
                log.info("getAction: " + spResponse.getAction());
                log.info("getAsr: " + spResponse.getAsr());
                log.info("getExtra: " + spResponse.getExtra());
                log.info("getNlp: " + spResponse.getNlp());
                log.info("getResult: " + spResponse.getResult());
                log.info("getType: " + spResponse.getType());
                log.info("****************************");
                resultJson.put("action", spResponse.getAction());
                if (spResponse.getType().equals(SpeechV2.RespType.ASR_FINISH)) {
                    resultJson.put("asr", spResponse.getAsr());
                }
                if (spResponse.getExtra().length() > 1) {
                    resultJson.put("extra", spResponse.getExtra());
                } else {
                    resultJson.put("extra", "");
                }
                resultJson.put("nlp", spResponse.getNlp());
                if (!spResponse.getResult().toString().equalsIgnoreCase("SUCCESS")){
                    resultJson.put("result", spResponse.getResult());
                } else {
                    resultJson.put("result", spResponse.getResult());
                }
                resultJson.put("type", spResponse.getType());

                if (spResponse.getType().equals(SpeechV2.RespType.FINISH)) {
                    countDownLatch.countDown();
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Disconnected" + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

}

