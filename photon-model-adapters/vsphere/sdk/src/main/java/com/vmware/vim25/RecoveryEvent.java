
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RecoveryEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RecoveryEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:vim25}DvsEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="hostName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="portKey" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="dvsUuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vnic" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecoveryEvent", propOrder = {
    "hostName",
    "portKey",
    "dvsUuid",
    "vnic"
})
public class RecoveryEvent
    extends DvsEvent
{

    @XmlElement(required = true)
    protected String hostName;
    @XmlElement(required = true)
    protected String portKey;
    protected String dvsUuid;
    protected String vnic;

    /**
     * Gets the value of the hostName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets the value of the hostName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostName(String value) {
        this.hostName = value;
    }

    /**
     * Gets the value of the portKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortKey() {
        return portKey;
    }

    /**
     * Sets the value of the portKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortKey(String value) {
        this.portKey = value;
    }

    /**
     * Gets the value of the dvsUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDvsUuid() {
        return dvsUuid;
    }

    /**
     * Sets the value of the dvsUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDvsUuid(String value) {
        this.dvsUuid = value;
    }

    /**
     * Gets the value of the vnic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVnic() {
        return vnic;
    }

    /**
     * Sets the value of the vnic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVnic(String value) {
        this.vnic = value;
    }

}
