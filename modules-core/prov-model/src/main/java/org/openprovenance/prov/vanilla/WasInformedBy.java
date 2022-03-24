package org.openprovenance.prov.vanilla;

import org.openprovenance.apache.commons.lang.builder.*;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Value;

import java.util.*;
import java.util.stream.Collectors;
import static org.openprovenance.prov.vanilla.ActedOnBehalfOf.QUALIFIED_NAME_XSD_STRING;

public class WasInformedBy implements org.openprovenance.prov.model.WasInformedBy, Equals, HashCode, ToString, HasAttributes {

    private Optional<QualifiedName> id=Optional.empty();
    private List<org.openprovenance.prov.model.LangString> labels = new LinkedList<>();
    private List<org.openprovenance.prov.model.Other> other = new LinkedList<>();
    private List<org.openprovenance.prov.model.Type> type = new LinkedList<>();
    protected QualifiedName informed;
    protected QualifiedName informant;


    static final ProvUtilities u=new ProvUtilities();



    private WasInformedBy() {}

    public WasInformedBy(QualifiedName id,
                         Collection<Attribute> attributes) {
        this.setId(id);
        u.populateAttributes(attributes, labels, new LinkedList<>(), type,new LinkedList<>(),other,null);
    }

    public WasInformedBy(QualifiedName id,
                         QualifiedName informed,
                         QualifiedName informant,
                         Collection<Attribute> attributes) {
        this.setId(id);
        this.informed=informed;
        this.informant=informant;
        u.populateAttributes(attributes, labels, new LinkedList<>(), type,new LinkedList<>(),other,null);
    }



    @Override
    public void setInformed(QualifiedName informed) {
        this.informed=informed;
    }

    @Override
    public void setInformant(QualifiedName informant) {
        this.informant=informant;
    }

    @Override
    public QualifiedName getInformant() {
        return informant;
    }

    @Override
    public QualifiedName getInformed() {
        return informed;
    }



    @Override
    public QualifiedName getId() {
        return id.orElse(null);
    }


    @Override
    public Kind getKind() {
        return Kind.PROV_COMMUNICATION;
    }



    @Override
    public void setId(QualifiedName value) {
        id = Optional.ofNullable(value);
    }



    @Override
    public List<org.openprovenance.prov.model.LangString> getLabel() {
        return labels;
    }


    @Override
    public List<org.openprovenance.prov.model.Type> getType() {
        return type;
    }


    @Override
    public List<org.openprovenance.prov.model.Other> getOther() {
        return other;
    }



    public void equals(Object object, EqualsBuilder equalsBuilder) {
        if (!(object instanceof WasInformedBy)) {
            equalsBuilder.appendSuper(false);
            return ;
        }
        if (this == object) {
            return ;
        }
        final WasInformedBy that = ((WasInformedBy) object);
        equalsBuilder.append(this.getId(), that.getId());
        equalsBuilder.append(this.getInformed(), that.getInformed());
        equalsBuilder.append(this.getInformant(), that.getInformant());
        equalsBuilder.append(this.getIndexedAttributes(), that.getIndexedAttributes());
    }

    public boolean equals(Object object) {
        if (!(object instanceof WasInformedBy)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final EqualsBuilder equalsBuilder = new EqualsBuilder();
        equals(object, equalsBuilder);
        return equalsBuilder.isEquals();
    }

    public void hashCode(HashCodeBuilder hashCodeBuilder) {
        hashCodeBuilder.append(this.getId());
        hashCodeBuilder.append(this.getInformed());
        hashCodeBuilder.append(this.getInformant());
        hashCodeBuilder.append(this.getIndexedAttributes());
    }

    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCode(hashCodeBuilder);
        return hashCodeBuilder.toHashCode();
    }

    public void toString(ToStringBuilder toStringBuilder) {


        {
            QualifiedName theId;
            theId = this.getId();
            toStringBuilder.append("id", theId);
        }

        {
            QualifiedName theInformed;
            theInformed = this.getInformed();
            toStringBuilder.append("informed", theInformed);
        }

        {
            QualifiedName theInformant;
            theInformant = this.getInformant();
            toStringBuilder.append("informant", theInformant);
        }

       {
            Map<QualifiedName, Set<Attribute>> theAttributes;
            theAttributes = this.getIndexedAttributes();
            toStringBuilder.append("attributes", theAttributes);
        }



    }

    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toString(toStringBuilder);
        return toStringBuilder.toString();
    }

    @Override
    public Collection<Attribute> getAttributes() {
        LinkedList<Attribute> result=new LinkedList<>();
        result.addAll(getLabel().stream().map(s -> new Label(QUALIFIED_NAME_XSD_STRING,s)).collect(Collectors.toList()));
        result.addAll(getType());
        result.addAll(getOther().stream().map(o -> (Attribute)o).collect(Collectors.toList())); //TODO: collect directly into result
        return result;
    }

    public void setIndexedAttributes (Object qn, Set<Attribute> attributes) {
        List<Value> values=new LinkedList<>();
        List<org.openprovenance.prov.model.Location> locations=new LinkedList<>();
        u.distribute((QualifiedName)qn,attributes,getLabel(),values, locations,getType(),Collections.EMPTY_LIST, getOther());
    }


    @Override
    public Map<QualifiedName, Set<Attribute>> getIndexedAttributes() {
        return u.split(getAttributes());
    }

}
