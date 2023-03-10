package mqttclient.mqttv3;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author 32825
 */
public class ConnectionPara {
    private String hostURI;
    private String clientID;
    private String username;
    private String password="test";
    private int keepAliveInterval = 60;
    private int connectionTimeout = 60;
    private boolean automaticReconnect=true;
    private boolean clean_session=false;
    boolean isSSL=true;
    private MqttConnectOptions conOpts = new MqttConnectOptions();

    public ConnectionPara() {
        try {
            //1.新建属性集对象
            Properties properties = new Properties();
            //2通过反射，新建字符输入流，读取db.properties文件
            InputStream input = ConnectionPara.class.getClassLoader().getResourceAsStream("connect.properties");
            //3.将输入流中读取到的属性，加载到properties属性集对象中
            properties.load(input);
            //4.根据键，获取properties中对应的值
            hostURI = properties.getProperty("mqttURI");
            clientID=properties.getProperty("clientID");
            username = properties.getProperty("mqttUsername");
            password = properties.getProperty("mqttPassword");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isSSL){
            try {
                SSLSocketFactory socketFactory =SSLUtils.getSocketFactory();
                conOpts.setSocketFactory(socketFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        conOpts.setUserName(username);
        conOpts.setPassword(password.toCharArray());
        conOpts.setKeepAliveInterval(keepAliveInterval);
        conOpts.setConnectionTimeout(connectionTimeout);
        conOpts.setAutomaticReconnect(automaticReconnect);
        conOpts.setCleanSession(clean_session);
    }

    public String getHostURI() {
        return hostURI;
    }

    public String getClientID() {
        return clientID;
    }

    public MqttConnectOptions getConOpts() {
        return conOpts;
    }

    public boolean isAutomaticReconnectEnabled() {
        return this.automaticReconnect;
    }
}
