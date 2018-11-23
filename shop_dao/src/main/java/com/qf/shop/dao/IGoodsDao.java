package com.qf.shop.dao;

import com.qf.entity.Goods;

import java.util.List;

/**
 * @Author Kinglee
 * @Time 2018/11/19 20:15
 * @Version 1.0
 */
public interface IGoodsDao {

    List<Goods> queryAll();

    /**
     * 保存商品
     * @param goods
     * @return
     */
    int insert(Goods goods);


    List<Goods> queryNewGoods();
}
