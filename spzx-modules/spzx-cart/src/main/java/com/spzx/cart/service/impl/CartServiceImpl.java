package com.spzx.cart.service.impl;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.service.ICartService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements ICartService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private RemoteProductService remoteProductService;


    //获取Redis中的购物车的key的方法
    private String getCartKey() {
        //获取用户的id
        Long userId = SecurityContextHolder.getUserId();
        return "user:cart:" + userId;
    }
    @Override
    public void add2Cart(Long skuId, Integer num) {
        //获取购物车的key
        String cartKey = getCartKey();
        //从购物车中获取当前商品对应的购物项
        CartInfo cartInfo = (CartInfo) redisTemplate.opsForHash().get(cartKey, String.valueOf(skuId));
        //判断是否为空商品
        if (null != cartInfo) {
            //如果商品存在，将商品数量累加
            cartInfo.setSkuNum(Math.min(cartInfo.getSkuNum()+num,99));
        }else {
            //证明是第一次添加商品到购物车
            cartInfo = new CartInfo();
            //设置商品数量为1
            cartInfo.setSkuNum(1);
            //设置商品skuid
            cartInfo.setSkuId(skuId);
            //设置添加购物车的日期
            cartInfo.setCreateTime(new Date());
            //设置添加购物车的用户的名字
            cartInfo.setCreateBy(SecurityContextHolder.getUserName());
            //远程调用商品微服务根据商品skuid查询商品sku信息
            R<ProductSku> productSku = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);
            //判断是否有异常
            if(R.FAIL == productSku.getCode()){
                //远程调用失败
                throw new ServiceException(productSku.getMsg());
            }
            //获取商品sku信息
            ProductSku productSku1 = productSku.getData();
            //给购物项设置商品sku信息
            cartInfo.setSkuName(productSku1.getSkuName());
            //设置商品图片地址
            cartInfo.setThumbImg(productSku1.getThumbImg());
            //设置商品放入购物车时的价格
            cartInfo.setCartPrice(productSku1.getSalePrice());
            //设置商品Sku的实时价格
            cartInfo.setSkuPrice(productSku1.getSalePrice());
        }
        //将购物车放入redis中
        redisTemplate.opsForHash().put(cartKey, String.valueOf(skuId), cartInfo);
    }

    /**
     * 获取购物车列表
     *
     * @return 购物车列表
     */
    @Override
    public List<CartInfo> cartList() {
        //获取购物车的key
        String cartKey = getCartKey();
        //获取购物车的所有的购物项
        List<CartInfo> cartInfoList = redisTemplate.opsForHash().values(cartKey);
        //遍历所有的购物项
        cartInfoList.forEach(cartInfo -> {
            R<ProductSku> productSkuR = remoteProductService.getProductSku(cartInfo.getSkuId(), SecurityConstants.INNER);
            if(R.FAIL == productSkuR.getCode()){
                throw new ServiceException(productSkuR.getMsg());
            }
            //获取商品sku信息
            ProductSku productSku = productSkuR.getData();
            //获取最新的售价
            BigDecimal salePrice = productSku.getSalePrice();
            //更新CartInfo的实时价格
            cartInfo.setSkuPrice(salePrice);
            //将购物车放入redis中
            redisTemplate.opsForHash().put(cartKey, String.valueOf(cartInfo.getSkuId()), cartInfo);

        });
        return cartInfoList;
    }

    /**
     * 更新购物项的选中状态
     *
     * @param skuId  商品id
     * @param status 选中状态
     */
    @Override
    public void updateCartInfoStatus(Long skuId, Integer status) {
        //获取购物车的key
        String cartKey = getCartKey();
        //从购物车中获取当前商品对应的购物项
        CartInfo cartInfo = (CartInfo) redisTemplate.opsForHash().get(cartKey, String.valueOf(skuId));
        //如果商品存在，将商品选中状态更新
        cartInfo.setIsChecked(status);
        //将购物车放入redis中
        redisTemplate.opsForHash().put(cartKey, String.valueOf(skuId), cartInfo);
    }

    /**
     * 全选购物车或取消全选
     *
     * @param status 选中状态   1：全选 0：取消全选
     */
    @Override
    public void allCheckCart(Integer status) {
        //获取购物车的key
        String cartKey = getCartKey();
        //从购物车中获取所有的购物项
        List<CartInfo> cartInfoList = redisTemplate.opsForHash().values(cartKey);
        //遍历所有的购物项
        cartInfoList.forEach(cartInfo -> {
            //将购物项的选中状态更新为status
            cartInfo.setIsChecked(status);
            //将购物车放入redis中
            redisTemplate.opsForHash().put(cartKey, String.valueOf(cartInfo.getSkuId()), cartInfo);
        });
    }

    /**
     * 删除购物车中的商品
     *
     * @param skuId 商品id
     */
    @Override
    public void deleteCart(Long skuId) {
        //获取购物车的key
        String cartKey = getCartKey();
        //从购物车中删除当前商品对应的购物项
        redisTemplate.opsForHash().delete(cartKey, String.valueOf(skuId));
    }

    /**
     * 清空购物车
     */
    @Override
    public void clearCart() {
        //获取购物车的key
        String cartKey = getCartKey();
        //清空购物车
        redisTemplate.delete(cartKey);
//        redisTemplate.delete(getCartKey());
    }

    /**
     * 获取购物车中选中的购物项
     *
     * @return 购物车中选中的购物项
     */
    @Override
    public List<CartInfo> getCheckedCartInfo() {
        //获取购物车的key
        String cartKey = getCartKey();
        //从购物车中获取所有的购物项
        List<CartInfo> cartInfoList = redisTemplate.opsForHash().values(cartKey);
        //遍历所有的购物项
        List<CartInfo> checkedCartInfoList = cartInfoList.stream().filter(cartInfo -> cartInfo.getIsChecked() == 1).collect(Collectors.toList());
        return checkedCartInfoList;
    }

    /**
     * 清除选中的购物项
     */
    @Override
    public void clearCheckedCartInfo() {
        //获取购物车的key
        String cartKey = getCartKey();
        //从购物车中获取所有的购物项
        List<CartInfo> checkedCartInfo = getCheckedCartInfo();
        //从redis中删除选中的购物项
        checkedCartInfo.forEach(cartInfo -> {
            redisTemplate.opsForHash().delete(cartKey, String.valueOf(cartInfo.getSkuId()));
        });
    }


}