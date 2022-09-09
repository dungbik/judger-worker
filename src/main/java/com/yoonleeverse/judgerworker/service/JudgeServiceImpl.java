package com.yoonleeverse.judgerworker.service;

import com.yoonleeverse.judgerworker.client.JudgeClient;
import com.yoonleeverse.judgerworker.domain.CompleteMessage;
import com.yoonleeverse.judgerworker.domain.JudgeMessage;
import com.yoonleeverse.judgerworker.domain.RunResult;
import com.yoonleeverse.judgerworker.exception.JudgeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import yoonleeverse.Judger;

import java.util.*;

import static com.yoonleeverse.judgerworker.config.RabbitMQConfig.*;
import static yoonleeverse.Constants.ResultCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    private final RabbitTemplate rabbitTemplate;
    private final Judger judger;

    /**
     * 문제 채점
     *  1. 채점 환경 세팅
     *  2. 제출한 코드 컴파일
     *  3. 문제의 테스트케이스로 채점 (Async)
     *  4. 채점 결과 통지
     *  5. 채점 환경 정리
     * @param judgeMessage
     */
    @Override
    public void judge(JudgeMessage judgeMessage) {
        String submissionId = judgeMessage.getSubmissionId();
        log.debug("start {} - submissionId={}", Thread.currentThread().getName(), submissionId);
        JudgeClient judgeClient = null;
        try {
            judgeClient = new JudgeClient(judgeMessage, judger);
            judgeClient.initEnv();

            RunResult compileResult = judgeClient.compileCode();

            List<RunResult> runResults = new ArrayList<>(List.of(compileResult));
            boolean isCompileSuccess = (compileResult.getResult() == SUCCESS.getValue());
            if (isCompileSuccess) {
                runResults.addAll(judgeClient.runCode());
            }
            completeJudge(submissionId, runResults);
        } catch (JudgeException e) {
            completeJudge(submissionId, List.of(RunResult.ofFail(e.getResultValue())));
        } catch (Exception e) {
            completeJudge(submissionId, List.of(RunResult.ofFail(UNK_ERROR.getValue())));
        } finally {
            if (judgeClient != null) {
                judgeClient.cleanUp();
            }
        }
    }

    /**
     * 채점 완료 통지
     * [judge-worker] - [online-judge-api]
     *  - 결합도(Coupling)를 최대한 낮추기 위해 처리 속도를 손해보고, 유저에게 바로가 아닌 [online-judge-api] 서버를 통해 유저로 채점 완료 통지를 한다.
     * @param submissionId
     */
    public void completeJudge(String submissionId, List<RunResult> results) {
        log.debug("complete - submissionId={}", submissionId);
        CompleteMessage completeMessage = new CompleteMessage();
        completeMessage.setSubmissionId(submissionId);
        completeMessage.setResults(results);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, COMPLETE_ROUTING_KEY, completeMessage);
    }
}
