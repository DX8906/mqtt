package mqttclient.mqttv3;

/**
 * @author 32825
 */
public class Result {
    public String cmd;
    public Data data;

    public Result() {
    }

    public Result(String cmd, String type, int device_id, float value, String unit) {
        this.cmd = cmd;
        this.data = new Data(type, device_id, value, unit);
    }
}

class Data {
    public Data() {
    }

    public Data(String type, int device_id, float value, String unit) {
        this.type = type;
        this.device_id = device_id;
        this.value = value;
        this.unit = unit;
    }

    public String type;
    public int device_id;
    public float value;
    public String unit;
}
