package com.yoonleeverse.judgerworker.client;

import com.yoonleeverse.judgerworker.constant.RunConfig;
import com.yoonleeverse.judgerworker.domain.JudgeMessage;
import com.yoonleeverse.judgerworker.domain.RunResult;
import com.yoonleeverse.judgerworker.domain.TestCaseInput;
import com.yoonleeverse.judgerworker.exception.JudgeException;
import com.yoonleeverse.judgerworker.util.FileUtil;
import com.yoonleeverse.judgerworker.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.FileSystemUtils;
import yoonleeverse.Judger;
import yoonleeverse.JudgerParam;
import yoonleeverse.JudgerResult;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.yoonleeverse.judgerworker.constant.JudgerConstant.*;
import static yoonleeverse.Constants.ResultCode.*;

@Slf4j
public class JudgeClient {

    private JudgeMessage judgeMessage;
    private RunConfig runConfig;

    private Path basePath;
    private Path srcPath;
    private Path exePath;

    private Judger judger;
    private ThreadPoolTaskExecutor taskExecutor;

    public JudgeClient(JudgeMessage judgeMessage, Judger judger) {
        this.judgeMessage = judgeMessage;
        this.runConfig = RUN_CONFIG_MAP.get(judgeMessage.getLanguage());

        this.basePath = Path.of(ROOT_PATH, SUBMISSION_PATH, judgeMessage.getSubmissionId());
        this.srcPath = Path.of(this.runConfig.getSrcName());
        this.exePath = Path.of(this.runConfig.getExeName());

        this.judger = judger;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(12);
        executor.initialize();
        this.taskExecutor = executor;
    }

    public void initEnv() throws JudgeException {
        if (this.runConfig == null) {
            throw new JudgeException(INIT_EVN_ERROR);
        }

        if (!FileUtil.makeFolder(this.basePath)) {
            throw new JudgeException(INIT_EVN_ERROR);
        }

        if (!FileUtil.saveText(this.basePath.resolve(this.srcPath), this.judgeMessage.getCode())) {
            throw new JudgeException(COMPILE_ERROR);
        }
    }

    public RunResult compileCode() throws JudgeException {
        String[] compileCommand = String.format(this.runConfig.getCompileCommand(), this.srcPath, this.exePath).split(" ");

        log.debug("compileCode srcPath={}, exePath={}, compileCommand={}", this.srcPath, this.exePath, Arrays.toString(compileCommand));

        JudgerResult compileResult = this.judger.compile(this.basePath.toString(), compileCommand);
        log.debug("compileCode compileResult={}", compileResult);

        RunResult runResult = RunResult.makeResult(0, compileResult);
        if (runResult.getResult() != SUCCESS.getValue()) {
            throw new JudgeException(runResult.getResult());
        }

        return runResult;
    }

    public List<RunResult> runCode() {
        log.debug("runCode start");

        List<CompletableFuture<RunResult>> completableFutures = this.judgeMessage.getInputs().stream()
                .map(input -> CompletableFuture.supplyAsync(() -> judgeOne(input), this.taskExecutor)
                        .exceptionally(e -> {
                            e.printStackTrace();
                            return null;
                        }))
                .collect(Collectors.toList());

        List<RunResult> runResults = completableFutures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.debug("runCode firstElement={}", runResults.get(0));
        log.debug("runCode end size={}", runResults.size());

        return runResults;
    }

    public void cleanUp() {
        try {
            FileSystemUtils.deleteRecursively(this.basePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RunResult judgeOne(TestCaseInput testCaseInput) {
        try {
            int id = testCaseInput.getId();

            Path outputPath = this.basePath.resolve(id + ".out");
            String[] command = String.format(this.runConfig.getCommand(), this.exePath).split(" ");
            JudgerParam judgerParam = JudgerParam.builder()
                    .exe_path(command[0])
                    .input_path(List.of(testCaseInput.getInput().split("\n")))
                    .output_path(outputPath.toString())
                    .error_path(outputPath.toString())
                    .max_cpu_time(this.judgeMessage.getMaxCpuTime())
                    .max_real_time(this.judgeMessage.getMaxRealTime())
                    .max_memory(this.judgeMessage.getMaxMemory() * 1024 * 1024)
                    .max_stack(128 * 1024 * 1024)
                    .max_output_size(16 * 1024 * 1024)
                    .args(command.length > 1 ? List.of(command).subList(1, command.length) : null)
                    .uid(0)
                    .gid(0)
                    .build();
            log.debug("{}", judgerParam);

            JudgerResult judgerResult = this.judger.judge(this.basePath.toString(), judgerParam);
            RunResult runResult = RunResult.makeResult(id, judgerResult);
            String output = FileUtil.loadText(outputPath);
            if (output != null) {
                runResult.setOutput(output);
                runResult.setOutputMD5(StringUtil.encryptMD5(output));
            }
            return runResult;
        } catch (Exception e) {
            log.debug(e.toString());
        }
        return null;
    }
}
