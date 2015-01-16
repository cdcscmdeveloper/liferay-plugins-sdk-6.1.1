package my.ep.web.integration.crm.governmentuserinfo;

public class UpdateGovernmentUserInfoPortTypeProxy implements my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortType_PortType {
  private String _endpoint = null;
  private my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortType_PortType updateGovernmentUserInfoPortType_PortType = null;
  
  public UpdateGovernmentUserInfoPortTypeProxy() {
    _initUpdateGovernmentUserInfoPortTypeProxy();
  }
  
  public UpdateGovernmentUserInfoPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initUpdateGovernmentUserInfoPortTypeProxy();
  }
  
  private void _initUpdateGovernmentUserInfoPortTypeProxy() {
    try {
      updateGovernmentUserInfoPortType_PortType = (new my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortType_ServiceLocator()).getUpdateGovernmentUserInfoPortTypePort();
      if (updateGovernmentUserInfoPortType_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)updateGovernmentUserInfoPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)updateGovernmentUserInfoPortType_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (updateGovernmentUserInfoPortType_PortType != null)
      ((javax.xml.rpc.Stub)updateGovernmentUserInfoPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public my.ep.web.integration.crm.governmentuserinfo.UpdateGovernmentUserInfoPortType_PortType getUpdateGovernmentUserInfoPortType_PortType() {
    if (updateGovernmentUserInfoPortType_PortType == null)
      _initUpdateGovernmentUserInfoPortTypeProxy();
    return updateGovernmentUserInfoPortType_PortType;
  }
  
  public my.ep.web.integration.crm.governmentuserinfo.EPMFRs update(my.ep.web.integration.crm.governmentuserinfo.EPMFRq part) throws java.rmi.RemoteException{
    if (updateGovernmentUserInfoPortType_PortType == null)
      _initUpdateGovernmentUserInfoPortTypeProxy();
    return updateGovernmentUserInfoPortType_PortType.update(part);
  }
  
  
}