
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LicenseServerUnavailableEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LicenseServerUnavailableEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:vim25}LicenseEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="licenseServer" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LicenseServerUnavailableEvent", propOrder = {
    "licenseServer"
})
public class LicenseServerUnavailableEvent
    extends LicenseEvent
{

    @XmlElement(required = true)
    protected String licenseServer;

    /**
     * Gets the value of the licenseServer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenseServer() {
        return licenseServer;
    }

    /**
     * Sets the value of the licenseServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenseServer(String value) {
        this.licenseServer = value;
    }

}
