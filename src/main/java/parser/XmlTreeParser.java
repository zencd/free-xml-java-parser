package parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * todo start using generics
 */
public class XmlTreeParser extends DefaultHandler {

    private final static Logger log = LoggerFactory.getLogger(XmlTreeParser.class);

    private final Class rootClass;
    private final String xmlContent;
    private Object rootNode;

    private final Stack<NodeInfo> nodes = new Stack<>();

    private static final NodeInfo DEAD_NODE = new NodeInfo(null);

    public static class BindingException extends RuntimeException {
        BindingException(String msg) {
            super(msg);
        }

        BindingException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public XmlTreeParser(Class rootClass, String xmlContent) {
        this.rootClass = rootClass;
        this.xmlContent = xmlContent;
    }

    public static <T> T parse(Class<T> rootClass, String xmlContent) throws IOException, SAXException, ParserConfigurationException {
        XmlTreeParser parser = new XmlTreeParser(rootClass, xmlContent);
        return (T) parser.parse();
    }

    private Object parse() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(makeStringInputSource(xmlContent), this);
        return rootNode;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        //super.startElement(uri, localName, qName, attributes);
        //log.debug("<{}>", qName);
        NodeInfo parentNode = nodes.isEmpty() ? null : nodes.peek();
        NodeInfo currentNode = createObjectFromTag(qName, parentNode);
        addAttributes(currentNode.object, attrs);

        nodes.push(currentNode);

        if (rootNode == null) {
            rootNode = currentNode.object;
        }
    }

    private NodeInfo createObjectFromTag(String tagName, NodeInfo parentNode) {
        if (parentNode == null) {
            // XML root element
            return new NodeInfo(newInstance(rootClass));
        } else if (parentNode == DEAD_NODE) {
            // skipping XML nodes without a Java model
            return DEAD_NODE;
        } else if (parentNode.isList()) {
            // instantiating an object (list element)
            // then adding it to the enclosing list

            Object single = newInstance(parentNode.listElementType);
            addToCollection(parentNode.object, single);
            return new NodeInfo(single);

            //try {
            //    Constructor ctor = parentNode.listElementType.getConstructor(parentNode.object.getClass());
            //    ctor.setAccessible(true);
            //    Object single = ctor.newInstance(parentNode.object);
            //    return new NodeInfo(single);
            //} catch (Exception e) {
            //    throw new BindingException("failed instantiating inner class", e);
            //}
        } else {
            Field assignedProperty = getField(parentNode.object.getClass(), tagName);
            if (assignedProperty != null) {
                Class assignedPropertyType = assignedProperty.getType();
                boolean creatingACollectionHere = isList(assignedPropertyType);
                if (creatingACollectionHere) {
                    Class elementType = getListElementType(assignedProperty);
                    Object list = newInstance(ArrayList.class);
                    setProperty(assignedProperty, parentNode.object, list);
                    return new NodeInfo(list, elementType);
                } else {
                    Object single = newInstance(assignedPropertyType);
                    setProperty(assignedProperty, parentNode.object, single);
                    return new NodeInfo(single, assignedProperty, parentNode.object);
                }
            }

            if (tagName.equals("item")) {
                // if this node is "wheel", let's find the "wheels" property at the parent node
                // if it's a list, then add this element to it
                String pluralName = tagName + "s";
                Field listProperty = getField(parentNode.object.getClass(), pluralName);
                if (isList(listProperty)) {
                    Class elementType = getListElementType(listProperty);
                    Object elementInstance = newInstance(elementType);
                    Object listInstance = getProperty(listProperty, parentNode.object);
                    addToCollection(listInstance, elementInstance);
                    int stop = 0;
                    return new NodeInfo(elementInstance);
                }
            }

            log.debug("there is no property `{}` at {} - the deeper XML gonna be skipped", tagName, parentNode.object.getClass().getName());
            return DEAD_NODE;
        }
    }

    private void addToCollection(Object list, Object element) {
        Class<?> listClass = list.getClass();
        try {
            Method method = listClass.getMethod("add", new Class[]{Object.class});
            method.invoke(list, element);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new BindingException("Failed adding element to collection " + listClass, e);
        }
    }

    static class NodeInfo {
        final Object object;
        final Class listElementType;
        Field assignedProperty = null;
        Object propertyOwner = null;

        NodeInfo(Object nodeObject) {
            this(nodeObject, null);
        }

        NodeInfo(Object nodeObject, Class listElementType) {
            this.object = nodeObject;
            this.listElementType = listElementType;
        }

        public NodeInfo(Object nodeObject, Field assignedProperty, Object propertyOwner) {
            this.object = nodeObject;
            this.listElementType = null;
            this.assignedProperty = assignedProperty;
            this.propertyOwner = propertyOwner;
        }

        public boolean isList() {
            return object instanceof Collection;
        }
    }

    private void addAttributes(Object object, Attributes attrs) {
        if (object != null) {
            final int len = attrs.getLength();
            for (int i = 0; i < len; i++) {
                String attrName = attrs.getLocalName(i);
                String value = attrs.getValue(i);
                setFieldFromStringValue(object, attrName, value);
                log.debug("attrName: {}", attrName);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //log.debug("</{}>", qName);
        nodes.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String value = new String(ch, start, length).trim();
        if (value.length() == 0) {
            return;
        }

        NodeInfo currentNode = nodes.peek();
        if (currentNode == null) {
            return;
        }

        if (currentNode.object != null) {
            //log.debug("characters: [{}] for {}", value, currentNode.object.getClass());
            setProperty(currentNode.assignedProperty, currentNode.propertyOwner, value);
        }
    }

    private Field getField(Class clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private Object getProperty(Field field, Object thisObject) {
        try {
            return field.get(thisObject);
        } catch (IllegalAccessException e) {
            throw new BindingException("Failed getting value from field " + field, e);
        }
    }

    private void setProperty(Field field, Object thisObject, Object value) {
        try {
            field.set(thisObject, value);
        } catch (IllegalAccessException e) {
            throw new BindingException("Failed assigning field " + field, e);
        }
    }

    private Object newInstance(Class clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BindingException("Failed instantiating " + clazz, e);
        }
    }

    private boolean isList(Class type) {
        return List.class.isAssignableFrom(type);
    }

    private boolean isList(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

    private Class getListElementType(Field field) {
        ParameterizedType integerListType = (ParameterizedType) field.getGenericType();
        return (Class<?>) integerListType.getActualTypeArguments()[0];
    }

    private void setFieldFromStringValue(Object object, String attrName, String value) {
        try {
            Field field = object.getClass().getDeclaredField(attrName);
            setFieldFromStringValue(object, field, value);
        } catch (NoSuchFieldException e) {
            log.debug("there is no property {}.{} in the Java model - no assignment performed", object.getClass().getName(), attrName);
        }
    }

    private void setFieldFromStringValue(Object object, Field field, String stringValue) {
        try {
            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            if (fieldType.equals(String.class)) {
                field.set(object, stringValue);
            } else if (isBoolean(fieldType)) {
                if (StringUtils.isNotEmpty(stringValue)) {
                    field.set(object, Boolean.parseBoolean(stringValue));
                }
            } else if (fieldType == int.class && fieldType == Integer.class) {
                field.set(object, NumberUtils.toInt(stringValue, 0));
            } else if (fieldType == long.class && fieldType == Long.class) {
                field.set(object, NumberUtils.toLong(stringValue, 0L));
            } else {
                log.warn("The field type is unsupported for assignment yet: {}", field);
            }
        } catch (IllegalAccessException e) {
            log.warn("Failed assigning property {} from value {}: " + e.getMessage(), field, stringValue);
        }
    }

    private boolean isBoolean(Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }

    public static InputSource makeStringInputSource(String content) {
        return new InputSource(new StringReader(content));
    }

}
