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
import yoonleeverse.Result;

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
        this.srcPath = this.basePath.resolve(this.runConfig.getSrcName());
        this.exePath = this.basePath.resolve(this.runConfig.getExeName());

        this.judger = judger;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
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

        if (!FileUtil.saveText(this.srcPath, this.judgeMessage.getCode())) {
            throw new JudgeException(COMPILE_ERROR);
        }
    }

    public RunResult compileCode() throws JudgeException {
        Path outputPath = this.basePath.resolve("compiler.out");
        String[] compileCommand = String.format(this.runConfig.getCompileCommand(), this.srcPath, this.exePath).split(" ");
        JudgerParam judgerParam = JudgerParam.builder()
                .exe_path(compileCommand[0])
                .input_path(List.of(this.srcPath.toString()))
                .output_path(outputPath.toString())
                .error_path(outputPath.toString())
                .max_cpu_time(this.runConfig.getCompileMaxCpuTime())
                .max_real_time(this.runConfig.getCompileMaxRealTime())
                .max_memory(this.runConfig.getCompileMaxMemory() * 1024 * 1024)
                .max_stack(128 * 1024 * 1024)
                .max_output_size(20 * 1024 * 1024)
                .args(List.of(compileCommand).subList(1, compileCommand.length))
                .uid(0)
                .gid(0)
                .build();

        log.debug("compile {}, {}, {}", srcPath, outputPath, Arrays.toString(compileCommand));
        Result.ByValue judgerResult = this.judger.judge(judgerParam);

        RunResult compileResult = RunResult.ofSuccess(0, judgerResult);
        if (compileResult.getResult() != SUCCESS.getValue()) {
            compileResult.setOutput(FileUtil.loadText(outputPath));
        }

        return compileResult;
    }

    public List<RunResult> runCode() {
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

            log.debug("judgeOne - input");
            List.of(testCaseInput.getInput().split("\n")).forEach(input -> log.debug("{}", input));

            Result.ByValue judgerResult = this.judger.judge(judgerParam);
            RunResult runResult = RunResult.ofSuccess(id, judgerResult);
            String output = FileUtil.loadText(outputPath);
            if (output != null) {
                log.debug("judgeOne - output");
                log.debug(output);
                runResult.setOutput(StringUtil.encryptMD5(output));
            }
            return runResult;
        } catch (Exception e) {
            log.debug(e.toString());
        }
        return null;
    }
}
