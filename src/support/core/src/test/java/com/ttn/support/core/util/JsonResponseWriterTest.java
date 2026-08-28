package com.ttn.support.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ttn.support.core.exception.ValidationException;
import org.junit.jupiter.api.Test;

class JsonResponseWriterTest {

    @Test
    void rejectsMalformedJson() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> JsonResponseWriter.readObject("{'title':'Login failure'}"));

        assertEquals("VALIDATION_ERROR", exception.getCode());
        assertEquals(400, exception.getStatus());
    }
}
