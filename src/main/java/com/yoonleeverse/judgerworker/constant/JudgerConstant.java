package com.yoonleeverse.judgerworker.constant;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import static com.yoonleeverse.judgerworker.constant.ProgrammingLanguage.*;

public class JudgerConstant {

    public static final String ROOT_PATH = "/home/www/judger-worker";
    public static final String SUBMISSION_PATH = "/submission";

    public static final Map<ProgrammingLanguage, RunConfig> RUN_CONFIG_MAP = Map.ofEntries(
            new SimpleEntry<>(C, new RunConfig("main.c", "main", 3000, 10000, 256, "/usr/bin/gcc -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c11 %s -lm -o %s", "%s")),
            new SimpleEntry<>(CPP, new RunConfig("main.cpp", "main", 3000, 10000, 256, "/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++14 %s -lm -o %s", "%s"))
    );


}
