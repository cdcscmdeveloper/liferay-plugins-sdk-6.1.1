package my.ep.web.backingbean.sm.applicationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.managedbean.sm.applicationView.ViewSmApplManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmOverlapCategoryVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "viewSmApplBackingBean")
@RequestScoped
public class ViewSmApplBackingBean extends
		BaseReviewApplicationBackingBean<ViewSmApplManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3813287377990129080L;
	public static Logger log = Logger.getLogger(ViewSmApplBackingBean.class);

	/**
	 *  Initialized and construct ViewSmApplBackingBean
	 */
	public ViewSmApplBackingBean() {
		
	}
	//START 31018
	public void onViewOverlapCategory(SmAppl smAppl) {
		SmBulkApplicationVO vo = new SmBulkApplicationVO();
		SmOverlapCategoryVO overlapCategoryVO = getOverlapCategoryVO(smAppl);
		if (overlapCategoryVO != null) {
			vo.setOverlappedCategory(overlapCategoryVO
					.isOverLappedCategoryExist());
			vo.setOverlapMsg(overlapCategoryVO.getOverLappedCategoryMsg());
			vo.setOverlapCategoryVOList(overlapCategoryVO
					.getOverlapCategoryVOList());
		}
		getManagedBean()
				.setOverlapCategoryVOList(vo.getOverlapCategoryVOList());
		RequestContext.getCurrentInstance().execute(
				"overlapCategoryDialog.show();");
	}
	private SmOverlapCategoryVO getOverlapCategoryVO(SmAppl smAppl) {
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
					"lbl.sm.category.overlappingMsg", null) + "<br />");
			Locale loc = new Locale("ms", "MY");
			ResourceBundleUtils.getMessage("lbl.sm.category.overlappingMsg",
					null, ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc);
			retVal.setOverLappedCategoryRemarks(ResourceBundleUtils.getMessage(
					"lbl.sm.category.overlappingMsg", null,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc)
					+ System.lineSeparator() + System.lineSeparator());
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
			if (suppType.equals(SupplierManagementConst.SUPP_TYPE_CONSULTANT)) {
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
	//END 31018


}
