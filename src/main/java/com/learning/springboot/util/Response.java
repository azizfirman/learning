package com.learning.springboot.util;

import lombok.Data;

@Data
public class Response {
    private boolean status;
    private Object message;
    private Object data;
}