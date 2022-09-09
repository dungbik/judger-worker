package com.yoonleeverse.judgerworker.constant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RunConfig {
    private String srcName;
    private String exeName;
    private int compileMaxCpuTime;
    private int compileMaxRealTime;
    private int compileMaxMemory;
    private String compileCommand;
    private String command;
}
