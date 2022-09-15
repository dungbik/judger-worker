package com.yoonleeverse.judgerworker.constant;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

public class JudgerConstant {

    public static final String ROOT_PATH = "/home/www/judger-worker";
    public static final String SUBMISSION_PATH = "/submission";

    public static final Map<String, RunConfig> RUN_CONFIG_MAP = Map.ofEntries(
            new SimpleEntry<>("C", new RunConfig("main.c", "main", 3000, 10000, 256, "/usr/bin/gcc -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c11 %s -lm -o %s", "%s"))
    );


}
