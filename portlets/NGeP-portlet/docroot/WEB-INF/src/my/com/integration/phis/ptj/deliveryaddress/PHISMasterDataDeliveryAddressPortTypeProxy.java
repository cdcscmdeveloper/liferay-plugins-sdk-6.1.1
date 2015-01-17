package my.com.integration.phis.ptj.deliveryaddress;

public class PHISMasterDataDeliveryAddressPortTypeProxy implements PHISMasterDataDeliveryAddressPortType_PortType {
  private String _endpoint = null;
  private PHISMasterDataDeliveryAddressPortType_PortType pHISMasterDataDeliveryAddressPortType_PortType = null;
  
  public PHISMasterDataDeliveryAddressPortTypeProxy() {
    _initPHISMasterDataDeliveryAddressPortTypeProxy();
  }
  
  public PHISMasterDataDeliveryAddressPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initPHISMasterDataDeliveryAddressPortTypeProxy();
  }
  
  private void _initPHISMasterDataDeliveryAddressPortTypeProxy() {
    try {
      pHISMasterDataDeliveryAddressPortType_PortType = (new PHISMasterDataDeliveryAddressPortType_ServiceLocator()).getPHISMasterDataDeliveryAddressPortTypePort();
      if (pHISMasterDataDeliveryAddressPortType_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)pHISMasterDataDeliveryAddressPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)pHISMasterDataDeliveryAddressPortType_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (pHISMasterDataDeliveryAddressPortType_PortType != null)
      ((javax.xml.rpc.Stub)pHISMasterDataDeliveryAddressPortType_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public PHISMasterDataDeliveryAddressPortType_PortType getPHISMasterDataDeliveryAddressPortType_PortType() {
    if (pHISMasterDataDeliveryAddressPortType_PortType == null)
      _initPHISMasterDataDeliveryAddressPortTypeProxy();
    return pHISMasterDataDeliveryAddressPortType_PortType;
  }
  
  public  EPMFRs_Type inquire( EPMFRq_Type part) throws java.rmi.RemoteException{
    if (pHISMasterDataDeliveryAddressPortType_PortType == null)
      _initPHISMasterDataDeliveryAddressPortTypeProxy();
    return pHISMasterDataDeliveryAddressPortType_PortType.inquire(part);
  }
  
  
}