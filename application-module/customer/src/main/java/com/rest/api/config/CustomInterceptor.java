package com.rest.api.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CustomInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 쿠키 생성
//        Cookie cookie = new Cookie("myCookie", "cookieValue");
//        cookie.setPath("/");
//        response.addCookie(cookie);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 생성된 쿠키 읽기
        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("myCookie".equals(cookie.getName())) {
//                    String cookieValue = cookie.getValue();
//                    // 쿠키 값 사용
//                }
//            }
//        }
    }

}
