package com.gg.gong9.user.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Address {

    private String postcode;

    private String detail;

    public String toFormattedString() {
        return String.format("(%s) %s", postcode, detail);
    }

}
