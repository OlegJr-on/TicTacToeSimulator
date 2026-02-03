package com.tictactoe.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.tictactoe.contracts.BoardDto;
import com.tictactoe.contracts.GameDto;
import com.tictactoe.contracts.GameStatus;
import com.tictactoe.contracts.PlayerSymbol;
import com.tictactoe.contracts.SessionDto;
import com.tictactoe.contracts.SimulationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9999",
    "spring.kafka.consumer.auto-startup=false"
})
class SessionIntegrationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @RegisterExtension
    static WireMockExtension engineMock = WireMockExtension.newInstance()
        .options(WireMockConfiguration.options().dynamicPort())
        .build();

    @DynamicPropertySource
    static void engineUrl(DynamicPropertyRegistry registry) {
        registry.add("engine.base-url", () -> "http://localhost:" + engineMock.getPort());
    }

    @Autowired
    private MockMvc mockMvc;

    private UUID gameId;

    @BeforeEach
    void stubEngine() throws Exception {
        gameId = UUID.randomUUID();
        GameDto createResponse = new GameDto();
        createResponse.setGameId(gameId);
        BoardDto board = new BoardDto();
        board.setRows(List.of(
            List.of("", "", ""),
            List.of("", "", ""),
            List.of("", "", "")
        ));
        createResponse.setBoard(board);
        createResponse.setStatus(GameStatus.IN_PROGRESS);
        createResponse.setNextPlayer(PlayerSymbol.X);
        createResponse.setMoveNumber(0);

        engineMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/games"))
            .willReturn(WireMock.aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(MAPPER.writeValueAsString(createResponse))));

        GameDto moveResponse = new GameDto();
        moveResponse.setGameId(gameId);
        BoardDto winBoard = new BoardDto();
        winBoard.setRows(List.of(
            List.of("X", "", ""),
            List.of("", "", ""),
            List.of("", "", "")
        ));
        moveResponse.setBoard(winBoard);
        moveResponse.setStatus(GameStatus.WIN);
        moveResponse.setWinner(PlayerSymbol.X);
        moveResponse.setMoveNumber(1);

        engineMock.stubFor(WireMock.post(WireMock.urlPathMatching("/games/.*/move"))
            .willReturn(WireMock.aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(MAPPER.writeValueAsString(moveResponse))));
    }

    @Test
    void fullGameFlow_createSession_simulate_pollUntilFinished() throws Exception {
        UUID correlationId = UUID.randomUUID();

        MvcResult createResult = mockMvc.perform(post("/sessions")
                .header("X-Correlation-Id", correlationId.toString()))
            .andExpect(status().isOk())
            .andReturn();
        SessionDto created = MAPPER.readValue(createResult.getResponse().getContentAsString(), SessionDto.class);
        assertThat(created.getSessionId()).isNotNull();
        assertThat(created.getGameId()).isNotNull();
        assertThat(created.getSimulationStatus()).isEqualTo(SimulationStatus.CREATED);

        mockMvc.perform(post("/sessions/{sessionId}/simulate", created.getSessionId())
                .header("X-Correlation-Id", correlationId.toString()))
            .andExpect(status().isAccepted());

        SessionDto session = pollUntilFinished(created.getSessionId());
        assertThat(session.getSimulationStatus()).isEqualTo(SimulationStatus.FINISHED);
        assertThat(session.getGame()).isNotNull();
        assertThat(session.getGame().getStatus()).isEqualTo(GameStatus.WIN);
        assertThat(session.getGame().getWinner()).isEqualTo(PlayerSymbol.X);
    }

    private SessionDto pollUntilFinished(UUID sessionId) throws Exception {
        for (int i = 0; i < 50; i++) {
            MvcResult result = mockMvc.perform(get("/sessions/{sessionId}", sessionId))
                .andExpect(status().isOk())
                .andReturn();
            SessionDto session = MAPPER.readValue(result.getResponse().getContentAsString(), SessionDto.class);
            if (session.getSimulationStatus() == SimulationStatus.FINISHED
                || session.getSimulationStatus() == SimulationStatus.FAILED) {
                return session;
            }
            Thread.sleep(100);
        }
        throw new AssertionError("Simulation did not finish within timeout");
    }
}
