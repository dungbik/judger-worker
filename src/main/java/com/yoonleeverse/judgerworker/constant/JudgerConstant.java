package com.yoonleeverse.judgerworker.constant;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import static com.yoonleeverse.judgerworker.constant.ProgrammingLanguage.*;

public class JudgerConstant {

    public static final String ROOT_PATH = "/home/www/judger-worker";
    public static final String SUBMISSION_PATH = "/submission";

    public static final Map<ProgrammingLanguage, RunConfig> RUN_CONFIG_MAP = Map.ofEntries(
            new SimpleEntry<>(C, new RunConfig("main.c", "main", 3000, 10000, 256, "/usr/bin/gcc %s -o %s -O2 -Wall -lm -static -std=gnu11 -DONLINE_JUDGE", "%s")),
            new SimpleEntry<>(CPP, new RunConfig("main.cpp", "main", 3000, 10000, 256, "/usr/bin/g++ %s -o %s -O2 -Wall -lm -static -std=gnu++11 -DONLINE_JUDGE", "%s")),
            new SimpleEntry<>(PYTHON2, new RunConfig("main.py", "/usr/bin/python2 main.pyc", 3000, 10000, 256, "/usr/bin/python2 -m py_compile %s", "%s"))
    );


}
