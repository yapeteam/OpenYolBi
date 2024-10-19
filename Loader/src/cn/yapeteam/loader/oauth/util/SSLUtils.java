package cn.yapeteam.loader.oauth.util;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class SSLUtils {
    public static class miTM implements TrustManager, X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }
    }
}
