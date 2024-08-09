package com.example.utils;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class XMLParser {
    public static Map<String, String> ParsertoNodeList(String xmlData) {
        Map<String, String> map = new HashMap<>();
        try {

            // 创建DocumentBuilder工厂
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 解析XML字符串
            Document doc = builder.parse(new java.io.ByteArrayInputStream(xmlData.getBytes()));

            // 创建XPath对象
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // 获取所有polygon标签内容
            NodeList polygonNodes = (NodeList) xpath.evaluate("//polygon", doc, XPathConstants.NODESET);
            map.put("polygon", polygonNodes.item(0).getTextContent());
            for (int i = 0; i < polygonNodes.getLength(); i++) {
                System.out.println("Polygon: " + polygonNodes.item(i).getTextContent());
            }

            // 获取所有circle标签内容
            NodeList circleNodes = (NodeList) xpath.evaluate("//circle", doc, XPathConstants.NODESET);
            map.put("circle", circleNodes.item(0).getTextContent());
            for (int i = 0; i < circleNodes.getLength(); i++) {
                System.out.println("Circle: " + circleNodes.item(i).getTextContent());
            }

            // 获取所有geocode标签内容并拼接起来
            NodeList geocodeNodes = (NodeList) xpath.evaluate("//geocode", doc, XPathConstants.NODESET);

            StringBuilder geocodeBuilder = new StringBuilder();
            for (int i = 0; i < geocodeNodes.getLength(); i++) {
                if (geocodeBuilder.length() > 0) {
                    geocodeBuilder.append(",");
                }
                geocodeBuilder.append(geocodeNodes.item(i).getTextContent());
            }
            map.put("geocode", String.valueOf(geocodeBuilder));
            System.out.println("Combined Geocode: " + geocodeBuilder.toString());
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String[] args) {
        // 解析XML字符串（你也可以解析文件或其他输入源）
        String xmlData = "<data>"
                + "<area>"
                + "<areaDesc>浙江省杭州市、宁波市、绍兴市</areaDesc>"
                + "<polygon>39.87132449822707,116.43805764744252 39.87128923401099,116.4382911465195 39.8712502877112,116.43788996409587 39.87112784527392,116.43772789392722 39.87104020541204,116.4375042478286 39.87132449822707,116.43805764744252</polygon>"
                + "<circle>39.85799384124283,116.40441202579439 10000</circle>"
                + "<geocode>330100000000,330200000000,330600000000</geocode>"
                + "</area>"
                + "<area>"
                + "<areaDesc>浙江省嘉兴市、湖州市</areaDesc>"
                + "<geocode>330400000000,330500000000</geocode>"
                + "</area>"
                + "</data>";


        Map<String, String> map = ParsertoNodeList(xmlData);
        map.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                System.out.println(s2);
            }
        });
    }
}
