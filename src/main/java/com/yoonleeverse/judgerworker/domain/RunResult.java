package com.yoonleeverse.judgerworker.domain;

import lombok.Data;
import yoonleeverse.Result;

import java.io.Serializable;

@Data
public class RunResult implements Serializable {
    private int id;
    private int cpuTime;
    private int real_time;
    private long memory;
    private int signal;
    private int exit_code;
    private int error;
    private int result;
    private String output;

    public static RunResult ofSuccess(int id, Result.ByValue judgerResult) {
        RunResult runResult = makeRunResult(judgerResult);
        runResult.setId(id);
        return runResult;
    }

    public static RunResult ofFail(int id) {
        RunResult runResult = new RunResult();
        runResult.setId(id);
        runResult.setResult(6);
        return runResult;
    }

    private static RunResult makeRunResult(Result.ByValue judgerResult) {
        RunResult runResult = new RunResult();
        runResult.setCpuTime(judgerResult.cpu_time);
        runResult.setReal_time(judgerResult.real_time);
        runResult.setMemory(judgerResult.memory);
        runResult.setSignal(judgerResult.signal);
        runResult.setExit_code(judgerResult.exit_code);
        runResult.setError(judgerResult.error);
        runResult.setResult(judgerResult.result);
        return runResult;
    }
}
