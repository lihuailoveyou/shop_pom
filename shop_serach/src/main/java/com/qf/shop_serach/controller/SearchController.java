package com.qf.shop_serach.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.entity.Goods;
import com.qf.entity.PageSolr;
import com.qf.service.ISearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author Kinglee
 * @Time 2018/11/21 0:31
 * @Version 1.0
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    @Reference
    private ISearchService searchService;

    //查看商品图片的前缀路径
    @Value("${image.path}")
    private String path;

    /**
     * 根据关键字搜索商品(分页)
     * @param keyword
     * @return
     */
    @RequestMapping("/list")
    public String search(String keyword, Model model, PageSolr<Goods> pageSolr){

        //System.out.println("搜索的关键字:"+keyword);
        //List<Goods> goods = searchService.queryIndex(keyword);

        //分页搜索
        pageSolr= searchService.queryIndexPage(keyword,pageSolr);
        model.addAttribute("pageSolr",pageSolr);
        //model.addAttribute("goods",goods);
        //将查看商品图片的路径前缀传到前端页面
        model.addAttribute("path",path);

        //分页的时候关键字不能变，得传过去页面去
        model.addAttribute("keyword",keyword);
        return "searchlist";
    }


    /**
     * 添加索引
     * @return
     * @RequestBody注解：可以把请求体中的纯json直接转成goods对象
     */
    @RequestMapping("/add")
    @ResponseBody//直接返回个页面
    public String addIndex(@RequestBody Goods goods){

        //接收从后台工程发送过来的goods同步数据
        System.out.println("搜索工程接收到同步数据的请求："+goods);

        //同步索引库(放到系统服务区实现)
        int result = searchService.addIndex(goods);
        if (result == 1){
            return "success";
        }
        return "error";
    }
}
