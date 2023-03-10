package mqttclient.mqttv3;


import java.io.InputStream;
import java.util.Properties;

/**
 * @author 32825
 */
public class PublishPara {
    private byte[] payload = null;
    private int qos = 0;
    private String publishTopic = null;
    private boolean retain = false;

    public PublishPara(byte[] payload) {
        try {
            Properties properties = new Properties();
            InputStream input = ConnectionPara.class.getClassLoader().getResourceAsStream("connect.properties");
            properties.load(input);
            publishTopic = properties.getProperty("publishTopic");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.payload = payload;
    }
}
