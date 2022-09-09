package com.yoonleeverse.judgerworker.exception;

import lombok.Getter;
import yoonleeverse.Constants.ResultCode;

public class JudgeException extends Exception {

    private final static String ERR_MSG = "Judge Exception";

    @Getter
    private int resultValue;

    public JudgeException(ResultCode resultEnum) {
        super(ERR_MSG);
        this.resultValue = resultEnum.getValue();
    }
}
