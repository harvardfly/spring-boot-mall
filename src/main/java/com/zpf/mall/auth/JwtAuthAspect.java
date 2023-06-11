package com.zpf.mall.auth;

import com.zpf.mall.exception.ServerErrorException;
import com.zpf.mall.util.JwtOperatorUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 定义切面
 */
@Aspect
@Component
public class JwtAuthAspect {

    /**
     * 只要加了@CheckLogin的方法都会走到这里
     *
     * @param point
     * @return
     */
    @Around("@annotation(com.zpf.mall.auth.JwtAuth)")
    public Object jwtAuth(ProceedingJoinPoint point) {
        try {
            // 从header中获取token
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();

            String token = request.getHeader("token");

            // 校验token是否合法
            JwtOperatorUtil jwtOperator = new JwtOperatorUtil();
            jwtOperator.setExpirationTimeInSecond(1209600L);
            jwtOperator.setSecret("aaabbbcccdddeeefffggghhhiiijjjkkklllmmmnnnooopppqqqrrrsssttt");
            Boolean valid = jwtOperator.validateToken(token);
            if (!valid) {
                throw new ServerErrorException(HttpStatus.UNAUTHORIZED.value(), "Token 不合法");
            }

            // 执行后续的方法
            return point.proceed();
        } catch (Throwable throwable) {
            throw new ServerErrorException(HttpStatus.UNAUTHORIZED.value(), "Token 不合法");
        }
    }
}
