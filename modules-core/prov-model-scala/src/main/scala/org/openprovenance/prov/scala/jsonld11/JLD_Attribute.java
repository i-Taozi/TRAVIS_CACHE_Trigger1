package org.openprovenance.prov.scala.jsonld11;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.scala.jsonld11.serialization.deserial.CustomQualifiedNameDeserializer;

@JsonPropertyOrder({ "elementName", "type", "value" })
public interface JLD_Attribute {

    @JsonDeserialize(using = CustomQualifiedNameDeserializer.class)
    public QualifiedName getElementName();

    @JsonIgnore
    public Attribute.AttributeKind getKind();

    @JsonDeserialize(using = CustomQualifiedNameDeserializer.class)
    public QualifiedName getType();

    public Object getValue();

    @JsonIgnore
    public Object getConvertedValue();

    @JsonIgnore
    public void setValueFromObject(Object anObject);

    @JsonIgnore
    void setValueFromObject(org.w3c.dom.Node n);

    @JsonIgnore
    void setValueFromObject(byte[] bytes);

}
