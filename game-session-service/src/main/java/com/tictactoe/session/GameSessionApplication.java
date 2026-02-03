package com.tictactoe.session;

import com.tictactoe.common.CorrelationFilter;
import com.tictactoe.common.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CorrelationFilter.class, GlobalExceptionHandler.class})
public class GameSessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameSessionApplication.class, args);
    }
}
