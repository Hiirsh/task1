package utils;

import org.springframework.util.Base64Utils;

public class EncodeToBase64 {

  public static String createBasicAuthorization(String login, String password) {
    return "Basic " + Base64Utils.encodeToString((login + ":" + password).getBytes());
  }

  public static String createBasicAuthorization(String login) {
    return "Basic " + Base64Utils.encodeToString((login + ":" + login).getBytes());
  }
}
