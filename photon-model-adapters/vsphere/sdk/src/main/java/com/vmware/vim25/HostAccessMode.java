
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HostAccessMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HostAccessMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="accessNone"/&gt;
 *     &lt;enumeration value="accessAdmin"/&gt;
 *     &lt;enumeration value="accessNoAccess"/&gt;
 *     &lt;enumeration value="accessReadOnly"/&gt;
 *     &lt;enumeration value="accessOther"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "HostAccessMode")
@XmlEnum
public enum HostAccessMode {

    @XmlEnumValue("accessNone")
    ACCESS_NONE("accessNone"),
    @XmlEnumValue("accessAdmin")
    ACCESS_ADMIN("accessAdmin"),
    @XmlEnumValue("accessNoAccess")
    ACCESS_NO_ACCESS("accessNoAccess"),
    @XmlEnumValue("accessReadOnly")
    ACCESS_READ_ONLY("accessReadOnly"),
    @XmlEnumValue("accessOther")
    ACCESS_OTHER("accessOther");
    private final String value;

    HostAccessMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HostAccessMode fromValue(String v) {
        for (HostAccessMode c: HostAccessMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
