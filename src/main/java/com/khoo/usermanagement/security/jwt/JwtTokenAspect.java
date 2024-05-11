package com.khoo.usermanagement.security.jwt;

import com.khoo.usermanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class JwtTokenAspect {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @Around("@annotation(com.khoo.usermanagement.security.jwt.CheckJwtToken)")
    public Object checkJwtToken(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String header = request.getHeader("Authorization");
        if (header==null || !header.toLowerCase().startsWith("bearer ")) {
//            throw new UnauthorizedException("Invalid or missing JWT token");
           return null;
        }

        final String token = header.split(" ")[1].trim();
        if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
//            throw new UnauthorizedException("Invalid or missing JWT token");
            return null;
        }

        UserDetails userDetails = userService.loadUserByUsername(jwtUtil.extractUsername(token));

        if(!userDetails.isEnabled()){
            //            throw new UnauthorizedException("Invalid or missing JWT token");
            return null;
        }

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        return joinPoint.proceed();
    }
}
