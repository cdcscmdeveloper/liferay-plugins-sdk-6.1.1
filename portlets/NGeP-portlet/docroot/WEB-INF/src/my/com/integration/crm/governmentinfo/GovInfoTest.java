package my.com.integration.crm.governmentinfo;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpEjbRemoteBusinessDelegate;


public class GovInfoTest {
	IOrgProfileSO orgProfileSO = EpEjbRemoteBusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IOrgProfileSO.class);

	

	public static void main(String[] args) {
		UpdateGovernmentInfoPortTypeProxy proxy = new UpdateGovernmentInfoPortTypeProxy();
		UpdateGovernmentInfoPortType_PortType port= proxy.getUpdateGovernmentInfoPortType_PortType();
		EPMFRq req = new EPMFRq();
		List<GovInfo> govInfo = new ArrayList<GovInfo>();
		UpdateGovernmentInfoRqMinistryArrayMinistry [] ministryArray  = new UpdateGovernmentInfoRqMinistryArrayMinistry[govInfo.size()];
		int i = 0;
		for (GovInfo gov : govInfo) {
			UpdateGovernmentInfoRqMinistryArrayMinistry del = new UpdateGovernmentInfoRqMinistryArrayMinistry();
			del.setAddress1(gov.getAddress1());
			del.setAddress2(gov.getAddress2());
			del.setAddress3(gov.getAddress3());
			//del.setAddress4(gov.getAddress4());
			del.setCity(gov.getCity());
			del.setDistrict(gov.getDistrict());
			del.setStateName(gov.getStateName());
			//del.setStatus(addressType.getRecordStatusFlg());
			//del.setDeliveryAddressID("1");
			ministryArray[i] = del;
			i++;
		}
		RqHeader_Type rqHeader = new RqHeader_Type();
		rqHeader.setConsumerID("");
		rqHeader.setIpAddress("");
		rqHeader.setOrgUnit("");
		rqHeader.setSegment("");
		rqHeader.setUID(new RqUID_Type());
		
		req.setRqHeader(rqHeader);
		req.setUpdateGovernmentInfoRq(new UpdateGovernmentInfoRq());
		
		req.getUpdateGovernmentInfoRq().setMinistryArray(ministryArray);
		
		EPMFRs rs = null;
		try {
			rs = port.update(req);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != rs) {
			System.out.println(rs.getRsHeader().getStatus().getSeverity());
			System.out.println("Status Code :::"+rs.getRsHeader().getStatus().getStatusCode());
		}

	}
	
	public List<GovInfo> getOrgProfileInfo(){
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "03-11-2013";
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		reqDTO.setCreateDate(date);
		reqDTO.setOrgTypeCode(OrganizationType.DIVISION.value());

		OrgProfileResDTO resDTO = orgProfileSO.retrieveMinistryByCreateDate(reqDTO);

		List<PmOrgProfile> orgProfile = resDTO.getOrgProfileList();
		System.out.println("OrgProfile" + orgProfile.size());
		for (PmOrgProfile orgProfil : orgProfile) {
			orgProfil.getOrgCode();
			System.out.println("Parent OrgProfile Id:::"+orgProfil.getParentOrgProfile().getOrgProfileId());

			List<PmAddressType> pmAddressTypes = orgProfil.getPmAddressTypes();

			if (pmAddressTypes != null) {
				for (PmAddressType pmAddressType : pmAddressTypes) {

					System.out.println("AddressType"
							+ pmAddressType.getAddressType());
					System.out.println("Address One :::::"
							+ pmAddressType.getPmAddress().getAddress1());
					System.out.println("Address Two :::::"
							+ pmAddressType.getPmAddress().getAddress2());
					System.out.println("Address Three :::::"
							+ pmAddressType.getPmAddress().getAddress3());
					System.out
							.println("pmAddressType.getPmAddress().getPmState().getStateCode()"
									+ pmAddressType.getPmAddress().getPmState()
											.getStateName());
					System.out.println("pmAddressType.getPmAddress().Country "
							+ pmAddressType.getPmAddress().getPmCountry()
									.getCountryName());
					System.out.println("pmAddressType.getPmAddress().City "
							+ pmAddressType.getPmAddress().getPmCity()
									.getCityName());
					System.out.println("pmAddressType.getPmAddress().District "
							+ pmAddressType.getPmAddress().getPmDistrict()
									.getDistrictName());

				}
			}
			System.out.println("done");
		}
		return null;
	}

}
