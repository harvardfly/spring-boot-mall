package com.zpf.mall.service;

import com.github.pagehelper.PageInfo;
import com.zpf.mall.model.pojo.Product;
import com.zpf.mall.model.request.AddProductReq;
import com.zpf.mall.model.request.ProductListReq;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 描述：     商品Service
 */
public interface ProductService {

    void add(AddProductReq addProductReq);

    void update(Product updateProduct);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);

    void addProductByExcel(File destFile) throws IOException;
}
