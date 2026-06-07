package com.spzx.cart.api;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.api.factory.RemoteCartFallbackFactory;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(contextId = "remoteCartService",
        value = ServiceNameConstants.CART_SERVICE,
        fallbackFactory = RemoteCartFallbackFactory.class)
public interface RemoteCartService {

    /**
     * 获取选中的购物项
     * @param token 用户token
     * @return 选中的购物项
     */
    @GetMapping("/getCheckedCartInfo")
    R<List<CartInfo>> getCheckedCartInfo(@RequestHeader(SecurityConstants.FROM_SOURCE) String token);

    /**
     * 清除选中的购物项
     * @param source 来源
     * @return 清除结果
     */
    @GetMapping("/clearCheckedCartInfo")
    R<Void> clearCheckedCartInfo(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}