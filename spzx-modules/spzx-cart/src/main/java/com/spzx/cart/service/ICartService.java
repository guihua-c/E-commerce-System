package com.spzx.cart.service;

import com.spzx.cart.api.domain.CartInfo;

import java.util.List;

public interface ICartService {


    /**
     * 添加商品到购物车
     * @param skuId 商品id
     * @param num 数量
     */
    void add2Cart(Long skuId, Integer num);

    /**
     * 获取购物车列表
     * @return 购物车列表
     */
    List<CartInfo> cartList();

    /**
     * 更新购物项的选中状态
     * @param skuId 商品id
     * @param status 选中状态
     */
    void updateCartInfoStatus(Long skuId, Integer status);

    /**
     * 全选购物车或取消全选
     * @param status 选中状态   1：全选 0：取消全选
     */
    void allCheckCart(Integer status);

    /**
     * 删除购物车中的商品
     * @param skuId 商品id
     */
    void deleteCart(Long skuId);

    /**
     * 清空购物车
     */
    void clearCart();

    /**
     * 获取购物车中选中的购物项
     * @return 购物车中选中的购物项
     */
    List<CartInfo> getCheckedCartInfo();

    /**
     * 清除选中的购物项
     */
    void clearCheckedCartInfo();
}
