package org.openprovenance.prov.scala.jsonld11.serialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.openprovenance.prov.scala.jsonld11.serialization.serial.CustomDateSerializer;
import org.openprovenance.prov.scala.jsonld11.serialization.serial.CustomKindSerializer;
import org.openprovenance.prov.scala.jsonld11.serialization.serial.CustomNamespaceSerializer;
import org.openprovenance.prov.scala.jsonld11.serialization.serial.CustomQualifiedNameSerializer;
import org.openprovenance.prov.scala.immutable.QualifiedName;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.model.exception.UncheckedException;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.OutputStream;

public class ProvSerialiser extends org.openprovenance.prov.core.json.serialization.ProvSerialiser {


    private final boolean embedContext;


    public ProvSerialiser () {
      embedContext=true;
    }

    public ProvSerialiser (boolean embedContext) {
        this.embedContext=embedContext;
    }

    public ProvMixin provMixin() {
        return new ProvMixin();
    }



    @Override
    public void serialiseDocument(OutputStream out, Document document, boolean formatted) {
        ObjectMapper mapper = new ObjectMapper();
        if (formatted) mapper.enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule module =
                new SimpleModule("CustomKindSerializer",
                        new Version(1, 0, 0, null, null, null));

        module.addSerializer(StatementOrBundle.Kind.class, new CustomKindSerializer());
        module.addSerializer(QualifiedName.class, new CustomQualifiedNameSerializer());
        module.addSerializer(XMLGregorianCalendar.class, new CustomDateSerializer());
        module.addSerializer(Namespace.class, new CustomNamespaceSerializer(embedContext));
        mapper.registerModule(module);

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("nsFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept("prefixes", "defaultNamespace"));
        mapper.setFilterProvider(filterProvider);

        provMixin().addProvMixin(mapper);


        try {
            mapper.writeValue(out,document);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedException(e);
        }
    }

}
