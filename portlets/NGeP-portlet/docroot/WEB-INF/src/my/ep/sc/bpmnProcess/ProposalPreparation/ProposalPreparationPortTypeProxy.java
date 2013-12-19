package my.ep.sc.bpmnProcess.ProposalPreparation;

public class ProposalPreparationPortTypeProxy implements my.ep.sc.bpmnProcess.ProposalPreparation.ProposalPreparationPortType {
  private String _endpoint = null;
  private my.ep.sc.bpmnProcess.ProposalPreparation.ProposalPreparationPortType proposalPreparationPortType = null;
  
  public ProposalPreparationPortTypeProxy() {
    _initProposalPreparationPortTypeProxy();
  }
  
  public ProposalPreparationPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initProposalPreparationPortTypeProxy();
  }
  
  private void _initProposalPreparationPortTypeProxy() {
    try {
      proposalPreparationPortType = (new my.ep.sc.bpmnProcess.ProposalPreparation.ProposalPreparationServiceLocator()).getProposalPreparationPort();
      if (proposalPreparationPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)proposalPreparationPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)proposalPreparationPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (proposalPreparationPortType != null)
      ((javax.xml.rpc.Stub)proposalPreparationPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public my.ep.sc.bpmnProcess.ProposalPreparation.ProposalPreparationPortType getProposalPreparationPortType() {
    if (proposalPreparationPortType == null)
      _initProposalPreparationPortTypeProxy();
    return proposalPreparationPortType;
  }
  
  public void start(my.ep.sc.Data.SC_SiteVisitProposal_Data.SC_SiteVisitProposal_DataType SC_SiteVisitProposal_Data, java.lang.String document_number) throws java.rmi.RemoteException{
    if (proposalPreparationPortType == null)
      _initProposalPreparationPortTypeProxy();
    proposalPreparationPortType.start(SC_SiteVisitProposal_Data, document_number);
  }
  
  
}