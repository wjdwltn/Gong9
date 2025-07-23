package com.gg.gong9.global.enums;

public enum GroupBuyStatus {
    BEFORE_START("모집 전"),
    RECRUITING("모집 중"),
    COMPLETED("모집 완료"),
    CANCELED("모집 취소");

    private final String value;

    GroupBuyStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
