package com.qf.shop_service_provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.entity.Goods;
import com.qf.service.IGoodsService;
import com.qf.shop.dao.IGoodsDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author Kinglee
 * @Time 2018/11/19 19:35
 * @Version 1.0
 */
@Service
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private IGoodsDao goodsDao;

    @Override
    public List<Goods> queryAll() {

        return goodsDao.queryAll();
    }

    /**
     * 保存商品
     * @param goods
     * @return
     */
    @Override
    public Goods saveGoods(Goods goods) {
        //系统服务这里的goods实现了主键回填，但并不代表后台工程的goods
        // 的主键也实现了回填(保存商品时，goods有后台工程传到系统服务，再到数据库是一个序列化和反序列化的过程)
        goodsDao.insert(goods);//dao层主键回填给goods
        //把goods以序列化反序列化的形式返回到后台工程(序列化：把一个对象转成二进制)
        return goods;
    }

    @Override
    public List<Goods> queryNewGoods() {
        return goodsDao.queryNewGoods();
    }
}
