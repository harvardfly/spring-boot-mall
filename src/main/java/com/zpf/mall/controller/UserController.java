package com.zpf.mall.controller;

import com.zpf.mall.auth.JwtAuth;
import com.zpf.mall.common.ApiRestResponse;
import com.zpf.mall.common.Constant;
import com.zpf.mall.exception.ImoocMallException;
import com.zpf.mall.exception.ImoocMallExceptionEnum;
import com.zpf.mall.model.pojo.User;
import com.zpf.mall.model.request.RegisterUserReq;
import com.zpf.mall.model.vo.UserVO;
import com.zpf.mall.service.EmailService;
import com.zpf.mall.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.zpf.mall.util.EmailUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;

/**
 * 描述：     用户控制器
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    ExecutorService executorService;

    @JwtAuth  // 这里是自定义的注解，表示需要JWT鉴权
    @GetMapping("/test")
    @ResponseBody
    public User personalPage() {
        return userService.getUser();
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public ApiRestResponse register(@Valid @RequestBody RegisterUserReq registerUserReq) throws ImoocMallException {
        String userName = registerUserReq.getUsername();
        String password = registerUserReq.getPassword();
//        if (StringUtils.isEmpty(userName)) {
//            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
//        }
//        if (StringUtils.isEmpty(password)) {
//            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
//        }
        //密码长度不能少于8位
        if (password.length() < 8) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName, password);
        return ApiRestResponse.success();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName,
                                 @RequestParam("password") String password, HttpSession session)
            throws ImoocMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        UserVO user = userService.login(userName, password);
        //保存用户信息时，不保存密码
        user.setPassword(null);
        session.setAttribute(Constant.IMOOC_MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    /**
     * 更新个性签名
     */
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature)
            throws ImoocMallException {
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    /**
     * 登出，清除session
     */
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    /**
     * 管理员登录接口
     */
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName,
                                      @RequestParam("password") String password, HttpSession session)
            throws ImoocMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        UserVO user = userService.login(userName, password);
        User user1 = new User();
        BeanUtils.copyProperties(user, user1);
        //校验是否是管理员
        if (userService.checkAdminRole(user1)) {
            //是管理员，执行操作
            //保存用户信息时，不保存密码
            user.setPassword(null);
            session.setAttribute(Constant.IMOOC_MALL_USER, user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }

    /**
     * 发送邮件 使用线程池异步 避免发邮件阻塞
     */
    @PostMapping("/user/sendEmail")
    @ResponseBody
    public ApiRestResponse sendEmail(@RequestParam("emailAddress") String emailAddress)
            throws ImoocMallException {
        //检查邮件地址是否有效，检查是否已注册
        boolean validEmailAddress = EmailUtil.isValidEmailAddress(emailAddress);
        if (validEmailAddress) {
            boolean emailPassed = userService.checkEmailRegistered(emailAddress);
            if (!emailPassed) {
                return ApiRestResponse.error(ImoocMallExceptionEnum.EMAIL_ALREADY_BEEN_REGISTERED);
            } else {
                String verificationCode = EmailUtil.genVerificationCode();
                Boolean saveEmailToRedis = emailService.saveEmailToRedis(emailAddress, verificationCode);
                if (saveEmailToRedis) {
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            emailService.sendSimpleMessage(emailAddress, Constant.EMAIL_SUBJECT, "欢迎注册，您的验证码是" + verificationCode);
                        }
                    });
                    return ApiRestResponse.success();
                } else {
                    return ApiRestResponse.error(ImoocMallExceptionEnum.EMAIL_ALREADY_BEEN_SEND);
                }
            }
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.WRONG_EMAIL);
        }
    }
}

