import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLValidator {

	private static final String xsdOrt = "C:\\Users\\Thieron\\SWE-Projekt-zentraler-Servermanager\\communication\\test.xsd";
    public static void main(String[] args) throws SAXException, IOException {

        // 1. Erstellt ein Factory-Objekt f�r XML zu validieren.
        SchemaFactory factory = 
            SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        
        // 2. Kompiliert das Schema. 
        File schemaLocation = new File(xsdOrt);
        Schema schema = factory.newSchema(schemaLocation);
    
        // 3. Erstellt einen Validator von diesem Schema.
        Validator validator = schema.newValidator();
        
        // 4. �bersetzt das zu �berpr�fende Dokument.
        DocumentBuilder parser=null;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
        Document document = parser.parse(new File("test.xml"));
        
        // 5. �berpr�ft das Dokument
        try {
            validator.validate(new DOMSource(document));
            System.out.println(args[0] + " ist gueltig.");
        }
        catch (SAXException ex) {
            System.out.println(args[0] + " is nicht gueltig wegen: ");
            System.out.println(ex.getMessage());
        }  
        
    }

}