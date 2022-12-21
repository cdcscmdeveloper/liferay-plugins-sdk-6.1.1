/**
 * BatchPortType_Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package my.ep.web.integration.osb;

public interface BatchPortType_Service extends javax.xml.rpc.Service {
    public java.lang.String getBatchPortTypePortAddress();

    public my.ep.web.integration.osb.BatchPortType_PortType getBatchPortTypePort() throws javax.xml.rpc.ServiceException;

    public my.ep.web.integration.osb.BatchPortType_PortType getBatchPortTypePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
