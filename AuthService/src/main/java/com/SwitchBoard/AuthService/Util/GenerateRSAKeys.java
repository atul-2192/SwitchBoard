package com.SwitchBoard.AuthService.Util;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Base64;

public class GenerateRSAKeys {

    public static void main(String[] args) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(pair.getPrivate().getEncoded()) +
                "\n-----END PRIVATE KEY-----";
        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(pair.getPublic().getEncoded()) +
                "\n-----END PUBLIC KEY-----";

        Files.write(Paths.get("private.pem"), privateKeyPem.getBytes());
        Files.write(Paths.get("public.pem"), publicKeyPem.getBytes());

        System.out.println("Keys generated: private.pem & public.pem");
    }
}

