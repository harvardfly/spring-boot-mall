package com.zpf.mall.model.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class RegisterUserReq {
    @NotNull(message = "username不能为null")
    private String username;
    @NotNull(message = "password不能为null")
    private String password;

    @Override
    public String toString() {
        return "RegisterUserReq{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RegisterUserReq(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
