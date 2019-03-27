package parser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class XmlTreeParserTest {

    private static final Logger log = LoggerFactory.getLogger(XmlTreeParserTest.class);

    public static class CarXml {
        public List<Wheel> wheels;
        public static class Wheel {
            public String name;
        }
    }

    public static class TypeBindingXml {
        public Thing thing;
        public static class Thing {
            public String string;
            public int integerNumber;
            public long longNumber;
        }
    }

    @Test
    public void test_types() throws Exception {
        String xmlContent = "" +
                "<root>\n" +
                "  <thing string='Jon' integerNumber='200' longNumber='500'/>\n" +
                "</root>";
        TypeBindingXml.Thing thing = XmlTreeParser.parse(TypeBindingXml.class, xmlContent).thing;
        //log.debug("thing: {}", thing);
        assertEquals("Jon", thing.string);
        assertEquals(200, thing.integerNumber);
        assertEquals(500, thing.longNumber);
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