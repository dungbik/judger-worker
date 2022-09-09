package com.yoonleeverse.judgerworker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yoonleeverse.Judger;

@Configuration
public class JudgerConfig {

    @Bean
    Judger judger() {
        return new Judger();
    }
}
