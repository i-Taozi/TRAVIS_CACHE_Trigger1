package org.openprovenance.prov.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.openprovenance.prov.core.json.serialization.deserial.CustomQualifiedNameDeserializer;
import org.openprovenance.prov.model.QualifiedName;

@JsonPropertyOrder({ "@id",  "alternate1", "alternate2" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface JSON_QualifiedAlternateOf extends JSON_Generic2, JSON_Qualified {


    @JsonDeserialize(using = CustomQualifiedNameDeserializer.class)
    public QualifiedName getAlternate2();

    @JsonDeserialize(using = CustomQualifiedNameDeserializer.class)
    public QualifiedName getAlternate1();


}
