# free-xml-java-parser

A very simple XML parser binding text into the given Java class structure.

Key features:

- No annotations needed
- XML may come in different (free) forms but it gonna be parsed anyway

Let's there is a model:

    public static class CarXml {
        public List<Wheel> wheel;
        public static class Wheel {
            public String name;
        }
    }

Any of the following XMLs will be parsed as you might expect.
The full form:

    <root>
        <wheels>
            <wheel name='one'/>
            <wheel name='two'/>
        </wheels>
    </root>

Without parent `<wheels>` it works too:

    <root>
      <wheel name='one'/>
      <wheel name='two'/>
    </root>

A special tag `<item>` automatically recognized as a `wheel` tag here:

    <root>
        <wheels>
            <item name='one'/>
        </wheels>
    </root>

Parser usage:

    CarXml root = XmlTreeParser.parse(CarXml.class, xmlContent);
