package com.bookmarker.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class KeyGeneratorSample {


    public static void main (String args[]){

        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.init(256); // for example
        SecretKey secretKey = keyGen.generateKey();
        System.out.println(secretKey.getEncoded());
    }
}
