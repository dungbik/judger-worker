package com.yoonleeverse.judgerworker.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import yoonleeverse.JudgerResult;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RunResult extends JudgerResult implements Serializable {
    private int id;
    private String output;

    public static RunResult makeResult(int id, JudgerResult judgerResult) {
        RunResult runResult = RunResult.init(judgerResult);
        runResult.setId(id);
        return runResult;
    }

    public static RunResult ofFail(int id) {
        RunResult runResult = new RunResult();
        runResult.setId(0);
        runResult.setResult(id);
        return runResult;
    }

    public static RunResult init(JudgerResult judgerResult) {
        RunResult runResult = new RunResult();
        runResult.setCpu_time(judgerResult.getCpu_time());
        runResult.setReal_time(judgerResult.getReal_time());
        runResult.setSignal(judgerResult.getSignal());
        runResult.setExit_code(judgerResult.getExit_code());
        runResult.setError(judgerResult.getError());
        runResult.setResult(judgerResult.getResult());
        return runResult;
    }
}


