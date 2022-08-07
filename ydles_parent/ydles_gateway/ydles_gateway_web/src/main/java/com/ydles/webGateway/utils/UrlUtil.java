package com.ydles.webGateway.utils;

// 哪些路径需要令牌
public class UrlUtil {

    public static String filterPath = "/api/wseckillorder,/api/seckill,/api/wxpay,/api/wxpay/**,/api/worder/**,/api/user/**,/api/address/**,/api/wcart/**,/api/cart/**,/api/categoryReport/**,/api/orderConfig/**,/api/order/**,/api/orderItem/**,/api/orderLog/**,/api/preferential/**,/api/returnCause/**,/api/returnOrder/**,/api/returnOrderItem/**";

    // 传来一个路径 需不需要令牌
    public static boolean hasAuthorize(String url){
        String[] split = filterPath.replace("**","").split(",");
        for (String s : split) {
            if (url.startsWith(s)){
                // 需要令牌
                return true;
            }
        }
        // 不需要令牌
        return false;
    }
}
