package zencd.freeformxml;

import java.lang.reflect.Field;

public interface PropertyBinder {
    void setFieldFromStringValue(Object object, Field field, String stringValue);
}
