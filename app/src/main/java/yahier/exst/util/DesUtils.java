package yahier.exst.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.util.Base64;


public class DesUtils {
	
	private final static String key ="stbl.com";
	
	/**
	 *  加密
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String EncryptAsDoNet(String message) throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		byte[] encryptbyte = cipher.doFinal(message.getBytes());
		return new String(Base64.encode(encryptbyte, Base64.DEFAULT));
	}
}
