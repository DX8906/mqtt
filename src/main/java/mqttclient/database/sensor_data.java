package mqttclient.database;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

import static com.baomidou.mybatisplus.annotation.IdType.AUTO;

/**
 * @author 32825
 */
@Data
@TableName("sensor_data")
public class sensor_data {

    @TableId(type = AUTO)
    private Long id;

    private int device_id;

    private String type;

    private double value;

    private String unit;

    private Date createTime;

    private Date updateTime;
}

