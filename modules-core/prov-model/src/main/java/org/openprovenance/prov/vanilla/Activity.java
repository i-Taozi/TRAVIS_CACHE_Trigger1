package org.openprovenance.prov.vanilla;

import org.openprovenance.apache.commons.lang.builder.*;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Role;
import org.openprovenance.prov.model.Value;
import static org.openprovenance.prov.vanilla.ActedOnBehalfOf.QUALIFIED_NAME_XSD_STRING;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;
import java.util.stream.Collectors;

public class Activity implements org.openprovenance.prov.model.Activity, Equals, HashCode, ToString, HasAttributes { //, JLD_Activity {

    private Optional<org.openprovenance.prov.model.QualifiedName> id;
    private Optional<XMLGregorianCalendar> startTime=Optional.empty();
    private Optional<XMLGregorianCalendar> endTime=Optional.empty();
    private List<org.openprovenance.prov.model.LangString> labels = new LinkedList<>();
    private List<org.openprovenance.prov.model.Location> location = new LinkedList<>();
    private List<org.openprovenance.prov.model.Other> other = new LinkedList<>();
    private List<org.openprovenance.prov.model.Type> type = new LinkedList<>();

    static final org.openprovenance.prov.vanilla.ProvUtilities u=new ProvUtilities();


    protected Activity() {}

    public Activity(org.openprovenance.prov.model.QualifiedName id,
                    XMLGregorianCalendar startTime,
                    XMLGregorianCalendar endTime,
                    Collection<Attribute> attributes) {
        this.setId(id);
        this.setStartTime(startTime);
        this.setEndTime(endTime);

        u.populateAttributes(attributes, labels, location, type, new LinkedList<>(),other,null);


    }


    @Override
    public org.openprovenance.prov.model.QualifiedName getId() {
        return id.orElse(null);
    }


    @Override
    public Kind getKind() {
        return Kind.PROV_ACTIVITY;
    }



    @Override
    public void setId(org.openprovenance.prov.model.QualifiedName value) {
        id = Optional.ofNullable(value);
    }



    @Override
    public XMLGregorianCalendar getStartTime() {
        return startTime.orElse(null);
    }

    @Override
    public void setStartTime(XMLGregorianCalendar value) {
        startTime = Optional.ofNullable(value);
    }

    @Override
    public XMLGregorianCalendar getEndTime() {
        return endTime.orElse(null);
    }

    @Override
    public void setEndTime(XMLGregorianCalendar value) {
        endTime = Optional.ofNullable(value);
    }

    @Override
    public List<org.openprovenance.prov.model.LangString> getLabel() {
        return labels;
    }

    @Override
    public List<org.openprovenance.prov.model.Location> getLocation() {
        return location;
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
        if (!(object instanceof Activity)) {
            equalsBuilder.appendSuper(false);
            return ;
        }
        if (this == object) {
            return ;
        }
        final Activity that = ((Activity) object);
        equalsBuilder.append(this.getStartTime(), that.getStartTime());
        equalsBuilder.append(this.getEndTime(), that.getEndTime());
        equalsBuilder.append(this.getId(), that.getId());
       equalsBuilder.append(this.getIndexedAttributes(), that.getIndexedAttributes());
    }

    public boolean equals(Object object) {
        if (!(object instanceof Activity)) {
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
        hashCodeBuilder.append(this.getStartTime());
        hashCodeBuilder.append(this.getEndTime());
        hashCodeBuilder.append(this.getId());
        hashCodeBuilder.append(this.getIndexedAttributes());
    }

    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCode(hashCodeBuilder);
        return hashCodeBuilder.toHashCode();
    }

    public void toString(ToStringBuilder toStringBuilder) {
        {
            XMLGregorianCalendar theStartTime;
            theStartTime = this.getStartTime();
            toStringBuilder.append("startTime", theStartTime);
        }
        {
            XMLGregorianCalendar theEndTime;
            theEndTime = this.getEndTime();
            toStringBuilder.append("endTime", theEndTime);
        }

        {
            Map<QualifiedName, Set<Attribute>> theAttributes;
            theAttributes = this.getIndexedAttributes();
            toStringBuilder.append("attributes", theAttributes);
        }


        {
            org.openprovenance.prov.model.QualifiedName theId;
            theId = this.getId();
            toStringBuilder.append("id", theId);
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
        result.addAll(getLocation());
        result.addAll(getOther().stream().map(o -> (Attribute)o).collect(Collectors.toList())); //TODO: collect directly into result
        return result;
    }

    public void setIndexedAttributes(Object qn, Set<Attribute> attributes) {
        List<Value> values_discard=new LinkedList<>();
        List<Role> roles_discard=new LinkedList<>();
        u.distribute((QualifiedName)qn,attributes,getLabel(),values_discard,getLocation(),getType(),roles_discard,getOther());
    }



    @Override
    public Map<QualifiedName, Set<Attribute>> getIndexedAttributes() {
        return u.split(getAttributes());
    }


}
