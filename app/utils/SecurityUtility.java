package utils;

import org.mindrot.jbcrypt.BCrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtility {
  public static byte[] getSha512(String value) {
      try {
          return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));

      } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
          throw new RuntimeException(e);
      }
  }

  public static String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  public static boolean checkPassword(String candidate, String encryptedPassword) {
    if (candidate == null || encryptedPassword == null) {
      return false;
    }

    return BCrypt.checkpw(candidate, encryptedPassword);
  }
}
