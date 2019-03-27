package parser;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class XmlTreeParserTest {

    public static class CarXml {
        public List<Wheel> wheels;
        public static class Wheel {
            public String name;
        }
    }

    @Test
    public void test_single_tag_form_works_inside_a_collection() throws Exception {
        String xmlContent = "" +
                "<root>\n" +
                "    <wheels>\n" +
                "        <wheel name='one'/>\n" +
                "        <wheel name='two'/>\n" +
                "    </wheels>\n" +
                "</root>";
        CarXml root = XmlTreeParser.parse(CarXml.class, xmlContent);
        List<CarXml.Wheel> wheels = root.wheels;
        assertEquals(2, wheels.size());
        assertEquals("one", wheels.get(0).name);
        assertEquals("two", wheels.get(1).name);
    }

    @Test
    public void test_single_tag_form_works_outside_a_collection() throws Exception {
        String xmlContent = "" +
                "<root>\n" +
                "  <wheel name='one'/>\n" +
                "  <wheel name='two'/>\n" +
                "</root>";
        CarXml root = XmlTreeParser.parse(CarXml.class, xmlContent);
        List<CarXml.Wheel> wheels = root.wheels;
        assertEquals(2, wheels.size());
        assertEquals("one", wheels.get(0).name);
        assertEquals("two", wheels.get(1).name);
    }

    @Test
    public void test_special_tag_name_item_works_in_a_collection() throws Exception {
        String xmlContent = "" +
                "<root>\n" +
                "    <wheels>\n" +
                "        <item name='one'/>\n" +
                "        <wheel name='two'/>\n" +
                "    </wheels>\n" +
                "</root>";
        CarXml root = XmlTreeParser.parse(CarXml.class, xmlContent);
        List<CarXml.Wheel> wheels = root.wheels;
        assertEquals(2, wheels.size());
        assertEquals("one", wheels.get(0).name);
        assertEquals("two", wheels.get(1).name);
    }

}