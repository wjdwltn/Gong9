package com.gg.gong9.global.exception.exceptions.participation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ParticipationExceptionMessage {

    ;

    private final String text;
    private final HttpStatus status;
}
