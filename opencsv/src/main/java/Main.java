import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Object> parseXMLParameters = new ArrayList<>();
    public static List<Employee> employeesXML = new ArrayList<>();

    public static int changeJsonName = 0;

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        dataCSV();
        dataXML();
    }

    public static void dataXML() throws IOException, ParserConfigurationException, SAXException {
        String fileName = "data.xml";
        List<Employee> list = parseXML(fileName);
        String json = listToJson(list);
        writeString(json);
    }


    public static List<Employee> parseXML(String filename) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filename));

        Node root = doc.getDocumentElement();
        read(root);
        return employeesXML;
    }

    public static void read(Node node) {

        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);

            if (node_.getNodeName().equals("employee")) {
                Element element = (Element) node_;
                read(element);
                Employees(parseXMLParameters);
            }

            if (node_.getNodeType() == Node.ELEMENT_NODE) {
                Element element_ = (Element) node_;
                parseXMLParameters.add(element_.getTextContent());
            }
        }
    }

    public static void Employees(List<Object> parameters) {
        employeesXML.add(new Employee(Long.parseLong((String) parameters.get(0)), (String) parameters.get(1), (String) parameters.get(2), (String) parameters.get(3), Integer.parseInt((String) parameters.get(4))));
    }


    public static void dataCSV() {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json);
    }


    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;

        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            String[] nextLine;
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;

    }


    public static String listToJson(List list) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        String json = gson.toJson(list, listType);

        return json;
    }


    public static void writeString(String json) {
        String jsonName;

        if (changeJsonName == 0) {
            jsonName = "data.json";
        } else {
            jsonName = "data2.json";
        }
        try (FileWriter file = new FileWriter(jsonName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        changeJsonName++;

    }

}

