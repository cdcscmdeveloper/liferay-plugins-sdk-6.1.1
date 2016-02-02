package my.ep.web.backingbean.sm.blacklistdirectorowner;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.dto.sm.blacklistDirector.BlacklistDirectorReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.blacklistdirectorowner.SmBlacklistDirectorManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

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
	public static String COMPANY_RESULT_PAGE = "RC";
	public static String DIRECTOR_RESULT_PAGE = "RD";

	public void preRenderBlacklistDirector() {
		renewSesionObject();
		getManagedBean().setTargetPage(SEARCH_PAGE);
		SmCommonUtil.setMDCUserId();
		SmCommonUtil
				.redirectToPage("blacklistDirectorBasePage.xhtml", "", null);
	}

	public void searchSupplier() {
		getManagedBean().getSupplierDataModel().reloadData();
	}

	public void searchOwnerDirector() {
		getManagedBean().getOwnerDirectorDataModel().reloadData();
	}

	public void viewSupplier(String supplierId) {
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
						|| (per.isDirector() || per.isEquityOwner())) {
					getManagedBean().getOwnerDirectorList().add(per);
				}
			}
		}
	}

	public void viewDirectorOrOwner(String identificationNo, String name) {
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

		if (getManagedBean().getCompanyList() == null) {
			getManagedBean().setCompanyList(new ArrayList<SmSupplier>());
		}
	}

	public void navigateToPage(String page) {
		getManagedBean().setTargetPage(page);
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

	public String getCOMPANY_RESULT_PAGE() {
		return COMPANY_RESULT_PAGE;
	}

	public String getDIRECTOR_RESULT_PAGE() {
		return DIRECTOR_RESULT_PAGE;
	}
}
