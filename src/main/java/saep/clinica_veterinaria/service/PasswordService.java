package saep.clinica_veterinaria.service;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private static final int ITERATIONS = 310_000;
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private final SecureRandom random = new SecureRandom();

    public String hash(String senha) {
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        byte[] hash = derivar(senha.toCharArray(), salt, ITERATIONS);
        return "pbkdf2_sha256$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public boolean confere(String senha, String armazenada) {
        if (senha == null || armazenada == null) {
            return false;
        }
        // Senha em texto puro (não criptografada)
        if (senha.equals(armazenada)) {
            return true;
        }
        try {
            String[] partes = armazenada.split("\\$", -1);
            if (partes.length != 4 || !partes[0].equals("pbkdf2_sha256")) {
                return false;
            }
            int iteracoes = Integer.parseInt(partes[1]);
            if (iteracoes < 100_000 || iteracoes > 1_000_000) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            if (salt.length != SALT_LENGTH || esperado.length != HASH_LENGTH) {
                return false;
            }
            return java.security.MessageDigest.isEqual(
                    esperado, derivar(senha.toCharArray(), salt, iteracoes));
        } catch (Exception ex) {
            return false;
        }
    }

    private byte[] derivar(char[] senha, byte[] salt, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, HASH_LENGTH * 8);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Não foi possível verificar a senha.", ex);
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(senha, '\0');
        }
    }
}
