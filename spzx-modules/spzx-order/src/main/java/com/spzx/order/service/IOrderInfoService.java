package com.spzx.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.domain.vo.OrderForm;
import com.spzx.order.domain.vo.TradeVo;

import java.util.List;

public interface IOrderInfoService extends IService<OrderInfo> {
    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单集合
     */
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

    /**
     * 查询订单
     *
     * @param id 订单主键
     * @return 订单
     */
    public OrderInfo selectOrderInfoById(Long id);

    /**
     * 去结算
     * @return 结算信息
     */
    TradeVo trade();
    /**
     * 立即购买
     * @param skuId 商品ID
     * @return 结算信息
     */
    TradeVo buy(Long skuId);

    /**
     * 提交订单
     * @param orderForm 订单表单
     * @return 订单ID
     */
    Long submitOrder(OrderForm orderForm);

    /**
     * 分页查询我的订单
     * @param orderStatus 订单状态
     * @return 订单列表
     */
    List<OrderInfo> getMyOrderInfoList(String orderStatus);

    /**
     * 取消订单
     * @param orderId 订单ID
     * @param flag 取消标志 1：用户取消 2：系统取消
     */
    void cancelOrder(Long orderId,Integer flag);

    /**
     * 根据订单号查询订单信息
     * @param orderNo 订单号
     * @return 订单信息
     */
    OrderInfo getOrderInfoByOrderNo(String orderNo);
}