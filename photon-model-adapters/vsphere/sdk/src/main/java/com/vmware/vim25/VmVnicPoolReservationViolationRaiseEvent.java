
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VmVnicPoolReservationViolationRaiseEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VmVnicPoolReservationViolationRaiseEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:vim25}DvsEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="vmVnicResourcePoolKey" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="vmVnicResourcePoolName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VmVnicPoolReservationViolationRaiseEvent", propOrder = {
    "vmVnicResourcePoolKey",
    "vmVnicResourcePoolName"
})
public class VmVnicPoolReservationViolationRaiseEvent
    extends DvsEvent
{

    @XmlElement(required = true)
    protected String vmVnicResourcePoolKey;
    protected String vmVnicResourcePoolName;

    /**
     * Gets the value of the vmVnicResourcePoolKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVmVnicResourcePoolKey() {
        return vmVnicResourcePoolKey;
    }

    /**
     * Sets the value of the vmVnicResourcePoolKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVmVnicResourcePoolKey(String value) {
        this.vmVnicResourcePoolKey = value;
    }

    /**
     * Gets the value of the vmVnicResourcePoolName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVmVnicResourcePoolName() {
        return vmVnicResourcePoolName;
    }

    /**
     * Sets the value of the vmVnicResourcePoolName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVmVnicResourcePoolName(String value) {
        this.vmVnicResourcePoolName = value;
    }

}
