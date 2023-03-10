import mqttclient.mqttv3.*;

/**
 * @author 32825
 */
public class Main {
    public static void main(String[] args) {
        ConnectionPara cp = new ConnectionPara();
        MqttV3Executor me = new MqttV3Executor(cp, true, false);
        me.connect();
        SubscribePara sp=new SubscribePara();
        me.subscribeTopic(sp);
    }
}
