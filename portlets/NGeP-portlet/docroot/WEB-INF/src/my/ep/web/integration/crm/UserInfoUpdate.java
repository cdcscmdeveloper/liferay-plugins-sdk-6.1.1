package my.ep.web.integration.crm;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.List;

import my.ep.biz.Integration.crm.BelongsTo;
import my.ep.biz.Integration.crm.SoftCert;
import my.ep.biz.Integration.crm.SupplierUserInfo;
import my.ep.web.integration.crm.userinfo.EPMFRq;
import my.ep.web.integration.crm.userinfo.EPMFRs;
import my.ep.web.integration.crm.userinfo.RqHeader_Type;
import my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoPortTypeProxy;
import my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRq;
import my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRqBelongsToArrayBelongsTo;
import my.ep.web.integration.crm.userinfo.UpdateSupplierUserInfoRqSoftCertArraySoftCert;


public class UserInfoUpdate {

	public static void updateSupplierUserinfo(
			List<SupplierUserInfo> supplierUserInfoList) {

		try {
			RqHeader_Type header_Type = new RqHeader_Type();
			header_Type.setConsumerID("EPP-001");
			header_Type.setIpAddress("192.168.62.17");
			header_Type.setOrgUnit("1");
			header_Type.setSegment("2");

			UpdateSupplierUserInfoRq updateSupplierUserInfoRq = new UpdateSupplierUserInfoRq();

			if (supplierUserInfoList.size() > 0) {
				for (SupplierUserInfo supplierUserInfo : supplierUserInfoList) {

					updateSupplierUserInfoRq.setUserName(supplierUserInfo
							.getUserName());
					updateSupplierUserInfoRq.setIdentityNo(supplierUserInfo
							.getIdentityNo());
					updateSupplierUserInfoRq.setIdentityType(new BigDecimal(
							supplierUserInfo.getIdentityType()));
					updateSupplierUserInfoRq.setNationality(supplierUserInfo
							.getNationality());
					updateSupplierUserInfoRq.setRecordStatus(supplierUserInfo
							.getRecordStatus());
					updateSupplierUserInfoRq.setEmail(supplierUserInfo
							.getEmail());
					updateSupplierUserInfoRq.setMobileNo(new BigDecimal(
							supplierUserInfo.getMobileNo()));
					updateSupplierUserInfoRq.setPosition(supplierUserInfo
							.getPosition());
					updateSupplierUserInfoRq.setAccessStatus(new BigDecimal(
							supplierUserInfo.getAccessStatus()));

					if (supplierUserInfo.getBelongsToList().size() > 0) {
						UpdateSupplierUserInfoRqBelongsToArrayBelongsTo[] updateBelongsToArray = new UpdateSupplierUserInfoRqBelongsToArrayBelongsTo[supplierUserInfo
								.getBelongsToList().size()];
						UpdateSupplierUserInfoRqBelongsToArrayBelongsTo updateBelongsTo = null;
						int i = 0;
						for (BelongsTo belongsTo : supplierUserInfo
								.getBelongsToList()) {
							updateBelongsTo = new UpdateSupplierUserInfoRqBelongsToArrayBelongsTo();
							updateBelongsTo.setBranchID(new BigDecimal(
									belongsTo.getBranchID()));
							updateBelongsTo.setSupplierID(new BigDecimal(
									belongsTo.getSupplierID()));
							updateBelongsToArray[i] = updateBelongsTo;
							i++;
						}
						updateSupplierUserInfoRq
								.setBelongsToArray(updateBelongsToArray);
					}

					if (supplierUserInfo.getSoftCertList().size() > 0) {

						UpdateSupplierUserInfoRqSoftCertArraySoftCert[] updateSoftCertArray = new UpdateSupplierUserInfoRqSoftCertArraySoftCert[supplierUserInfo
								.getSoftCertList().size()];
						UpdateSupplierUserInfoRqSoftCertArraySoftCert updateSoftCert = null;
						int i = 0;
						for (SoftCert softCert : supplierUserInfo
								.getSoftCertList()) {
							updateSoftCert = new UpdateSupplierUserInfoRqSoftCertArraySoftCert();
							updateSoftCert.setSerialNo(new BigDecimal(softCert
									.getSerialNo()));
							updateSoftCert.setDateFrom(softCert.getDateFrom());
							updateSoftCert.setDateTo(softCert.getDateTo());
							updateSoftCertArray[i] = updateSoftCert;
							i++;

						}
						updateSupplierUserInfoRq
								.setSoftCertArray(updateSoftCertArray);

					}

				}

			}

			UpdateSupplierUserInfoPortTypeProxy proxy = new UpdateSupplierUserInfoPortTypeProxy();
			EPMFRq part = new EPMFRq(header_Type, updateSupplierUserInfoRq);
			EPMFRs ePMFRs = proxy.update(part);
			System.out.println("getStatusCode  :"
					+ ePMFRs.getRsHeader().getStatus().getStatusCode());
			System.out.println("getStatusDesc :"
					+ ePMFRs.getRsHeader().getStatus().getStatusDesc());

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
