
package com.vmware.vim25;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfPnicUplinkProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfPnicUplinkProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PnicUplinkProfile" type="{urn:vim25}PnicUplinkProfile" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPnicUplinkProfile", propOrder = {
    "pnicUplinkProfile"
})
public class ArrayOfPnicUplinkProfile {

    @XmlElement(name = "PnicUplinkProfile")
    protected List<PnicUplinkProfile> pnicUplinkProfile;

    /**
     * Gets the value of the pnicUplinkProfile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pnicUplinkProfile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPnicUplinkProfile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PnicUplinkProfile }
     * 
     * 
     */
    public List<PnicUplinkProfile> getPnicUplinkProfile() {
        if (pnicUplinkProfile == null) {
            pnicUplinkProfile = new ArrayList<PnicUplinkProfile>();
        }
        return this.pnicUplinkProfile;
    }

}
