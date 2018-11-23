package com.qf.service;

import com.qf.entity.Goods;

import java.util.List;

/**
 * @Author Kinglee
 * @Time 2018/11/19 20:06
 * @Version 1.0
 */
public interface IGoodsService {

    List<Goods> queryAll();

    /**
     * 保存商品
     * @param goods
     * @return
     */
    //既然系统服务的goods没办法回填到表现层(后台工程)，就把回填后的对象传回到表现层
    Goods saveGoods(Goods goods);


    List<Goods> queryNewGoods();
}
