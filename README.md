# Free Form XML Java Parser

A very simple XML parser binding text into the given Java class structure. Written by zencd.

## Key features

- Parsing done by a Java model, no annoytations needed
- XML may come in different (free) forms but it gonna be parsed anyway

## A sample model

    public static class CarXml {
        public String name;
        public List<Wheel> wheel;
        public static class Wheel {
            public String name;
        }
    }

## Free form XML

Any of the following XMLs will be parsed as you might expect.

Setting a Java property could be done as via an XML attribute, as via a dedicated tag:

    <root name='one'/>

    <root>
        <name>one</name>
    </root>

Working with collections. The full form:

    <root>
        <wheels>
            <wheel name='one'/>
            <wheel name='two'/>
        </wheels>
    </root>

Without the parent `<wheels>` it works too:

    <root>
      <wheel name='one'/>
      <wheel name='two'/>
    </root>

A special tag `<item>` automatically recognized as a `<wheel>` being inside a collection:

    <root>
        <wheels>
            <item name='one'/>
        </wheels>
    </root>

## Parser usage

    CarXml root = FreeFormXmlParser.parse(CarXml.class, xmlContent);

Also refer to tests like `FreeFormXmlParserTest`. 
