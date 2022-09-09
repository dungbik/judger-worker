package com.yoonleeverse.judgerworker.service;

import com.yoonleeverse.judgerworker.domain.JudgeMessage;
import com.yoonleeverse.judgerworker.domain.RunResult;

import java.util.List;

public interface JudgeService {
    void judge(JudgeMessage judgeMessage);
    void completeJudge(String submissionId, List<RunResult> results);
}
