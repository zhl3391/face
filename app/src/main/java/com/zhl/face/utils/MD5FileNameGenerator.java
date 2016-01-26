package com.zhl.face.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5FileNameGenerator {

    public MD5FileNameGenerator() {
    }

    public String generate(String key) {
        String cacheKey;
        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(key.getBytes());
            cacheKey = this.bytesToHexString(e.digest());
        } catch (NoSuchAlgorithmException var4) {
            cacheKey = String.valueOf(key.hashCode());
        }

        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(255 & bytes[i]);
            if(hex.length() == 1) {
                sb.append('0');
            }

            sb.append(hex);
        }

        return sb.toString();
    }
}
