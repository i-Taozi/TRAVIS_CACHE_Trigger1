package org.openprovenance.prov.dot;
import junit.framework.TestCase;
import org.openprovenance.prov.xml.Document;
import org.openprovenance.prov.xml.ProvFactory;

import javax.xml.bind.JAXBException;
import org.openprovenance.prov.notation.Utility;

public class ASNTest extends TestCase {

    public void asnToDot(String asnFile, String xmlFile, String dotFile, String pdfFile, String title)
        throws java.io.FileNotFoundException,  java.io.IOException, JAXBException, Throwable {
        Utility u=new Utility();

        ProvFactory pFactory=ProvFactory.getFactory();

        Document o= (Document) u.convertASNToJavaBean(asnFile, pFactory);

        //serial.serialiseDocument(new File(xmlFile),o,true);

        ProvToDot toDot=new ProvToDot(pFactory,"src/main/resources/defaultConfigWithRoleNoLabel.xml");
        
        toDot.convert(o,dotFile,pdfFile, title);
    }

    public void testAsnToDot1() throws java.io.FileNotFoundException,  java.io.IOException, JAXBException, Throwable {
        asnToDot("src/test/resources/prov/file-example2.asn",
                 "target/file-example2.prov-xml",
                 "target/file-example2.dot",
                 "target/file-example2.pdf",
                 "file-example2");
    }
}
