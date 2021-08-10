package kr.co.bigsnow.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class EncodeUtils {

    /**
     * <pre>
     * SHA256 암호화 처리
     * </pre>
     *
     */
    public static String encodeSHA256(String str) {
        String encodedString;

        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(str.getBytes());
            byte[] byteData = sha.digest();
            StringBuilder sb = new StringBuilder();

            for (byte byteDatum : byteData) {
                sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
            }

            encodedString = sb.toString();
        }
        catch (RuntimeException | NoSuchAlgorithmException e) {
            log.warn("encode exception : ", e);
            encodedString = null;
        }

        return encodedString;
    }

    /**
     * <pre>
     * 16진수 문자열을 바이트 배열로 변환
     * </pre>
     *
     */
    @SuppressWarnings("unused")
	private static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];

        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return ba;
    }

    /**
     * <pre>
     * unsigned byte(바이트) 배열을 16진수 문자열로 변경
     * </pre>
     *
     */
    @SuppressWarnings("unused")
	private static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder(ba.length * 2);
        String hexNumber;

        for (byte b : ba) {
            hexNumber = "0" + Integer.toHexString(0xff & b);
            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }

        return sb.toString();
    }

}
