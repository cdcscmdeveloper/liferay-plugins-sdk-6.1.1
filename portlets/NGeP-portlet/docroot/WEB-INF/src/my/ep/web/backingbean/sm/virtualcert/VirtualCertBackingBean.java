package my.ep.web.backingbean.sm.virtualcert;

import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.virtualcert.IVirtualCertSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.questionnaire.QuestionnaireBackingBean;
import my.ep.web.managedbean.sm.virtualcert.VirtualCertManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmVirtualCertHelper;

import org.apache.log4j.Logger;

@ManagedBean(name = "virtualCertBackingBean")
@RequestScoped
public class VirtualCertBackingBean extends BaseBackingBean<VirtualCertManagedBean> {
	public static Logger log = Logger.getLogger(VirtualCertBackingBean.class);
	private final static String XHTML_VIRTUAL_DET = "/xhtml/sm/virtualcert/virtualCertDet.xhtml";
	private final static String XHTML_QUESTIONNAIRE = "/xhtml/sm/questionnaire/questionnaireView.xhtml";
	
	public void viewVirtualCert(){
		renewSesionObject();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		SmSupplier supp = getSmSupplier(); 
		setExpiredMofAccountMessageIfApplicable(fc, supp);
		
		try{
			SmVirtualCertHelper.getInstance().autoGenerateVirtualCert(supp.getSmAppl().getApplId());
		}catch(Exception ex){
			log.info("<<<<virtual cert - error when generate Virtual Cert>>>>" + ex.getMessage());
		}
		
		if(!supp.getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)){
			if(!supp.getSmAppl().isQuestionnaire()){
				QuestionnaireBackingBean quesBackingBean = new QuestionnaireBackingBean();
				quesBackingBean.getManagedBean().setTargetUrl(XHTML_VIRTUAL_DET);
				quesBackingBean.getManagedBean().setSmSupp(supp);
				quesBackingBean.getManagedBean().setSmAppl(supp.getSmAppl());
				quesBackingBean.retrieveQuestionnaire();	
				retrieveCertInfo(supp.getSupplierId());
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null,XHTML_QUESTIONNAIRE);
			}else{
				retrieveCertInfo(supp.getSupplierId());
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, XHTML_VIRTUAL_DET);
			}
		}else{
			retrieveCertInfo(supp.getSupplierId());
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, XHTML_VIRTUAL_DET);
		}
	}
	
	/**
	 * Set message for expired MOF account
	 * 
	 * @param FacesContext
	 *            fc
	 * @param SmSupplier
	 *            supp
	 */
	private void setExpiredMofAccountMessageIfApplicable(FacesContext fc,
			SmSupplier supp) {
		if (supp.getSmAppl() != null) {
			SmMofAccount mofAcc = SmCommonUtil
					.getMofAccountBySuppIdAndSuppType(supp.getSupplierId(),
							supp.getSmAppl().getSupplierType());
			if (mofAcc != null) {
				Date today = new Date();
				if (!(today.compareTo(mofAcc.getExpDate()) < 0)) {
					fc.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									SmCommonUtil
											.getMessage(
													"lbl.sm.alert.expired.mof.view.virtualcert",
													null)));
				}
			}
		}
	}

	/**
	 * Get Supplier Object
	 * @param mofAccountNo
	 * @return SmSupplier
	 */
	private SmSupplier getSmSupplier() {
		
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		log.info("******getSmSupplier******" + pmUser.getUserId());
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setUserId(pmUser.getUserId());
		
		ISupplierSearchSO supplierSearchSO= null;
		SmSupplier smSupp = null;
		try {
			supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
//			SupplierSearchResDTO smResDTO = supplierSearchSO.retrieveSupplierForVirtualCertification(supplierSearchReqDTO);
			SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfileForVirtualCert(supplierSearchReqDTO);
//			smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0);
			smSupp = smResDTO.getSmSupplier();
		} finally {
			supplierSearchSO = null;
		}
		
		return smSupp;
	}
	
	/**
	 * retrieve Cert Info
	 * @param supplierId
	 */
	private void retrieveCertInfo(Long supplierId){
		IVirtualCertSO virtualCertSO = (IVirtualCertSO) BusinessDelegate.getInstance().getServiceObject("sm", IVirtualCertSO.class);
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setSupplierId(supplierId);
		VirtualCertResDTO resDTO = virtualCertSO.retrieveMOFCertBySupplierId(reqDTO);
		this.getManagedBean().setMofCertLs(new ArrayList<SmMofCert>());
		if(resDTO.getSmMofCerts().size() > 0){
			this.getManagedBean().setMofCertLs(resDTO.getSmMofCerts());
		}
	}

}

