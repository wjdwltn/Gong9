package com.gg.gong9.global.exception.exceptions.participation;

import com.gg.gong9.global.exception.BaseException;

public class ParticipationException extends BaseException {
    public ParticipationException(ParticipationExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}