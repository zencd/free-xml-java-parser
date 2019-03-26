# free-xml-java-parser

An XML parser which parses a string into a Java object tree basing on the given class structure; without any annotations and directives.

Sample usage:

    public static class SmallXml {
        public List<Item> items;
        public static class Item {
            public String name;
        }
    }

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