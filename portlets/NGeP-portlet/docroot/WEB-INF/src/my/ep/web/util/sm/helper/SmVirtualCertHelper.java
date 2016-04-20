package my.ep.web.util.sm.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.virtualcert.IVirtualCertSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;


public class SmVirtualCertHelper {
	private static SmVirtualCertHelper instance = null;
	private static Logger log = EpLogger.getLogger(SmVirtualCertHelper.class);
	private final static String VIRTUAL_CERT_PATH = "SM/VIRTUAL_CERT";
	private final static String CERT_EXT = ".pdf";
	private final static String HTTP_HEADER_TYPE_PDF = "application/pdf";
	
	public SmVirtualCertHelper(){
		
	}

	public static SmVirtualCertHelper getInstance() {
		if (instance == null) {
			instance = new SmVirtualCertHelper();
		}
		return instance;
	}
	
	public void autoGenerateVirtualCert(Long applId) throws Exception{
		log.info("******autoGenerateVirtualCert*****" + applId);
		//SmSupplier supp = getSmSupplier();
		List<SmMofCert> mCertLs = retrieveCurrentMofCert(applId);
		
		if(mCertLs.size() > 0){
			for(SmMofCert mCert : mCertLs){
				log.info("****mCertLs****" + mCertLs.size());
				// if both is null, generate the pdf and save the file name and path
				if(mCert != null){
					log.info("****MCert****" + mCert);
					if(mCert.getFileName() == null || mCert.getFilePath() == null){
						log.info("****MCert id****" + mCert.getMofCertId());
						log.info("****MCert bumi****" + mCert.isBumiCert());
						genReport(getReportType(mCert.isBumiCert()), getReportParam(mCert), mCert);
					}
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
			urlConnection.setUseCaches(false);
			
			if(urlConnection.getContentType().equalsIgnoreCase(HTTP_HEADER_TYPE_PDF)){
				BufferedInputStream buf = new BufferedInputStream(urlConnection.getInputStream());
				//FileInputStream buf = new FileInputStream("E:/HKS/Mof_1_1402497715752.pdf");

				log.info("Content Type>>>>" + urlConnection.getContentType());
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
			}else{
				log.info("****genReport****" + urlConnection.getContentType());
				return null;
			}
			//commonDownload(finalFile, fileName);
		}catch(Exception ex){
			log.info("******genReport******" + cert.getMofCertId() + " - " + ex.getMessage());
		}
		
		return null;
	}
	
	
	private List<SmMofCert> retrieveCurrentMofCert(Long applId){
		IVirtualCertSO reqSO = SmServiceLookup.getVirtualCertSO();
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setApplId(applId);
		
		VirtualCertResDTO resDTO = reqSO.retrieveMofCertsByApplId(reqDTO);
		
		return resDTO.getSmMofCerts();
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
	
	private SmMofCert updateMOFCertFilePath(SmMofCert mofCert){
		IVirtualCertSO virtualCertSO = SmServiceLookup.getVirtualCertSO();
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setMofCert(mofCert);
		
		VirtualCertResDTO resDTO = virtualCertSO.updateMOFCert(reqDTO);
		
		return resDTO.getSmMofCert();
	}
	
	private String generateReportURL(String reportFile,
			Map<String, String> paramMap) {
		EpPropertiesUtils propertiesUtils = EpPropertiesUtils.getInstance();
		String excPrevUrl = propertiesUtils
				.getProperty(EpPropertiesConst.EXC_PREV_URL);
		String excNextUrl = propertiesUtils
				.getProperty(EpPropertiesConst.EXC_NEXT_URL);

		StringBuilder reportUrl = new StringBuilder(excPrevUrl);
		reportUrl.append(ReportConst.PARAM_SEPARATOR);
		reportUrl.append(ReportConst.PARAM_KEY_REPORT);
		reportUrl.append(ReportConst.VALUE_SEPARATOR);
		reportUrl.append(reportFile);

		for (Entry<String, String> params : paramMap.entrySet()) {
			reportUrl.append(ReportConst.PARAM_SEPARATOR);
			reportUrl.append(params.getKey());
			reportUrl.append(ReportConst.VALUE_SEPARATOR);
			reportUrl.append(params.getValue());
		}
		reportUrl.append(excNextUrl);

		if (log.isDebugEnabled()) {
			log.debug("reportUrl: " + reportUrl);
		}
		return reportUrl.toString();
	}
}
