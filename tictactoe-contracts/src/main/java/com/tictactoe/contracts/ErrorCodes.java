package com.tictactoe.contracts;

/**
 * Shared error codes for RFC7807 Problem Details across all services.
 */
public final class ErrorCodes {
    private ErrorCodes() {}

    public static final String INVALID_MOVE = "INVALID_MOVE";
    public static final String GAME_FINISHED = "GAME_FINISHED";
    public static final String GAME_NOT_FOUND = "GAME_NOT_FOUND";
    public static final String SESSION_NOT_FOUND = "SESSION_NOT_FOUND";
    public static final String SIMULATION_ALREADY_RUNNING = "SIMULATION_ALREADY_RUNNING";
    public static final String ENGINE_UNAVAILABLE = "ENGINE_UNAVAILABLE";
}
