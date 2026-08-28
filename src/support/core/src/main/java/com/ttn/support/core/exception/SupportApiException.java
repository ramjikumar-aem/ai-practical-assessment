package com.ttn.support.core.exception;

import java.util.Collections;
import java.util.Map;

public class SupportApiException extends Exception {

    private final String code;
    private final int status;
    private final Map<String, String> fields;

    public SupportApiException(String code, String message, int status) {
        this(code, message, status, Collections.emptyMap());
    }

    public SupportApiException(String code, String message, int status, Map<String, String> fields) {
        super(message);
        this.code = code;
        this.status = status;
        this.fields = fields == null ? Collections.emptyMap() : fields;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
