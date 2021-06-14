package com.yyds.recipe.controller;

import com.yyds.recipe.model.LoginUser;
import com.yyds.recipe.model.User;
import com.yyds.recipe.service.UserService;
import com.yyds.recipe.utils.ResponseUtil;
import com.yyds.recipe.utils.UserSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Map<String, Object> register(@RequestBody User userReq, HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> rsp = ResponseUtil.getResponse();
        try {
            String userId = userService.saveUser(userReq);

            // set session
            UserSession userSession = new UserSession(userId);
            httpSession.setAttribute(UserSession.ATTRIBUTE_ID, userSession);

            // set cookie
            Cookie userCookie = new Cookie("user-login-cookie", userId);
            // set cookie expiry time to 2 hours
            userCookie.setMaxAge(2 * 60 * 60);
            userCookie.setPath(request.getContextPath());
            response.addCookie(userCookie);
        } catch (Exception e) {
            rsp.put("error message", e.toString());
            rsp.put("code", -1);
            return rsp;
        }
        return rsp;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, Object> login(@RequestBody LoginUser loginUser, HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> rsp = ResponseUtil.getResponse();
        LoginUser user = null;
        try {
            user = userService.loginUser(loginUser);

            UserSession userSession = new UserSession(user.getUserId());
            httpSession.setAttribute(UserSession.ATTRIBUTE_ID, userSession);

            Cookie userCookie = new Cookie("user-login-cookie", user.getUserId());
            userCookie.setMaxAge(2 * 60 * 60);
            userCookie.setPath(request.getContextPath());
            response.addCookie(userCookie);
            rsp.put("userId", user.getUserId());

        } catch (Exception e) {
            e.printStackTrace();
            rsp.put("code", -1);
            rsp.put("error message", e.toString());
            return rsp;
        }
        return rsp;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Map<String, Object> logout(@RequestParam(value = "userId") String userId, HttpSession httpSession, HttpServletResponse response ) {
        // TODO: unhandled exceptions
        Map<String, Object> rsp = ResponseUtil.getResponse();
        httpSession.removeAttribute(UserSession.ATTRIBUTE_ID);
        Cookie cookie = new Cookie("user-login-cookie", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return rsp;
    }

    @RequestMapping(value = "/editProfile", method = RequestMethod.POST)
    public Map<String, Object> editProfile(@RequestBody User user) {
        Map<String, Object> rsp = ResponseUtil.getResponse();
        try {
            userService.editUser(user);
        } catch (Exception e) {
            rsp.put("code", -1);
            rsp.put("error message", e.toString());
            return rsp;
        }
        return rsp;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class editPasswordReq {
        @NotNull
        private String oldPassword;
        @NotNull
        private String newPassword;
        @NotNull
        private String userId;
    }

    @RequestMapping(value = "/editPassword", method = RequestMethod.POST)
    public Map<String, Object> editPassword(@RequestBody editPasswordReq req) {
        Map<String, Object> rsp = ResponseUtil.getResponse();

        String oldPassword = req.getOldPassword();
        String newPassword = req.getNewPassword();
        String userId = req.getUserId();

        try {
            userService.editPassword(oldPassword, newPassword, userId);
        } catch (Exception e) {
            rsp.put("error message", e.toString());
            rsp.put("code", -1);
            return rsp;
        }
        return rsp;
    }

    // TODO: just for test! Delete me!
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public Map<String, Object> testIndex() {
        Map<String, Object> rsp = ResponseUtil.getResponse();
        rsp.put("text", "test message");
        return rsp;
    }
}

