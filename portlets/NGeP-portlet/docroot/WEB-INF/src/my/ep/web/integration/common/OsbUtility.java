package my.ep.web.integration.common;

import java.rmi.RemoteException;

import my.ep.web.integration.osb.BatchPortTypeProxy;

/*
* @author P.Ganga prasad
*/
public class OsbUtility {

	public void osbServiceCallOutBound(String processId) {
		System.out.println("osb service outbound...");

		try {
			my.ep.web.integration.osb.RqHeader_Type header_Type = new my.ep.web.integration.osb.RqHeader_Type();
			header_Type.setConsumerID(IntegrationConstants.CONSUMER_ID);
			header_Type.setIpAddress(IntegrationConstants.OSB_HOST);
			header_Type.setOrgUnit(processId);
			header_Type.setSegment(IntegrationConstants.SEQUENCE_NO);

			my.ep.web.integration.osb.RqUID_Type rqUID_Type = new my.ep.web.integration.osb.RqUID_Type(
					processId,
					IntegrationConstants.SEQUENCE_NO);
			header_Type.setUID(rqUID_Type);

			my.ep.web.integration.osb.BatchOutboundRq_Type batchOutboundRq_Type = new my.ep.web.integration.osb.BatchOutboundRq_Type();

			batchOutboundRq_Type.setServiceCode(processId);

			my.ep.web.integration.osb.EPMFRq_Type epmfRq_Type = new my.ep.web.integration.osb.EPMFRq_Type();
			epmfRq_Type.setRqHeader(header_Type);
			epmfRq_Type.setBatchOutboundRq(batchOutboundRq_Type);

			my.ep.web.integration.osb.BatchPortTypeProxy batchPortTypeProxy = new BatchPortTypeProxy("http://192.168.120.14:8011/Batch/Generic/Outbound/v1.0?wsdl");

			my.ep.web.integration.osb.EPMFRs_Type epmfRs_Type = batchPortTypeProxy.invoke(epmfRq_Type);

			System.out.println("getStatusCode  :"
					+ epmfRs_Type.getRsHeader().getStatus().getStatusCode());
			System.out.println("getStatusDesc :"
					+ epmfRs_Type.getRsHeader().getStatus().getStatusDesc());

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	
	public boolean osbServiceCallInBound(String processId) {

		  try {
		   my.ep.web.integration.osb.inbound.RqHeader_Type header_Type = new my.ep.web.integration.osb.inbound.RqHeader_Type();
		   header_Type.setConsumerID(IntegrationConstants.CONSUMER_ID);
		   header_Type.setIpAddress(IntegrationConstants.OSB_HOST);
		   header_Type.setOrgUnit(processId);
		   header_Type.setSegment(IntegrationConstants.SEQUENCE_NO);

		   my.ep.web.integration.osb.inbound.RqUID_Type rqUID_Type = new my.ep.web.integration.osb.inbound.RqUID_Type(processId, IntegrationConstants.SEQUENCE_NO);
		   header_Type.setUID(rqUID_Type);

		   my.ep.web.integration.osb.inbound.BatchInboundRq_Type batchOutboundRq_Type = new my.ep.web.integration.osb.inbound.BatchInboundRq_Type();

		   batchOutboundRq_Type.setServiceCode(processId);

		   my.ep.web.integration.osb.inbound.EPMFRq_Type epmfRq_Type = new my.ep.web.integration.osb.inbound.EPMFRq_Type();
		   epmfRq_Type.setRqHeader(header_Type);
		   epmfRq_Type.setBatchInboundRq(batchOutboundRq_Type);

		   my.ep.web.integration.osb.inbound.BatchPortTypeProxy batchPortTypeProxy = new my.ep.web.integration.osb.inbound.BatchPortTypeProxy("http://192.168.120.14:8011/Batch/Generic/Inbound/v1.0?wsdl");

		   my.ep.web.integration.osb.inbound.EPMFRs_Type epmfRs_Type =batchPortTypeProxy.invoke(epmfRq_Type);
		   
		   if(epmfRs_Type.getRsHeader().getStatus().getStatusCode().equalsIgnoreCase(IntegrationConstants.SERVICE_SUCCESS_CODE)){
			   return true;   
		   }
		      
		  } catch (RemoteException e) {
		   e.printStackTrace();
		  }

		 return false;
	}

}
