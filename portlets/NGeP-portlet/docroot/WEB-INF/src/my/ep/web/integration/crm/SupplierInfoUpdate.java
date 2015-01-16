package my.ep.web.integration.crm;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.List;

import my.ep.web.integration.common.IntegrationConstants;
import my.ep.web.integration.common.Test;
import my.ep.web.integration.crm.supplierinfo.EPMFRq;
import my.ep.web.integration.crm.supplierinfo.EPMFRs;
import my.ep.web.integration.crm.supplierinfo.RqHeader_Type;
import my.ep.web.integration.crm.supplierinfo.RqUID_Type;
import my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoPortTypeProxy;
import my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRq;
import my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqApplArrayAppl;
import my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqBidangArrayBidang;
import my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqBranchArrayBranch;
import my.ep.web.integration.crm.supplierinfo.UpdateSupplierInfoRqMainContactArrayMainContact;


public class SupplierInfoUpdate {


	public static void main(String[] args) {

		Test test = new Test();
		updateSupplierinfo(test.crmSupplierInfo());

	}

	/**
	 * This method is used to update supplierInfo for crm integration
	 */
	public static void updateSupplierinfo(List<SupplierInfo> supplierInfoList) {
		try {
			RqHeader_Type header_Type = new RqHeader_Type();
			header_Type.setConsumerID("EPP-001");
			header_Type.setIpAddress("192.168.62.17");
			header_Type.setOrgUnit("1");
			header_Type.setSegment("2");
			
			RqUID_Type rqUID_Type = new RqUID_Type(
					"20141126",
					IntegrationConstants.SEQUENCE_NO);
			header_Type.setUID(rqUID_Type);

			UpdateSupplierInfoRq supplierInfoRq = new UpdateSupplierInfoRq();
			
			for (SupplierInfo supplierInfo : supplierInfoList) {

				System.out.println("supplierInfo.getCompanyName() :"+supplierInfo.getCompanyName());
				supplierInfoRq.setCompanyName(supplierInfo.getCompanyName());
				supplierInfoRq.setSupplierID(new BigDecimal(supplierInfo
						.getSupplierID()));

//				if (supplierInfo.getBranchList().size() > 0) {
				if (supplierInfo.getBranchList()!=null) {
					UpdateSupplierInfoRqBranchArrayBranch[] updateBranchArray = new UpdateSupplierInfoRqBranchArrayBranch[supplierInfo
							.getBranchList().size()];
					UpdateSupplierInfoRqBranchArrayBranch updateBranch = null;
					int i = 0;
					for (Branch branch : supplierInfo.getBranchList()) {
						updateBranch = new UpdateSupplierInfoRqBranchArrayBranch();
						updateBranch.setBranchName(branch.getBranchName());
						updateBranch.setBranchID(new BigDecimal(branch
								.getBranchID()));
						updateBranch.setAddress1(branch.getAddress1());
						updateBranch.setAddress2(branch.getAddress2());
						updateBranch.setAddress3(branch.getAddress3());
						updateBranch.setAddress4(branch.getAddress4());
						updateBranch.setZipCode(branch.getZipCode());
						updateBranch.setDistrict(branch.getDistrict());
						updateBranch.setCity(branch.getCity());
						updateBranch.setStateName(branch.getStateName());
						updateBranch.setCountryName(branch.getCountryName());
						updateBranch.setWebsite(branch.getWebsite());
						updateBranch.setPhone(branch.getPhone());
						updateBranch.setFax(branch.getFax());
						updateBranchArray[i] = updateBranch;
						i++;
					}
					supplierInfoRq.setBranchArray(updateBranchArray);

				}

//				if (supplierInfo.getSmApplList().size() > 0) {
				if (supplierInfo.getSmApplList()!=null) {
					UpdateSupplierInfoRqApplArrayAppl[] updateApplArray = new UpdateSupplierInfoRqApplArrayAppl[supplierInfo
							.getSmApplList().size()];
					UpdateSupplierInfoRqApplArrayAppl updateAppl = null;
					int i = 0;

					for (Appl smAppl : supplierInfo.getSmApplList()) {
						updateAppl = new UpdateSupplierInfoRqApplArrayAppl();
						updateAppl.setApplNo(smAppl.getApplNo());
						updateAppl.setApplStatus(new BigDecimal(smAppl
								.getApplStatus()));
						updateAppl.setAppType(smAppl.getAppType());
						updateAppl.setMofNo(smAppl.getMofNo());
						updateAppl.setRegNo(smAppl.getRegNo());
						updateAppl.setExpiryDate(smAppl.getExpiryDate());
						updateAppl.setCapitalPaidUp(new BigDecimal(smAppl
								.getCapitalPaidUp()));
						updateApplArray[i] = updateAppl;
						i++;
					}
					supplierInfoRq.setApplArray(updateApplArray);
				}

			//	if (supplierInfo.getMainContactList().size() > 0) {
				if (supplierInfo.getMainContactList()!=null) {
					UpdateSupplierInfoRqMainContactArrayMainContact[] updateMainContactArray = new UpdateSupplierInfoRqMainContactArrayMainContact[supplierInfo
							.getMainContactList().size()];
					UpdateSupplierInfoRqMainContactArrayMainContact updateMainContact = null;
					int i = 0;
					for (MainContact mainContact : supplierInfo
							.getMainContactList()) {

						updateMainContact = new UpdateSupplierInfoRqMainContactArrayMainContact();
						updateMainContact.setContactName(mainContact
								.getContactName());
						updateMainContact
								.setMobileNo(mainContact.getMobileNo());
						updateMainContact.setPhoneNo(mainContact.getPhoneNo());
						// updateMainContact.setContactEmail(mainContact);
						updateMainContactArray[i] = updateMainContact;
						i++;
					}
					supplierInfoRq.setMainContactArray(updateMainContactArray);
				}

//				if (supplierInfo.getBidangList().size() > 0) {
				if (supplierInfo.getBidangList()!=null) {
					UpdateSupplierInfoRqBidangArrayBidang[] updateBidangArray = new UpdateSupplierInfoRqBidangArrayBidang[supplierInfo
							.getBidangList().size()];
					UpdateSupplierInfoRqBidangArrayBidang updateBidang = null;
					int i = 0;

					for (Bidang bidang : supplierInfo.getBidangList()) {

						updateBidang = new UpdateSupplierInfoRqBidangArrayBidang();
						updateBidang.setKodBidangCategoryL1(bidang
								.getKodBidangCategoryL1());
						updateBidang.setKodBidangCategoryL2(bidang
								.getKodBidangCategoryL2());
						updateBidang.setKodBidangCategoryL3(bidang
								.getKodBidangCategoryL3());
						updateBidang.setBidangDescriptionCategoryL1(bidang
								.getDescriptionCategoryL1());
						updateBidang.setBidangDescriptionCategoryL2(bidang
								.getDescriptionCategoryL2());
						updateBidang.setBidangDescriptionCategoryL3(bidang
								.getDescriptionCategoryL3());
						updateBidangArray[i] = updateBidang;
						i++;
					}
					supplierInfoRq.setBidangArray(updateBidangArray);
				}

			}
			UpdateSupplierInfoPortTypeProxy supplierInfoPortTypeProxy = new UpdateSupplierInfoPortTypeProxy();
			EPMFRq part = new EPMFRq();
			part.setRqHeader(header_Type);
			part.setUpdateSupplierInfoRq(supplierInfoRq);
			EPMFRs ePMFRs = supplierInfoPortTypeProxy.update(part);

			System.out.println("getStatusCode  :"
					+ ePMFRs.getRsHeader().getStatus().getStatusCode());
			System.out.println("getStatusDesc :"
					+ ePMFRs.getRsHeader().getStatus().getStatusDesc());

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
