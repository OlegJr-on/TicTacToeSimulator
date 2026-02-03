package com.tictactoe.ui;

import com.tictactoe.contracts.SessionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

@Controller
public class PageController {

    private static final Logger log = LoggerFactory.getLogger(PageController.class);

    private final SessionClient sessionClient;

    public PageController(SessionClient sessionClient) {
        this.sessionClient = sessionClient;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object startSimulation(RedirectAttributes redirectAttributes,
                                  @org.springframework.web.bind.annotation.RequestHeader(value = "Accept", required = false) String accept,
                                  @org.springframework.web.bind.annotation.RequestParam(value = "json", required = false) String jsonParam) {
        UUID correlationId = UUID.randomUUID();
        boolean wantJson = "1".equals(jsonParam) || (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE));
        try {
            SessionDto session = sessionClient.createSession(correlationId);
            if (session == null || session.getSessionId() == null) {
                if (wantJson) return ResponseEntity.status(503).body(Map.of("error", "Session service returned no session"));
                redirectAttributes.addFlashAttribute("error", "Session service returned no session");
                return "redirect:/";
            }
            sessionClient.startSimulation(session.getSessionId(), correlationId);
            log.info("Started simulation: correlationId={}, sessionId={}", correlationId, session.getSessionId());
            if (wantJson) return ResponseEntity.ok(Map.of("sessionId", session.getSessionId().toString()));
            return "redirect:/sessions/" + session.getSessionId() + "/view";
        } catch (Exception e) {
            log.error("Failed to start simulation: correlationId={}", correlationId, e);
            String msg = e.getMessage() != null ? e.getMessage() : "Failed to start simulation";
            if (wantJson) return ResponseEntity.status(503).body(Map.of("error", msg));
            redirectAttributes.addFlashAttribute("error", msg);
            return "redirect:/";
        }
    }

    @GetMapping("/sessions/{sessionId}/view")
    public String viewSession(@org.springframework.web.bind.annotation.PathVariable("sessionId") UUID sessionId, Model model) {
        try {
            SessionDto session = sessionClient.getSession(sessionId);
            model.addAttribute("session", session);
            model.addAttribute("sessionId", sessionId.toString());
            return "session-view";
        } catch (Exception e) {
            log.error("Failed to load session: sessionId={}", sessionId, e);
            model.addAttribute("error", e.getMessage() != null ? e.getMessage() : "Session not found");
            model.addAttribute("sessionId", sessionId.toString());
            return "session-view";
        }
    }

    @GetMapping("/sessions/{sessionId}/state")
    public org.springframework.http.ResponseEntity<SessionDto> getSessionState(@org.springframework.web.bind.annotation.PathVariable("sessionId") UUID sessionId) {
        try {
            SessionDto session = sessionClient.getSession(sessionId);
            return org.springframework.http.ResponseEntity.ok(session);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(503).build();
        }
    }
}
