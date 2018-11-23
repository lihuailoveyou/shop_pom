package com.qf.entity;

import lombok.*;

import java.io.Serializable;

/**
 * @Author Kinglee
 * @Time 2018/11/19 19:26
 * @Version 1.0
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Goods implements Serializable {

    private Integer id;
    private String title;
    private String ginfo;
    private Integer gcount;
    private Integer tid = 1;
    private Double allprice;
    private Double price;
    private String gimage;
}
