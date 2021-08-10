package kr.co.bigsnow.core.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import kr.co.bigsnow.config.properties.BigsnowConfigProperties;
import kr.co.bigsnow.config.properties.BigsnowConfigProperties;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import kr.co.bigsnow.core.exception.BusinessException;

 
@Component
public class AES256EnDeUtil {

	@Autowired
	private BigsnowConfigProperties properties;

    private static Key keySpec;
    private static byte[] iv;

    public void init() {
    	byte[] keyBytes = new byte[16];
		int len = 0;
		byte[] b = null;
		b = properties.getAesKey().getBytes(StandardCharsets.UTF_8);

		len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		keySpec = new SecretKeySpec(keyBytes, "AES");
		this.iv = properties.getAesKey().substring(0, 16).getBytes();
    }

    // 암호화
    public String aesEncode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
    	init();
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));

        byte[] encrypted = c.doFinal(str.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64URLSafeString(encrypted);
    }

    // 복호화
    public String aesDecode(String str) {
    	init();
    	String encodedText = str.replace(" ", "+");
        Cipher c;
        String decrypt = "";
		try {
			c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
			byte[] byteStr = Base64.decodeBase64(encodedText);
			decrypt = new String(c.doFinal(byteStr), StandardCharsets.UTF_8);
		} catch (NoSuchAlgorithmException e) {
			throw new BusinessException("복호화에 실패하였습니다.");
		} catch (NoSuchPaddingException e) {
			throw new BusinessException("복호화에 실패하였습니다.");
		} catch (InvalidKeyException e) {
			throw new BusinessException("복호화에 실패하였습니다.");
		} catch (IllegalBlockSizeException e) {
			throw new BusinessException("복호화에 실패하였습니다.");
		} catch (BadPaddingException e) {
			throw new BusinessException("복호화에 실패하였습니다.");
		} catch (InvalidAlgorithmParameterException e) {
			throw new BusinessException("복호화에 실패하였습니다.");
		}

        return decrypt;
    }
}
