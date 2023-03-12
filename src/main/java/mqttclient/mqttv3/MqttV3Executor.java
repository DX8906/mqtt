package mqttclient.mqttv3;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import mqttclient.database.UserMapper;
import mqttclient.database.sensor_data;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.InputStream;
import java.sql.Date;

/**
 * @author 32825
 */
public class MqttV3Executor implements MqttCallback {

    ConnectionPara v3ConnectionParameters;
    PublishPara v3PublishParameters;
    SubscribePara v3SubscriptionParameters;
    boolean quiet = false;
    boolean debug = false;
    MqttClient v3Client;
    private int actionTimeout=10;

    // To allow a graceful disconnect.

    final Thread mainThread = Thread.currentThread();
    static volatile boolean keepRunning = true;

    /**
     * Initialises the MQTTv3 Executor
     * @param debug         - Whether to print debug data to the console
     * @param quiet         - Whether to hide error messages
     */
    public MqttV3Executor(ConnectionPara cp, boolean debug, boolean quiet) {
        this.v3ConnectionParameters = cp;
        this.debug = debug;
        this.quiet = quiet;
    }

    public void connect() {
        try {
            this.v3Client = new MqttClient(this.v3ConnectionParameters.getHostURI(),
                    this.v3ConnectionParameters.getClientID(), new MemoryPersistence());
            this.v3Client.setCallback(this);

            // Connect to Server
            logMessage(String.format("Connecting to MQTT Broker: %s, Client ID: %s", v3Client.getServerURI(),
                    v3Client.getClientId()));
            v3Client.connect(v3ConnectionParameters.getConOpts());
        } catch (MqttException e) {
            logError(e.getMessage());
        }
    }

        public void PublishContent(PublishPara pp){

        }

    public void subscribeTopic(SubscribePara sp){
        this.v3SubscriptionParameters=sp;
        // Subscribe to a topic
        logMessage(String.format("Subscribing to %s, with QoS %d", v3SubscriptionParameters.getTopic(),
                v3SubscriptionParameters.getQos()));
        try{
            this.v3Client.subscribe(v3SubscriptionParameters.getTopic(),
                    v3SubscriptionParameters.getQos());

            while (true) {
                // Do nothing
            }
//            disconnectClient();
//            closeClientAndExit();
        }catch (MqttException e){
            logError(e.getMessage());
        }
    }

    /**
     * Simple helper function to publish a message.
     * @param payload
     * @param qos
     * @param retain
     * @param topic
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    private void publishMessage(byte[] payload, int qos, boolean retain, String topic)
            throws MqttPersistenceException, MqttException {
        MqttMessage v3Message = new MqttMessage(payload);
        v3Message.setQos(qos);
        v3Message.setRetained(retain);
        v3Client.publish(topic, v3Message);
    }

    /**
     * Log a message to the console, nothing fancy.
     * @param message
     */
    private void logMessage(String message) {
        if (this.debug == true) {
            System.out.println(message);
        }
    }

    /**
     * Log an error to the console
     * @param error
     */
    private void logError(String error) {
        if (this.quiet == false) {
            System.err.println(error);
        }
    }

    private void disconnectClient() throws MqttException {
        // Disconnect
        logMessage("Disconnecting from server.");
        v3Client.disconnect();
    }

    private void closeClientAndExit() {
        // Close the client
        logMessage("Closing Connection.");
        try {
            this.v3Client.close();
            logMessage("Client Closed.");
            System.exit(0);
            mainThread.join();
        } catch (MqttException | InterruptedException e) {
            // End the Application
            System.exit(1);
        }

    }

    @Override
    public void connectionLost(Throwable cause) {
        if (v3ConnectionParameters.isAutomaticReconnectEnabled()) {
            logMessage(String.format("The connection to the server was lost, cause: %s. Waiting to reconnect.",
                    cause.getMessage()));
        } else {
            logMessage(String.format("The connection to the server was lost, cause: %s. Closing Client",
                    cause.getMessage()));
            closeClientAndExit();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String messageContent = new String(message.getPayload());
        logMessage(String.format("topic: %s, Qos: %d", v3SubscriptionParameters.getTopic(),
                v3SubscriptionParameters.getQos()));
        String resultStr = new String(message.getPayload());
        System.out.println("topic: " + topic);
        System.out.println("Qos: " + message.getQos());
        System.out.println("message content: " + new String(resultStr));

        ObjectMapper objectMapper = new ObjectMapper();
        Result result = objectMapper.readValue(resultStr, Result.class);

        InputStream inputStream = Resources.getResourceAsStream("db.xml");
        MybatisSqlSessionFactoryBuilder builder = new MybatisSqlSessionFactoryBuilder();
        //获取session工厂
        SqlSessionFactory sessionFactory = builder.build(inputStream);
        //获取一个session会话
        SqlSession session = sessionFactory.openSession();

        UserMapper mapper = session.getMapper(UserMapper.class);
        sensor_data userEntity = new sensor_data();
        userEntity.setDevice_id(result.data.device_id);
        userEntity.setType(result.data.type);
        userEntity.setValue(result.data.value);
        userEntity.setUnit(result.data.unit);
        java.util.Date date = new java.util.Date();
        long times = date.getTime();
        userEntity.setCreateTime(new Date(times));
        userEntity.setUpdateTime(new Date(times));
        mapper.insert(userEntity);
        session.commit();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        logMessage(String.format("Message %d was delivered.", token.getMessageId()));
    }
}
