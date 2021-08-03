package com.weds.devmanages.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA非对称加密算法工具类
 * 超117字节后采用分段加密
 * @since: 2019年10月8日  上午10:22:22
 * @history:
 */
public class RSAUtil3 {

    private static final String  ALGORITHM         = "RSA";
    private static final String  PUBLICK_EY        = "PUBLICK_EY";
    private static final String  PRIVATE_KEY       = "PRIVATE_KEY";
    /**
     * 加密算法
     */
    private static final String  CIPHER_DE         = "RSA";
    /**
     * 解密算法
     */
    private static final String  CIPHER_EN         = "RSA";
    /**
     * 密钥长度
     */
    private static final Integer KEY_LENGTH        = 512;

    /**
     * RSA最大加密明文大小
     */
    private static final int     MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA最大解密密文大小
     */
    private static final int     MAX_DECRYPT_BLOCK = 512;

    /**
     * 生成秘钥对，公钥和私钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, Object> genKeyPair() throws NoSuchAlgorithmException {
        Map<String, Object> keyMap = new HashMap<String, Object>();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_LENGTH); // 秘钥字节数
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        keyMap.put(PUBLICK_EY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 获取公钥
     *
     * @param keyMap
     * @return
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLICK_EY);
        String str = new String(Base64.encodeBase64(key.getEncoded()));
        return str;
    }

    /**
     * 获取私钥
     *
     * @param keyMap
     * @return
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        String str = new String(Base64.encodeBase64(key.getEncoded()));
        return str;
    }

    /***
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception 
     * @create  2019年10月8日 下午7:21:38 luochao
     * @history
     */
    public static String encryptByPublicKey(String data, String publicKey) throws Exception {
        byte[] decodeBase64Data = data.getBytes();
        byte[] decodeBase64Key = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decodeBase64Key);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key key = keyFactory.generatePublic(x509EncodedKeySpec);
        return encrypt(key, decodeBase64Data);
    }

    /**
     * 私钥加密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception 
     * @create  2020年1月15日 下午7:55:08 luochao
     * @history
     */
    public static String encryptByPrivateKey(String data, String privateKey) throws Exception {
        byte[] decodeBase64Data = data.getBytes();
        byte[] decodeBase64Key = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(decodeBase64Key);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey key = keyFactory.generatePrivate(pKCS8EncodedKeySpec);
        return encrypt(key, decodeBase64Data);
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception 
     * @create  2020年1月15日 上午11:29:23 luochao
     * @history
     */
    public static String decryptByPrivateKey(String data, String privateKey) throws Exception {
        byte[] decodeBase64Data = Base64.decodeBase64(data.getBytes());
        byte[] decodeBase64Key = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(decodeBase64Key);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key key = keyFactory.generatePrivate(pKCS8EncodedKeySpec);
        return decrypt(key, decodeBase64Data);
    }

    /**
     * 公钥解密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception 
     * @create  2020年1月15日 上午11:29:37 luochao
     * @history
     */
    public static String decryptByPublicKey(String data, String publicKey) throws Exception {
        byte[] decodeBase64Data = Base64.decodeBase64(data.getBytes());
        byte[] decodeBase64Key = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decodeBase64Key);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key key = keyFactory.generatePublic(x509EncodedKeySpec);
        return decrypt(key, decodeBase64Data);
    }

    /**
     * 分段加密
     *
     * @param key 加密key
     * @param bytes 被加密字节数组
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException 
     * @create  2020年1月15日 上午11:28:15 luochao
     * @history
     */
    private static String encrypt(Key key, byte[] bytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
                                                         IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance(CIPHER_EN);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        int inputLength = bytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] encryptedData;
        try {
            int offset = 0;
            byte[] cache;
            int i = 0;
            while (inputLength - offset > 0) {
                if (inputLength - offset > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(bytes, offset, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(bytes, offset, inputLength - offset);
                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i * MAX_ENCRYPT_BLOCK;
            }
            encryptedData = out.toByteArray();
        } finally {
            out.close();
        }

        return new String(Base64.encodeBase64(encryptedData));
    }

    /**
     * 分段解密
     *
     * @param key 解密key
     * @param bytes 被解密字节数组
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException 
     * @create  2020年1月15日 上午11:26:23 luochao
     * @history
     */
    private static String decrypt(Key key, byte[] bytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
                                                         IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance(CIPHER_DE);
        cipher.init(Cipher.DECRYPT_MODE, key);
        int inputLength = bytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] decryptedData;
        try {
            int offset = 0;
            byte[] cache;
            int i = 0;
            while (inputLength - offset > 0) {
                if (inputLength - offset > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(bytes, offset, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(bytes, offset, inputLength - offset);
                }
                out.write(cache);
                i++;
                offset = i * MAX_DECRYPT_BLOCK;
            }
            decryptedData = out.toByteArray();
        } finally {
            out.close();
        }
        return new String(decryptedData);
    }

    /**
    * 单文件测试入口
    *
    * @param args
    * @throws Exception 
    * @create  2020年2月18日 下午4:13:35 luochao
    * @history
    */
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        Map<String, Object> keyMap = RSAUtil3.genKeyPair();
        String publicKey = RSAUtil3.getPublicKey(keyMap);
        String privateKey = RSAUtil3.getPrivateKey(keyMap);
        System.out.println("公钥：" + publicKey);
        System.out.println("私钥：" + privateKey);
        long start1 = System.currentTimeMillis();
        System.out.println(start1 - start);
        // 公钥加密  
        String sourceStr = "<REQ><HEAD><ReqCode>WDIS1234566785645674567";
        System.out.println("加密前：" + sourceStr);
        String encryptStr = RSAUtil3.encryptByPublicKey(sourceStr, publicKey);
        System.out.println("公钥加密后：" + encryptStr);
        System.out.println("长度：" + encryptStr.length());
        long start2 = System.currentTimeMillis();
        System.out.println(start2 - start1);
        // 私钥解密  
        String sourceStr_1 = RSAUtil3.decryptByPrivateKey(encryptStr, privateKey);
        System.out.println("私钥解密后：" + sourceStr_1);
        long start3 = System.currentTimeMillis();
        System.out.println(start3 - start2);

        String encryptByPrivateKey = RSAUtil3.encryptByPrivateKey(sourceStr, privateKey);
        System.out.println("私钥加密后：" + encryptByPrivateKey);
        String decryptByPublicKey = RSAUtil3.decryptByPublicKey(encryptByPrivateKey, publicKey);
        System.out.println("公钥解密后：" + decryptByPublicKey);
    }
}
