package zencd.freeformxml;

import java.lang.reflect.Field;
import java.util.Collection;

class NodeInfo {
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

    NodeInfo(Object nodeObject, Field assignedProperty, Object propertyOwner) {
        this.object = nodeObject;
        this.listElementType = null;
        this.assignedProperty = assignedProperty;
        this.propertyOwner = propertyOwner;
    }

    boolean isCollection() {
        return object instanceof Collection;
    }
}
