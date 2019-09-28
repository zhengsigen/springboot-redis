package cn.zhengsigen.demo.redis.redisdemo.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Browser implements Serializable {
    //地址
    private String addr;
    //最后访问时间
    private String time;

    private Integer count;
}
