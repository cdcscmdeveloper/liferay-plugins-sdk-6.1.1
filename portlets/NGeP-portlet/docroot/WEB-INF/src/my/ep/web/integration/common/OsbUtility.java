package my.ep.web.integration.common;

import java.rmi.RemoteException;

import my.ep.web.integration.osb.BatchOutboundRq_Type;
import my.ep.web.integration.osb.BatchPortTypeProxy;
import my.ep.web.integration.osb.EPMFRq_Type;
import my.ep.web.integration.osb.EPMFRs_Type;
import my.ep.web.integration.osb.RqHeader_Type;
import my.ep.web.integration.osb.RqUID_Type;

public class OsbUtility {

	public void osbServiceCall(String processId) {

		try {
			RqHeader_Type header_Type = new RqHeader_Type();
			header_Type.setConsumerID(IntegrationConstants.CONSUMER_ID);
			header_Type.setIpAddress(IntegrationConstants.OSB_HOST);
			header_Type.setOrgUnit(processId);
			header_Type.setSegment(IntegrationConstants.SEQUENCE_NO);

			RqUID_Type rqUID_Type = new RqUID_Type(
					processId,
					IntegrationConstants.SEQUENCE_NO);
			header_Type.setUID(rqUID_Type);

			BatchOutboundRq_Type batchOutboundRq_Type = new BatchOutboundRq_Type();

			batchOutboundRq_Type.setServiceCode(processId);

			EPMFRq_Type epmfRq_Type = new EPMFRq_Type();
			epmfRq_Type.setRqHeader(header_Type);
			epmfRq_Type.setBatchOutboundRq(batchOutboundRq_Type);

			BatchPortTypeProxy batchPortTypeProxy = new BatchPortTypeProxy();

			EPMFRs_Type epmfRs_Type = batchPortTypeProxy.invoke(epmfRq_Type);

			System.out.println("getStatusCode  :"
					+ epmfRs_Type.getRsHeader().getStatus().getStatusCode());
			System.out.println("getStatusDesc :"
					+ epmfRs_Type.getRsHeader().getStatus().getStatusDesc());

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
