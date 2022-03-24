package org.openprovenance.prov.service.core.memory;

import org.openprovenance.prov.storage.api.NonDocumentResource;
import org.openprovenance.prov.storage.api.ResourceIndex;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NonDocumentResourceInMemory implements NonDocumentResource {

    public Map<String, Object> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, Object> extension) {
        this.extension = extension;
    }


    public String getVisibleId() {
        return visibleId;
    }

    public void setVisibleId(String visibleId) {
        this.visibleId = visibleId;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public ResourceIndex.StorageKind getKind() {
        return kind;
    }

    public void setKind(ResourceIndex.StorageKind kind) {
        this.kind = kind;
    }

    private String visibleId;
    private String storageId;
    private Date expires;
    private ResourceIndex.StorageKind kind;
    private String mediaType;
    public Map<String, Object> extension = new HashMap<>();




    @Override
    public String getMediaType() {
        return mediaType;
    }

    @Override
    public void setMediaType(String type) {
        this.mediaType=type;
    }
}