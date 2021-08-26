package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constants.CommonConstants;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ApplStatusRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "applStatusRptBB")
@ViewScoped
public class ApplStatusRptBackingBean extends
		SmReportBackingBean<ApplStatusRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3440385149367317737L;
	
	public static Logger log = Logger.getLogger(ApplStatusRptBackingBean.class);

	public ApplStatusRptBackingBean() {
		setDefaultDateFromAndDateTo();
		getSupplierInfo();
	}

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_DATE_FROM,
				df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO,
				df.format(getManagedBean().getDateTo()));
		
		if(getManagedBean().getMofNo() == null){
			getManagedBean().setMofNo("");
		}
		if(getManagedBean().getRegNo() == null){
			getManagedBean().setRegNo("");
		}
		
		params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getMofNo());
		params.put(SmReportConst.PARAM_APPL_NO, getManagedBean().getApplNo());
		params.put(SmReportConst.PARAM_REG_NO, getManagedBean().getRegNo());
		if (!StringUtils.isBlank(getManagedBean().getApplType())) {
			params.put(SmReportConst.PARAM_APPL_TYPE, getManagedBean()
					.getApplType());
		}
		log.info("----Appl No: "+ getManagedBean().getApplNo());
		log.info("----Appl Type: "+ getManagedBean().getApplType());
		log.info("----Mof No: "+ getManagedBean().getMofNo());
		log.info("----Reg No: "+ getManagedBean().getRegNo());
		
		
		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setMofNo(StringUtils.EMPTY);
		getManagedBean().setApplNo(StringUtils.EMPTY);
		getManagedBean().setApplType(StringUtils.EMPTY);
		getManagedBean().setRegNo(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

	public void getSupplierInfo(){
		PmUser pmUser = new FacesScopes().getUserSessionBean();

		if(pmUser != null){
		
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());
			ISupplierSearchSO supplierSearchSO = SmServiceLookup.getSupplierSearchSO();
			SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
			
			if(smResDTO.getResultBean().getResultList().size() > 0){
				SmSupplier supplier = (SmSupplier)smResDTO.getResultBean().getResultList().get(0); 

				if(supplier != null){
					getManagedBean().setRegNo(supplier.getRegNo());

					MofAccountReqDTO reqDTO = new MofAccountReqDTO();
					reqDTO.setSupplierId(supplier.getSupplierId());
					SmMofAccount mofAccount = SmServiceLookup.getMofAccountSO().retrieveMOFAccBySupplierId(reqDTO).getMofAccount();

					if(mofAccount != null){
						getManagedBean().setMofNo(mofAccount.getMofNo());
					}
				}
			}
		}
		
	}
}
