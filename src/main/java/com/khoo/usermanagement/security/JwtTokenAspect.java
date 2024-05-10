package com.khoo.usermanagement.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class JwtTokenAspect {

    @Autowired
    private JwtUtil jwtUtil;

    @Around("@annotation(CheckJwtToken)")
    public Object checkJwtToken(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String header = request.getHeader("Authorization");
        if (header==null || !header.startsWith("Bearer ")) {
//            throw new UnauthorizedException("Invalid or missing JWT token");
           return null;
        }

        final String token = header.split(" ")[1].trim();
        if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
//            throw new UnauthorizedException("Invalid or missing JWT token");
            return null;
        }

        return joinPoint.proceed();
    }
}
