package parser;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class XmlTreeParserTest {

    public static class SmallXml {
        public List<Item> items;
        public static class Item {
            public String name;
        }
    }

    @Test
    public void test_small() throws Exception {
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
    }

}