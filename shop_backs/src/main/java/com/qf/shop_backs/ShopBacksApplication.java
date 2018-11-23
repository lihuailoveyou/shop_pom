package com.qf.shop_backs;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(FdfsClientConfig.class)//导入启动类
public class ShopBacksApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopBacksApplication.class, args);
    }
}
