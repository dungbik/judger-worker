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
            new SimpleEntry<>(PYTHON2, new RunConfig("Main.py", "Main.pyc", 3000, 10000, 256, "/usr/bin/python2 -m py_compile %s", "/usr/bin/python2 %s")),
            new SimpleEntry<>(PYTHON3, new RunConfig("Main.py", "__pycache__/Main.cpython-310.pyc", 3000, 10000, 256, "/usr/bin/python -m py_compile %s", "/usr/bin/python %s")),
            new SimpleEntry<>(JAVA, new RunConfig("Main.java", "Main", 3000, 10000, 256, "/usr/bin/javac -source 11 -J-Xms1024m -J-Xmx1920m -J-Xss512m -encoding UTF-8 %s", "/usr/bin/java -Dfile.encoding=UTF-8 -XX:+UseSerialGC -DONLINE_JUDGE=1 %s"))
    );


}
