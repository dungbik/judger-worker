package com.yoonleeverse.judgerworker.listener;

import com.yoonleeverse.judgerworker.config.RabbitMQConfig;
import com.yoonleeverse.judgerworker.domain.JudgeMessage;
import com.yoonleeverse.judgerworker.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JudgeListener {

    private final JudgeService judgeService;

    @RabbitListener(queues = RabbitMQConfig.JUDGE_QUEUE_NAME, containerFactory = "containerFactory")
    public void onMessage(JudgeMessage judgeMessage) {
        judgeService.judge(judgeMessage);
    }
}
