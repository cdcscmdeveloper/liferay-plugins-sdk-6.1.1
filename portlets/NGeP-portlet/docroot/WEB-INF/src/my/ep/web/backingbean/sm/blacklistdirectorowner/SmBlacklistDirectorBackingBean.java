package my.ep.web.backingbean.sm.blacklistdirectorowner;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorReqDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.blacklistdirectorowner.SmBlacklistDirectorManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "smBlacklistDirectorBackingBean")
@RequestScoped
public class SmBlacklistDirectorBackingBean extends
		BaseBackingBean<SmBlacklistDirectorManagedBean> {
	public static String SEARCH_BY_COMPANY = "C";
	public static String SEARCH_BY_DIRECTOR = "D";
	public static String DIRECTOR_SEARCH_BY_IDNO = "DI";
	public static String DIRECTOR_SEARCH_BY_NAME = "DN";
	public static String COMPANY_SEARCH_BY_NAME = "CN";
	public static String COMPANY_SEARCH_BY_REGNO = "CR";
	public static String COMPANY_SEARCH_BY_EPNO = "CE";
	public static String SEARCH_PAGE = "S";
	public static String VIEW_COMPANY_RESULT_PAGE = "RC";
	public static String VIEW_DIRECTOR_RESULT_PAGE = "RD";

	/**
	 * Pre-render blacklist director main page
	 */
	public void preRenderBlacklistDirector() {
		renewSesionObject();
		getManagedBean().setTargetPage(SEARCH_PAGE);
		getManagedBean().setSupplier(new SmSupplier());
		getManagedBean().setDirectorOwner(new SmPersonnel());
		getManagedBean().setOwnerDirectorList(new ArrayList<SmPersonnel>());
		getManagedBean().setCompanyList(new ArrayList<SmSupplier>());
		SmCommonUtil.setMDCUserId();
		SmCommonUtil
				.redirectToPage("blacklistDirectorBasePage.xhtml", "", null);
	}

	/**
	 * Search company
	 */
	public void searchCompany() {
		getManagedBean().getSupplierDataModel().reloadData();
	}

	public void resetCompanySearch() {
		getManagedBean().setCompanyName(StringUtils.EMPTY);
		getManagedBean().setBussRegNo(StringUtils.EMPTY);
		getManagedBean().setEpNo(StringUtils.EMPTY);
	}

	/**
	 * Reset director search criteria
	 */
	public void resetDirectorSearch() {
		getManagedBean().setName(StringUtils.EMPTY);
		getManagedBean().setIdentificationNo(StringUtils.EMPTY);
	}

	/**
	 * Search director
	 */
	public void searchDirector() {
		getManagedBean().getOwnerDirectorDataModel().reloadData();
	}

	/**
	 * View company in result table
	 * 
	 * @param String
	 *            supplierId
	 */
	public void viewCompany(String supplierId) {
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setSupplierId(Long.parseLong(supplierId));

		getManagedBean().setSupplier(
				SmServiceLookup.getSupplierSearchSO()
						.retriveSupplierById(reqDTO).getSmSupplier());

		if (getManagedBean().getSupplier() != null
				&& getManagedBean().getSupplier().getSmAppl() != null
				&& !EntityHelper.isCollectionEmpty(getManagedBean()
						.getSupplier().getSmAppl().getSmPersonnels())) {
			getManagedBean().setOwnerDirectorList(new ArrayList<SmPersonnel>());
			for (SmPersonnel per : getManagedBean().getSupplier().getSmAppl()
					.getSmPersonnels()) {
				if ((per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE || per
						.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)
						&& (per.isDirector() || per.isEquityOwner())) {
					getManagedBean().getOwnerDirectorList().add(per);
				}
			}
		}
		getManagedBean().setTargetPage(VIEW_COMPANY_RESULT_PAGE);
	}

	/**
	 * View director in result table
	 * 
	 * @param String
	 *            identificationNo
	 * @param String
	 *            name
	 */
	public void viewDirector(String identificationNo, String name) {
		getManagedBean().setDirectorOwner(new SmPersonnel());
		getManagedBean().getDirectorOwner().setName(name);
		getManagedBean().getDirectorOwner().setIdentificationNo(
				identificationNo);

		BlacklistDirectorReqDTO reqDTO = new BlacklistDirectorReqDTO();
		reqDTO.setDirectorIdNo(identificationNo);

		getManagedBean().setCompanyList(
				SmServiceLookup.getBlacklistDirectorSO()
						.retrieveSupplierByOwnerDirectorIdNo(reqDTO)
						.getSupplierList());

		// Get blacklist by identification no
		BlacklistDirectorResDTO resDTO = new BlacklistDirectorResDTO();
		resDTO = SmServiceLookup.getBlacklistDirectorSO()
				.isIdentificatioNoBlacklisted(reqDTO);
		getManagedBean().setRemarks(resDTO.getRemarks());
		getManagedBean().setCurrentlyBlacklist(resDTO.isBlacklisted());
		getManagedBean().setBlacklist(getManagedBean().isCurrentlyBlacklist());

		if (getManagedBean().getCompanyList() == null) {
			getManagedBean().setCompanyList(new ArrayList<SmSupplier>());
		} else {
			for (SmSupplier sup : getManagedBean().getCompanyList()) {
				sup.setSmPersonnel((SmPersonnel) EntityHelper
						.getEntityBySinglePropertyFromList(identificationNo,
								sup.getSmAppl().getSmPersonnels(),
								"identificationNo"));
			}
		}
		getManagedBean().setTargetPage(VIEW_DIRECTOR_RESULT_PAGE);
	}

	/**
	 * Submit blacklist/remove blacklist director
	 */
	public void submit() {
		BlacklistDirectorReqDTO reqDTO = new BlacklistDirectorReqDTO();
		reqDTO.setBlacklist(getManagedBean().isBlacklist());
		reqDTO.setRemarks(getManagedBean().getRemarks());
		reqDTO.setDirectorIdNo(getManagedBean().getDirectorOwner()
				.getIdentificationNo());
		SmServiceLookup.getBlacklistDirectorSO().submitBlacklist(reqDTO);
		SmCommonUtil.redirectToPage(
				SmApplicationUtil.getApplicationErrorPage(),
				MessageConst.INFO_711001, null);
	}

	/**
	 * On click submit button
	 */
	public void onClickSubmit() {
		RequestContext.getCurrentInstance().execute(
				"confirmSubmitDialog.show();");
	}

	/**
	 * Navigate to page
	 * 
	 * @param String
	 *            page
	 */
	public void navigateToPage(String page) {
		getManagedBean().setTargetPage(page);
	}

	/**
	 * Back to previous page from view director page
	 */
	public void backFromDirectorPage() {
		if (getManagedBean().getSearchBy().equals(SEARCH_BY_COMPANY)) {
			getManagedBean().setTargetPage(VIEW_COMPANY_RESULT_PAGE);
		} else {
			getManagedBean().setTargetPage(SEARCH_PAGE);
		}
	}

	public String getSEARCH_BY_COMPANY() {
		return SEARCH_BY_COMPANY;
	}

	public String getSEARCH_BY_DIRECTOR() {
		return SEARCH_BY_DIRECTOR;
	}

	public String getDIRECTOR_SEARCH_BY_IDNO() {
		return DIRECTOR_SEARCH_BY_IDNO;
	}

	public String getDIRECTOR_SEARCH_BY_NAME() {
		return DIRECTOR_SEARCH_BY_NAME;
	}

	public String getCOMPANY_SEARCH_BY_NAME() {
		return COMPANY_SEARCH_BY_NAME;
	}

	public String getCOMPANY_SEARCH_BY_REGNO() {
		return COMPANY_SEARCH_BY_REGNO;
	}

	public String getCOMPANY_SEARCH_BY_EPNO() {
		return COMPANY_SEARCH_BY_EPNO;
	}

	public String getSEARCH_PAGE() {
		return SEARCH_PAGE;
	}

	public String getVIEW_COMPANY_RESULT_PAGE() {
		return VIEW_COMPANY_RESULT_PAGE;
	}

	public String getVIEW_DIRECTOR_RESULT_PAGE() {
		return VIEW_DIRECTOR_RESULT_PAGE;
	}
}
