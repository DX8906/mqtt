package mqttclient.mqttv3;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import mqttclient.mqttv3.ConnectionPara;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

/**
 * @author 32825
 */
public class SSLUtils {
    private static String caCrtFile;
    private static String protocol;
    public static SSLSocketFactory getSocketFactory() throws Exception {

        try {
            Properties properties = new Properties();
            InputStream input = ConnectionPara.class.getClassLoader().getResourceAsStream("connect.properties");
            properties.load(input);
//            caCrtFile = properties.getProperty("mqttCaCrtFile");
            protocol = properties.getProperty("mqttSSLProtocol");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate
        X509Certificate caCert = null;
        InputStream input = ConnectionPara.class.getClassLoader().getResourceAsStream("mqtt.suwako.cn_root.crt");
//        FileInputStream fis = new FileInputStream(caCrtFile);
        BufferedInputStream bis = new BufferedInputStream(input);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        while (bis.available() > 0) {
            caCert = (X509Certificate) cf.generateCertificate(bis);
        }

        // client key and certificates are sent to server so it can authenticate us
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        // finally, create SSL socket factory
        if(StringUtils.isBlank(protocol)){
            protocol= "TLSv1.2";
        }
        SSLContext context = SSLContext.getInstance(protocol);
        context.init(null, tmf.getTrustManagers(), new SecureRandom());
        return context.getSocketFactory();
    }
}

