package org.openprovenance.prov.storage.api;

/* A PROV document generated by template expansion with some bindings. */
public interface TemplateResource extends DocumentResource, Cloneable{
     String TEMPLATE = "TEMPLATE";
     static String getResourceKind() {
          return TEMPLATE;
     }

     String getBindingsStorageId() ;

     void setBindingsStorageId(String bindingsStorageId);

     String getTemplateStorageId();

     void setTemplateStorageId(String templateStorageId);
}
