package my.ep.web.integration.crm.supplierinfo;

public class UpdateSupplierInfoPortTypeProxy implements my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoPortType_PortType {
  private String _endpoint = null;
  private my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoPortType_PortType updateSupplierInfoPortType_PortType = null;
  
  public UpdateSupplierInfoPortTypeProxy() {
    _initUpdateSupplierInfoPortTypeProxy();
  }
  
  public UpdateSupplierInfoPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initUpdateSupplierInfoPortTypeProxy();
  }
  
  private void _initUpdateSupplierInfoPortTypeProxy() {
    try {
      updateSupplierInfoPortType_PortType = (new my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoPortType_ServiceLocator()).getUpdateSupplierInfoPortTypePort();
      if (updateSupplierInfoPortType_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)updateSupplierInfoPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)updateSupplierInfoPortType_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (updateSupplierInfoPortType_PortType != null)
      ((javax.xml.rpc.Stub)updateSupplierInfoPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoPortType_PortType getUpdateSupplierInfoPortType_PortType() {
    if (updateSupplierInfoPortType_PortType == null)
      _initUpdateSupplierInfoPortTypeProxy();
    return updateSupplierInfoPortType_PortType;
  }
  
  public my.ep.web.integration.crm.supplierinfo.EPMFRs update(my.ep.web.integration.crm.supplierinfo.EPMFRq part) throws java.rmi.RemoteException{
    if (updateSupplierInfoPortType_PortType == null)
      _initUpdateSupplierInfoPortTypeProxy();
    return updateSupplierInfoPortType_PortType.update(part);
  }
  
  
}