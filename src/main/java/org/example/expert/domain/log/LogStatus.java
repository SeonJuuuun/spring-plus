package org.example.expert.domain.log;

import java.util.Arrays;
import org.example.expert.domain.common.exception.InvalidRequestException;

public enum LogStatus {
    SUCCESS,
    FAIL;

    public static LogStatus from(final String status) {
        return Arrays.stream(values())
                .filter(logStatus -> status.equalsIgnoreCase(logStatus.name()))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 상태입니다."));
    }
}
