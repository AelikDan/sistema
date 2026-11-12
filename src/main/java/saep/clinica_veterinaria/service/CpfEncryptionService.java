package saep.clinica_veterinaria.service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CpfEncryptionService {
    private static final int NONCE_LENGTH = 12;
    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    public CpfEncryptionService(@Value("${app.encryption-key:chave-padrao-clinica-veterinaria-123456}") String secret) {
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            key = new SecretKeySpec(keyBytes, "AES");
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Não foi possível configurar a criptografia.", ex);
        }
    }

    public String criptografar(String valor) {
        try {
            byte[] nonce = new byte[NONCE_LENGTH];
            random.nextBytes(nonce);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, nonce));
            byte[] encrypted = cipher.doFinal(valor.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(
                    ByteBuffer.allocate(nonce.length + encrypted.length).put(nonce).put(encrypted).array());
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Não foi possível criptografar o CPF.", ex);
        }
    }
}
