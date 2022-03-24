package org.openprovenance.prov.model;

import org.openprovenance.prov.vanilla.ProvFactory;
import org.openprovenance.prov.model.BeanTraversal;
import org.openprovenance.prov.model.Document;

/**
 * Unit test for PROV roundtrip conversion between Java represenations
 */
public class RoundTripJavaToJavaTest extends RoundTripFromJavaTest {

    public RoundTripJavaToJavaTest(String testName) {
        super(testName);
        test=false;
    }


    ProvFactory pFactory=new ProvFactory();

    @Override
    public void compareDocAndFile(Document doc, String file, boolean check) {
        BeanTraversal bc=new BeanTraversal(pFactory, pFactory);
        org.openprovenance.prov.model.Document doc2=bc.doAction(doc);
        compareDocuments(doc, doc2, check && checkTest(file));
    }

    public boolean checkSchema(String name) {
        return false;
    }

}
