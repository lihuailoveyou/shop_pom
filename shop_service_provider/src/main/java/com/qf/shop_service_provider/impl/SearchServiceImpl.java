package com.qf.shop_service_provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.entity.Goods;
import com.qf.entity.PageSolr;
import com.qf.service.ISearchService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Kinglee
 * @Time 2018/11/22 0:08
 * @Version 1.0
 */
@Service
public class SearchServiceImpl implements ISearchService {


    @Autowired//注入索引对象
    private SolrClient solrClient;
    /**
     * 添加索引库
     * @param goods
     * @return
     */
    @Override
    public int addIndex(Goods goods) {

        //添加索引字段
        try {
            SolrInputDocument solrDocument = new SolrInputDocument();
            solrDocument.addField("id",goods.getId());
            solrDocument.addField("gtitle",goods.getTitle());
            solrDocument.addField("ginfo",goods.getGinfo());
            solrDocument.addField("gprice",goods.getPrice());
            solrDocument.addField("gcount",goods.getGcount());
            solrDocument.addField("gimage",goods.getGimage());

            solrClient.add(solrDocument);
            solrClient.commit();

            return 1;//添加成功
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;//添加失败
    }


    /**
     * 根据关键字查询索引库(因为专门操作索引库，所以不用调用dao层)
     * @param keyword
     * @return
     */
    @Override
    public List<Goods> queryIndex(String keyword) {

        String queryStr = null;
        if (keyword == null || keyword.trim().equals("")){

            //keyword为空时的查询语句:查询全部
            queryStr = "*:*";
        }else {
            //要么标题中有，要么详情中有，%s是c语言的写法,s%的地方相应被keyword替换
            queryStr = String.format("gtitle:%s || ginfo:%s", keyword, keyword);
        }

        System.out.println(queryStr);
        //构建一个查询
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(queryStr);



        //通过solrQuery开启高亮
        solrQuery.setHighlight(true);
        //设置高亮的前后缀
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font>");
        //设置那些字段要高亮显示
        solrQuery.addHighlightField("gtitle");

        //设置高亮的折叠
        solrQuery.setHighlightSnippets(2);//折叠的次数
        solrQuery.setHighlightFragsize(5);//关键字前后内容的小

        List<Goods> goodsList = new ArrayList<>();
        QueryResponse response = null;
        try {
            //搜索的响应
            response = solrClient.query(solrQuery);
            //搜索的结果
            SolrDocumentList results = response.getResults();

            //额外地获得高亮结果
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

            //遍历普通的搜索结果(内容无高亮,哪怕开启了也没用)
            for (SolrDocument document : results){
                //获取商品信息
                Integer id = Integer.parseInt(document.get("id")+"");//字符串转成int类型
                String title = (String) document.get("gtitle");
                float price = (float) document.get("gprice");
                Integer gcount = (Integer) document.get("gcount");
                String gimage = (String) document.get("gimage");

                //判断当前商品是否高亮显示
                if (highlighting.containsKey(id+"")){
                    //包含了这个key说明当前这个id的商品存在高亮内容,获取高亮显示的内容
                    Map<String, List<String>> stringListMap = highlighting.get(id + "");

                    //获取高亮显示的标题
                    List<String> gtitle = stringListMap.get("gtitle");
                    if (gtitle != null){
                        //获取高亮显示的第一处内容，赋给title
                        title = "";
                        for (String str : gtitle)
                            title += str + "...";
                    }

                }

                //把获取到的商品信息封装到goods对象中
                Goods goods = new Goods(id,title,null,gcount,null,null, (double) price,gimage);


                //将goods对象添加到集合中
                goodsList.add(goods);
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return goodsList;
    }

    @Override
    public PageSolr<Goods> queryIndexPage(String keyword, PageSolr<Goods> pageSolr) {
        String queryStr = null;
        if (keyword == null || keyword.trim().equals("")){

            //keyword为空时的查询语句:查询全部
            queryStr = "*:*";
        }else {
            //要么标题中有，要么详情中有，%s是c语言的写法,s%的地方相应被keyword替换
            queryStr = String.format("gtitle:%s || ginfo:%s", keyword, keyword);
        }

        System.out.println(queryStr);
        //构建一个查询
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(queryStr);



        //通过solrQuery开启高亮
        solrQuery.setHighlight(true);
        //设置高亮的前后缀
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font>");
        //设置那些字段要高亮显示
        solrQuery.addHighlightField("gtitle");

        //设置分页：只需要设置以下参数(limit ?,?)
        solrQuery.setStart((pageSolr.getPage() -1) * pageSolr.getPageSize());//设置查询的开始位置
        solrQuery.setRows(pageSolr.getPageSize());//设置每页显示多少条


        List<Goods> goodsList = new ArrayList<>();
        QueryResponse response = null;
        try {
            //搜索的响应
            response = solrClient.query(solrQuery);
            //搜索的结果
            SolrDocumentList results = response.getResults();

            //获得搜索的总条数
            pageSolr.setPageCount((int)results.getNumFound());
            pageSolr.setPageSum(pageSolr.getPageCount() % pageSolr.getPageSize() == 0 ?
                    pageSolr.getPageCount() / pageSolr.getPageSize() :
                    pageSolr.getPageCount() / pageSolr.getPageSize() + 1);


            //额外地获得高亮结果
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

            //遍历普通的搜索结果(内容无高亮,哪怕开启了也没用)
            for (SolrDocument document : results){
                //获取商品信息
                Integer id = Integer.parseInt(document.get("id")+"");//字符串转成int类型
                String title = (String) document.get("gtitle");
                float price = (float) document.get("gprice");
                Integer gcount = (Integer) document.get("gcount");
                String gimage = (String) document.get("gimage");

                //判断当前商品是否高亮显示
                if (highlighting.containsKey(id+"")){
                    //包含了这个key说明当前这个id的商品存在高亮内容,获取高亮显示的内容
                    Map<String, List<String>> stringListMap = highlighting.get(id + "");

                    //获取高亮显示的标题
                    List<String> gtitle = stringListMap.get("gtitle");
                    if (gtitle != null){
                        //获取高亮显示的第一处内容，赋给title
                        title = gtitle.get(0);
                    }

                }

                //把获取到的商品信息封装到goods对象中
                Goods goods = new Goods(id,title,null,gcount,null,null, (double) price,gimage);


                //将goods对象添加到集合中
                goodsList.add(goods);
            }
            //把集合传过去
            pageSolr.setDatas(goodsList);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageSolr;
    }


}
