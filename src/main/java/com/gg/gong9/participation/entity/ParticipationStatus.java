package com.gg.gong9.participation.entity;

public enum ParticipationStatus {
    JOINED("참여 중"),
    CANCELED("참여 취소");

    private final String value;

    ParticipationStatus(String value) {this.value = value;}

    public String toString() {return value;}
}
