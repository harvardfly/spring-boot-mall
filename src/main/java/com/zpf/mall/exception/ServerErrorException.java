package com.zpf.mall.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 自定义异常
 */
@Data
@AllArgsConstructor
public class ServerErrorException extends RuntimeException {

    public Integer code;
    public String message;

}
