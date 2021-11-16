/*
 * 
 */
package my.ep.web.backingbean.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.constant.cm.CmConst;
import my.ep.web.constant.cm.CmReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.CMReportManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
// #29592 - start
// #29592 - end
// #29592 - start
// #29592 - end
// #29592 - start
// #29592 - end
// #29592 - start
// #29592 - end

@ManagedBean(name = "cmReportsBackingBean")
@ViewScoped
public class CMReportsBackingBean extends ReportBaseBackingBean<CMReportManagedBean> {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger.getLogger(ReportBaseBackingBean.class);
	
	// #29592 - start
	private static final String CARET_CHAR = "^";
	// #29592 - end

	public void preRenderCmReport() {
		log.info(".................preRenderCmReport................");


		retrieveReportListByAuthorization(CmConst.CM_ModuleCode);

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "cmReportMain");
	}


	public void generateReportCM002(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());




		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}

		if(this.getManagedBean().getGroup()!=null && this.getManagedBean().getGroup().trim().length()>0){
			params.put(CmReportConst.GROUP, this.getManagedBean().getGroup());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}


		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		/*String paramsURL = constructParameterString(params);

		System.out.println("paramsURL:"+paramsURL);
		 */
		preGenerateReportAPI(params);
		/*	RPTRequestResDTO resDTO = rptRequestSO.generateReport(paramsURL, this.getManagedBean().getReportCode(), 
				this.getManagedBean().getReportName(), userId, false, this.getManagedBean().isTempAccess());*/




		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}


	public void preGenerateReportAPI(LinkedHashMap<String, String> map){

		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);
		
		System.out.println("TempAccess"+this.getManagedBean().isTempAccess());
		System.out.println("this.getManagedBean().getReportName():"+this.getManagedBean().getReportName());
		System.out.println("this.getManagedBean().getReportCode():"+this.getManagedBean().getReportCode());
		System.out.println("this.getManagedBean().getDefaultOutputType():"+this.getManagedBean().getDefaultOutputType());

		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		reqDTO.setDefaultOutputType(this.getManagedBean().getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(map);
		try {
			RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
			System.out.println("resDTO.getRequestId():"+resDTO.getRequestId());
			getPopUpMsg(String.valueOf(resDTO.getRequestId()));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void resetReport(){

		this.getManagedBean().setDateFrom(null);
		this.getManagedBean().setDateTo(null);
		this.getManagedBean().setSelectedCmUNSPSC(null);
		this.getManagedBean().setSelectedCmItem(null);
		this.getManagedBean().setSelectedOrgProfile(null);
		this.getManagedBean().setItemType("");
		this.getManagedBean().setMofNo("");
		this.getManagedBean().setRegType("");
		this.getManagedBean().setIcNo("");
		this.getManagedBean().setGroup("");
		this.getManagedBean().setBusRegNo("");
		this.getManagedBean().setEpNo("");
		this.getManagedBean().setRequestNo("");
		// #29592 - start
		this.getManagedBean().setSelectedMinistryOrgProfile(null);
		this.getManagedBean().setSelectedKumpulanPTJOrgProfile(null);
		this.getManagedBean().setSelectedPTJOrgProfile(null);
		this.getManagedBean().setDocNoSearchInput(null);
		// #29592 - end
		
		//#29640 - Start
		this.getManagedBean().setRequestType("");
		this.getManagedBean().setDocStatus(null);
		//#29640 - End
	}






	public void dateValidation(Date dateFrom, Date dateTo){

		if (dateFrom == null || dateTo == null) {
			throw new EpException(MessageConst.ERROR_715050);
		}

		if ((dateFrom != null) && (dateTo != null)) {
			if (dateFrom.after(dateTo)) {
				throw new EpException(MessageConst.ERROR_REPORT_780008);
			}
		}
		if ((dateFrom != null) && (dateTo != null)) {
			if (dateTo.after(dateFrom)) {
				checkOneYearDifferent(dateFrom,dateTo);
			}
		}

	}

	public void generateAction(String methodActionStr) {
		System.out.println("methodActionStr:"+methodActionStr);
		if (StringUtils.isNotBlank(methodActionStr)) {
			MethodExpression methodExp = FacesScopes.createMethodExpression(
					FacesContext.getCurrentInstance(), methodActionStr, null,
					new Class[] {});

			methodExp.invoke(FacesContext.getCurrentInstance().getELContext(),
					new Object[] {});

		}
	}


	public void resetAction(String resetAction) {
		System.out.println("resetAction:"+resetAction);
		if (StringUtils.isNotBlank(resetAction)) {
			MethodExpression methodExp = FacesScopes.createMethodExpression(
					FacesContext.getCurrentInstance(), resetAction, null,
					new Class[] {});

			methodExp.invoke(FacesContext.getCurrentInstance().getELContext(),
					new Object[] {});

		}
	}


	public void retrieveCmReportValueFromDb(){

		resetReport();
		retrieveReportValueFromDb();
	}

	public void generateReportCM008(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());

		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}


		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}



	public void generateReportCM009(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();

		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());

		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}

		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}



	public void generateReportCM010(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());

		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}


		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}


	public void generateReportCM011(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();

		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());

		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}

		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}



	public void generateReportCM005(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());




		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}

		if(this.getManagedBean().getGroup()!=null && this.getManagedBean().getGroup().trim().length()>0){
			params.put(CmReportConst.GROUP, this.getManagedBean().getGroup());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}


		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}
	
	
	public void generateReportCM017(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();

		
	
		if (this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().isEmpty() 
				&& this.getManagedBean().getSelectedCmItem().getItemName()==null) {
			throw new EpException(MessageConst.ERROR_780003);
		}
		
		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}
		
		if(this.getManagedBean().getSelectedCmItem().getItemName()!=null
				&& this.getManagedBean().getSelectedCmItem().getItemName().trim().length()>0){
			params.put(CmReportConst.ITEM_NAME, this.getManagedBean().getSelectedCmItem().getItemName());
		}
		
		

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}
	
	public void generateReportCM016(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();

		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());

		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}

		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}
	
	
	public void generateReportCM004(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());

		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}

		if(this.getManagedBean().getGroup()!=null && this.getManagedBean().getGroup().trim().length()>0){
			params.put(CmReportConst.GROUP, this.getManagedBean().getGroup());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}


		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));

		preGenerateReportAPI(params);
		
		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}
	
	
	public void generateReportCM012(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}
		// #29592 - start
		if (this.getManagedBean().getSelectedMinistryOrgProfile() != null
				&& this.getManagedBean().getSelectedMinistryOrgProfile().getPmOrgValidity() != null
				&& StringUtils.isNotBlank(this.getManagedBean().getSelectedMinistryOrgProfile().getPmOrgValidity()
						.getOrgName())) {
			params.put(CmReportConst.MINISTRY, this.getManagedBean().getSelectedMinistryOrgProfile().getPmOrgValidity()
					.getOrgName());
		}
		if (this.getManagedBean().getSelectedKumpulanPTJOrgProfile() != null
				&& this.getManagedBean().getSelectedKumpulanPTJOrgProfile().getPmOrgValidity() != null
				&& StringUtils.isNotBlank(this.getManagedBean().getSelectedKumpulanPTJOrgProfile().getPmOrgValidity()
						.getOrgName())) {
			params.put(CmReportConst.KUMPULAN_PTJ, this.getManagedBean().getSelectedKumpulanPTJOrgProfile()
					.getPmOrgValidity().getOrgName());
		}
		if (this.getManagedBean().getSelectedPTJOrgProfile() != null
				&& this.getManagedBean().getSelectedPTJOrgProfile().getPmOrgValidity() != null
				&& StringUtils.isNotBlank(this.getManagedBean().getSelectedPTJOrgProfile().getPmOrgValidity()
						.getOrgName())) {
			params.put(CmReportConst.PTJ, this.getManagedBean().getSelectedPTJOrgProfile().getPmOrgValidity()
					.getOrgName());
		}
		if (StringUtils.isNotBlank(this.getManagedBean().getDocNoSearchInput())) {
			params.put(CmReportConst.DOCUMENT_NO, this.getManagedBean().getDocNoSearchInput());
		}
		// #29592 - end

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}
	
	
	public void generateReportCM001(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());




		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}

		if(this.getManagedBean().getGroup()!=null && this.getManagedBean().getGroup().trim().length()>0){
			params.put(CmReportConst.GROUP, this.getManagedBean().getGroup());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}


		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);
		

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}
	
	
	
	public void generateReportCM003(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());




		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}

		if(this.getManagedBean().getGroup()!=null && this.getManagedBean().getGroup().trim().length()>0){
			params.put(CmReportConst.GROUP, this.getManagedBean().getGroup());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}


		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);
		

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}
	
	
	
	public void generateReportCM015(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());




		

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}

		if(this.getManagedBean().getRequestNo()!=null && this.getManagedBean().getRequestNo().trim().length()>0){
			params.put(CmReportConst.REQUEST_NO, this.getManagedBean().getRequestNo());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}


		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);
		

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}
	
	
	public void generateReportCM013(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());


		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}


		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}


		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		
		preGenerateReportAPI(params);
	


		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}
	
	
	public void generateReportCM014(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());


		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}


		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}


		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}

		if(this.getManagedBean().getEpNo()!=null && this.getManagedBean().getEpNo().trim().length()>0){
			params.put(CmReportConst.EP_NO, this.getManagedBean().getEpNo());
		}

		if(this.getManagedBean().getIcNo()!=null && this.getManagedBean().getIcNo().trim().length()>0){
			params.put(CmReportConst.IC_NO, this.getManagedBean().getIcNo());
		}

		if(this.getManagedBean().getMofNo()!=null && this.getManagedBean().getMofNo().trim().length()>0){
			params.put(CmReportConst.MOF_NO, this.getManagedBean().getMofNo());
		}

		if(this.getManagedBean().getBusRegNo()!=null && this.getManagedBean().getBusRegNo().trim().length()>0){
			params.put(CmReportConst.REG_NO, this.getManagedBean().getBusRegNo());
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		
		preGenerateReportAPI(params);
	


		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}
	
	// #29592 - start
	public void updateMinistrySearchCriteria(BaseEntity entity) {
		log.info("updateMinistrySearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPmSearchBean()
				.setMinistry(orgProfile);
		
		resetPtjGroupOrgProfile(); 
	}
	
	public String constructMinistryConditionFields() {		
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.MINISTRY.value());
		sb.append("|recordStatus^1");
		return sb.toString();
	}
	
	public void updateKPTJSearchCriteria(BaseEntity entity) {
		log.info("updateKPTJSearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPmSearchBean()
				.setKptj(orgProfile);

		resetPtjOrgProfile();
	}
	
	public String constuctConditionFieldsForPTJGroup() {
		StringBuffer parentOrgProfile = new StringBuffer();
		PmOrgProfile ministryOrgProfile = this.getManagedBean().getSelectedMinistryOrgProfile();
		Long MinistryOrgProfileId = ministryOrgProfile != null ? ministryOrgProfile
				.getOrgProfileId() : null;
		if (null != MinistryOrgProfileId) {
			String minLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_MINISTRY);
			String ptjGrpLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_PTJGROUP);
			if (minLevel != null && ptjGrpLevel != null) {
				int ministryLevel = Integer.valueOf(minLevel);
				int ptjGroupLevel = Integer.valueOf(ptjGrpLevel);
				int orgLevel = ministryLevel - ptjGroupLevel;

				for (int i = 0; i < orgLevel; i++) {
					parentOrgProfile.append("parentOrgProfile.");
				}
				parentOrgProfile.append("orgProfileId");
				parentOrgProfile.append(CARET_CHAR);
				parentOrgProfile.append(MinistryOrgProfileId);
			}
		} else {
			parentOrgProfile.append("  ");
		}
		return parentOrgProfile.toString();
	}
	
	public void updateSearchCriteria(BaseEntity entity) {
		log.info("updateSearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPmSearchBean()
				.setPtj(orgProfile);
	}
	
	public String constuctConditionFieldsForPTJ() {
		String parentOrgProfile = null;
		
		PmOrgProfile ministryOrgProfile = this.getManagedBean().getSelectedMinistryOrgProfile();
		Long MinistryOrgProfileId = ministryOrgProfile != null ? ministryOrgProfile.getOrgProfileId() : null;
		PmOrgProfile groupOrgProfile = this.getManagedBean().getSelectedKumpulanPTJOrgProfile();
		Long PTJGroupOrgProfileId = groupOrgProfile != null ? groupOrgProfile.getOrgProfileId() : null;
		if (null != PTJGroupOrgProfileId) {
			parentOrgProfile = OrgProfileUtil.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
					+ CARET_CHAR + PTJGroupOrgProfileId + ("|groupOrgType^") + ParameterConst.ORG_AUTH_TYPE_ORG_GROUP;
		} else if (null != MinistryOrgProfileId) {
			parentOrgProfile = OrgProfileUtil.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ CARET_CHAR + MinistryOrgProfileId + ("|groupOrgType^") + ParameterConst.ORG_AUTH_TYPE_ORG_GROUP;
		} else {
			parentOrgProfile = ("groupOrgType^") + ParameterConst.ORG_AUTH_TYPE_ORG_GROUP;
		}

		return parentOrgProfile;
	}
	
	public void resetPtjGroupOrgProfile() {
		this.getManagedBean().setSelectedKumpulanPTJOrgProfile(null);		
		this.getManagedBean().getPmSearchBean().setKptj(null);
		resetPtjOrgProfile();
	}
	
	public void resetPtjOrgProfile() {
		this.getManagedBean().setSelectedPTJOrgProfile(null);		
		this.getManagedBean().getPmSearchBean().setPtj(null);
	}
	
	// #29592 - end
	
	//#29640 - Start
	public SelectItem[] getDocStatus() {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {

			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);
			String languageCode = new FacesScopes().getUserLocale().getLanguage();

			StatusReqDTO reqStatusDTO = new StatusReqDTO();

			SearchBean<PmStatusDesc> searchBean = new SearchBean<PmStatusDesc>();

			reqStatusDTO.setLanguageCode(languageCode);
			List<String> cmDocTypes = new ArrayList<String>();
			cmDocTypes.add("CE");
			cmDocTypes.add("CU");
			reqStatusDTO.setDocTypeList(cmDocTypes);
			reqStatusDTO.setModuleCode("CM");

			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
			bean.setSearchField("pmStatus.statusId");
			bean.setSearchValue(new ArrayList<String>(Arrays.asList(new String[] { "15001", "15011", "15008", "15018",
					"15014", "15016", "15004" })));
			searchBean.getDataFilterBeans().add(bean);

			reqStatusDTO.setStatusDescSearchBean(searchBean);

			StatusResDTO resStatusDTO = statusSO.retrieveStatusDescList(reqStatusDTO);

			if (resStatusDTO.getStatusDescList() != null && resStatusDTO.getStatusDescList().size() > 0) {

				items = new SelectItem[resStatusDTO.getStatusDescList().size() + 1];

				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.rpt.all",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
				int i = 1;
				for (PmStatusDesc desc : resStatusDTO.getStatusDescList()) {
					log.info("desc.getStatusId()" + desc.getPmStatus().getStatusId());
					items[i++] = new SelectItem(desc.getPmStatus().getStatusId(), desc.getStatusName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		return items;
	}
	
	public void generateReportCM020(){

		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();


		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());
		

		if(this.getManagedBean().getItemType()!=null && this.getManagedBean().getItemType().trim().length()>0){
			log.info("ITEM_TYPE " + this.getManagedBean().getItemType());
			params.put(CmReportConst.ITEM_TYPE, this.getManagedBean().getItemType());
		}

		if(this.getManagedBean().getSelectedOrgProfile()!=null &&
				this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName().trim().length()>0){
			log.info("COMP_NAME " + this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
			params.put(CmReportConst.COMP_NAME, this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}
		
		if(this.getManagedBean().getRequestType()!=null && this.getManagedBean().getRequestType().trim().length()>0){
			log.info("REQUEST_TYPE " + this.getManagedBean().getRequestType());
			params.put(CmReportConst.REQUEST_TYPE, this.getManagedBean().getRequestType());
		}
		
		if(this.getManagedBean().getRegType()!=null && this.getManagedBean().getRegType().trim().length()>0){
			log.info("REGISTRATION_TYPE " + this.getManagedBean().getRegType());
			params.put(CmReportConst.REG_TYPE, this.getManagedBean().getRegType());
		}
		
		if(this.getManagedBean().getRequestNo()!=null && this.getManagedBean().getRequestNo().trim().length()>0){
			log.info("REQUEST_NO " + this.getManagedBean().getRequestNo());
			params.put(CmReportConst.REQUEST_NO, this.getManagedBean().getRequestNo());
		}

		if(this.getManagedBean().getDocStatus()!=null && this.getManagedBean().getDocStatus().trim().length()>0){
			log.info("REQUEST_NO " + this.getManagedBean().getDocStatus());
			params.put(CmReportConst.DOCUMENT_STATUS, this.getManagedBean().getDocStatus());
		}


		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		if(this.getManagedBean().getDateFrom()!=null ){
			params.put(CmReportConst.DATE_FROM, df.format(this.getManagedBean().getDateFrom()));
		}

		if(this.getManagedBean().getDateTo()!=null ){
			params.put(CmReportConst.DATE_TO, df.format(this.getManagedBean().getDateTo()));
		}

		FacesScopes facesScopes =new FacesScopes();
		Long userId = facesScopes.getUserSessionBean().getUserId();

		params.put(CmReportConst.REPORT_ID, this.getManagedBean().getReportCode());
		params.put(CmReportConst.REPORT, this.getManagedBean().getReportName());
		params.put(CmReportConst.USER, String.valueOf(userId));


		preGenerateReportAPI(params);
		

		RequestContext.getCurrentInstance().execute("confirmationId.show()");
	}
	
	public Date getMinDate(){
		Calendar cal = Calendar.getInstance();
			cal.setTime(DateUtils.getCurrentDateWithoutTime());
			cal.add(Calendar.YEAR, -7);
		return cal.getTime();
	}
	//#29640 - End
}
