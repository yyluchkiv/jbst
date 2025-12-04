package jbst.foundation.domain.cryptography;

import lombok.experimental.UtilityClass;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static jbst.foundation.domain.asserts.JbstAsserts.assertNonNullOrThrow;

@UtilityClass
public class JbstEncryption {

    // AES-128
    private static final String AES_128 = "AES";
    private static final String AES_128_CIPHER_INSTANCE = "AES/GCM/NoPadding";
    private static final int AES_128_KEY_LENGTH = 128;

    public static String encryptAes128(String value, String encryptionInitVector, String encryptionKey) {
        assertNonNullOrThrow(value, "Encryption: AES, 128, encrypt. Argument 'value' is required");
        assertNonNullOrThrow(value, "Encryption: AES, 128, encrypt. Argument 'encryptionInitVector' is required");
        assertNonNullOrThrow(value, "Encryption: AES, 128, encrypt. Argument 'encryptionKey' is required");
        try {
            var gcmParameterSpec = new GCMParameterSpec(AES_128_KEY_LENGTH, encryptionInitVector.getBytes(UTF_8));
            var spec = new SecretKeySpec(encryptionKey.getBytes(UTF_8), AES_128);
            var cipher = Cipher.getInstance(AES_128_CIPHER_INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, spec, gcmParameterSpec);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException |
                 NoSuchPaddingException |
                 InvalidAlgorithmParameterException |
                 InvalidKeyException |
                 BadPaddingException |
                 IllegalBlockSizeException e) {
            var message = String.format("Encryption Failure. Algorithm: AES, 128. Value `%s`", value);
            throw new IllegalArgumentException(message);
        }
    }

    public static String decryptAes128(String value, String encryptionInitVector, String encryptionKey) {
        assertNonNullOrThrow(value, "Encryption: AES, 128, decrypt. Argument 'value' is required");
        assertNonNullOrThrow(value, "Encryption: AES, 128, decrypt. Argument 'encryptionInitVector' is required");
        assertNonNullOrThrow(value, "Encryption: AES, 128, decrypt. Argument 'encryptionKey' is required");
        try {
            var gcmParameterSpec = new GCMParameterSpec(AES_128_KEY_LENGTH, encryptionInitVector.getBytes(UTF_8));
            var spec = new SecretKeySpec(encryptionKey.getBytes(UTF_8), AES_128);
            var cipher = Cipher.getInstance(AES_128_CIPHER_INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, spec, gcmParameterSpec);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(value));
            return new String(original);
        } catch (NoSuchAlgorithmException |
                 NoSuchPaddingException |
                 InvalidAlgorithmParameterException |
                 InvalidKeyException |
                 BadPaddingException |
                 IllegalBlockSizeException e) {
            var message = String.format("Decryption Failure. Algorithm: AES, 128. Value `%s`", value);
            throw new IllegalArgumentException(message);
        }
    }
}
