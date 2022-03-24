
package org.openprovenance.prov.sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openprovenance.prov.xml.builder.Equals;
import org.openprovenance.prov.xml.builder.HashCode;
import org.openprovenance.prov.xml.builder.ToString;
import org.openprovenance.prov.xml.builder.JAXBEqualsBuilder;
import org.openprovenance.prov.xml.builder.JAXBHashCodeBuilder;
import org.openprovenance.prov.xml.builder.JAXBToStringBuilder;
import org.openprovenance.apache.commons.lang.builder.EqualsBuilder;
import org.openprovenance.apache.commons.lang.builder.HashCodeBuilder;
import org.openprovenance.apache.commons.lang.builder.ToStringBuilder;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.Other;
import org.openprovenance.prov.model.ProvUtilities;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.xml.AttributeList;
import org.openprovenance.prov.xml.HasAllAttributes;
import org.openprovenance.prov.xml.SortedAttributeList;


/**
 * <p>Java class for Activity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Activity"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/ns/prov#}AStatement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="startTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="endTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.w3.org/ns/prov#}label" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.w3.org/ns/prov#}location" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.w3.org/ns/prov#}type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.w3.org/ns/prov#}others" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute ref="{http://www.w3.org/ns/prov#}id"/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Activity", propOrder = {
    "startTime",
    "endTime",
    "label",
 //   "location",
 //   "type",
 //   "others",
 //   "any",
    "all"
})
@Entity(name = "Activity")
@Table(name = "ACTIVITY")
public class Activity
    extends AStatement
    implements Equals, HashCode, ToString, org.openprovenance.prov.model.Activity, HasAllAttributes
{

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endTime;
    @XmlElement(type = org.openprovenance.prov.sql.InternationalizedString.class)
    protected List<org.openprovenance.prov.model.LangString> label;
    
    transient protected List<org.openprovenance.prov.model.Location> location;
    transient protected List<org.openprovenance.prov.model.Type> type;
    transient protected List<Other> others;
    
    @XmlAnyElement
    protected List<Attribute> all;
    @XmlAttribute(name = "id", namespace = "http://www.w3.org/ns/prov#")
    @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(QualifiedNameAdapter.class)
    protected QualifiedName id;
    

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Transient
    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartTime(XMLGregorianCalendar value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Transient
    public XMLGregorianCalendar getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndTime(XMLGregorianCalendar value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the label property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLabel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.openprovenance.prov.sql.InternationalizedString }
     * 
     * 
     */
    @OneToMany(targetEntity = org.openprovenance.prov.sql.InternationalizedString.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "LABEL_ACTIVITY_PK")
    public List<org.openprovenance.prov.model.LangString> getLabel() {
        if (label == null) {
            label = new ArrayList<org.openprovenance.prov.model.LangString>();
        }
        return this.label;
    }

    /**
     * 
     * 
     */
    public void setLabel(List<org.openprovenance.prov.model.LangString> label) {
        this.label = label;
    }

    /**
     * Gets the value of the location property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the location property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.openprovenance.prov.sql.Location }
     * 
     * 
     */
    @OneToMany(targetEntity = org.openprovenance.prov.sql.Location.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "ACTIVITY")
    public List<org.openprovenance.prov.model.Location> getLocation() {
        if (location == null) {
            location=AttributeList.populateKnownAttributes(this,all, org.openprovenance.prov.model.Location.class);
        }
        return this.location;
    }

    /**
     * 
     * 
     */
    public void setLocation(List<org.openprovenance.prov.model.Location> location) {
        this.location = location;
    }

    /**
     * Gets the value of the type property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the type property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.openprovenance.prov.sql.Type }
     * 
     * 
     */
    @OneToMany(targetEntity = org.openprovenance.prov.sql.Type.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "TYPE__ACTIVITY_PK")
    public List<org.openprovenance.prov.model.Type> getType() {
        if (type == null) {
            type=AttributeList.populateKnownAttributes(this,all, org.openprovenance.prov.model.Type.class);
        }
        return this.type;
    }

    /**
     * 
     * 
     */
    public void setType(List<org.openprovenance.prov.model.Type> type) {
        this.type = type;
    }

    /**
     * Gets the value of the others property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the others property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOthers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Other }
     * 
     * 
     */
    @OneToMany(targetEntity =  org.openprovenance.prov.sql.Other.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "OTHERS_ACTIVITY_PK")
    public List<Other> getOther() {
        if (others == null) {
            others=AttributeList.populateKnownAttributes(this,all, org.openprovenance.prov.model.Other.class);
        }
        return this.others;
    }

    /**
     * 
     * 
     */
    public void setOther(List<Other> others) {
        this.others = others;
    }



    /** Gets the List of all attributes
     * @see org.openprovenance.prov.xml.HasAllAttributes#getAllAttributes()
     */
    @Transient
    public List<Attribute> getAllAttributes() {
        if (all == null) {
            all = new SortedAttributeList<Attribute>();
        }
        return this.all;
    }
    



    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link QualifiedName }
     *     
     */
    @ManyToOne(targetEntity = org.openprovenance.prov.sql.QualifiedName.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "ID")
    public QualifiedName getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link QualifiedName }
     *     
     */
    public void setId(QualifiedName value) {
        this.id = value;
    }

    @Basic
    @Column(name = "START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartTimeItem() {
        return ProvUtilities.toDate(this.getStartTime());
    }

    public void setStartTimeItem(Date target) {
        setStartTime(ProvUtilities.toXMLGregorianCalendar(target));
    }

    @Basic
    @Column(name = "END_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndTimeItem() {
        return ProvUtilities.toDate(this.getEndTime());
    }

    public void setEndTimeItem(Date target) {
        setEndTime(ProvUtilities.toXMLGregorianCalendar(target));
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
        equalsBuilder.append(this.getLabel(), that.getLabel());
        equalsBuilder.append(this.getLocation(), that.getLocation());
        equalsBuilder.append(this.getType(), that.getType());
        equalsBuilder.append(this.getOther(), that.getOther());
        equalsBuilder.append(this.getId(), that.getId());
    }

    public boolean equals(Object object) {
        if (!(object instanceof Activity)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final EqualsBuilder equalsBuilder = new JAXBEqualsBuilder();
        equals(object, equalsBuilder);
        return equalsBuilder.isEquals();
    }

    public void hashCode(HashCodeBuilder hashCodeBuilder) {
        hashCodeBuilder.append(this.getStartTime());
        hashCodeBuilder.append(this.getEndTime());
        hashCodeBuilder.append(this.getLabel());
        hashCodeBuilder.append(this.getLocation());
        hashCodeBuilder.append(this.getType());
        hashCodeBuilder.append(this.getOther());
        hashCodeBuilder.append(this.getId());
    }

    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new JAXBHashCodeBuilder();
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
            List<org.openprovenance.prov.model.LangString> theLabel;
            theLabel = this.getLabel();
            toStringBuilder.append("label", theLabel);
        }
        {
            List<org.openprovenance.prov.model.Location> theLocation;
            theLocation = this.getLocation();
            toStringBuilder.append("location", theLocation);
        }
        {
            List<org.openprovenance.prov.model.Type> theType;
            theType = this.getType();
            toStringBuilder.append("type", theType);
        }
        {
            List<org.openprovenance.prov.model.Other> theOthers;
            theOthers = this.getOther();
            toStringBuilder.append("others", theOthers);
        }
        {
            org.openprovenance.prov.model.QualifiedName theId;
            theId = this.getId();
            toStringBuilder.append("id", theId);
        }
    }
    
    public String toString() {
        final ToStringBuilder toStringBuilder = new JAXBToStringBuilder(this);
        toString(toStringBuilder);
        return toStringBuilder.toString();
    }
    @Transient
    public Kind getKind() {
	return StatementOrBundle.Kind.PROV_ACTIVITY;
    }


}
