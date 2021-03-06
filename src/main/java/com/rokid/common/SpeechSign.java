package com.rokid.common;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 作者: mashuangwei
 * 日期: 2017/9/18
 */

@Slf4j
public class SpeechSign {

    public static String getMD5(String sign) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(sign.getBytes());
        String md5Value = Hashing.md5().hashString(sign, Charsets.UTF_8).toString();
        log.info("sign md5 is " + md5Value);
        return md5Value;
    }

}
