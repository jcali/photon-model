
package com.vmware.vim25;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.1.6
 * 2016-07-18T20:02:09.021+03:00
 * Generated source version: 3.1.6
 */

@WebFault(name = "DatastoreNotWritableOnHostFault", targetNamespace = "urn:vim25")
public class DatastoreNotWritableOnHostFaultMsg extends Exception {
    public static final long serialVersionUID = 1L;
    
    private com.vmware.vim25.DatastoreNotWritableOnHost datastoreNotWritableOnHostFault;

    public DatastoreNotWritableOnHostFaultMsg() {
        super();
    }
    
    public DatastoreNotWritableOnHostFaultMsg(String message) {
        super(message);
    }
    
    public DatastoreNotWritableOnHostFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public DatastoreNotWritableOnHostFaultMsg(String message, com.vmware.vim25.DatastoreNotWritableOnHost datastoreNotWritableOnHostFault) {
        super(message);
        this.datastoreNotWritableOnHostFault = datastoreNotWritableOnHostFault;
    }

    public DatastoreNotWritableOnHostFaultMsg(String message, com.vmware.vim25.DatastoreNotWritableOnHost datastoreNotWritableOnHostFault, Throwable cause) {
        super(message, cause);
        this.datastoreNotWritableOnHostFault = datastoreNotWritableOnHostFault;
    }

    public com.vmware.vim25.DatastoreNotWritableOnHost getFaultInfo() {
        return this.datastoreNotWritableOnHostFault;
    }
}
