package com.qf.service;

import com.qf.entity.Goods;

import java.util.List;

/**
 * @Author Kinglee
 * @Time 2018/11/22 0:01
 * @Version 1.0
 */
public interface ISearchService {

    /**
     * 添加索引
     * @param goods
     * @return
     */
    int addIndex(Goods goods);


    /**
     * 根据关键字查询索引
     * @param keyword
     * @return
     */
    List<Goods> queryIndex(String keyword);
}

