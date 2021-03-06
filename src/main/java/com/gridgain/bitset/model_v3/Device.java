package com.gridgain.bitset.model_v3;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Device definition.
 * 
 **/
public class Device implements Serializable {
    /** */
    private static final long serialVersionUID = 0L;

    /** Value for updatets. */
    private Timestamp updatets;

    /** Value for demographics. */
    private byte[] demographics;

    /** Value for comments. */
    private String comments;

    /** Value for content. */
    private byte[] content;

    /** Empty constructor. **/
    public Device() {
        // No-op.
    }

    /** SQL constructor. **/
    public Device(Timestamp updatets) {
        this.updatets = updatets;
    }

    /** V1 Migration constructor. *
     *  After running migration utility new Devices will default to having "Migrated" comments
     *  With the extra field, ther toString, equals, etc. have been adjusted
     *  In order to expose this extra field to SQL, change the SQL Schema by issuing command:
     *  "ALTER TABLE DEVICE ADD COLUMN COMMENTS"
    */
    public Device(com.gridgain.bitset.model.Device oDevice) {
        this.updatets = new Timestamp(System.currentTimeMillis());
        this.demographics = oDevice.getDemographics();
        this.comments = "Migrated from V1/V2 Device Type";
        this.content = oDevice.getContent();
    }

    /** Full constructor. **/
    public Device(
        Timestamp updatets,
        byte[] demographics,
        String comments,
        byte[] content) {
        this.updatets = updatets;
        this.demographics = demographics;
        this.comments = comments;
        this.content = content;
    }

    /**
     * Gets updatets
     * 
     * @return Value for updatets.
     **/
    public Timestamp getUpdatets() {
        return updatets;
    }

    /**
     * Sets updatets
     * 
     * @param updatets New value for updatets.
     **/
    public void setUpdatets(Timestamp updatets) {
        this.updatets = updatets;
    }

    /**
     * Gets demographics
     * 
     * @return Value for demographics.
     **/
    public byte[] getDemographics() {
        return demographics;
    }

    /**
     * Sets demographics
     * 
     * @param demographics New value for demographics.
     **/
    public void setDemographics(byte[] demographics) {
        this.demographics = demographics;
    }

    /**
     * Gets comments
     * 
     * @return Value for comments.
     **/
    public String getComments() {
        return comments;
    }

    /**
     * Sets comments
     * 
     * @param comments New value for comments.
     **/
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Gets content
     * 
     * @return Value for content.
     **/
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets content
     * 
     * @param content New value for content.
     **/
    public void setContent(byte[] content) {
        this.content = content;
    }

    /** {@inheritDoc} **/
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        
        if (!(o instanceof Device))
            return false;
        
        Device that = (Device)o;

        if (updatets != null ? !updatets.equals(that.updatets) : that.updatets != null)
            return false;
        

        if (demographics != null ? !demographics.equals(that.demographics) : that.demographics != null)
            return false;
        

        if (comments != null ? !comments.equals(that.comments) : that.comments != null)
            return false;
        
        if (content != null ? !content.equals(that.content) : that.content != null)
            return false;
    
        return true;
    }

    /** {@inheritDoc} **/
    @Override public int hashCode() {
        int res = updatets != null ? updatets.hashCode() : 0;

        res = 31 * res + (demographics != null ? demographics.hashCode() : 0);

        res = 31 * res + (comments != null ? comments.hashCode() : 0);

        res = 31 * res + (content != null ? content.hashCode() : 0);

        return res;
    }

    /** {@inheritDoc} **/
    @Override public String toString() {
        return "Device [" + 
            "updatets=" + updatets + ", " + 
            "demographics=" + demographics + ", " + 
            "comments=" + comments + ", " +
            "content=" + content +
        "]";
    }
}