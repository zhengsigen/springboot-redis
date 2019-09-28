package cn.zhengsigen.demo.redis.redisdemo.controller;

import cn.zhengsigen.demo.redis.redisdemo.model.Browser;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping("/work")
public class addressController {

    @Resource(name = "redisTemplate")
    RedisTemplate<String, Object> template;

    @GetMapping
    private Set<ZSetOperations.TypedTuple<Object>> getFileContent() {
        File file = new File("src/main/resources/access.log");
        if (!file.exists()) {
            System.out.println("模板文件不存在:{}");
        }
        BufferedReader bufferedReader = null;
        try {
            long start = System.currentTimeMillis();
            Integer line = 0;
            bufferedReader = new BufferedReader(new FileReader(file));
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                String[] split = temp.split("\" \"Mozilla");
                String leftSplit = split[0];
                int i = leftSplit.lastIndexOf("\"");
                String msg = leftSplit.substring(i, leftSplit.length());
                msg = msg.replaceAll("\"", "");
                if (!"".equals(msg)) {
                    String[] split1 = msg.split("[?]");
                    Pattern pattern = Pattern.compile("(?<=\\[).*?(?=])");
                    Matcher matcher = pattern.matcher(temp);
                    SimpleDateFormat sf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss Z", Locale.ENGLISH);
                    Browser browser = new Browser();
                    if (matcher.find()) {
                        String time = matcher.group(0);
                        time = time.replaceFirst(":", " ");
                        time = time.replaceAll("/", " ");
                        Date date = sf.parse(time);
                        browser.setAddr(split1[0]);
                        browser.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(date));
                        template.opsForZSet().incrementScore("set", browser, 1);
                        if (split1.length > 1) {
                            Browser detailed = (Browser) template.opsForHash().get("detailed", split1[0]);
                            if (detailed != null) {
                                detailed.setCount(detailed.getCount() + 1);
                                template.opsForHash().put("detailed", split1[0], detailed);
                            } else {
                                Browser newBrowser = new Browser();
                                newBrowser.setAddr(msg);
                                newBrowser.setCount(1);
                                newBrowser.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(date));
                                template.opsForHash().put("detailed", split1[0], newBrowser);
                            }
                        }
                    }
                }
                if (++line > 1000) {
                    break;
                }
            }
            long time = (System.currentTimeMillis() - start) / 1000;
            System.out.println("读取耗时：" + time + "秒");
            return template.opsForZSet().reverseRangeByScoreWithScores("set", 0, 999999999, 0, 200);
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到异常:{}");
        } catch (IOException e) {
            System.out.println("读取文件异常:{}");
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.out.println("关闭文件流异常:{}");
                }
            }
        }
        return null;
    }


    @GetMapping("/key")
    private Browser getDetails(@RequestParam("key") String key) {
        return (Browser) template.opsForHash().get("detailed", key);
    }
}