package com.gg.gong9.global.exception.exceptions.groupbuy;

import com.gg.gong9.global.exception.BaseException;

public class GroupBuyException extends BaseException {
    public GroupBuyException(GroupBuyExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}
