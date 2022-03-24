package org.openprovenance.prov.core.jsonld11;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openprovenance.prov.core.jsonld11.serialization.Constants;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeTypedValue implements Constants {
    @JsonProperty(PROPERTY_AT_TYPE)
    public String typex;
    @JsonProperty(PROPERTY_AT_VALUE)
    public String value;
    @JsonProperty(PROPERTY_STRING_LANG)
    public String language;
}
