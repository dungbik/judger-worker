package com.yoonleeverse.judgerworker.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestCaseInput implements Serializable {
    private int id;
    private String input;
}
