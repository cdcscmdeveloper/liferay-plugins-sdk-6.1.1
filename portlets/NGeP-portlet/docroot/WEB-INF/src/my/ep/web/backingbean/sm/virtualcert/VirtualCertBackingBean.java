package my.ep.web.backingbean.sm.virtualcert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.virtualcert.IVirtualCertSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.questionnaire.QuestionnaireReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.questionnaire.QuestionnaireBackingBean;
import my.ep.web.managedbean.sm.virtualcert.VirtualCertManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.SmVirtualCertHelper;
//#28175 start
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.constant.ModuleCodeConst;
//#28175 end

import org.apache.log4j.Logger;

@ManagedBean(name = "virtualCertBackingBean")
@RequestScoped
public class VirtualCertBackingBean extends BaseBackingBean<VirtualCertManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(VirtualCertBackingBean.class);
	private final static String XHTML_VIRTUAL_DET = "/xhtml/sm/virtualcert/virtualCertDet.xhtml";
	private final static String XHTML_QUESTIONNAIRE = "/xhtml/sm/questionnaire/questionnaireView.xhtml";
	
	public void viewVirtualCert(){
		long startTime = System.currentTimeMillis();
		renewSesionObject();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		SmSupplier supp = getSmSupplier();
		supp = updateMofCertPendingCertGeneration(supp);
		setExpiredMofAccountMessageIfApplicable(fc, supp);
		
		try{
			SmVirtualCertHelper.getInstance().autoGenerateVirtualCertBySupplier(supp.getSupplierId());
		}catch(Exception ex){
			log.info("<<<<virtual cert - error when generate Virtual Cert>>>>" + ex.getMessage());
		}
		
		if(!supp.getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)){
			if(!supp.getSmAppl().isQuestionnaire()){
				QuestionnaireReqDTO reqDTO = new QuestionnaireReqDTO();
				reqDTO.setSmAppl(supp.getSmAppl());
				SmServiceLookup.getQuestionnaireSO().deleteSupplierAnswerByApplId(reqDTO);
				QuestionnaireBackingBean quesBackingBean = new QuestionnaireBackingBean();
				quesBackingBean.getManagedBean().setTargetUrl(XHTML_VIRTUAL_DET);
				quesBackingBean.getManagedBean().setSmSupp(supp);
				quesBackingBean.getManagedBean().setSmAppl(supp.getSmAppl());
				quesBackingBean.retrieveQuestionnaire();	
				retrieveCertInfo(supp.getSupplierId());
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null,XHTML_QUESTIONNAIRE);
			}else{
				retrieveCertInfo(supp.getSupplierId());
				SLAUtils.getInstance().logSLAInfo("SM","SR",
						"VirtualCertBackingBean.viewVirtualCert", startTime);
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, XHTML_VIRTUAL_DET);
			}
		}else{
			retrieveCertInfo(supp.getSupplierId());
			SLAUtils.getInstance().logSLAInfo("SM","SR",
					"VirtualCertBackingBean.viewVirtualCert", startTime);
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
				//Date today = new Date();
				/*if (!(today.compareTo(mofAcc.getExpDate()) < 0)) {
					fc.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									SmCommonUtil
											.getMessage(
													"lbl.sm.alert.expired.mof.view.virtualcert",
													null)));
				}*/
			}
		}
	}
	
	/**
	 * Update migrated supplier sm_mof_cert with pending mof cert generation
	 * 
	 * @param SmSupplier
	 *            supplier
	 * @return SmSupplier
	 */
	private SmSupplier updateMofCertPendingCertGeneration(SmSupplier supplier) {
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setSupplierId(supplier.getSupplierId());

		VirtualCertResDTO resDTO = new VirtualCertResDTO();
		resDTO = SmServiceLookup.getVirtualCertSO()
				.updateMofCertForPendingCertGeneration(reqDTO);
		if (resDTO.getSupplier() != null) {
			supplier = resDTO.getSupplier();
		}
		return supplier;
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
		if(new FacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value())){ 
			log.info("****Role is Supplier temp*****");
			supplierSearchReqDTO.setAllStatuses(true);
		}
		
		
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
	private void retrieveCertInfo(Long supplierId) {
		IVirtualCertSO virtualCertSO = (IVirtualCertSO) BusinessDelegate.getInstance().getServiceObject("sm",
				IVirtualCertSO.class);
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setSupplierId(supplierId);
		VirtualCertResDTO resDTO = virtualCertSO.retrieveMOFCertBySupplierId(reqDTO);
		this.getManagedBean().setMofCertLs(new ArrayList<SmMofCert>());
		if (resDTO.getSmMofCerts().size() > 0) {
			// #36401 - Start
			if (countCerts(resDTO.getSmMofCerts())) {
				this.getManagedBean().setMofCertLs(getLatestSmMofCertsIfDuplicate(resDTO.getSmMofCerts()));
			} else {
				this.getManagedBean().setMofCertLs(resDTO.getSmMofCerts());
			}
			// #36401 - End
		}
	}

	// #36401 - Start
	private boolean countCerts(List<SmMofCert> certs) {
		int countCert = 0;
		int countBumi = 0;
		for (SmMofCert cert : certs) {
			if (!cert.isBumiCert()) {
				countCert++;
			}
			if (cert.isBumiCert()) {
				countBumi++;
			}
		}
		return countCert > 1 || countBumi > 1;
	}

	// If there is duplicate cert on same applId, prevention step, To make sure display latest smMofcert only by applId
	private List<SmMofCert> getLatestSmMofCertsIfDuplicate(List<SmMofCert> mofCerts) {
		List<SmMofCert> cert = new ArrayList<>();
		List<SmMofCert> bumiCert = new ArrayList<>();

		log.info("#36401 :: Size of cert >>>>>>>> " + cert.size());
		log.info("#36401 :: Size of bCert >>>>>>>> " + bumiCert.size());

		for (SmMofCert mofCert : mofCerts) {
			if (!mofCert.isBumiCert()) {
				cert.add(mofCert);
			}

			if (mofCert.isBumiCert()) {
				bumiCert.add(mofCert);
			}
		}

		log.info("#36401 :: Size of cert >>>>>>>> " + cert.size());
		log.info("#36401 :: Size of bCert >>>>>>>> " + bumiCert.size());

		// if more than 1 Cert on a same appl_id, need reorder it and means there is duplicate, sort cert by asc order
		if (cert.size() > 1) {
			sortCertsListByAsc(cert);
		}

		// if more than 1 bumiCert on a same appl_id, need reorder it and means there is duplicate, Sort bumiCert by asc order
		if (bumiCert.size() > 1) {
			sortCertsListByAsc(bumiCert);
		}

		if (cert.size() > 1 || bumiCert.size() > 1) {
			// clear actual list
			mofCerts.clear();

			if (!cert.isEmpty()) {
				mofCerts.add(cert.get(0));
			}

			if (!bumiCert.isEmpty()) {
				mofCerts.add(bumiCert.get(0));
			}
		}

		return mofCerts;
	}

	/**
	 * To sort list of smMofCert by asc
	 * 
	 * @param smMofCert
	 */
	public void sortCertsListByAsc(List<SmMofCert> smMofCert) {
		Collections.sort(smMofCert, new Comparator<SmMofCert>() {
			@Override
			public int compare(SmMofCert cert1, SmMofCert cert2) {
				return cert1.getCreatedDate().compareTo(cert2.getCreatedDate());
			}
		});
	}
	// #36401 - End
	
	/**
	 * Print Virtual Cert
	 * @param cert
	 * @throws IOException
	 */
	public void printVirtualCert(SmMofCert cert) throws IOException{
		//#28175
		if(!checkCatCodeList(cert.getSmAppl())){
			cert.setFileName(null);
			cert.setFilePath(null);
		}
		//#28175 end
		if(cert.getFileName() != null || cert.getFilePath() != null){
			String file = cert.getFilePath() + "/" + cert.getFileName();
			commonDownload(file,cert.getFileName());
		}else{
			SmMofCert mofCert = SmVirtualCertHelper.getInstance().genReport(
					SmVirtualCertHelper.getInstance().getReportType(cert.getBumiCertFlg()), 
					SmVirtualCertHelper.getInstance().getReportParam(cert), cert, false);
			String newfile = mofCert.getFilePath() + "/" + cert.getFileName();
			commonDownload(newfile,mofCert.getFileName());
		}
	}

	/**
	 * get Supplier Application Type description
	 * @param applType
	 * @return String
	 */
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
	//#28175 start
	/**
	 * Checks if the category codes in the supplier application match the
	 * category codes in the profile management.
	 * 
	 * @param appl
	 *            the supplier application to check
	 * @return true if the category codes match, false otherwise
	 */
	public boolean checkCatCodeList(SmAppl appl) {
		boolean check = true;

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
		SupplierSearchReqDTO supReqDTO = new SupplierSearchReqDTO();
		SupplierSearchResDTO supResDTO = new SupplierSearchResDTO();
		supReqDTO.setApplId(appl.getApplId());
		supResDTO = supplierSearchSO.retrieveSupplierActiveCategoryCodeByApplId(supReqDTO);
		CategoryCodeReqDTO catReqDTO = new CategoryCodeReqDTO();
		CategoryCodeResDTO catResDTO = new CategoryCodeResDTO();
		// Max loop 30
		for (SmSupplierCategory catCode : supResDTO.getSmSupplierCategoryList()) {
			catReqDTO.setCategoryL1Id(catCode.getPmCategoryL1().getCategoryL1Id());
			catResDTO = categoryCodeSO.retrieveCategoryL1DtlById(catReqDTO);
			if (!catCode.getCategoryL1Name().equalsIgnoreCase(catResDTO.getCategoryL1().getCategoryName())) {
				check = false;
				break;
			} else {
				catReqDTO.setCategoryL2Id(catCode.getPmCategoryL2().getCategoryL2Id());
				catResDTO = categoryCodeSO.retrieveCategoryL2DtlById(catReqDTO);
				if (!catCode.getCategoryL2Name().equalsIgnoreCase(catResDTO.getCategoryL2().getCategoryName())) {
					check = false;
					break;
				} else {
					catReqDTO.setCategoryL3Id(catCode.getPmCategoryL3().getCategoryL3Id());
					catResDTO = categoryCodeSO.retrieveCategoryL3DtlById(catReqDTO);
					if (!catCode.getCategoryL3Name().equalsIgnoreCase(catResDTO.getCategoryL3().getCategoryName())) {
						check = false;
						break;
					}
				}
			}
		}
		return check;
	}
	//#28175 end
}

