package com.github.zencd.freeformxml;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class FreeFormXmlParserTest {

    private static final Logger log = LoggerFactory.getLogger(FreeFormXmlParserTest.class);

    public static class CarXml {
        public String name;
        public List<Wheel> wheels;
        public static class Wheel {
            public String name;
        }
    }

    public static class TypeBindingXml {
        public String string;
        public int intNumber;
        public long longNumber;
        public float floatNumber;
        public double doubleNumber;
    }

    @Test
    public void test_types() throws Exception {
        String xmlContent = "" +
                "<root string='Jon' intNumber='200' longNumber='500' \n" +
                " floatNumber='1.23'" +
                " doubleNumber='4.56'" +
                ">" +
                "</root>";
        TypeBindingXml obj = FreeFormXmlParser.parse(TypeBindingXml.class, xmlContent);
        assertEquals("Jon", obj.string);
        assertEquals(200, obj.intNumber);
        assertEquals(500L, obj.longNumber);
        assertEquals(1.23F, obj.floatNumber);
        assertEquals(4.56D, obj.doubleNumber);
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
        CarXml root = FreeFormXmlParser.parse(CarXml.class, xmlContent);
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
        CarXml root = FreeFormXmlParser.parse(CarXml.class, xmlContent);
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
        CarXml root = FreeFormXmlParser.parse(CarXml.class, xmlContent);
        List<CarXml.Wheel> wheels = root.wheels;
        assertEquals(2, wheels.size());
        assertEquals("one", wheels.get(0).name);
        assertEquals("two", wheels.get(1).name);
    }

    @Test
    public void test_root_property_via_xml_tag() throws Exception {
        String xmlContent = "" +
                "<root>\n" +
                "  <name>July</name>" +
                "</root>";
        CarXml root = FreeFormXmlParser.parse(CarXml.class, xmlContent);
        assertEquals("July", root.name);
    }

    @Test
    public void test_root_property_via_xml_attr() throws Exception {
        String xmlContent = "" +
                "<root name='July'>\n" +
                "</root>";
        CarXml root = FreeFormXmlParser.parse(CarXml.class, xmlContent);
        assertEquals("July", root.name);
    }

}