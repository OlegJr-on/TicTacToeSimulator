package com.tictactoe.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import com.tictactoe.common.CorrelationFilter;
import com.tictactoe.common.GlobalExceptionHandler;

@SpringBootApplication
@Import({CorrelationFilter.class, GlobalExceptionHandler.class})
public class GameEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameEngineApplication.class, args);
    }
}
