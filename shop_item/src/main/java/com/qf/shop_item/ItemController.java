package com.qf.shop_item;

import com.qf.entity.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Kinglee
 * @Time 2018/11/24 11:20
 * @Version 1.0
 */
@Controller
@RequestMapping("/item")
public class ItemController {

    //注入配置对象
    @Autowired
    private Configuration configuration;

    /**
     * 添加商品时让别人调用这个方法生成静态页面
     * @return
     */
    @RequestMapping("/createhtml")
    //基于goods对象生成静态页面
    public String createHtml(@RequestBody Goods goods, HttpServletRequest request){

        Writer out = null;
        try {
            //通过配置对象获得模板对象
            Template template = configuration.getTemplate("goods.ftl");
            //生成静态页面所需的数据
            Map<String,Object> map = new HashMap<>();
            map.put("goods",goods);
            map.put("context",request.getContextPath());

            //获取clsspath下面的html路径(不能放到本地，得在项目中建一个文件夹(文件夹中随便写个文件)，项目打包的时候会一起打包)
            String path = this.getClass().getResource("/static/html/").getPath();
            System.out.println("classpath路径：" + path);
            System.out.println("最终静态页面的全路径：" + path + goods.getId() + ".html");
            //用商品的id做文件名
            out = new FileWriter(path+goods.getId() + ".html");

            //生成静态页面
            template.process(map,out);


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return null;
    }
}
