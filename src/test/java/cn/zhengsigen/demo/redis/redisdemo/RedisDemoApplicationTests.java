package cn.zhengsigen.demo.redis.redisdemo;

import cn.zhengsigen.demo.redis.redisdemo.model.Browser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisDemoApplicationTests {

    // @Autowired
    //private RedisTemplate<Object, Object> template;

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> template;
    Set<ZSetOperations.TypedTuple<Object>> set = new HashSet<ZSetOperations.TypedTuple<Object>>();

    @Test
    public void template() {
        Browser browser = new Browser();
        browser.setAddr("127.0.0.1");
        browser.setTime(new Date().toString());

        Browser browser1 = new Browser();
        browser1.setAddr("192.168.6.182");
        browser1.setTime(new Date().toString());

        Browser browser2 = new Browser();
        browser2.setAddr("192.168.6.111");
        browser2.setTime(new Date().toString());

        template.opsForZSet().add("stext", set);

        template.opsForZSet().reverseRange("stext", 0, -1);
    }

}
