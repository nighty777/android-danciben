package com.study.electronic_dictionary;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
//解析xml的信息
public class ApiXmlParser {
    public SAXParserFactory factory=null;
    public XMLReader reader=null;

    public ApiXmlParser(){

        try {
            factory=SAXParserFactory.newInstance();
            reader=factory.newSAXParser().getXMLReader();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void parseJinShanXml(DefaultHandler content, InputSource inSource){
        if(inSource==null)
            return;
        try {
            reader.setContentHandler(content);
            reader.parse(inSource);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
