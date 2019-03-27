# free-xml-java-parser

A very simple XML parser binding text into the given Java class structure without annotations.

Sample usage:

    public static class SmallXml {
        public List<Item> items;
        public static class Item {
            public String name;
        }
    }

    ```java
    String xmlContent = "" +
            "<root>\n" +
            "    <items>\n" +
            "        <item name='one'/>\n" +
            "    </items>\n" +
            "    <item name='two'/>\n" +
            "</root>";
    SmallXml root = XmlTreeParser.parse(SmallXml.class, xmlContent);

    List<SmallXml.Item> items = root.items;
    assertEquals(2, items.size());
    assertEquals("one", items.get(0).name);
    assertEquals("two", items.get(1).name);
    ```
