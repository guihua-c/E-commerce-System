package com.spzx.payment.service;

public interface IAlipayService {

    /**
     * 调用支付宝获取打开支付宝表单的方法
     * @param orderNo 订单号
     * @return 支付宝表单
     */
    String submitAlipay(String orderNo);
}
