
package my.ep.web.backingbean.report;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestDtlSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.ct.ContractConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.persistence.entity.rpt.RPTRequest;
import my.ep.persistence.entity.rpt.RPTRequestDtl;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import java.io.FileNotFoundException;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.RPTRequestManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

import my.ep.web.constant.CommonUIConst;

@ManagedBean(name = "rptRequestBackingBean")
@ViewScoped
public class RPTRequestBackingBean extends BaseBackingBean<RPTRequestManagedBean> {
	
	private static final Logger logger = EpLogger
			.getLogger(RPTRequestBackingBean.class);

	private IPagingDataModel<RPTRequest> rptRequestDataModel = new RPTRequestDataModel();	

	public RPTRequestBackingBean() {
		this.getManagedBean().setDeleteButtonHide(true);
		this.getLatestStatusCodeForRequests();
	}
	
	private String strLanguage = ResourceBundleUtils.getMessage(
			ReportConst.LABEL_LANG, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
			new FacesScopes().getUserLocale());
	
	@PostConstruct
	public void init(){
		this.getLatestStatusCodeForRequests();
	}

	public void retrieveRptRequestList() {
		
		debugLogger("retrieveRptRequestList");
		
		rptRequestDataModel.resetData();
		rptRequestDataModel.reloadData();
		
		resetSelectedRPTRequest();
	}

	public void removeRPTRequests() {
		
		debugLogger("removeRPTRequest");
		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		
		if (this.getManagedBean().getSelectedRPTRequests() != null) {
			
			debugLogger("getSelectedRPTRequests length["
					+ this.getManagedBean().getSelectedRPTRequests().length + "]");
			
			IRPTRequestSO reqSO = (IRPTRequestSO) BusinessDelegate.getInstance()
					.getServiceObject("rpt", IRPTRequestSO.class);
			
			RPTRequest[] rptRequestArr = this.getManagedBean().getSelectedRPTRequests();
			
			//reload the same object from db
			
			long[] rptRequetsIds = new long[rptRequestArr.length];
			
			for(int i = 0; i<rptRequestArr.length; i++){
				//pmNotifyUserIds[i] = rptRequestArr[i].getNotifyUserId();
			}

			//rptRequestArr = reqSO.getPmReportList(pmNotifyUserIds);

			//end
			
			if(rptRequestArr.length > 0){
				
				this.getManagedBean().setSelectedRPTRequests(rptRequestArr);

				for (RPTRequest rptRequest : this.getManagedBean()
						.getSelectedRPTRequests()) {
					reqDTO.getRPTRequestList().add(rptRequest);
				}
				
				RPTRequestResDTO resDTO = reqSO.removeRPTRequestList(reqDTO);
				
				debugLogger("RPTRequestResDTO isSuccessful[" + resDTO.isSuccessful()
						+ "]");
				
				if (resDTO.isSuccessful()) {
					resetSelectedRPTRequest();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									ResourceBundleUtils.getMessage("lbl.portal.notify.recordRemovedSuccesssfully",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()), ""));
					rptRequestDataModel.reloadData();
				}
				
			} else {
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								ResourceBundleUtils.getMessage("lbl.portal.notify.selectAtLeastARecord",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()), ""));
				rptRequestDataModel.reloadData();
			}
		}
		
		resetSelectedRPTRequest();
	}

	public void selectedRPTRequest() {
		
		debugLogger("=== selectedNotify ===");
		
		RPTRequest rptRequest = this.getManagedBean().getSelectedRPTRequest();
		
	}
	
	public void rowSelectUnselectCheckbox(){
		
		if (this.getManagedBean().getSelectedRPTRequests() != null) {
			
			RPTRequest[] rptRequestArr = this.getManagedBean().getSelectedRPTRequests();
			
			if(rptRequestArr != null && rptRequestArr.length > 0){

				this.getManagedBean().setDeleteButtonHide(false);
			
			} else {
			
				this.getManagedBean().setDeleteButtonHide(true);
			}
			
		}
	}

	/**
	 * @author Jimmy
	 */

	private class RPTRequestDataModel extends PagingDataModel<RPTRequest> {
		
		public RPTRequestDataModel() {
			super("reportTable");
		}

		@Override
		public ISearchResultBean<RPTRequest> pageChangeListener(
				ISearchBean<RPTRequest> searchBean) {
			
			if (logger.isDebugEnabled()) {
				logger.debug("reportTable's pageChangeListener");
			}

			IRPTRequestSO reportSO;
			
			try {
			
				FacesScopes facesScopes = new FacesScopes();
				PmUser user = facesScopes.getUserSessionBean();

				if (user == null) {
					logger.error("User is empty.");
				} else {
					reportSO = (IRPTRequestSO) BusinessDelegate.getInstance()
							.getServiceObject("rpt",
									IRPTRequestSO.class);

					// Filter By - status
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("recordStatus",
									SearchOperator.OPERATOR_EQUALS.value(), 1));
					
					// Filter By - recipient Id
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("createdBy",
									SearchOperator.OPERATOR_EQUALS.value(),
									user.getUserId()));

					// Order by created date.
					searchBean.getSortColumns().add(
							new SearchOrderBean("createdDate",
									false));
						
					RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
					reqDTO.setSearchBean(searchBean);
					RPTRequestResDTO resDTO = reportSO.searchRPTRequestList(reqDTO);
					
					ISearchResultBean<RPTRequest> searchResultBean= resDTO.getSearchResultBean();
					List<RPTRequest> rptRequestList = searchResultBean.getResultList();
					
					for(int i = 0; i<rptRequestList.size(); i++){
						
						RPTRequest rptRequest = rptRequestList.get(i);
						logger.info("RPTRequestDataModel("+i+")= " +rptRequest.getStatusCode());
						if(isInProgress(rptRequest.getStatusCode())){
							RPTRequestBackingBean rptRequestBackingBean = new RPTRequestBackingBean();
							rptRequestBackingBean.update(rptRequest);	
						}
					}
					
					if (resDTO.isSuccessful()) {
						return resDTO.getSearchResultBean();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				reportSO = null;
			}

			return null;
		}
	}

	/**
	 * @return the rptRequestDataModel
	 */
	
	public IPagingDataModel<RPTRequest> getRptRequestDataModel() {
		return rptRequestDataModel;
	}

	/**
	 * @param setrptRequestDataModel
	 *            the specMasterDataModel to set
	 */
	
	public void setrptRequestDataModel(
			IPagingDataModel<RPTRequest> specMasterDataModel) {
		this.rptRequestDataModel = specMasterDataModel;
	}
	
	private void resetSelectedRPTRequest() {
		getManagedBean().setSelectedRPTRequests(null);
	}

	private void debugLogger(Object message) {
		
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		
		logger.info(message);
	}
	
	public void update(RPTRequest rptRequest){
		
		IRPTRequestSO reqSO = (IRPTRequestSO) BusinessDelegate.getInstance()
				.getServiceObject("rpt", IRPTRequestSO.class);
		
		PmUser user = new FacesScopes().getUserSessionBean();
		rptRequest.setChangedBy(user.getUserId());
		new LogKey().setUserId(user.getUserId());
		
		reqSO.updateStatusCode(rptRequest);
		
		rptRequestDataModel.reloadData();
	}
	
	public void preRender(){
		
		this.getLatestStatusCodeForRequests();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "rptrequestcontent");
	}
	
	
	public void getLatestStatusCodeForRequests(){
		
		rptRequestDataModel.resetData();
		rptRequestDataModel.reloadData();
	}
	
	public void generateReport(RPTRequest rptRequest){
		
		String reportCode = rptRequest.getReportCode();
		
		IPMReportSO reqSO = (IPMReportSO) BusinessDelegate.getInstance()
				.getServiceObject("rpt", IPMReportSO.class);
		
		PMReportReqDTO reqDTO = new PMReportReqDTO();
		reqDTO.setReportCode(reportCode);
		PMReportResDTO resDTO = reqSO.retriveReportByrptId(reqDTO);
		
		
		HttpURLConnection urlConnection = null;
		URL obj;
		BufferedInputStream buf;
		
		String reportServerName = rptRequest.getReportServer(); 
		
		String gnrtdURL = "http://ngepdevrps01.ngep.com:5901/reports/rwservlet/getjobid" + rptRequest.getJobId() + "?server=" + reportServerName;
		
		try {
			
			obj = new URL(gnrtdURL);
			urlConnection = (HttpURLConnection) obj.openConnection();
			buf = new BufferedInputStream(urlConnection.getInputStream());
			
			PortletResponse portletResponse = (PortletResponse) FacesContext
					.getCurrentInstance().getExternalContext().getResponse();
			HttpServletResponse res = PortalUtil
					.getHttpServletResponse(portletResponse);
			writeReportDataToPDFOrExcel(reportCode, resDTO.getPmReportDtls().getReportTitle(), DateUtils.getCurrentDate(), buf, res);
		     
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			urlConnection.disconnect();
		}
	}

	private void writeReportDataToPDFOrExcel(String reportCode, String reportTitle,
			Timestamp time, BufferedInputStream buf, HttpServletResponse res)
			throws Exception{
		
		if (reportCode.equals(ReportConst.SM_LIST_OF_SOFTCERT_PAYMENT_RPT_VAL) || 
			reportCode.equals(ReportConst.SM_REJECTED_SOFTCERT_RPT_VAL) ) {
			
			res.setHeader(ReportConst.CONTENT_DISPOSITION,
					ReportConst.ATTACHMENTNFILENAMEEQUIFON + reportTitle
							+ ReportConst.UNDERSCORE
							+ ReportConst.REPORT_DATE_TIME.format(new Date())
							+ ReportConst.DOTXLSEIFON);//
			res.setHeader(ReportConst.CONTENT_TRANSFER_ENCODING,
					ReportConst.BINARY);
			res.setContentType(ReportConst.APPLICATIONEIFONXLS);
		} else {
			res.setHeader(ReportConst.CONTENT_DISPOSITION,
					ReportConst.ATTACHMENTNFILENAMEEQUIFON + reportTitle
							+ ReportConst.UNDERSCORE
							+ ReportConst.REPORT_DATE_TIME.format(new Date())
							+ ReportConst.DOTPDFEIFON);//
			res.setHeader(ReportConst.CONTENT_TRANSFER_ENCODING,
					ReportConst.BINARY);
			res.setContentType(ReportConst.APPLICATIONEIFONPDF);
		}
		
		res.flushBuffer();
		
		OutputStream out = res.getOutputStream();

		byte[] b = new byte[4096];
		
		int size;
		
		while ((size = buf.read(b, 0, b.length)) != -1) {
			out.write(b, 0, size);
		}
		
		buf.close();
		out.close();
	}
		
	public String getTrimmedStatusCode(String reportStatusStr){
		
		int reportStatus = Integer.parseInt(reportStatusStr);
		
		if(reportStatus == 1 || reportStatus == 2 || reportStatus == 3
		   || reportStatus == 9 || reportStatus == 10){
			return ResourceBundleUtils.getMessage(
					"lbl.rpt.inprogress", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (reportStatus == 4){
			return ResourceBundleUtils.getMessage(
					"lbl.rpt.success", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else {
			return ResourceBundleUtils.getMessage(
					"lbl.rpt.fail", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		}
	}
	
	public String getReportName(String reportCode){
		
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		
		PMReportReqDTO reqDTO = new PMReportReqDTO();
		reqDTO.setReportCode(reportCode);
		
		PMReportResDTO resDTO = reportSO.retriveReportByrptId(reqDTO);
		PMReport pmReport = resDTO.getPmReportDtls();
		
		//#32926 - START
		logger.info("retriveSubModuleNameByReportCode - #32926 start");
		long startTime = System.currentTimeMillis();
		
		String submodule = reportSO.retriveSubModuleNameByReportCode(strLanguage, reportCode);
		
		SLAUtils.getInstance().logSQLPerformanceInfo("PM","QueueGenerateReports","PMReportSO.retriveSubModuleNameByReportCode", startTime,"32926");
		logger.info("retriveSubModuleNameByReportCode - #32926 end");
		//#32926 - END
		
		String reportTitleInEnglish = pmReport.getReportTitle();
		String reportTitleInMalay = pmReport.getReportTitleMalay();
		
		String reportTitle = "";
		
		if(strLanguage.equals("en")){
			reportTitle = pmReport.getReportTitle();
		} else if(strLanguage.equals("ms")){
			reportTitle = pmReport.getReportTitleMalay();
		}
			
		if(submodule != null && !submodule.isEmpty()){
		
			System.out.println("1");
			
			StringBuilder str = new StringBuilder();
			str.append(submodule);
			str.append(" / ");
			str.append(reportTitle);
		
			return str.toString();
			
		} else {
			return reportTitle;
		}
	}
	
	public String getReportModule(String reportCode){
		
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		
		return reportSO.retriveModuleNameByReportCode(strLanguage, reportCode);
		
	}

	public boolean isSuccess(String reportStatus){
		//logger.info("isSuccess : "+reportStatus);
		if(reportStatus != null && reportStatus.equals("4")){
			return true;
		}
		
		return false;
	}
	
	public boolean isInProgress(String reportStatus){
		
		if(reportStatus != null){
			if(reportStatus.equals("1") ||
			   reportStatus.equals("2") ||
			   reportStatus.equals("3") ||
			   reportStatus.equals("9") ||
			   reportStatus.equals("10")){
					return true;
			}
		}
		
		return false;
	}
	
	public String getIconPath(String reportCode){
		
		if (reportCode.equals(ReportConst.SM_LIST_OF_SOFTCERT_PAYMENT_RPT_VAL) || 
				reportCode.equals(ReportConst.SM_REJECTED_SOFTCERT_RPT_VAL) ) {
			return CommonUIConst.IMAGE_PATH + "/ico-excel.png";
		} else {
			return CommonUIConst.IMAGE_PATH + "/ico-pdf.png";
		}
	}
		
	public void attachmentDownload(RPTRequest rptRequest) throws IOException{
		logger.info("attachmentDownload");
		String dir = EpPropertiesUtils.getInstance().getProperty("document.repository.shared.report.dir");
		
		String path = dir + "/" + rptRequest.getReportPath();
		
		String fileType = FilenameUtils.getExtension(path);
		
		String displayName = this.getReportName(rptRequest.getReportCode());
		System.out.print("attachmentDownload------"+new File(dir).getAbsolutePath());
		System.out.print("attachmentDownload------"+displayName);
		logger.info(new File(dir).getAbsolutePath());
		logger.info(displayName);
		logger.info("User id =="+rptRequest.getCreatedBy());
		if(rptRequest.getCreatedBy() == 21018 || rptRequest.getCreatedBy() == 435488){
			logger.info("DOwnload as zip");
			this.commonDownloadUtilTemp(path, displayName + "." + fileType);
			//this.downloadWithProgress();
			
		}else{
			logger.info("Normal download");
			this.commonDownloadUtil(path, displayName + "." + fileType);
		}
		
		
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);
		
		rptRequest.setDownloaded(true);
		rptRequestSO.updateDownloadedFlag(rptRequest);
		
	}
	
	//#14076 start
	public String getDocumentsNo(BigDecimal requestId){

		StringBuilder documentsNo = new StringBuilder(100);
		IRPTRequestDtlSO iRPTRequestDtlSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,IRPTRequestDtlSO.class);
		RPTRequestDtl rdtl = iRPTRequestDtlSO.findbyRequestId(requestId);
		
		if(rdtl != null){
			if(rdtl.getDocumentNo01() != null){	documentsNo.append(rdtl.getDocumentNo01());}
			if(rdtl.getDocumentNo02() != null){	documentsNo.append(","+rdtl.getDocumentNo02());}
			if(rdtl.getDocumentNo03() != null){	documentsNo.append(","+rdtl.getDocumentNo03());}
			if(rdtl.getDocumentNo04() != null){	documentsNo.append(","+rdtl.getDocumentNo04());}		
			if(documentsNo.length()>0){if(documentsNo.charAt(0) == ','){ documentsNo.deleteCharAt(0); }}
		}

		if(documentsNo.length() == 0){
			if(strLanguage.equals("en")){
				documentsNo.append("N/A");
			}else{			
				documentsNo.append("T/B");
			}
		}
		
		return documentsNo.toString();
	}
	//end
	
	/**
	 * Download files as a zip.
	 *
	 * @param zipFileName the zip file name
	 * @param fileList the list of the files that want to compress
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadFilesAsZip(String filePath,String zipFileName) throws IOException {
		zipFileName = zipFileName.replaceAll("\\s+","") + ".zip";
		PortletResponse portalresponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil
				.getHttpServletResponse(portalresponse);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
		
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
				response.getOutputStream()));
		try {
			List<File> fileList = new ArrayList<File>();
			fileList.add(new File(filePath));
			for (File file : fileList) {
				zos.putNextEntry(new ZipEntry(file.getName()));	
				// Get the file
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					zos.write(("ERROR: Could not find file " + file
							.getName()).getBytes());
					zos.closeEntry();
					continue;
				}
				BufferedInputStream fif = new BufferedInputStream(fis);
				// Write the contents of the file
				int data = 0;
				while ((data = fif.read()) != -1) {
					zos.write(data);
				}
				fif.close();
				zos.closeEntry();
			}
		} catch (Exception ex) {

		} finally {
			zos.close();
			response.flushBuffer();

			FacesContext.getCurrentInstance().responseComplete();
		}
	}
	public void downloadWithProgress(){
		final JProgressBar jProgressBar = new JProgressBar();
        jProgressBar.setMaximum(100000);
        JFrame frame = new JFrame();
        frame.setContentPane(jProgressBar);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(300, 70);
        frame.setVisible(true);

        Runnable updatethread = new Runnable() {
            public void run() {
                try {

                    URL url = new URL("http://downloads.sourceforge.net/project/bitcoin/Bitcoin/blockchain/bitcoin_blockchain_170000.zip");
                    HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
                    long completeFileSize = httpConnection.getContentLength();

                    java.io.BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(
                            "package.zip");
                    java.io.BufferedOutputStream bout = new BufferedOutputStream(
                            fos, 1024);
                    byte[] data = new byte[1024];
                    long downloadedFileSize = 0;
                    int x = 0;
                    while ((x = in.read(data, 0, 1024)) >= 0) {
                        downloadedFileSize += x;

                        // calculate progress
                        final int currentProgress = (int) ((((double)downloadedFileSize) / ((double)completeFileSize)) * 100000d);

                        // update progress bar
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                jProgressBar.setValue(currentProgress);
                            }
                        });

                        bout.write(data, 0, x);
                    }
                    bout.close();
                    in.close();
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }
            }
        };
        new Thread(updatethread).

        start();
    }
	public void commonDownloadUtilNio(String filePath, String displayFileName) throws IOException {

		URL urlObj = null;
		ReadableByteChannel rbcObj = null;
		FileOutputStream fOutStream  = null;
		String sampleUrl = "https://www.eperolehan.gov.my/documents/10182/100000002257870/Pendaftaran+Pembekal+updated+v180118.pdf/57987fb9-f6c2-435b-ac44-e348b72737ee";
		// Checking If The File Exists At The Specified Location Or Not
		//Path filePathObj = Paths.get(filePath);
		boolean fileExists = true;
		PortletResponse resp = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; filename=\""+ displayFileName + "\"");
//		response.setContentType("application/octet-stream");
		response.setContentType(getMimeType(displayFileName));
		response.flushBuffer();
		OutputStream out = null;
		if(fileExists) {
			try {
				java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
				java.lang.System.setProperty("http.agent", "Chrome");
				urlObj = new URL(sampleUrl);
				rbcObj = Channels.newChannel(urlObj.openStream());
				fOutStream = new FileOutputStream(filePath);

				fOutStream.getChannel().transferFrom(rbcObj, 0, Long.MAX_VALUE);
				System.out.println("! File Successfully Downloaded From The Url !");
				
			} catch (IOException ioExObj) {
				System.out.println("Problem Occured While Downloading The File= " + ioExObj.getMessage());				
			} finally {
				try {
					InputStream docInputStream = null;
					
					//out = response.getOutputStream();
					//byte[] buffer = new byte[8192];
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					int bytesRead;
					
//					while (rbcObj.read(buffer) >= 0 || buffer.position() > 0)
//					{
//					  buffer.flip();
//					  fOutStream.getChannel().write(buffer);
//					  buffer.compact();
//					}
//					
					FileChannel inChannel =fOutStream.getChannel();
			        while(inChannel.read(buffer) > 0)
			        {
			            buffer.flip();
			            for (int i = 0; i < buffer.limit(); i++)
			            {
			            	fOutStream.getChannel().write(buffer);
			            }
			            buffer.clear(); // do something with the data and clear/compact it.
			        }
					if(fOutStream != null){
						fOutStream.close();
					}
					if(rbcObj != null) {
						rbcObj.close();
					}
				} catch (IOException ioExObj) {
					System.out.println("Problem Occured While Closing The Object= " + ioExObj.getMessage());
				}				
			}
		} else {
			System.out.println("File Not Present! Please Check!");
		}
	}
	
	public void commonDownloadUtilNew(String filePath, String displayFileName) throws IOException{
		logger.info("START TO DOWNLOAD FILE");
		PortletResponse resp = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; filename=\""+ displayFileName + "\"");
//		response.setContentType("application/octet-stream");
		response.setContentType(getMimeType(displayFileName));
		response.flushBuffer();

		InputStream docInputStream = null;
		try {
			URLConnection uc = new URL(filePath).openConnection();
			docInputStream = new BufferedInputStream(uc.getInputStream());
			logger.info("ZAIM getURL "+uc.getURL().toString());
			logger.info("filePath----"+filePath);
		} catch (Exception e) {
			// Cannot open connection, maybe local file
			docInputStream = new FileInputStream(filePath);
			logger.info("docInputStream==="+docInputStream);
			logger.info("Error found while downloading file "+filePath+" : "+e);
			logger.error(e);
			e.printStackTrace();
			
		}

		OutputStream out = null;
		Scanner sc = null;
		try {		

			out = response.getOutputStream();
			byte[] buffer = new byte[1024 * 1024];
			long  bytesRead;
			int n = 0;
	       /* while (-1 != (n = docInputStream.read(buffer))) {
	        	out.write(buffer, 0, n);
				buffer = new byte[1024 * 1024];
	           
	        }*/
	        
	        sc = new Scanner(docInputStream, "UTF-8");
	        while (sc.hasNextLine()) {
	            String line = sc.nextLine();
	            // System.out.println(line);
	        }
	        // note that Scanner suppresses exceptions
	        if (sc.ioException() != null) {
	            throw sc.ioException();
	        }
		} catch(Exception e) {
			logger.error(e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (docInputStream != null) {
				docInputStream.close();	
			}
		}
		////NEW METHOD
		/*URLConnection url = new URL(filePath).openConnection();
		ReadableByteChannel  readableByteChannel = Channels.newChannel(url.getURL().openStream());
		log.info("readableByteChannel----"+readableByteChannel);
		log.info("url.getURL()----"+url.getURL());
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		log.info("fileOutputStream----"+fileOutputStream);
		FileChannel fileChannel = fileOutputStream.getChannel();
		log.info("fileChannel----"+fileChannel);
		fileOutputStream.getChannel()
		  .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);*/
		logger.info("END TO DOWNLOAD FILE");
	}
	
	public void commonDownloadUtilNew2(String filePath, String displayFileName) throws IOException{
		logger.info("START TO DOWNLOAD FILE");
		PortletResponse resp = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; filename=\""+ displayFileName + "\"");
//		response.setContentType("application/octet-stream");
		response.setContentType(getMimeType(displayFileName));
		response.flushBuffer();

		InputStream docInputStream = null;
		try {
			URLConnection uc = new URL(filePath).openConnection();
			docInputStream = new BufferedInputStream(uc.getInputStream());
			logger.info("ZAIM getURL "+uc.getURL().toString());
			logger.info("filePath----"+filePath);
		} catch (Exception e) {
			// Cannot open connection, maybe local file
			docInputStream = new FileInputStream(filePath);
			logger.info("docInputStream==="+docInputStream);
			logger.info("Error found while downloading file "+filePath+" : "+e);
			logger.error(e);
			e.printStackTrace();
			
		}

		OutputStream out = null;
		Scanner sc = null;
		try {		

			out = response.getOutputStream();
			byte[] buffer = new byte[1024 * 1024];
			long  bytesRead;
			int n = 0;
	       /* while (-1 != (n = docInputStream.read(buffer))) {
	        	out.write(buffer, 0, n);
				buffer = new byte[1024 * 1024];
	           
	        }*/
	        
	        sc = new Scanner(docInputStream, "UTF-8");
	        while (sc.hasNextLine()) {
	            String line = sc.nextLine();
	            // System.out.println(line);
	        }
	        // note that Scanner suppresses exceptions
	        if (sc.ioException() != null) {
	            throw sc.ioException();
	        }
		} catch(Exception e) {
			logger.error(e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (docInputStream != null) {
				docInputStream.close();	
			}
		}
		////NEW METHOD
		/*URLConnection url = new URL(filePath).openConnection();
		ReadableByteChannel  readableByteChannel = Channels.newChannel(url.getURL().openStream());
		log.info("readableByteChannel----"+readableByteChannel);
		log.info("url.getURL()----"+url.getURL());
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		log.info("fileOutputStream----"+fileOutputStream);
		FileChannel fileChannel = fileOutputStream.getChannel();
		log.info("fileChannel----"+fileChannel);
		fileOutputStream.getChannel()
		  .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);*/
		logger.info("END TO DOWNLOAD FILE");
	}
	private String getMimeType(String fileName) {
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
		
		String mimeType = (String)CommonConstants.getFileExtMap().get(fileExt);
		
		return (mimeType != null) ? mimeType : "octet-stream";
	}
	public void commonDownloadUtilTemp(String filePath, String displayFileName) throws IOException{
		logger.info("START TO DOWNLOAD FILE");
		long startTime = System.currentTimeMillis();
		
		
		logger.info("retriveSubModuleNameByReportCode - #32926 end");
		PortletResponse resp = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; filename=\""+ displayFileName + "\"");
//		response.setContentType("application/octet-stream");
		response.setContentType(getMimeType(displayFileName));
		response.flushBuffer();

		InputStream docInputStream = null;
		try {
			URLConnection uc = new URL(filePath).openConnection();
			docInputStream = new BufferedInputStream(uc.getInputStream());
			logger.info("ZAIM getURL "+uc.getURL().toString());
			logger.info("filePath----"+filePath);
		} catch (Exception e) {
			// Cannot open connection, maybe local file
			docInputStream = new FileInputStream(filePath);
			logger.info("docInputStream==="+docInputStream);
			logger.info("Error found while downloading file "+filePath+" : "+e);
			logger.error(e);
			e.printStackTrace();
			
		}

		OutputStream out = null;
		try {		

			out = response.getOutputStream();
			byte[] buffer = new byte[1024 * 1024];
			int bytesRead;
			while ((bytesRead = docInputStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				buffer = new byte[1024 * 1024];
			}
		} catch(Exception e) {
			logger.error(e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (docInputStream != null) {
				docInputStream.close();	
			}
		}
		////NEW METHOD
		/*URLConnection url = new URL(filePath).openConnection();
		ReadableByteChannel  readableByteChannel = Channels.newChannel(url.getURL().openStream());
		log.info("readableByteChannel----"+readableByteChannel);
		log.info("url.getURL()----"+url.getURL());
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		log.info("fileOutputStream----"+fileOutputStream);
		FileChannel fileChannel = fileOutputStream.getChannel();
		log.info("fileChannel----"+fileChannel);
		fileOutputStream.getChannel()
		  .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);*/
		SLAUtils.getInstance().logSQLPerformanceInfo("PM","QueueGenerateReports","RPTRequestBackingBean.commonDownloadUtilTemp", startTime,"7754");
		logger.info("END TO DOWNLOAD FILE");
	}
}