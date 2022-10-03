package com.yoonleeverse.judgerworker.domain;

import com.yoonleeverse.judgerworker.constant.ProgrammingLanguage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JudgeMessage implements Serializable {
    private String submissionId;
    private long problemId;
    private int maxCpuTime;
    private int maxRealTime;
    private int maxMemory;
    private List<TestCaseInput> inputs;
    private ProgrammingLanguage language;
    private String code;
}
