package my.ep.web.util.sm.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.managedbean.sm.application.SupplierApplicationManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.SmOverlapCategoryVO;
import my.ep.web.vo.SmSupplierBasicInfoVO;

import org.apache.commons.lang.StringUtils;

public class SupplierApplicationUtil {

	/**
	 * Gets the sm supplier basic info vo for mapping data.
	 * 
	 * @param bean
	 *            - SmApplicationManagedBean object
	 * @return the sm supplier basic info vo object
	 */
	public static SmSupplierBasicInfoVO getSmSupplierBasicInfoVO(
			SmSupplierBasicInfoVO basicVO, SmSupplier supplier,
			List<SmPersonnel> personnels, SmCompanyBasic comp, SmMofAccount acc) {
		SmSupplierBasicInfoVO smSupplierBasicInfoVO = basicVO;
		SmGstSupplier hqGst = SmCommonUtil
				.getSmGstSupplierBySupplierIdAndBranchCode(
						supplier.getSupplierId(), null, null, true);
		if (smSupplierBasicInfoVO == null) {
			smSupplierBasicInfoVO = new SmSupplierBasicInfoVO();
		}
		// gst details
		if (comp != null) {
			smSupplierBasicInfoVO
					.setGstNum((hqGst == null || StringUtils.isBlank(hqGst
							.getGstRegNo())) ? "--" : hqGst.getGstRegNo());
			smSupplierBasicInfoVO.setGstRegDate((hqGst == null || hqGst
					.getGstEffDate() == null) ? "--" : DateUtils.dateToString(
					"dd/MM/yyyy", hqGst.getGstEffDate()));
			smSupplierBasicInfoVO.setGstEndDate((hqGst == null || hqGst
					.getGstEndDate() == null) ? "--" : DateUtils.dateToString(
					"dd/MM/yyyy", hqGst.getGstEndDate()));
		}
		// mof account
		if (acc != null) {
			SmMofAccount smMofAccount = acc;
			smSupplierBasicInfoVO.setMofNo(smMofAccount.getMofNo());
			smSupplierBasicInfoVO
					.setStartDate(smMofAccount.getEffDate() == null ? ""
							: DateUtils.dateToString("dd/MM/yyyy",
									smMofAccount.getEffDate()));
			smSupplierBasicInfoVO
					.setEndDate(smMofAccount.getExpDate() == null ? ""
							: DateUtils.dateToString("dd/MM/yyyy",
									smMofAccount.getExpDate()));
		}
		smSupplierBasicInfoVO.setEpNo(supplier == null ? "" : supplier
				.getEpNo());
		smSupplierBasicInfoVO.setRegNo(supplier == null ? "" : supplier
				.getRegNo());

		// address & company
		if (comp != null && comp.getSmCompanyAddresses() != null
				&& !comp.getSmCompanyAddresses().isEmpty()) {
			smSupplierBasicInfoVO.setCompanyName(comp != null ? comp
					.getCompanyName() : "");
			smSupplierBasicInfoVO.setAddress(comp.getSmCompanyAddresses()
					.get(0));
			if (smSupplierBasicInfoVO.getAddress().getSmAddress() != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(smSupplierBasicInfoVO.getAddress().getSmAddress()
						.getAddress1());
				if (StringUtils.isNotBlank(smSupplierBasicInfoVO.getAddress()
						.getSmAddress().getAddress2())) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getAddress2());
				}
				if (StringUtils.isNotBlank(smSupplierBasicInfoVO.getAddress()
						.getSmAddress().getAddress3())) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getAddress3());
				}

				if (smSupplierBasicInfoVO.getAddress().getSmAddress()
						.getPmDistrict() != null) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getPmDistrict()
									.getDistrictName());
				}
				if (StringUtils.isNotBlank(smSupplierBasicInfoVO.getAddress()
						.getSmAddress().getPostcode())
						&& smSupplierBasicInfoVO.getAddress().getSmAddress()
								.getPmCity() != null) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getPostcode())
							.append("  ")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getPmCity().getCityName());

				}

				if (smSupplierBasicInfoVO.getAddress().getSmAddress()
						.getPmState() != null) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getPmState().getStateName());
				}
				if (smSupplierBasicInfoVO.getAddress().getSmAddress()
						.getPmCountry() != null) {
					sb.append(",").append(
							smSupplierBasicInfoVO.getAddress().getSmAddress()
									.getPmCountry().getCountryName());
				}
				smSupplierBasicInfoVO.setAddressLine(sb.toString());
			}

		}
		SmPersonnel smPersonnel = getAdminPersonnel(personnels);
		if (smPersonnel != null) {
			smSupplierBasicInfoVO.setAdminEmail(smPersonnel.getEmail());
			smSupplierBasicInfoVO.setAdminPhoneNo(smPersonnel.getMobileFull());
			smSupplierBasicInfoVO.setAdminName(smPersonnel.getName());
		}

		return smSupplierBasicInfoVO;
	}

	/**
	 * Gets the personnel with Admin role.
	 * 
	 * @param smPersonnels
	 *            - List of sm personnels
	 * @return SmPersonnel object
	 */
	public static SmPersonnel getAdminPersonnel(List<SmPersonnel> smPersonnels) {
		SmPersonnel smPersonnelRetVal = null;
		if (smPersonnels != null) {
			for (SmPersonnel smPersonnel : smPersonnels) {
				if (smPersonnel.getEpRole() != null
						&& (smPersonnel.getEpRole().equals(
								RoleCode.SUPPLIER_TEMP.value())
								|| smPersonnel
										.getEpRole()
										.equalsIgnoreCase(
												SupplierManagementConst.BASIC_SUPPLIER_ADMIN) || smPersonnel
								.getEpRole()
								.equalsIgnoreCase(
										SupplierManagementConst.MOF_SUPPLIER_ADMIN))) {
					smPersonnelRetVal = smPersonnel;
					break;
				}
			}
		}
		return smPersonnelRetVal;
	}

	public static void clonePoApplSectionReview(
			SupplierApplicationManagedBean managedBean) {
		SmApplSectionReview apprApplSecRev = new SmApplSectionReview();
		for (SmApplSectionReview applSecRev : managedBean
				.getSmApplSectionReviews()) {
			EntityHelper.copyEntityProperties(applSecRev, apprApplSecRev,
					new String[] { "applSectionReviewId" });
			apprApplSecRev.setSmAppl(managedBean.getSmAppl());
			apprApplSecRev
					.setRoleType(SupplierManagementConst.SECTION_REV_ROLE_APP);
			apprApplSecRev.setCheckCount(0);
			apprApplSecRev.setRecommendation(null);
			apprApplSecRev.setRemarkToApprover(null);
			apprApplSecRev.setPmRegStatus(null);
			managedBean.setApprvrSecReview(apprApplSecRev);
		}
		managedBean.getSmApplSectionReviews().add(
				managedBean.getApprvrSecReview());
	}

	/**
	 * Check if, application has already transacted before with basic
	 * account.Verify if application has a personnel with role
	 * Basic_Supplier_Admin and got soft certificate issued
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return boolean
	 */
	public static boolean isTransactWithBasicAccountApprovedSoftCert(
			List<SmPersonnel> smPersonnels) {
		boolean retVal = false;
		for (SmPersonnel smPersonnel : smPersonnels) {
			if (smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
					&& smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED
					&& smPersonnel.getEpRole() != null
					&& smPersonnel.getEpRole().equalsIgnoreCase(
							RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

	/**
	 * Check overlapping categories.Checks if Same owner exist in different
	 * supplier and has same one or more similar categories
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void checkOverLappingCategories(
			SupplierApplicationManagedBean managedBean) {
		SmOverlapCategoryVO overlapCategoryVO = getOverlapCategoryVO(managedBean
				.getSmAppl());

		managedBean.setOverLappedCategoryExists(false);
		managedBean.setOverLappedCategoryMsg(StringUtils.EMPTY);
		managedBean.setOverLappedCategoryRemarks(StringUtils.EMPTY);

		if (overlapCategoryVO != null) {
			managedBean.setOverLappedCategoryExists(overlapCategoryVO
					.isOverLappedCategoryExist());
			managedBean.setOverLappedCategoryMsg(overlapCategoryVO
					.getOverLappedCategoryMsg());
			managedBean.setOverLappedCategoryRemarks(overlapCategoryVO
					.getOverLappedCategoryRemarks());
			managedBean.setOverlapCategoryVOList(overlapCategoryVO
					.getOverlapCategoryVOList());
			managedBean.setOverLappedCategoryExists(true); //#31018
		}
		resetAddedCategoryOverlapStatus(managedBean.getSmSupplierCategories(),
				overlapCategoryVO);
	}

	public static SmOverlapCategoryVO getOverlapCategoryVO(SmAppl smAppl) {
		SmOverlapCategoryVO retVal = null;
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(smAppl.getSmSupplier().getSupplierId());
		reqDTO.setApplId(smAppl.getApplId());
		reqDTO.setLangCode(new FacesScopes().getUserLocale().getLanguage());
		List<Object[]> resultList = SmServiceLookup.getMofRegistrationSO()
				.retrieveOverLappedCategoryDetails(reqDTO).getResultList();
		if (!EntityHelper.isCollectionEmpty(resultList)
				&& resultList.size() > 0) {
			retVal = new SmOverlapCategoryVO();
			retVal.setOverLappedCategoryExist(true);
			retVal.setOverLappedCategoryMsg(SmCommonUtil.getMessage(
					"lbl.sm.category.overlappingMsg", null)
					+ "<br />"
					+ getOverlappedCategoryMsg(resultList));
			Locale loc = new Locale("ms", "MY");
			ResourceBundleUtils.getMessage("lbl.sm.category.overlappingMsg",
					null, ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc);
			retVal.setOverLappedCategoryRemarks(ResourceBundleUtils.getMessage(
					"lbl.sm.category.overlappingMsg", null,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc)
					+ System.lineSeparator()
					+ System.lineSeparator()
					+ getOverlappedCategoryRemarks(resultList));
			retVal.setOverlapCategoryVOList(getOverlapCategoryVOList(
					resultList, smAppl.getSupplierType()));
		}
		return retVal;
	}

	private static List<SmBulkApplOverlapCatVO> getOverlapCategoryVOList(
			List<Object[]> resultList, String suppType) {
		List<SmBulkApplOverlapCatVO> retVal = new ArrayList<SmBulkApplOverlapCatVO>();

		if (!EntityHelper.isCollectionEmpty(resultList)) {
			SmBulkApplOverlapCatVO voTmp = null;
			SmBulkApplOverlapCatVO voSubTmp = null;
			SmBulkApplOverlapCatVO voSubRemove = null;
			String catCode = StringUtils.EMPTY;
			String isActiveAppl = StringUtils.EMPTY;
			String applStatus = StringUtils.EMPTY;
			String language = new FacesScopes().getUserLocale().getLanguage();
			boolean isAdd = false;
			boolean isSubAdd = false;
			for (Object[] arr : resultList) {
				catCode = arr[0] + "" + arr[1] + arr[2] + "";
				isActiveAppl = arr[20] + "";
				voTmp = (SmBulkApplOverlapCatVO) EntityHelper
						.getEntityBySinglePropertyFromList(catCode, retVal,
								"categoryCode");
				if (voTmp == null) {
					voTmp = new SmBulkApplOverlapCatVO();
					voTmp.setCategoryCode(catCode);
					voTmp.setCategoryName("(" + arr[17] + "/" + arr[18] + "/"
							+ arr[19] + ")");
					isAdd = true;
				}

				if (isActiveAppl.equals("1")) {
					SmWorkflowStatus workflow = SmCommonUtil
							.getCurrentActiveWokFlowStatus(Long
									.parseLong(arr[22] + ""));
					if (workflow != null
							&& workflow.getPmStatus() != null
							&& !EntityHelper.isCollectionEmpty(workflow
									.getPmStatus().getPmStatusDescs())) {
						for (PmStatusDesc desc : workflow.getPmStatus()
								.getPmStatusDescs()) {
							if (!StringUtils.isBlank(desc.getLanguageCode())
									&& desc.getLanguageCode().equals(language)) {
								applStatus = desc.getStatusName();
							}
						}
					}
				}

				if (EntityHelper.isCollectionEmpty(voTmp.getOverlapInfoList())) {
					voTmp.setOverlapInfoList(new ArrayList<SmBulkApplOverlapCatVO>());
				}

				voSubTmp = getOverlapVOByRegNoAndIdNo(arr[7] + "",
						arr[16] + "", voTmp.getOverlapInfoList());

				if (voSubTmp == null) {
					isSubAdd = true;
				} else {
					if (isActiveAppl.equals("1") && !voSubTmp.isActiveAppl()) {
						voSubRemove = voSubTmp;
						isSubAdd = true;
					}
				}

				if (isSubAdd) {
					voSubTmp = new SmBulkApplOverlapCatVO();
					voSubTmp.setActiveAppl(isActiveAppl.equals("1") ? true
							: false);
					voSubTmp.setPersonnelName(arr[3] + "");
					voSubTmp.setIdentificationNo(arr[16] + "");
					voSubTmp.setRole(getRoleName(arr[9] + "", arr[10] + "",
							arr[11] + "", arr[15] + "", arr[12] + "", arr[13]
									+ "", arr[14] + "", suppType));
					voSubTmp.setCompanyName(arr[4] + "");
					voSubTmp.setMofNo(arr[5] + "");
					if (StringUtils.isBlank(voSubTmp.getMofNo())) {
						voSubTmp.setMofNo(SmCommonUtil.getMessage("lbl.txt.na",
								null));
					}
					voSubTmp.setExpDate(arr[6] + "");
					if (StringUtils.isBlank(voSubTmp.getExpDate())) {
						voSubTmp.setExpDate(SmCommonUtil.getMessage(
								"lbl.txt.na", null));
					}
					voSubTmp.setRegNo(arr[7] + "");
					voSubTmp.setApplNo(arr[8] + "");
					voSubTmp.setApplStatus(StringUtils.isBlank(applStatus) ? arr[21]
							+ ""
							: applStatus);
					voTmp.getOverlapInfoList().add(voSubTmp);
					isSubAdd = false;
				}

				if (voSubRemove != null) {
					voTmp.getOverlapInfoList().remove(voSubRemove);
				}

				if (isAdd) {
					retVal.add(voTmp);
					isAdd = false;
				}

			}
		}
		return retVal;
	}

	/**
	 * Get overlapped category message
	 * 
	 * @param Long
	 *            catL3Id
	 * @param Long
	 *            supplierId
	 * @param Long
	 *            applId
	 * @return String
	 */
	public static String getOverlappedCategoryMsg(List<Object[]> resultList) {
		StringBuilder msg = new StringBuilder();
		if (!SmCommonUtil.isCollectionEmpty(resultList)) {
			int count = 1;
			msg.append("<table style='width: 55%;table-layout:fixed'>");
			for (Object[] arr : resultList) {
				if (arr.length == 5) {
					setOverlappedCategoryMsg(msg, count, arr[0], arr[1],
							arr[2], arr[3], arr[4]);
					count++;
				}
			}
			msg.append("</table><br />");
		}
		return msg.toString();
	}

	/**
	 * Reset deactivated personnels status in the application.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return true, if successful
	 */
	public static boolean resetDeactivatedPersonnel(
			SupplierApplicationManagedBean managedBean) {
		boolean updateNecessary = false;
		if (managedBean.getSmPersonnels() != null
				&& managedBean.getSmPersonnels().size() > 0) {
			for (SmPersonnel personnel : managedBean.getSmPersonnels()) {
				if (personnel.getRecordStatusFlg().equals(
						SupplierManagementConst.DELETED_RECORD_STATUS)) {
					personnel
							.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
					personnel.setEpRole(null);
					personnel.setAuthorizedFlg(0);
					updateNecessary = true;
				}
			}
		}
		return updateNecessary;
	}

	private static void setOverlappedCategoryMsg(StringBuilder msg, int count,
			Object catCodeL1, Object catCodeL2, Object catCodeL3, Object name,
			Object suppName) {
		msg.append("<tr>");
		msg.append("<td style='width:5%'>").append(count).append(")")
				.append("</td>");
		msg.append("<td style='width:10%'>").append(catCodeL1)
				.append(catCodeL2).append(catCodeL3).append("</td>");
		msg.append("<td style='white-space: normal;'>").append("(")
				.append(name).append(" / ").append(suppName).append(")")
				.append("</td>");
	}

	public static String getOverlappedCategoryRemarks(List<Object[]> resultList) {
		StringBuilder msg = new StringBuilder();
		String SPACE = " ";
		if (!SmCommonUtil.isCollectionEmpty(resultList)) {
			int count = 1;
			for (Object[] arr : resultList) {
				if (arr.length == 5) {
					msg.append(SPACE).append(SPACE).append(SPACE);
					msg.append(count);
					msg.append(")").append(SPACE);
					msg.append(arr[0]).append(arr[1]).append(arr[2])
							.append(SPACE).append(SPACE);
					msg.append("(").append(arr[3]).append(" / ").append(arr[4])
							.append(")").append(System.lineSeparator());
					count++;
				}
			}
		}
		return msg.toString();
	}

	private static void resetAddedCategoryOverlapStatus(
			List<SmSupplierCategory> categories, SmOverlapCategoryVO vo) {
		for (SmSupplierCategory cat : categories) {
			if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
					|| cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL) {
				cat.setOverLapCategory(false);
				if (vo != null && vo.getOverlapCategoryVOList() != null
						&& cat.getPmCategoryL1() != null
						&& cat.getPmCategoryL2() != null
						&& cat.getPmCategoryL3() != null) {
					String catcode = cat.getPmCategoryL1().getCategoryL1Code()
							+ cat.getPmCategoryL2().getCategoryL2Code()
							+ cat.getPmCategoryL3().getCategoryL3Code();
					for (SmBulkApplOverlapCatVO bulkVO : vo
							.getOverlapCategoryVOList()) {
						if (!StringUtils.isBlank(bulkVO.getCategoryCode())
								&& !StringUtils.isBlank(catcode)
								&& catcode.equals(bulkVO.getCategoryCode())) {
							cat.setOverLapCategory(true);
						}
					}
				}
			}
		}
	}

	public void setAddressTransientValueAfterLoad(SmAddress addr) {
		if (addr != null) {
			if (addr.getPmCountry() != null
					&& addr.getPmCountry().getCountryId() != null) {
				addr.setCountryId(addr.getPmCountry().getCountryId());
			}
			if (addr.getPmState() != null
					&& addr.getPmState().getStateId() != null) {
				addr.setStateId(addr.getPmState().getStateId());
			}
			if (addr.getPmDistrict() != null
					&& addr.getPmDistrict().getDistrictId() != null) {
				addr.setDistrictId(addr.getPmDistrict().getDistrictId());
			}
			if (addr.getPmCity() != null
					&& addr.getPmCity().getCityId() != null) {
				addr.setCityId(addr.getPmCity().getCityId());
			}
		}
	}

	public void setAddressTransientValueBeforeSave(SmAddress addr) {
		if (addr != null) {
			if (addr.getCountryId() != null) {
				addr.setPmCountry(SmCommonUtil.getInstance().getCountryById(
						addr.getCountryId()));
			}
			if (addr.getStateId() != null) {
				addr.setPmState(SmCommonUtil.getInstance().getStateById(
						addr.getStateId()));
			}
			if (addr.getDistrictId() != null) {
				addr.setPmDistrict(SmCommonUtil.getInstance().getDistrictById(
						addr.getDistrictId()));
			}
			if (addr.getCityId() != null) {
				addr.setPmCity(SmCommonUtil.getInstance().getCityById(
						addr.getCityId()));
			}
		}
	}
	
	/**
	 * Sets the pm state dropdown values for basic information panel.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param countryId
	 *            - Long countryId
	 */
	public static void setPmStateDropdownValuesForBasicInfo(
			SupplierApplicationManagedBean managedBean, Long countryId) {

		List<SelectItem> stateLs = SmCommonUtil.getStateList(countryId);

		if (managedBean.getBasicCompanyStates() == null) {
			managedBean.setBasicCompanyStates(new ArrayList<SelectItem>());
		}
		if (managedBean.getSmSupplier().getBusinessType() != null
				&& !managedBean.getSmSupplier().getBusinessType()
						.equalsIgnoreCase("")) {

			if (managedBean
					.getBusinessTypeVal()
					.equalsIgnoreCase(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT)
					|| managedBean
							.getBusinessTypeVal()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT)) {

				managedBean.setBasicCompanyStates(new ArrayList<SelectItem>());
				if (managedBean
						.getSmSupplier()
						.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)
						|| managedBean
								.getSmSupplier()
								.getBusinessType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)) {
					for (SelectItem state : stateLs) {
						if (state.getLabel().equalsIgnoreCase("SARAWAK")) {
							managedBean.getBasicCompanyStates().add(state);
						}
					}
				} else if (managedBean
						.getSmSupplier()
						.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
						|| managedBean
								.getSmSupplier()
								.getBusinessType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)) {
					for (SelectItem state : stateLs) {
						if (state.getLabel().equalsIgnoreCase("SABAH")) {
							managedBean.getBasicCompanyStates().add(state);

						}
					}
				} else if (managedBean
						.getSmSupplier()
						.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
						|| managedBean
								.getSmSupplier()
								.getBusinessType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)) {

					for (SelectItem state : stateLs) {
						if (!state.getLabel().equalsIgnoreCase("SABAH")
								&& !state.getLabel()
										.equalsIgnoreCase("SARAWAK")) {
							managedBean.getBasicCompanyStates().add(state);
						}
					}
				} else {
					managedBean.setBasicCompanyStates(stateLs);
				}

			} else {
				managedBean.setBasicCompanyStates(stateLs);
			}
		} else {
			managedBean.setBasicCompanyStates(stateLs);
		}

	}
	
	public void populateStateByCountry(SmAddress addr, String panelName,
			SupplierApplicationManagedBean bean) {
		try {
			Long coutryId = addr.getCountryId();
			addr.setPmCountry(SmCommonUtil.getInstance().getCountryById(
					coutryId));
			List<PmState> stateLs = new ArrayList<PmState>();
			List<SelectItem> stateItems = SmCommonUtil.getStateList(coutryId);
			if (!panelName
					.equalsIgnoreCase(SupplierManagementConst.SM_BASIC_INFO_PANEL)) {
				stateLs = retrievePmStateList(coutryId);
			}
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				setPmStateDropdownValuesForBasicInfo(bean, coutryId);
				bean.setBasicCompanyDistricts(new ArrayList<SelectItem>());
				bean.setBasicCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				bean.setBasicOffFacStates(stateItems);
				bean.setBasicOffFacDistricts(new ArrayList<SelectItem>());
				bean.setBasicOffFacCities(new ArrayList<SelectItem>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				bean.setBranchCompanyStates(stateItems);
				bean.setBranchCompanyDistricts(new ArrayList<SelectItem>());
				bean.setBranchCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				bean.setAssocCompanyStates(stateItems);
				bean.setAssocCompanyDistricts(new ArrayList<SelectItem>());
				bean.setAssocCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				bean.setEquiCompanyStates(stateItems);
				bean.setEquiCompanyDistricts(new ArrayList<SelectItem>());
				bean.setEquiCompanyCities(new ArrayList<SelectItem>());
			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				bean.setFactoryFacilityStates(stateItems);
				bean.setFactoryFacilityDistricts(new ArrayList<SelectItem>());
				bean.setFactoryFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				bean.setDockFacilityStates(stateItems);
				bean.setDockFacilityDistricts(new ArrayList<SelectItem>());
				bean.setDockFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				bean.setWorkShopFacilityStates(stateItems);
				bean.setWorkShopFacilityDistricts(new ArrayList<SelectItem>());
				bean.setWorkShopFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				bean.setStoreFacilityStates(stateItems);
				bean.setStoreFacilityDistricts(new ArrayList<SelectItem>());
				bean.setStoreFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				bean.setShrHldrPermStates(stateItems);
				bean.setShrHldrPermDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrPermCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				bean.setShrHldrResdStates(stateItems);
				bean.setShrHldrResdDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrResdCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				bean.setShrHldrOffStates(stateItems);
				bean.setShrHldrOffDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrOffCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				bean.setShrHldrMailStates(stateItems);
				bean.setShrHldrMailDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrMailCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				bean.setBankStates(stateItems);
				bean.setBankDistricts(new ArrayList<SelectItem>());
				bean.setBankCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				bean.setPersnlPermStates(stateLs);
				bean.setPersnlPermDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlPermCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				bean.setPersnlResdStates(stateLs);
				bean.setPersnlResdDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlResdCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				bean.setPersnlOffStates(stateLs);
				bean.setPersnlOffDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlOffCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				bean.setPersnlMailStates(stateLs);
				bean.setPersnlMailDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlMailCities(new ArrayList<PmCity>());
				break;
			default:
				break;
			}
			addr.setStateId(null);
			addr.setDistrictId(null);
			addr.setCityId(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateDistrictsByState(SmAddress addr, String panelName,
			SmApplicationManagedBean bean) {
		try {
			Long stateId = addr.getStateId();
			if (stateId != null) {
				addr.setPmState(SmCommonUtil.getInstance()
						.getStateById(stateId));
			}
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(stateId);
			IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IDistrictSO.class);
			DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
			List<PmDistrict> distLs = resDTO.getDistrictList();
			List<SelectItem> distItems = SmCommonUtil.getDistrictList(stateId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				bean.setBasicCompanyDistricts(distItems);
				bean.setBasicCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				bean.setBasicOffFacDistricts(distItems);
				bean.setBasicOffFacCities(new ArrayList<SelectItem>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				bean.setBranchCompanyDistricts(distItems);
				bean.setBranchCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				bean.setAssocCompanyDistricts(distItems);
				bean.setAssocCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				bean.setEquiCompanyDistricts(distItems);
				bean.setEquiCompanyCities(new ArrayList<SelectItem>());
				break;

			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				bean.setFactoryFacilityDistricts(distItems);
				bean.setFactoryFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				bean.setDockFacilityDistricts(distItems);
				bean.setDockFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				bean.setWorkShopFacilityDistricts(distItems);
				bean.setWorkShopFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				bean.setStoreFacilityDistricts(distItems);
				bean.setStoreFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				bean.setShrHldrPermDistricts(distItems);
				bean.setShrHldrPermCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				bean.setShrHldrResdDistricts(distItems);
				bean.setShrHldrResdCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				bean.setShrHldrOffDistricts(distItems);
				bean.setShrHldrOffCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				bean.setShrHldrMailDistricts(distItems);
				bean.setShrHldrMailCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				bean.setPersnlPermDistricts(distLs);
				bean.setPersnlPermCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				bean.setPersnlResdDistricts(distLs);
				bean.setPersnlResdCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				bean.setPersnlOffDistricts(distLs);
				bean.setPersnlOffCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				bean.setPersnlMailDistricts(distLs);
				bean.setPersnlMailCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				bean.setBankDistricts(distItems);
				bean.setBankCities(new ArrayList<SelectItem>());
				break;
			default:
				break;
			}
			addr.setDistrictId(null);
			addr.setCityId(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateCitiesByDistrict(SmAddress addr, String panelName,
			SmApplicationManagedBean bean) {
		try {
			Long districtId = addr.getDistrictId();
			addr.setPmDistrict(SmCommonUtil.getInstance().getDistrictById(
					districtId));
			CityReqDTO reqDTO = new CityReqDTO();
			reqDTO.setDistrictId(districtId);
			ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", ICitySO.class);
			CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
			List<PmCity> cityLs = resDTO.getCityList();
			List<SelectItem> cityItems = SmCommonUtil.getCityList(districtId);

			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				bean.setBasicCompanyCities(SmCommonUtil.getCityList(districtId));
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				bean.setBasicOffFacCities(cityItems);
				break;
			case SupplierManagementConst.SM_BRANCH_PANEL:
				bean.setBranchCompanyCities(cityItems);
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				bean.setAssocCompanyCities(cityItems);
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				bean.setEquiCompanyCities(cityItems);
				break;
			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				bean.setFactoryFacilityCities(cityItems);
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				bean.setDockFacilityCities(cityItems);
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				bean.setWorkShopFacilityCities(cityItems);
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				bean.setStoreFacilityCities(cityItems);
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				bean.setShrHldrPermCities(cityItems);
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				bean.setShrHldrResdCities(cityItems);
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				bean.setShrHldrOffCities(cityItems);
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				bean.setShrHldrMailCities(cityItems);
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				bean.setPersnlPermCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				bean.setPersnlResdCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				bean.setPersnlOffCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				bean.setPersnlMailCities(cityLs);
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				bean.setBankCities(cityItems);
				break;
			default:
				break;
			}
			addr.setCityId(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve pm state list.
	 * 
	 * @param countryId
	 *            - long country id
	 * @return the List<PmState>
	 */
	public static List<PmState> retrievePmStateList(long countryId) {
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		return resDTO.getStateList();
	}

	private static String getRoleName(String equityOwner, String director,
			String higherManagement, String clerical, String management,
			String prof, String semiProf, String suppType) {
		StringBuilder sb = new StringBuilder();
		if (equityOwner.equals("1")) {
			sb.append(
					SmCommonUtil.getMessage(
							SupplierManagementConst.PERSONNEL_EQUI_OWNER, null))
					.append(",");
		}
		if (director.equals("1")) {
			sb.append(
					SmCommonUtil.getMessage(
							SupplierManagementConst.PERSONNEL_DIR_ROLENAME,
							null)).append(",");
		}
		if (higherManagement.equals("1")) {
			sb.append(
					SmCommonUtil.getMessage(
							SupplierManagementConst.PERSONNEL_HGHR_ROLENAME,
							null)).append(",");
		}
		if (management.equals("1")) {
			sb.append(
					SmCommonUtil
							.getMessage(
									SupplierManagementConst.PERSONNEL_MANAGEMENT_ROLENAME,
									null)).append(",");
		}
		if (clerical.equals("1")) {
			if (suppType.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
				sb.append(
						SmCommonUtil
								.getMessage(
										SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_ROLENAME,
										null)).append(",");
			} else {
				sb.append(
						SmCommonUtil
								.getMessage(
										SupplierManagementConst.PERSONNEL_STAFF_ROLENAME,
										null)).append(",");
			}
		}
		if (prof.equals("1")) {
			sb.append(
					SmCommonUtil
							.getMessage(
									SupplierManagementConst.PERSONNEL_PROF_STAFF_ROLENAME,
									null)).append(",");
		}
		if (semiProf.equals("1")) {
			sb.append(
					SmCommonUtil
							.getMessage(
									SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_ROLENAME,
									null)).append(",");
		}
		return sb.length() > 0 ? StringUtils.removeEnd(sb.toString(), ",")
				: StringUtils.EMPTY;
	}

	private static SmBulkApplOverlapCatVO getOverlapVOByRegNoAndIdNo(
			String regNo, String idNo, List<SmBulkApplOverlapCatVO> overlapLists) {
		SmBulkApplOverlapCatVO retVal = null;
		if (!EntityHelper.isCollectionEmpty(overlapLists)) {
			for (SmBulkApplOverlapCatVO vo : overlapLists) {
				if (vo.getRegNo() != null && regNo != null
						&& vo.getRegNo().equals(regNo)
						&& vo.getIdentificationNo() != null && idNo != null
						&& vo.getIdentificationNo().equals(idNo)) {
					retVal = vo;
					break;
				}
			}
		}
		return retVal;
	}

}
