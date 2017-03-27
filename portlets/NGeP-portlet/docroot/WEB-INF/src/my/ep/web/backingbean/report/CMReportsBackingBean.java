/*
 * 
 */
package my.ep.web.backingbean.report;

import java.util.Date;
import java.util.HashMap;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.MessageConst;
import my.ep.constant.rpt.ReportConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.constant.cm.CmConst;
import my.ep.web.constant.cm.CmReportConst;
import my.ep.web.managedbean.report.CMReportManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "cmReportsBackingBean")
@ViewScoped
public class CMReportsBackingBean extends ReportBaseBackingBean<CMReportManagedBean> {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger.getLogger(ReportBaseBackingBean.class);

	public void preRenderCmReport() {
		log.info(".................preRenderCmReport................");


		retrieveReportListByAuthorization(CmConst.CM_ModuleCode);

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "cmReportMain");
	}


	public void generateReportCM002(){

		HashMap<String,String> params = new HashMap<String,String>();

		System.out.println("this.getManagedBean().getDateFrom()"+this.getManagedBean().getDateFrom());
		System.out.println("this.getManagedBean().getDateTo()"+this.getManagedBean().getDateTo());
		dateValidation(this.getManagedBean().getDateFrom(), this.getManagedBean().getDateTo());



		if(this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode()!=null 
				&& this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode().trim().length()>0){
			params.put(CmReportConst.UNSPSC_CODE, this.getManagedBean().getSelectedCmUNSPSC().getUnspscCode());
		}
		
		
		

		getPopUpMsg("1234");

		RequestContext.getCurrentInstance().execute("confirmationId.show()");


	}

	public void resetReportCM002(){

		this.getManagedBean().setDateFrom(null);
		this.getManagedBean().setDateTo(null);
		this.getManagedBean().setSelectedCmUNSPSC(null);
		this.getManagedBean().setSelectedOrgProfile(null);
		this.getManagedBean().setItemType("");
		this.getManagedBean().setMofNo("");
		this.getManagedBean().setRegType("");
		this.getManagedBean().setIcNo("");
		this.getManagedBean().setGroup("");
		this.getManagedBean().setBusRegNo("");
		this.getManagedBean().setEpNo("");

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





}
