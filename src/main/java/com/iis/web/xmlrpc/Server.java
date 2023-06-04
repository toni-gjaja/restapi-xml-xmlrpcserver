package com.iis.web.xmlrpc;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.webserver.WebServer;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Server {

    private static final String CITIES_XML_PATH = "src/main/resources/files/cities.xml";

    public String getCityWeather(String name) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {

        getCurrentWeather();

        return getSearchedCity(name);

    }

    private String getSearchedCity(String name) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(CITIES_XML_PATH);

        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = "//Grad[GradIme='"+name+"']";

        NodeList nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

        if (nodeList.getLength() != 0){

            String temp = xpath.evaluate("Podatci/Temp", nodeList.item(0));
            String vlaga = xpath.evaluate("Podatci/Vlaga", nodeList.item(0));
            String vjetarSmjer = xpath.evaluate("Podatci/VjetarSmjer", nodeList.item(0));
            String vjetarBrzina = xpath.evaluate("Podatci/VjetarBrzina", nodeList.item(0));
            String vrijeme = xpath.evaluate("Podatci/Vrijeme", nodeList.item(0));

            return "Temp: " + temp + ", " + "Vlaga: " + vlaga + ", " + "VjetarSmjer: " + vjetarSmjer + ", " + "VjetarBrzina: " + vjetarBrzina + ", " + "Vrijeme: " + vrijeme;

        }
        else {
            return "failure";
        }

    }

    private void getCurrentWeather() throws FileNotFoundException {

        FileOutputStream out = new FileOutputStream(CITIES_XML_PATH);

        try{
            URL url = new URL("https://vrijeme.hr/hrvatska_n.xml");
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.close();
            in.close();

        } catch (IOException e) {
            System.out.println("Error while downloading the XML file: " + e.getMessage());
        }


    }

    public static void main(String[] args){

        try{

            System.out.println("Starting server...");

            WebServer server = new WebServer(5001);
            PropertyHandlerMapping mapping = new PropertyHandlerMapping();
            mapping.addHandler("SERVER", Server.class);
            server.getXmlRpcServer().setHandlerMapping(mapping);
            server.start();



        }catch (Exception e){
            System.out.println("Server error!");
        }






    }


}
