package com.bookbook.booklink.common.exception;

import com.bookbook.booklink.common.exception.ErrorCode;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiErrorResponses {
    ErrorCode[] value();
}
