package org.openprovenance.prov.template.log2prov.interfaces;

// This interface is useful to invoke method on generated classes, by means of the ProxyManagement class, without having to share any package/classes.
public interface ProxySQLInterface {

    String getSQLInsert();

    String getSQLInsertStatement();
    
    Object bean2sql();
    Object toBean(Object [] record);

}
