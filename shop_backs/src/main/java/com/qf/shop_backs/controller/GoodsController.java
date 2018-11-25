package com.qf.shop_backs.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.google.gson.Gson;
import com.qf.entity.Goods;
import com.qf.service.IGoodsService;
import com.qf.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author Kinglee
 * @Time 2018/11/19 19:20
 * @Version 1.0
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private IGoodsService goodsService;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 获取根路径
     */
    @Value("${image.path}")
    private String spath;

    /**
     * 查询所有商品
     * @param model
     * @return
     */
    @RequestMapping("/list")
    public String goodsManger(Model model){

        List<Goods> goods = goodsService.queryAll();
        //System.out.println(goods);
        model.addAttribute("goods",goods);

        //把图片传路径传到前端goodslist
        model.addAttribute("spath",spath);
        return "goodslist";
    }


    @RequestMapping("add")
    public String goodsAdd(Goods goods, MultipartFile gfile) throws Exception {
        //System.out.println("图片名称："+gfile.getOriginalFilename());
        //System.out.println("图片名称："+gfile.getSize());

        //上传图片(生成缩略图)
        StorePath result = fastFileStorageClient.uploadImageAndCrtThumbImage(
                gfile.getInputStream(),
                gfile.getSize(),
                "jpg", null);
        //tracker的地址：22122，storage的地址：23000
        //上传到fastdfs的全路径(client访问tracker以后，给你返回storage的地址)
        String path = result.getFullPath();
        //System.out.println("上传到fastdfs的全路径："+path);

        //上传失败则返回error页面，上传成功则将path路径放到setGimage里面，
        // 也就是封装到goods里面，然后到service层，再到dao层，最后到达数据库
        goods.setGimage(path);

        //调用service保存商品(不用加事务，因为只有一个操作)
        //接收系统服务传回来的主键回填的goods对象
        goods = goodsService.saveGoods(goods);
        System.out.println("添加后的主键："+goods.getId());

        //图片回显的问题在shop——backs的配置文件
        // 配image.path:192.168.81.188/(不用把ip写死在数据库中，后面文件系统换了位置，就可以在这里修改ip)

        //添加商品成功，调用搜索工程中的接口(add方法)，将商品同步到索引库，(添加商品的时候要有一个主键回填）
        //实现：后台工程将得到的goods对象转成json字符串传给搜索工程，
        // 搜索工程将json字符串转成goods对象同步到索引库---HttpClient(让你的代码模拟一个浏览器的行为)
        HttpClientUtil.sendJson("http://localhost:8082/search/add",new Gson().toJson(goods));
        //添加商品时同步索引库还要生成静态页面，静态请求也当成商品的对象以json的形式传到shop_item
        HttpClientUtil.sendJson("http://localhost:8083/item/createhtml",new Gson().toJson(goods));
        return "redirect:/goods/list";
    }

    /**
     * 查询新品上架
     * @return
     */
//    @RequestMapping("/newlist")
//    @ResponseBody
//    public String queryNewGoods(boolean flag){
//        List<Goods> goods = goodsService.queryNewGoods();
//        System.out.println("查询新品列表：" + goods);
//        return flag ? "hello(" + new Gson().toJson(goods) + ")" : new Gson().toJson(goods);
//    }



    /**
     *
     * springMVC实现跨域:通过添加@CrossOrigin注解浏览器加一个有跨域权限的响应头
     * @CrossOrigin
     * @param flag
     * @return
     */
    @RequestMapping("/newlist")
    @ResponseBody
    @CrossOrigin
    public List<Goods> queryNewGoods(boolean flag){
        List<Goods> goods = goodsService.queryNewGoods();
        System.out.println("查询新品列表：" + goods);
        return goods;
    }
}
