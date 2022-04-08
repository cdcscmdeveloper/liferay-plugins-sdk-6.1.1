package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.MessageConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DirOwnCeoOneCompRptManagedBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "dirOwnCeoOneCompRptBB")
@ViewScoped
public class DirOwnCeoOneCompRptBackingBean extends
		SmReportBackingBean<DirOwnCeoOneCompRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6988130580818997228L;
	public static Logger log = Logger.getLogger(DirOwnCeoOneCompRptBackingBean.class);
	
	public DirOwnCeoOneCompRptBackingBean() {
		setDefaultDateFromAndDateTo();
		getManagedBean().setMofRegNoFrom(SmReportConst.SUPPL_CONSULT_MOF_NO);
		getManagedBean().setMofRegNoTo(SmReportConst.SUPPL_CONSULT_MOF_NO);
	}

	public void generateReport() {
		if (StringUtils.isEmpty(getManagedBean().getMofRegNoFrom())
				|| StringUtils.isEmpty(getManagedBean().getMofRegNoTo())) {
			throw new EpException(MessageConst.ERROR_REPORT_780035);
		}
		validateMofRegNo();
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
				.getSupplierType());
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
					.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}
		params.put(SmReportConst.PARAM_IDENT_NO, getManagedBean()
				.getIdentificationNo());
		params.put(SmReportConst.PARAM_MOF_REG_NO_FROM, getManagedBean()
				.getMofRegNoFrom());
		params.put(SmReportConst.PARAM_MOF_REG_NO_TO, getManagedBean()
				.getMofRegNoTo());

		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		params.put(SmReportConst.PARAM_DATE_FROM,
				df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO,
				df.format(getManagedBean().getDateTo()));
		
		//15174 - start
		if (!StringUtils.isBlank(getManagedBean().getPersonnelName())){
			params.put(SmReportConst. PARAM_PERSONNEL_NAME,getManagedBean()
					.getPersonnelName());
			log.info(getManagedBean().getPersonnelName());
		}
		//15174 - end
		
		//#7754 - Start
		params.put(SmReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
		//#7754 - End

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setSupplierType(
				SupplierManagementConst.SUPP_TYPE_CONSULTANT);
		getManagedBean().setMofRegNoFrom(SmReportConst.SUPPL_CONSULT_MOF_NO);
		getManagedBean().setMofRegNoTo(SmReportConst.SUPPL_CONSULT_MOF_NO);
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
		getManagedBean().setIdentificationNo(StringUtils.EMPTY);
		//15174 - Start
		getManagedBean().setPersonnelName(StringUtils.EMPTY);
		//15174 - End
	}

	public void onChangeSuppType() {
		if (getManagedBean().getSupplierType().equals(
				SupplierManagementConst.SUPP_TYPE_CONTRACTOR)) {
			getManagedBean().setMofRegNoFrom(
					SmReportConst.SUPPL_CONTRACT_MOF_NO);
			getManagedBean().setMofRegNoTo(SmReportConst.SUPPL_CONTRACT_MOF_NO);
		} else {
			getManagedBean()
					.setMofRegNoFrom(SmReportConst.SUPPL_CONSULT_MOF_NO);
			getManagedBean().setMofRegNoTo(SmReportConst.SUPPL_CONSULT_MOF_NO);
		}
	}
}
