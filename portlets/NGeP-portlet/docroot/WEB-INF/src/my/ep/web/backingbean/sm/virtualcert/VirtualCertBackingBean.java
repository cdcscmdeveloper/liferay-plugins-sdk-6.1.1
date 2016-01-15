package my.ep.web.backingbean.sm.virtualcert;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.virtualcert.IVirtualCertSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.questionnaire.QuestionnaireBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sm.virtualcert.VirtualCertManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@ManagedBean(name = "virtualCertBackingBean")
@RequestScoped
public class VirtualCertBackingBean extends BaseBackingBean<VirtualCertManagedBean> {
	public static Logger log = Logger.getLogger(VirtualCertBackingBean.class);
	private final static String VIRTUAL_CERT_PATH = "SM/VIRTUAL_CERT";
	private final static String CERT_EXT = ".pdf";
	
	public void viewVirtualCert(){
		FacesContext fc = FacesContext.getCurrentInstance();
		SmSupplier supp = getSmSupplier(null);
		String xhtml = "/xhtml/sm/virtualcert/virtualCertDet.xhtml";
		setExpiredMofAccountMessageIfApplicable(fc, supp);

		log.info("viewVirtualCert appl ID>>>>" + supp.getSmAppl().getApplId());
		log.info("isQuestionnaire>>>>" + supp.getSmAppl().isQuestionnaire());
		log.info("Appl Type>>>>" + supp.getSmAppl().getApplType());
		
		SmMofCert mCert = retrieveCurrentMofCert(supp.getSmAppl().getApplId());
		
		// if both is null, generate the pdf and save the file name and path
		if(mCert != null){
			log.info("****MCert****" + mCert);
			if(mCert.getFileName() == null || mCert.getFilePath() == null){
				log.info("****MCert bumi****" + mCert.isBumiCert());
				genReport(getReportType(mCert.isBumiCert()), getReportParam(mCert), mCert);

			}
		}
		
		
		if(!supp.getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)){
			if(!supp.getSmAppl().isQuestionnaire()){
				QuestionnaireBackingBean quesBackingBean = new QuestionnaireBackingBean();
				quesBackingBean.getManagedBean().setTargetUrl(xhtml);
				quesBackingBean.getManagedBean().setSmSupp(supp);
				quesBackingBean.getManagedBean().setSmAppl(supp.getSmAppl());
				quesBackingBean.retrieveQuestionnaire();	
				retrieveCertInfo(supp.getSupplierId());
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/questionnaire/questionnaireView.xhtml");
			}else{
				retrieveCertInfo(supp.getSupplierId());
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, xhtml);
			}
		}else{
			retrieveCertInfo(supp.getSupplierId());
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, xhtml);
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
	private SmSupplier getSmSupplier(String mofAccountNo) {
		
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		log.info("******getSmSupplier******" + pmUser.getUserId());
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setUserId(pmUser.getUserId());
		
		if(mofAccountNo != null) {
			supplierSearchReqDTO.setMofAccountNo(mofAccountNo);	
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
	
	/**
	 * Print Virtual Cert
	 * @param cert
	 * @throws IOException
	 */
	public void printVirtualCert(SmMofCert cert) throws IOException{
		if(cert.getFileName() != null || cert.getFilePath() != null){
			String file = cert.getFilePath() + "/" + cert.getFileName();
			commonDownload(file,cert.getFileName());
		}else{
			SmMofCert mofCert = genReport(getReportType(cert.isBumiCert()), getReportParam(cert), cert);
			String newfile = mofCert.getFilePath() + "/" + cert.getFileName();
			commonDownload(newfile,mofCert.getFileName());
		}
	}
	
	/**
	 * Generate Report
	 * @param reportFile
	 * @param paramMap
	 * @param cert
	 * @return SmMofCert
	 */
	public SmMofCert genReport(String reportFile,Map<String, String> paramMap, SmMofCert cert) {
		try{
			String reportUrl = generateReportURL(reportFile, paramMap);
			log.info("reportUrl>>>>>>>" + reportUrl);

			HttpURLConnection urlConnection = null;
			URL obj = new URL(reportUrl);
			urlConnection = (HttpURLConnection) obj.openConnection();

			BufferedInputStream buf = new BufferedInputStream(urlConnection.getInputStream());
			//FileInputStream buf = new FileInputStream("E:/HKS/Mof_1_1402497715752.pdf");

			String path = Calendar.getInstance().get(Calendar.YEAR)  + "/" + VIRTUAL_CERT_PATH;  
			String fileName = String.valueOf(cert.getMofCertId()) + CERT_EXT;
			String filePath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
			filePath += "/" + path;

			File dir = new File(filePath);
			log.info(" dir.exists()" + dir.exists());
			if (!dir.exists()) {
				log.info("before make directory");
				dir.mkdirs();
				log.info("after make directory");
			}

			String finalFile = filePath + "/" + fileName;
			OutputStream out =  new FileOutputStream(new File(finalFile));

			byte[] b = new byte[128];
			int size;
			while ((size = buf.read(b, 0, b.length)) != -1) {
				out.write(b, 0, size);
			}
			buf.close();
			out.close();

			//update MOF Cert with file path & name
			cert.setFilePath(path);
			cert.setFileName(fileName);
			SmMofCert mofcert = updateMOFCertFilePath(cert);


			return mofcert;
			//commonDownload(finalFile, fileName);
		}catch(Exception ex){
			log.info("******genReport******" + cert.getMofCertId() + " - " + ex.getMessage());
		}
		
		return null;
	}
	
	public SmMofCert updateMOFCertFilePath(SmMofCert mofCert){
		IVirtualCertSO virtualCertSO = SmServiceLookup.getVirtualCertSO();
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setMofCert(mofCert);
		
		VirtualCertResDTO resDTO = virtualCertSO.updateMOFCert(reqDTO);
		
		return resDTO.getSmMofCert();
	}
	
	public SmMofCert retrieveCurrentMofCert(Long applId){
		IVirtualCertSO reqSO = SmServiceLookup.getVirtualCertSO();
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setApplId(applId);
		
		VirtualCertResDTO resDTO = reqSO.retrieveMofCertByApplId(reqDTO);
		
		return resDTO.getSmMofCert();
	} 
	
	public String getReportType(boolean isBumi){
		if(isBumi){
			return ReportConst.SM_VIRTUAL_VERIFICATION_BUMI_jsp;
		}else{
			return ReportConst.SM_VIRTUAL_VERIFICATION_jsp;
		}
	}
	
	public Map<String, String> getReportParam(SmMofCert cert){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("PAPPLID", cert.getSmAppl().getApplId().toString());
		
		return paramMap;
	}
}

