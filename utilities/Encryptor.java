package utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import system.Logger;

public class Encryptor {

	public static byte[] encryptString(String strToEncrypt, String pass) {
		try {
			try {
				byte[] key=pass.getBytes("UTF-8");
				SecretKeySpec secretKey = null;
				MessageDigest sha = null;
				try {
					sha = MessageDigest.getInstance("SHA-1");
					key = sha.digest(key);
					key = Arrays.copyOf(key, 16);
					secretKey = new SecretKeySpec(key, "AES");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				
				// Create key and cipher
				Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
				// encrypt the text
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
				byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
				return encrypted;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Logger.staticLog(e);
		}
		return null;
	}
	
	public static String decryptString(byte[] strToDecrypt, String pass) {
		try {
			try {
				byte[] key=pass.getBytes("UTF-8");
				SecretKeySpec secretKey = null;
				MessageDigest sha = null;
				try {
					sha = MessageDigest.getInstance("SHA-1");
					key = sha.digest(key);
					key = Arrays.copyOf(key, 16);
					secretKey = new SecretKeySpec(key, "AES");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				
				// Create key and cipher
				Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
				// encrypt the text
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
				byte[] encrypted = cipher.doFinal(strToDecrypt);
				return new String(encrypted, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Logger.staticLog("Error while decrypting:"+e.toString(), Logger.WARNING);
		}
		return null;
	}

}
