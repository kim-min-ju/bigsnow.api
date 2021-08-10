package kr.co.bigsnow.core.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import org.apache.commons.lang3.RandomUtils;

public class UniqueIDGenerator {

    private static long random;

    static {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(secureRandom.generateSeed(128));
            random = Math.abs(secureRandom.nextLong());
        }
        catch (NoSuchAlgorithmException e) {
            random = Math.abs(RandomUtils.nextLong());
        }
    }

    public static String generate() {
        return System.nanoTime() + "-" + UUID.randomUUID();
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String randomUUID(int length) {
        return randomUUID().replace("-", "").substring(0, length);
    }

}
