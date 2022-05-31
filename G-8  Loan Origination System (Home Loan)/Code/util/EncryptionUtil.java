package com.kuliza.workbench.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class EncryptionUtil {

  public static String encryptString(String key, String rawString) {
    String output = null;
    try {
      byte[] res = Hex.decodeHex(key.toCharArray());
      PublicKey publicKey =
          KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(res));
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.PUBLIC_KEY, publicKey);
      output = Base64.encodeBase64String(cipher.doFinal(rawString.getBytes("UTF-8")));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return output;
  }

  public static String getDecryptByPrivateString(String key, String input) {
    String output = null;
    try {
      byte[] privateKeyBytes = Hex.decodeHex(key.toCharArray());
      PrivateKey privateKey =
          KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      output = new String(cipher.doFinal(Base64.decodeBase64(input)), "UTF-8");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return output;
  }

  public static void main(String[] args) {
    String key = "A4586130";
    String rawString = "12345";
    String output = null;
    try {
      byte[] res = Hex.decodeHex(key.toCharArray());
      System.out.println(res);
      PublicKey publicKey =
          KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(res));
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.PUBLIC_KEY, publicKey);
      output = Base64.encodeBase64String(cipher.doFinal(rawString.getBytes("UTF-8")));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    System.out.println(output);
  }
}
