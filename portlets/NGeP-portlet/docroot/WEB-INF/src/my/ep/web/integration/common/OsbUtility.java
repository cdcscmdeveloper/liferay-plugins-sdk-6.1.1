package my.ep.web.integration.common;

import java.rmi.RemoteException;
import java.util.UUID;

import org.apache.log4j.Logger;

import my.ep.web.integration.osb.BatchPortTypeProxy;

/*
* @author P.Ganga prasad
*/
public class OsbUtility {
	private static Logger log = BatchLogger.getLogger(OsbUtility.class);

	public void osbServiceCallOutBound(String processId,String fileName) {
		log.info("osb service outbound...");

		try {
			my.ep.web.integration.osb.RqHeader_Type header_Type = new my.ep.web.integration.osb.RqHeader_Type();
			header_Type.setConsumerID(IntegrationConstants.CONSUMER_ID);
//			header_Type.setIpAddress(IntegrationConstants.OSB_HOST);
//			header_Type.setOrgUnit(processId);
//			header_Type.setSegment(IntegrationConstants.SEQUENCE_NO);

			my.ep.web.integration.osb.RqUID_Type rqUID_Type = new my.ep.web.integration.osb.RqUID_Type(
					UUID.randomUUID().toString(),
					IntegrationConstants.EMPTY_TRING);
			header_Type.setUID(rqUID_Type);

			my.ep.web.integration.osb.BatchOutboundRq_Type batchOutboundRq_Type = new my.ep.web.integration.osb.BatchOutboundRq_Type();

			batchOutboundRq_Type.setServiceCode(processId);
			batchOutboundRq_Type.setFilename(fileName);

			my.ep.web.integration.osb.EPMFRq_Type epmfRq_Type = new my.ep.web.integration.osb.EPMFRq_Type();
			epmfRq_Type.setRqHeader(header_Type);
			epmfRq_Type.setBatchOutboundRq(batchOutboundRq_Type);

			my.ep.web.integration.osb.BatchPortTypeProxy batchPortTypeProxy = new BatchPortTypeProxy();

			my.ep.web.integration.osb.EPMFRs_Type epmfRs_Type = batchPortTypeProxy.invoke(epmfRq_Type);

			log.info("getStatusCode  :"+ epmfRs_Type.getRsHeader().getStatus().getStatusCode());
			log.info("getStatusDesc :"+ epmfRs_Type.getRsHeader().getStatus().getStatusDesc());

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	
	public boolean osbServiceCallInBound(String processId,String fileName) {

		  try {
		   my.ep.web.integration.osb.inbound.RqHeader_Type header_Type = new my.ep.web.integration.osb.inbound.RqHeader_Type();
		   header_Type.setConsumerID(IntegrationConstants.CONSUMER_ID);
//		   header_Type.setIpAddress(IntegrationConstants.OSB_HOST);
//		   header_Type.setOrgUnit(processId);
//		   header_Type.setSegment(IntegrationConstants.SEQUENCE_NO);

		   my.ep.web.integration.osb.inbound.RqUID_Type rqUID_Type = new my.ep.web.integration.osb.inbound.RqUID_Type();
		   rqUID_Type.setRqUID(UUID.randomUUID().toString());
		   header_Type.setUID(rqUID_Type);

		   my.ep.web.integration.osb.inbound.BatchInboundRq_Type batchOutboundRq_Type = new my.ep.web.integration.osb.inbound.BatchInboundRq_Type();

		   batchOutboundRq_Type.setServiceCode(processId);

		   my.ep.web.integration.osb.inbound.EPMFRq_Type epmfRq_Type = new my.ep.web.integration.osb.inbound.EPMFRq_Type();
		   epmfRq_Type.setRqHeader(header_Type);
		   epmfRq_Type.setBatchInboundRq(batchOutboundRq_Type);

		   my.ep.web.integration.osb.inbound.BatchPortTypeProxy batchPortTypeProxy = new my.ep.web.integration.osb.inbound.BatchPortTypeProxy();

		   my.ep.web.integration.osb.inbound.EPMFRs_Type epmfRs_Type =batchPortTypeProxy.invoke(epmfRq_Type);
		   
		   if(epmfRs_Type.getRsHeader().getStatus().getStatusCode().equalsIgnoreCase(IntegrationConstants.SERVICE_SUCCESS_CODE)){
			   return true;   
		   }
		      
		  } catch (RemoteException e) {
			  log.info("Osb sysmtem down ");
		   e.printStackTrace();
		  }

		 return false;
	}

}
