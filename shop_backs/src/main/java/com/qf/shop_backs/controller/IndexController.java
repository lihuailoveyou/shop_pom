package com.qf.shop_backs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Kinglee
 * @Time 2018/11/19 17:57
 * @Version 1.0
 */
@Controller
public class IndexController {

    /**
     * 通过这个方法可以跳转到任意页面
     * @param page
     * @return
     */
    @RequestMapping("/topage/{page}")
    public String toPage(@PathVariable("page") String page){
        return page;
    }
}
