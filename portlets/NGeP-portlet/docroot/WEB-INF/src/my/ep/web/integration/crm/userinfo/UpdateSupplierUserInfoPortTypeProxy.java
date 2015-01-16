package my.ep.web.integration.crm.userinfo;

public class UpdateSupplierUserInfoPortTypeProxy implements my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoPortType_PortType {
  private String _endpoint = null;
  private my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoPortType_PortType updateSupplierUserInfoPortType_PortType = null;
  
  public UpdateSupplierUserInfoPortTypeProxy() {
    _initUpdateSupplierUserInfoPortTypeProxy();
  }
  
  public UpdateSupplierUserInfoPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initUpdateSupplierUserInfoPortTypeProxy();
  }
  
  private void _initUpdateSupplierUserInfoPortTypeProxy() {
    try {
      updateSupplierUserInfoPortType_PortType = (new my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoPortType_ServiceLocator()).getUpdateSupplierUserInfoPortTypePort();
      if (updateSupplierUserInfoPortType_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)updateSupplierUserInfoPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)updateSupplierUserInfoPortType_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (updateSupplierUserInfoPortType_PortType != null)
      ((javax.xml.rpc.Stub)updateSupplierUserInfoPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoPortType_PortType getUpdateSupplierUserInfoPortType_PortType() {
    if (updateSupplierUserInfoPortType_PortType == null)
      _initUpdateSupplierUserInfoPortTypeProxy();
    return updateSupplierUserInfoPortType_PortType;
  }
  
  public my.ep.web.integration.crm.userinfo.EPMFRs update(my.ep.web.integration.crm.userinfo.EPMFRq part) throws java.rmi.RemoteException{
    if (updateSupplierUserInfoPortType_PortType == null)
      _initUpdateSupplierUserInfoPortTypeProxy();
    return updateSupplierUserInfoPortType_PortType.update(part);
  }
  
  
}