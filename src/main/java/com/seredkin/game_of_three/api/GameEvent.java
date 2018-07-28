package com.seredkin.game_of_three.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
@ToString
@JsonDeserialize(builder = GameEvent.GameEventBuilder.class)
public class GameEvent {

    @NonNull
    private final String gameId;
    @Builder.Default
    private final LocalDateTime created = LocalDateTime.now();
    @Builder.Default
    private final String eventId = UUID.randomUUID().toString();
    @NonNull
    private String previousEventId;
    @NonNull
    private final Integer previousMoveValue;
    @NonNull
    private final PLAYER player;
    @NonNull
    private final TYPE type;
    @NonNull
    private final Integer moveValue;

    public enum PLAYER {PLAYER_1, PLAYER_2}

    public enum TYPE {START_GAME, NEXT_MOVE, END_GAME}

    public static final String EMPTY_ID = "EMPTY_ID";

    @JsonPOJOBuilder(withPrefix = "")
    public static class GameEventBuilder{}
}
