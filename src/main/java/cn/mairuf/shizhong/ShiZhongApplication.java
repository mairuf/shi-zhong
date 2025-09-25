package cn.mairuf.shizhong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author 阿麦
 * @date 2025-09-25
 */
@SpringBootApplication
public class ShiZhongApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiZhongApplication.class, args);
        System.out.println("时中 - 启动成功");
    }

}
