
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DistributedVirtualPortgroupPortgroupType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DistributedVirtualPortgroupPortgroupType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="earlyBinding"/&gt;
 *     &lt;enumeration value="lateBinding"/&gt;
 *     &lt;enumeration value="ephemeral"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DistributedVirtualPortgroupPortgroupType")
@XmlEnum
public enum DistributedVirtualPortgroupPortgroupType {

    @XmlEnumValue("earlyBinding")
    EARLY_BINDING("earlyBinding"),
    @XmlEnumValue("lateBinding")
    LATE_BINDING("lateBinding"),
    @XmlEnumValue("ephemeral")
    EPHEMERAL("ephemeral");
    private final String value;

    DistributedVirtualPortgroupPortgroupType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DistributedVirtualPortgroupPortgroupType fromValue(String v) {
        for (DistributedVirtualPortgroupPortgroupType c: DistributedVirtualPortgroupPortgroupType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
