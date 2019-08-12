package zencd.freeformxml;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class DefaultPropertyBinder implements PropertyBinder {
    private static final Logger log = LoggerFactory.getLogger(DefaultPropertyBinder.class);

    public static final DefaultPropertyBinder INSTANCE = new DefaultPropertyBinder();

    private DefaultPropertyBinder() {}

    @Override
    public void setFieldFromStringValue(Object object, Field field, String stringValue) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        if (fieldType.equals(String.class)) {
            field.set(object, stringValue);
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            field.set(object, Boolean.parseBoolean(stringValue));
        } else if (fieldType == int.class || fieldType == Integer.class) {
            field.set(object, NumberUtils.toInt(stringValue, 0));
        } else if (fieldType == long.class || fieldType == Long.class) {
            field.set(object, NumberUtils.toLong(stringValue, 0L));
        } else if (fieldType == short.class || fieldType == Short.class) {
            field.set(object, NumberUtils.toShort(stringValue, (short)0));
        } else if (fieldType == byte.class || fieldType == Byte.class) {
            field.set(object, NumberUtils.toByte(stringValue, (byte)0));
        } else if (fieldType == float.class || fieldType == Float.class) {
            field.set(object, NumberUtils.toFloat(stringValue, 0F));
        } else if (fieldType == double.class || fieldType == Double.class) {
            field.set(object, NumberUtils.toDouble(stringValue, 0D));
        } else {
            log.warn("The field type is unsupported for assignment yet: {}", field);
        }
    }

}
