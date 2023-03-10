package mqttclient.mqttv3;


import java.io.InputStream;
import java.util.Properties;

/**
 * @author 32825
 */
public class SubscribePara {

    private String subscribeTopic = null;
    private int qos = 0;

    public SubscribePara() {
        try {
            Properties properties = new Properties();
            InputStream input = ConnectionPara.class.getClassLoader().getResourceAsStream("connect.properties");
            properties.load(input);
            subscribeTopic = properties.getProperty("subscribeTopic");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTopic() {
        return subscribeTopic;
    }

    public int getQos() {
        return qos;
    }
}
