package my.ep.web.backingbean.sm.selfrequestsupplieradmin;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.dto.sm.selfrequestsupplieradmin.SelfRequestSupplierAdminReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.selfrequestsupplieradmin.SelfRequestSupplierAdminManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "selfRequestSupplierAdminBackingBean")
@RequestScoped
public class SelfRequestSupplierAdminBackingBean extends
		BaseBackingBean<SelfRequestSupplierAdminManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4774616656344235966L;

	private static final String COMPANY_PAGE = "1";
	private static final String PERSONNEL_PAGE = "2";

	public void preRender() {
		renewSesionObject();
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/selfRequestSupplierAdmin/selfRequestSuppAdminTnc.xhtml",
						"", null);
	}

	public void navigateToCompanyInfo() {
		getManagedBean().setPage(COMPANY_PAGE);
		getManagedBean().setSupplier(new SmSupplier());
	}

	public void onBlurMofNo() {
		String mofStatusMsg = SmCommonUtil.getMessage(
				"lbl.sm.selfrequest.mofstatus.notvalid.expired", null);
		String suppAdminStatusMsg = SmCommonUtil.getMessage(
				"lbl.sm.selfrequest.suppadmin.not.appointed", null);
		SelfRequestSupplierAdminReqDTO reqDTO = new SelfRequestSupplierAdminReqDTO();
		reqDTO.setMofNo(getManagedBean().getMofNo());
		getManagedBean().setSupplier(
				SmServiceLookup.getSelfRequestSupplierAdminSO()
						.retrieveSupplierByMofNo(reqDTO).getSupplier());

		// set mof account message status
		SmMofAccount mofAcc = getManagedBean().getSupplier().getSmMofAccounts()
				.get(0);

		if (mofAcc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
				&& mofAcc.getExpDate() != null
				&& mofAcc.getExpDate().after(new Date())) {
			mofStatusMsg = SmCommonUtil.getMessage(
					"lbl.sm.selfrequest.mofstatus.notvalid.expired", null);
		}

		// set supplier admin status
		if (getManagedBean().getSupplier().getSmAppl() != null
				&& !EntityHelper.isCollectionEmpty(getManagedBean()
						.getSupplier().getSmAppl().getSmPersonnels())) {
			List<SmPersonnel> personnels = getManagedBean().getSupplier()
					.getSmAppl().getSmPersonnels();

			for (SmPersonnel per : personnels) {
				if ((per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE || per
						.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)
						&& (!StringUtils.isBlank(per.getEpRole()) && (per
								.getEpRole().equals(
										RoleCode.BASIC_SUPPLIER_ADMIN.value()) || per
								.getEpRole().equals(
										RoleCode.MOF_SUPPLIER_ADMIN.value())))) {
					suppAdminStatusMsg = SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.suppadmin.appointed", null);
				}
			}
		}
	}

}
