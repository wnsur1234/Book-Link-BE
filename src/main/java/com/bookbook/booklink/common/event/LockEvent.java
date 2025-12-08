package com.bookbook.booklink.common.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LockEvent {
    private String key;
}
