package parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class DefaultPropertyBinder implements PropertyBinder {
    private static final Logger log = LoggerFactory.getLogger(DefaultPropertyBinder.class);

    public static DefaultPropertyBinder INSTANCE = new DefaultPropertyBinder();

    private DefaultPropertyBinder() {}

    @Override
    public void setFieldFromStringValue(Object object, Field field, String stringValue) {
        try {
            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            if (fieldType.equals(String.class)) {
                field.set(object, stringValue);
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                if (StringUtils.isNotEmpty(stringValue)) {
                    field.set(object, Boolean.parseBoolean(stringValue));
                }
            } else if (fieldType == int.class || fieldType == Integer.class) {
                field.set(object, NumberUtils.toInt(stringValue, 0));
            } else if (fieldType == long.class || fieldType == Long.class) {
                field.set(object, NumberUtils.toLong(stringValue, 0L));
            } else {
                log.warn("The field type is unsupported for assignment yet: {}", field);
            }
        } catch (IllegalAccessException e) {
            log.warn("Failed assigning property {} from value {}: " + e.getMessage(), field, stringValue);
        }
    }

}
