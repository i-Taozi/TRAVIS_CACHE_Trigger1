package org.openprovenance.prov.vanilla;

import org.openprovenance.apache.commons.lang.builder.*;
import org.openprovenance.prov.model.QualifiedName;


public class AlternateOf implements org.openprovenance.prov.model.AlternateOf, Equals, HashCode, ToString {


    protected QualifiedName alternate1;
    protected QualifiedName alternate2;




    private AlternateOf() {}



    public AlternateOf(QualifiedName alternate1,
                       QualifiedName alternate2) {
        this.alternate1 = alternate1;
        this.alternate2 = alternate2;
    }



    @Override
    public void setAlternate1(QualifiedName alternate1) {
        this.alternate1 =alternate1;
    }

    @Override
    public void setAlternate2(QualifiedName alternate2) {
        this.alternate2 =alternate2;
    }

    @Override
    public QualifiedName getAlternate2() {
        return alternate2;
    }

    @Override
    public QualifiedName getAlternate1() {
        return alternate1;
    }


    @Override
    public Kind getKind() {
        return Kind.PROV_ALTERNATE;
    }



    public void equals(Object object, EqualsBuilder equalsBuilder) {
        if (!(object instanceof AlternateOf)) {
            equalsBuilder.appendSuper(false);
            return ;
        }
        if (this == object) {
            return ;
        }
        final AlternateOf that = ((AlternateOf) object);
        equalsBuilder.append(this.getAlternate1(), that.getAlternate1());
        equalsBuilder.append(this.getAlternate2(), that.getAlternate2());
    }

    public void equals2(Object object, EqualsBuilder equalsBuilder) {
        if (!(object instanceof QualifiedAlternateOf)) {
            equalsBuilder.appendSuper(false);
            return ;
        }
        if (this == object) {
            return ;
        }
        final QualifiedAlternateOf that = ((QualifiedAlternateOf) object);
        equalsBuilder.append(this.getAlternate1(), that.getAlternate1());
        equalsBuilder.append(this.getAlternate2(),  that.getAlternate2());
    }

    public boolean equals(Object object) {
        if (!(object instanceof AlternateOf)) {

            if (object instanceof QualifiedAlternateOf) {
                QualifiedAlternateOf qalt=(QualifiedAlternateOf) object;
                if (qalt.isUnqualified()) {
                    final EqualsBuilder equalsBuilder2 = new EqualsBuilder();
                    equals2(object, equalsBuilder2);
                    return equalsBuilder2.isEquals();
                }
                return false;
            }
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
        hashCodeBuilder.append(this.getAlternate1());
        hashCodeBuilder.append(this.getAlternate2());
    }

    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCode(hashCodeBuilder);
        return hashCodeBuilder.toHashCode();
    }

    public void toString(ToStringBuilder toStringBuilder) {



        {
            QualifiedName theEntity;
            theEntity = this.getAlternate2();
            toStringBuilder.append("alternate2", theEntity);
        }

        {
            QualifiedName theAgent;
            theAgent = this.getAlternate1();
            toStringBuilder.append("alternate1", theAgent);
        }






    }

    @Override
    public String toString() {
        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toString(toStringBuilder);
        return toStringBuilder.toString();
    }


}
