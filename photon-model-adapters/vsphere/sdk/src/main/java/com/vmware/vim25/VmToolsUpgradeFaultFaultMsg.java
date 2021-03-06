
package com.vmware.vim25;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.1.6
 * 2016-07-18T20:02:09.006+03:00
 * Generated source version: 3.1.6
 */

@WebFault(name = "VmToolsUpgradeFaultFault", targetNamespace = "urn:vim25")
public class VmToolsUpgradeFaultFaultMsg extends Exception {
    public static final long serialVersionUID = 1L;
    
    private com.vmware.vim25.VmToolsUpgradeFault vmToolsUpgradeFaultFault;

    public VmToolsUpgradeFaultFaultMsg() {
        super();
    }
    
    public VmToolsUpgradeFaultFaultMsg(String message) {
        super(message);
    }
    
    public VmToolsUpgradeFaultFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public VmToolsUpgradeFaultFaultMsg(String message, com.vmware.vim25.VmToolsUpgradeFault vmToolsUpgradeFaultFault) {
        super(message);
        this.vmToolsUpgradeFaultFault = vmToolsUpgradeFaultFault;
    }

    public VmToolsUpgradeFaultFaultMsg(String message, com.vmware.vim25.VmToolsUpgradeFault vmToolsUpgradeFaultFault, Throwable cause) {
        super(message, cause);
        this.vmToolsUpgradeFaultFault = vmToolsUpgradeFaultFault;
    }

    public com.vmware.vim25.VmToolsUpgradeFault getFaultInfo() {
        return this.vmToolsUpgradeFaultFault;
    }
}
