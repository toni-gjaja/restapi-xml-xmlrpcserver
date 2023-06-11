package com.iis.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iis.web.dto.CityDTO;
import com.iis.web.dto.MotoDTO;
import com.iis.web.dto.MotoXmlDTO;
import com.iis.web.model.Moto;
import com.thaiopensource.validate.ValidationDriver;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("app")
public class MainController {

    private static final String XML_FILE_PATH = "src/main/resources/files/moto.xml";
    private static final String XSD_FILE_PATH = "src/main/resources/files/scheme1.xsd";
    private static final String RNG_FILE_PATH = "src/main/resources/files/scheme2.rng";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @GetMapping("/home")
    public String getMainPage(Model model) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.api-ninjas.com/v1/motorcycles?make=Ducati"))
                .header("X-Api-Key", "YOUR_SECRET_KEY")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        MotoDTO[] motorcycles = objectMapper.readValue(response.body(), MotoDTO[].class);

        List<Moto> motos = getMotos(motorcycles);

        model.addAttribute("motos", motos);

        return "home";
    }

    private List<Moto> getMotos(MotoDTO[] motorcycles) {

        List<Moto> motos = new ArrayList<>();

        for (MotoDTO motorcycle : motorcycles) {

            motos.add(new Moto(
                    motorcycle.getMake(),
                    motorcycle.getModel(),
                    motorcycle.getYear(),
                    motorcycle.getPower(),
                    motorcycle.getEngine(),
                    motorcycle.getTorque()
            ));
        }

        return motos;

    }

    @GetMapping("/xsd")
    public String getXsdPage(){
        return "xsd";
    }

    @PostMapping("/validateWithXsd")
    @ResponseBody
    public String validateWithXsd(@RequestBody MotoDTO formdata) throws JAXBException{

        File outputFile = getXmlFile(formdata);

        return checkXmlValidityXsd(outputFile) ? "success" : "failure";

    }

    private boolean checkXmlValidityXsd(File outputFile){

        try{

            File xsd = new File(XSD_FILE_PATH);

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Schema schema = schemaFactory.newSchema(xsd);

            Validator validator = schema.newValidator();

            validator.validate(new StreamSource(outputFile));

        }catch (SAXException | IOException e){

            return false;

        }

        return true;

    }

    @PostMapping("/validateWithRng")
    @ResponseBody
    public String validateWithRng(@RequestBody MotoDTO formdata) throws JAXBException{

        File outputFile = getXmlFile(formdata);
        return checkXmlValidityRng(outputFile) ? "success" : "failure";

    }

    private File getXmlFile(MotoDTO formdata) throws JAXBException {

        MotoXmlDTO moto = new MotoXmlDTO(formdata.getMake(), formdata.getModel(), formdata.getYear(), formdata.getPower(), formdata.getEngine(), formdata.getTorque());
        JAXBContext context = JAXBContext.newInstance(MotoXmlDTO.class);

        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        File outputFile = new File(XML_FILE_PATH);

        marshaller.marshal(moto, outputFile);

        return outputFile;

    }

    @GetMapping("/loadXsd")
    @ResponseBody
    public String loadXsd() throws IOException {

        String xsd;

        xsd = Files.readString(Paths.get(XSD_FILE_PATH));

        return xsd;

    }

    @GetMapping("/loadXml")
    @ResponseBody
    public String loadXml() throws IOException {

        String xml;

        xml = Files.readString(Paths.get(XML_FILE_PATH));

        return xml;

    }

    @GetMapping("/loadRng")
    @ResponseBody
    public String loadRng() throws IOException {

        String rng;

        rng = Files.readString(Paths.get(RNG_FILE_PATH));

        return rng;

    }

    private boolean checkXmlValidityRng(File outputFile){

        File rng = new File(RNG_FILE_PATH);

        ValidationDriver driver = new ValidationDriver();

        try{
            InputSource rngSource = new InputSource(rng.toURI().toString());
            driver.loadSchema(rngSource);

            InputSource xmlSource = new InputSource(outputFile.toURI().toString());
            return driver.validate(xmlSource);

        }catch (IOException | SAXException e) {
            return false;
        }

    }

    @GetMapping("/rng")
    public String getRngPage(){
        return "rng";
    }

    @GetMapping("/dhmz")
    public String getDhmzPage(){
        return "dhmz";
    }

    @PostMapping("/getCityWeather")
    @ResponseBody
    public String getCityWeather(@RequestBody CityDTO city) throws MalformedURLException {

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://localhost:5001"));

        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        try {

            String result = (String)client.execute("SERVER.getCityWeather", Collections.singletonList(city.getName()));
            if (!Objects.equals(result, "failure")){

                return result;

            }

        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }

        return "No city found";

    }

}
