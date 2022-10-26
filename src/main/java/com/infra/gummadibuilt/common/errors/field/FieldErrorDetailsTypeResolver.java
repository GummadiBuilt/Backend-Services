package com.infra.gummadibuilt.common.errors.field;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

import java.io.IOException;

/**
 * We use a custom type id strategy here because we want Jackson to always use the value of the "code" property
 * rather than trying to determine the type based on the actual class.
 */
class FieldErrorDetailsTypeResolver extends TypeIdResolverBase {

    @Override
    public String idFromValue(Object o) {
        if (!(o instanceof FieldErrorDetails)) {
            throw new IllegalArgumentException("Cannot determine type for unsupported class " + o.getClass());
        }

        FieldErrorDetails errorDetails = (FieldErrorDetails) o;
        return errorDetails.getCode().getJsonCode();
    }

    @Override
    public String idFromValueAndType(Object o, Class<?> aClass) {
        return idFromValue(o);
    }

    @Override
    public String idFromBaseType() {
        throw new IllegalArgumentException("No type information can be provided for the base type.");
    }

    @Override
    public JavaType typeFromId(DatabindContext databindContext, String s) throws IOException {
        FieldErrorCode code = FieldErrorCode.fromJsonCode(s);
        if (code == null) {
            throw new IOException("Unsupported field error code: " + s);
        }
        return databindContext.constructType(code.getErrorClass());
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

}
