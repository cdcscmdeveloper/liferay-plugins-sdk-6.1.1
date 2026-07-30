package my.ep.web.backingbean.sm.ai;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.json.JSONArray;

import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.ai.AIApiResponse;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.backingbean.sm.ai.AiDocSupport.AiBankDocDTO;
import my.ep.web.backingbean.sm.ai.AiDocSupport.AiDocParser;
import my.ep.web.backingbean.sm.ai.AiDocSupport.AiDocValidator;
import my.ep.web.backingbean.sm.ai.AiDocSupport.AiEpfResultDTO;
import my.ep.web.backingbean.sm.ai.AiDocSupport.AiTenancyDTO;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmFieldChangeUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;


/**
 * Backing bean for AI-assisted supporting-document extraction (EPF / Tenancy / Bank).
 */
@ManagedBean(name = "smAIDocBackingBean")
@ViewScoped
public class SmAIDocBackingBean extends BaseBackingBean<BaseManagedBean> {

	public static Logger log = Logger.getLogger(SmAIDocBackingBean.class);
	private static final long serialVersionUID = 1L;

	private Map<String, List<SmApplDocFile>> docsMap = new HashMap<>();
	private Map<String, Boolean> postAIMap = new HashMap<>();
	private List<SmApplDocFile> docs;
	
	private static final String TYPE_EPF = "epf";
	private static final String TYPE_TENANCY = "tenancy";
	private static final String TYPE_BANK = "bank";
	private static final String LBL_AI_UNAVAILABLE = "lbl.sm.ai.unavailable";
	private static final String AI_UPLOAD_MARKER = "_ai";

	private List<AiEpfResultDTO> epfs;
	private AiTenancyDTO tenancy;
	private List<AiEpfResultDTO.AiEmployeeDTO> selectedEmployees;
	private boolean postAI = false;
	private boolean blockSubmit = false;
	private String docType;
	private SmAppl smAppl;

	/**
	 * Set context for auto-processing
	 * @param docType
	 * @param smAppl
	 */
	public void initContext(String docType, SmAppl smAppl) {
		this.docType = docType;
		this.smAppl = smAppl;

		if (!docsMap.containsKey(docType)) {
			docsMap.put(docType, new ArrayList<SmApplDocFile>());
		}
		docs = docsMap.get(docType);
		// fileUpload re-renders this panel: keep what was just extracted, do not re-seed over it
		if (!docs.isEmpty()) {
			return;
		}
		epfs = null;
		tenancy = null;
		postAI = false;
		blockSubmit = false;

		seedFromPersisted(docType, smAppl);
		postAIMap.put(docType, Boolean.valueOf(postAI));

		if (!docs.isEmpty()) {
			recomputeBlockSubmit(docType, smAppl);
		}
	}

	/**
	 * block Submit button when Company Name does not match
	 * @param docType
	 * @param appl
	 */
	private void recomputeBlockSubmit(String docType, SmAppl appl) {
		blockSubmit = false;
		String companyName = (appl != null && appl.getSmSupplier() != null)
				? appl.getSmSupplier().getCompanyName() : "";
		if (TYPE_EPF.equals(docType) && !EntityHelper.isCollectionEmpty(epfs)) {
			for (AiEpfResultDTO ee : epfs) {
				boolean employerMatches = ee.getEmployerName() != null
						&& AiDocValidator.normalizedEquals(companyName, ee.getEmployerName());
				if (!employerMatches) {
					blockSubmit = true;
					log.info("EPF block on reopen: company='" + companyName + "' vs employer='"
							+ ee.getEmployerName() + "'");
					break;
				}
			}
		} else if (TYPE_TENANCY.equals(docType) && tenancy != null 
				&& !StringUtils.isEmpty(tenancy.getTenantsName())
				&& !AiDocValidator.normalizedEquals(companyName, tenancy.getTenantsName())) {
			
			blockSubmit = true;
			log.info("Tenancy block on reopen: company='" + companyName + "' vs tenant='"
						+ tenancy.getTenantsName() + "'");
		}
	}

	/**
	 * Show uploaded files and saved JSON result
	 * @param docType
	 * @param appl
	 */
	private void seedFromPersisted(String docType, SmAppl appl) {
		SmApplSupportDoc suppDoc = getSmApplSupportDoc(docType, appl);
		if (suppDoc == null || EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())) {
			return;
		}
		List<JsonWrapper> stored = new ArrayList<>();
		for (SmApplDocFile file : suppDoc.getSmApplDocFiles()) {
			// skip blank placeholder rows left behind by a soft-delete
			if (file.getFileName() == null || file.getFileName().trim().isEmpty()) {
				continue;
			}
			// set the back-ref so deleteDoc()/detachFromSupportDoc() work on seeded files
			file.setSmApplSupportDoc(suppDoc);
			docs.add(file);
			if (file.getAiApiResponseId() != null) {
				AIApiResponse res = SmServiceLookup.getAIApiProcessSO().getAIApiResponse(file.getAiApiResponseId());
				if (res != null) {
					stored.add(new JsonWrapper(res));
				}
			}
		}
		if (stored.isEmpty()) {
			return;
		}
		if (TYPE_EPF.equals(docType)) {
			processEPF(stored, appl);
		} else if (TYPE_TENANCY.equals(docType)) {
			processTenancyAgreement(stored, appl);
		}
		postAI = true;
		postAIMap.put(docType, Boolean.TRUE);
	}
	
	/**
	 * EPF
	 * @param id
	 * @return
	 */
	public static boolean isEpfDoc(Long id)     { 
		return id == 494L || id == 768L || id == 864L; 
	}
	
	/**
	 * Tenancy
	 * @param id
	 * @return
	 */
	public static boolean isTenancyDoc(Long id) { 
		return id == 679L || id == 662L || id == 654L || id == 1124L;
	}
	
	/**
	 * Bank Statement
	 * @param id
	 * @return
	 */
	public static boolean isBankDoc(Long id)    {
		return id == 529L || id == 633L || id == 676L;
	}
	
	/**
	 * Check AI gate
	 * @param appl
	 * @return
	 */
	public static boolean isAIApplicable(SmAppl appl) {
		return appl != null && appl.getAiProcessed() >= 1;
	}
	
	/**
	 * Display AI upload for Supporting Documents
	 * @param appl
	 * @param documentId
	 * @return
	 */
	public static boolean isAiUploadDocRow(SmAppl appl, Long documentId) {
		return isAIApplicable(appl) && (isBankDoc(documentId) || isTenancyDoc(documentId));
	}

	/**
	 * Check for app version AI flag
	 * @param appl
	 */
	public static void initAiState(SmAppl appl) {
		if (appl == null) {
			return;
		}
		if (SmApplicationUtil.getAppVersionDetailsWithRedmineNo(appl,
				SupplierManagementConst.APP_VERSION_REDMINE_NO_40112, SupplierManagementConst.PHASE_1)) {
			appl.setAiProcessed(0);
			return;
		}
		appl.setAiProcessed(1);
		recomputeAiProcessed(appl);
		// nothing extracted yet + AI off: hide everything. 2 = already extracted, ignores the toggle
		if (appl.getAiProcessed() == 1 && !AiDocSupport.isAiEnabled()) {
			appl.setAiProcessed(0);
			return;
		}
		validateBank(appl);
		validateTenancy(appl);
		validateEPF(appl);
		markEpfAiDocs(appl);
	}

	/**
	 * Start AI processing for the newly uploaded docs
	 * @param appl
	 * @param suppDoc
	 */
	public static void processUploadedSupportDoc(SmAppl appl, SmApplSupportDoc suppDoc) {
		if (!isAIApplicable(appl) || suppDoc == null || suppDoc.getPmDocument() == null) {
			return;
		}
		if (isTenancyDoc(suppDoc.getPmDocument().getDocumentId())) {
			processTenancyStatement(appl, suppDoc);
		} else {
			processBankStatement(appl, suppDoc);
		}
		recomputeAiProcessed(appl);
	}

	/**
	 * Re-check AI fields after deleting doc
	 * @param appl
	 */
	public static void revalidateAfterDelete(SmAppl appl) {
		if (!isAIApplicable(appl)) {
			return;
		}
		validateTenancy(appl);
		validateEPF(appl);
		markEpfAiDocs(appl);
		recomputeAiProcessed(appl);
	}

	/**
	 * Based on SmApplicationBackingBean
	 * @param event
	 */
	public void handleSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() == null) {
			return;
		}
		
		String filePath = Calendar.getInstance().get(Calendar.YEAR)
				+ "/SM/APPLICATION/" + smAppl.getApplNo() + "/SD/";
		
		SmApplDocFile doc = new SmApplDocFile();
		doc.setFileName(generateUniqueFilename(StringUtils.replace(event.getFile().getFileName(), " ", "-")));
		doc.setFilePath(filePath);

		SmCommonUtil.processUpload(doc.getFilePath(), event, doc.getFileName());
		SmApplSupportDoc suppDoc = getSmApplSupportDoc(docType, smAppl);
		if (suppDoc != null){
			if (suppDoc.getSmApplDocFiles() == null){
				suppDoc.setSmApplDocFiles(new ArrayList<SmApplDocFile>());
			}
			for(int i=0; i<suppDoc.getSmApplDocFiles().size(); i++){
				String filename = suppDoc.getSmApplDocFiles().get(i).getFileName();
				if(filename==null || " ".equalsIgnoreCase(filename)){
					suppDoc.getSmApplDocFiles().remove(i);
				}
			}
			// Tenancy is single-file: a new upload REPLACES the previous doc
			if (TYPE_TENANCY.equals(docType)){
				suppDoc.getSmApplDocFiles().clear();
				docs.clear();
			}
			doc.setSmApplSupportDoc(suppDoc);
			suppDoc.getSmApplDocFiles().add(doc);
		} else {
			log.info("No supporting-document row for type=" + docType + " on appl="
					+ smAppl.getApplNo() + ": AI file stays unattached");
		}

		docs.add(doc);
		if (docType != null) {
			processDoc(docType, smAppl);
			postAI = true;
			postAIMap.put(docType, Boolean.TRUE);
		}
	}
	
	private static String generateUniqueFilename(String originalFilename) {
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String oriFilename = "";
		String extension = "";
		int dotIndex = originalFilename.lastIndexOf('.');
		if (dotIndex > 0) {
			extension = originalFilename.substring(dotIndex);
			oriFilename = originalFilename.substring(0, dotIndex);
		}
		// keep marker last before the extension so delete-lock can find it
		return oriFilename + "_" + timestamp + AI_UPLOAD_MARKER + extension;
	}

	// null when the application has no supporting-doc row of this type
	private SmApplSupportDoc getSmApplSupportDoc(String docType, SmAppl appl){
		if(appl == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return null;
		}
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			Long docId = suppDoc.getPmDocument().getDocumentId();
			if (TYPE_EPF.equals(docType) && isEpfDoc(docId)){
				return suppDoc;
			}
			if (TYPE_TENANCY.equals(docType) && isTenancyDoc(docId)){
				return suppDoc;
			}
		}
		return null;
	}

	/**
	 * Test
	 * @param type
	 * @param appl
	 */
	public void processDoc(String type, SmAppl appl){
		if (StringUtils.isEmpty(type)){
			return;
		}
		docType = type.trim();
		docs = docsMap.get(docType);
		blockSubmit = false;
		epfs = null;
		tenancy = null;
		if (EntityHelper.isCollectionEmpty(docs)){
			return;
		}
		log.debug("processDoc: " + docs.size() + " document(s), type=" + docType);
		
		List<JsonWrapper> jsonWrappers = new ArrayList<>();
		for (SmApplDocFile doc : docs){
			// Skip already processed files
			if (doc.getAiApiResponseId() != null) {
				AIApiResponse res = SmServiceLookup.getAIApiProcessSO()
						.getAIApiResponse(doc.getAiApiResponseId());
				jsonWrappers.add(new JsonWrapper(res));
				continue;
			}
			
			JsonWrapper result = sendDocumentOCR(docType, doc.getFilePath(), doc.getFileName(), appl.getApplNo());
			if (result != null){
				long apiId = saveResponse(appl, result);
				doc.setAiApiResponseId(apiId);
				result.apiId = apiId;
				jsonWrappers.add(result);
			}
		}
		boolean hasError = showErrorMessage(docType, jsonWrappers);
		try {
			if (TYPE_EPF.equals(docType)){
				processEPF(jsonWrappers, appl);
				
			} else if (TYPE_TENANCY.equals(docType) && !hasError){
				processTenancyAgreement(jsonWrappers, appl);
				
			} else {
				processBank(jsonWrappers);
			}
			RequestContext.getCurrentInstance().addCallbackParam("success", true);
		} catch(NullPointerException e){
			if (!hasError){
				addMessageOnce(FacesMessage.SEVERITY_ERROR,
						TYPE_TENANCY.equals(docType) ? "lbl.sm.ai.tnc.invalid" : "lbl.sm.ai.epf.invalid");
			}
		}
	}
	
	//do parallel request using ExecutorService
	private static JsonWrapper sendDocumentOCR(String docType, String filepath, String filename, String clientRequestId){
		///temporary demo filter - start
		if (filename.endsWith(".json")){
			log.info("skip AI");
			String content = AiDocParser.getFakeData(filepath, filename);
			return new JsonWrapper(System.currentTimeMillis(), content, docType);
		}
		if (!filename.contains("ai_demo")){
			return null;
		}
		///temporary demo filter - end
		
		String sessionId = ((PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest()).getRequestedSessionId(); 
		long startTime = System.currentTimeMillis();
		
		try {
			String requestBody = AiDocParser.buildOcrRequestBody(docType, filepath, filename, clientRequestId);
			String result = AiOcrClient.callDocumentOcr(requestBody, docType);
			SLAUtils.getInstance().logSLASubInfo("SM", "AI", sessionId, "null",
					"SmAIDocbackingBean.sendDocumentOCR", "1:"+filename, startTime);
			if (result == null) {
				addMessageOnce(FacesMessage.SEVERITY_WARN, LBL_AI_UNAVAILABLE);
				return null;
			}
			return new JsonWrapper(startTime, result, docType);
		} catch (IOException e) {
			log.error("OCR call failed for " + filename, e);

			addMessageOnce(FacesMessage.SEVERITY_WARN, LBL_AI_UNAVAILABLE);
		}

		return null;
	}
	
	private static long saveResponse(SmAppl smAppl, JsonWrapper jsonWrapper){
		AIApiResponse apiResp = new AIApiResponse();
		
		apiResp.setSupplierId(smAppl.getSmSupplier().getSupplierId());
		apiResp.setDocType("SM");
		apiResp.setDocId(smAppl.getApplId());
		apiResp.setDocNo(smAppl.getApplNo());
		
		apiResp.setJsonResponse(jsonWrapper.rawContent);
		apiResp.setTimeTakenMs(jsonWrapper.timeTakenMs);
		apiResp.setOcrType(jsonWrapper.ocrType);
		apiResp.setApiName("test");		//temp
		apiResp.setResponseStatus(AiDocParser.getStatusCode(jsonWrapper.rawContent));
		
		return SmServiceLookup.getAIApiProcessSO().saveAIApiResponse(apiResp).getApiResponseId();
	}

	private static void addMessageOnce(FacesMessage.Severity severity, String key){
		String detail = AiDocParser.label(key);
		Iterator<FacesMessage> it = FacesContext.getCurrentInstance().getMessages("ai");
		while (it.hasNext()){
			FacesMessage existing = it.next();
			if (existing.getSeverity() == severity
					&& StringUtils.equals(existing.getDetail(), detail)){
				return;
			}
		}
		FacesContext.getCurrentInstance().addMessage("ai", new FacesMessage(severity, null, detail));
	}

	private static boolean hasErrorMessage(){
		Iterator<FacesMessage> it = FacesContext.getCurrentInstance().getMessages("ai");
		while (it.hasNext()){
			if (it.next().getSeverity() == FacesMessage.SEVERITY_ERROR){
				return true;
			}
		}
		return false;
	}

	/**
	 * Submit
	 * @param docType
	 * @param appl
	 */
	public void submit(String docType, SmAppl appl){
		if (StringUtils.isEmpty(docType) || appl == null){
			return;
		}
		// Hard block: a company-name mismatch prevents using the extracted data.
		if (blockSubmit){
			String key = TYPE_EPF.equals(docType) ? "lbl.sm.ai.epf.mismatch" : "lbl.sm.ai.tnc.mismatch";
			addMessageOnce(FacesMessage.SEVERITY_ERROR, key);
			return;
		}
		if (hasErrorMessage()){
			return;
		}
		log.info(docType+"\t"+appl.getApplNo());
		// recompute the task-list AI flag before the save flow persists the appl graph
		recomputeAiProcessed(appl);
		if (TYPE_EPF.equals(docType) && !EntityHelper.isCollectionEmpty(selectedEmployees)){
			submitEPF(appl);

		} else if (TYPE_TENANCY.equals(docType)){
			submitTenancy();
		}
	}
	
	private void submitEPF(SmAppl appl){
		if (appl.getSmPersonnels() == null){
			appl.setSmPersonnels(new ArrayList<SmPersonnel>());
		}
		Map<String, SmPersonnel> personnels = new HashMap<>();
		for (SmPersonnel per : appl.getSmPersonnels()){
			personnels.put(per.getIdentificationNo(), per);
		}
		
		//check for dupe IC
		for (AiEpfResultDTO.AiEmployeeDTO employee : selectedEmployees) {
			SmPersonnel per = personnels.get(employee.getNricNumber());
			if (per != null){
				per.setEpfNo(employee.getKwspNumber());
				// existing (SSM/eP) personnel: only EPF No. is AI-populated -> only it gets the icon.
				per.setEpfNoAI(employee.getKwspNumber());
				continue;
			}
			per = new SmPersonnel();
			per.setName(employee.getEmployeeName());
			per.setIdentificationNo(employee.getNricNumber());
			per.setEpfNo(employee.getKwspNumber());

			//defaults as per SmApplicationUtil.setDefaultPersonnelValues() / SssmSO
			per.setPersonnelId(null);
			per.setEpRole("");
			per.setShareAmt(new BigDecimal(0));
			per.setSharePercentage(new BigDecimal(0));
			per.setRevNo(1);
			per.setSmAppl(appl);
			per.setBumiFlg(1);
			per.setMuslimFlg(1);
			
			appl.getSmPersonnels().add(per);
		}
		
		//save smAppl
		FacesContext fc = FacesContext.getCurrentInstance();
		SmApplicationBackingBean bb = fc.getApplication().evaluateExpressionGet(fc, 
				"#{backingBean}", SmApplicationBackingBean.class);
		if (bb != null){
			bb.saveOrUpdateAppl();
			validateEPF(bb.getManagedBean().getSmAppl());
		}
	}


	private void submitTenancy(){
		FacesContext fc = FacesContext.getCurrentInstance();
		SmApplicationBackingBean bb = fc.getApplication().evaluateExpressionGet(fc, 
				"#{backingBean}", SmApplicationBackingBean.class);
		if (bb != null){
			bb.updateTenancyAI(tenancy);
		}
	}
	

	/**
	 * @param appl
	 * @param suppDoc
	 */
	public static void processBankStatement(SmAppl appl, SmApplSupportDoc suppDoc){
		if (suppDoc == null || suppDoc.getPmDocument() == null || appl == null || appl.getSmSupplier() == null){
			return;
		}
		if (EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())){
			return;
		}
		//PENYATA AKAUN SEMASA YANG TERKINI ATAS NAMA SYARIKAT / SLIP DEPOSIT (all zones)
		if (!isBankDoc(suppDoc.getPmDocument().getDocumentId())){
			log.info("...not bank statement");
			return;
		}
		List<JsonWrapper> jsonWrappers = new ArrayList<>();
		for (SmApplDocFile doc : suppDoc.getSmApplDocFiles()){
			if (doc.getAiApiResponseId() != null){
				continue;
			}
			JsonWrapper result = sendDocumentOCR(TYPE_BANK, doc.getFilePath(), doc.getFileName(), appl.getApplNo());
			if (result != null){
				long apiId = saveResponse(appl, result);
				doc.setAiApiResponseId(apiId);
				result.apiId = apiId;
				jsonWrappers.add(result);
			}
		}
		String sessionId = ((PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest()).getRequestedSessionId(); 
		long startTime = System.currentTimeMillis();
		boolean hasError = showErrorMessage(TYPE_BANK, jsonWrappers);
		
		if (!hasError){
			List<AiBankDocDTO> docEntities = processBank(jsonWrappers);
			validateBankEntity(appl, suppDoc, docEntities);
			showErrorMessageBank(docEntities, "ai");
		}
		
		SLAUtils.getInstance().logSLASubInfo("SM", "AI", sessionId, "null",
				"SmAIDocbackingBean.sendDocumentOCR", "2", startTime);
	}

	/**
	 * AI verification for a Tenancy Agreement uploaded in Supporting Documents (not the Premises dialog)
	 * @param appl
	 * @param suppDoc
	 */
	public static void processTenancyStatement(SmAppl appl, SmApplSupportDoc suppDoc){
		if (suppDoc == null || suppDoc.getPmDocument() == null || appl == null || appl.getSmSupplier() == null){
			return;
		}
		if (!isTenancyDoc(suppDoc.getPmDocument().getDocumentId()) || EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())){
			return;
		}
		List<JsonWrapper> jsonWrappers = new ArrayList<>();
		for (SmApplDocFile doc : suppDoc.getSmApplDocFiles()){
			if (doc.getFileName() == null || doc.getFileName().trim().isEmpty() || doc.getAiApiResponseId() != null){
				continue;
			}
			JsonWrapper result = sendDocumentOCR(TYPE_TENANCY, doc.getFilePath(), doc.getFileName(), appl.getApplNo());
			if (result != null){
				long apiId = saveResponse(appl, result);
				doc.setAiApiResponseId(apiId);
				result.apiId = apiId;
				jsonWrappers.add(result);
			}
		}
		boolean hasError = showErrorMessage(TYPE_TENANCY, jsonWrappers);
		if (!hasError){
			validateTenancy(appl);
		}
	}
	
	/**
	 * @param appl
	 */
	public static void validateBank(SmAppl appl){
		if (!isAIApplicable(appl)){
			return;
		}
		if (appl == null || appl.getSmSupplier() == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		SmApplSupportDoc bankStatementDoc = null;
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (isBankDoc(suppDoc.getPmDocument().getDocumentId())){
				bankStatementDoc = suppDoc;
				break;
			}
		}
		if (bankStatementDoc == null || EntityHelper.isCollectionEmpty(bankStatementDoc.getSmApplDocFiles())){
			log.info("No bank statement supporting document found for application " + appl.getApplNo());
			return;
		}
		
		List<AiBankDocDTO> entities = new ArrayList<>();
		for (SmApplDocFile docFile : bankStatementDoc.getSmApplDocFiles()){
			if (docFile.getFileName() == null ||docFile.getFileName().trim().isEmpty()){
				continue;
			}
			AIApiResponse response = SmServiceLookup.getAIApiProcessSO()
					.getAIApiResponse(docFile.getAiApiResponseId());
			if (response != null){
				entities.add(AiDocParser.toBankDoc(response.getApiResponseId(),
						AiDocParser.parseFields(response.getJsonResponse())));
			}
		}
		SmFieldChangeUtil.setBankFieldChangeStatus(appl);
		validateBankEntity(appl, bankStatementDoc, entities);
	}
	
	/**
	 * 
	 * @param appl
	 */
	public static void validateTenancy(SmAppl appl){
		if (!isAIApplicable(appl)){
			return;
		}
		if (appl == null || appl.getSmSupplier() == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		SmFacility hqFacility = AiDocValidator.getHeadquartersFacility(appl);
		AiDocValidator.clearTenancyAiFields(hqFacility);

		SmApplSupportDoc tenancyDoc = findTenancyDoc(appl);
		if (tenancyDoc == null || EntityHelper.isCollectionEmpty(tenancyDoc.getSmApplDocFiles())
				|| EntityHelper.isCollectionEmpty(appl.getSmFacilities())){
			return;
		}
		validateTenancyFiles(appl, tenancyDoc, hqFacility);
	}

	private static SmApplSupportDoc findTenancyDoc(SmAppl appl){
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if(isTenancyDoc(suppDoc.getPmDocument().getDocumentId())){
				return suppDoc;
			}
		}
		return null;
	}

	// Header facility supplies the supplier-side landlord/rental/address; each tenancy file is verified against it.
	private static void validateTenancyFiles(SmAppl appl, SmApplSupportDoc tenancyDoc, SmFacility hqFacility){
		String landlordsNameSupp = "";
		String companyName = appl.getSmSupplier().getCompanyName();

		BigDecimal rentalRate = null;
		SmAddress addr = null;
		for(SmFacility fac : appl.getSmFacilities()){
			if("H".equalsIgnoreCase(fac.getFacilityType())){
				landlordsNameSupp = fac.getOwnerName();
				if (fac.getRentalRate() != null){
					rentalRate = fac.getRentalRate().setScale(2);
				}
				addr = fac.getSmAddress();
			}
		}

		for (SmApplDocFile docFile : tenancyDoc.getSmApplDocFiles()){
			if (docFile.getFileName() == null ||docFile.getFileName().trim().isEmpty()){
				continue;
			}
			AIApiResponse response = SmServiceLookup.getAIApiProcessSO()
					.getAIApiResponse(docFile.getAiApiResponseId());
			if (response != null){
				AiTenancyDTO te = AiDocParser.toTenancy(AiDocParser.parseFields(response.getJsonResponse()));
				AiDocValidator.validateTenancy(te, landlordsNameSupp, companyName, rentalRate, addr);

				docFile.setMappedFieldAI(te.getLabel());
				docFile.setValidatedAI(te.hasError() ? 2 : 1); //1-ok, 2-err
				AiDocValidator.setTenancyAiFields(hqFacility, te);
			}
		}
	}

	/**
	 * EPF equivalent of validateTenancy
	 * @param appl
	 */
	public static void validateEPF(SmAppl appl){
		if (!isAIApplicable(appl)){
			return;
		}
		if (appl == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		clearPersonnelAiFields(appl);

		SmApplSupportDoc epfDoc = findEpfDoc(appl);
		if (epfDoc == null || EntityHelper.isCollectionEmpty(epfDoc.getSmApplDocFiles())
				|| EntityHelper.isCollectionEmpty(appl.getSmPersonnels())){
			return;
		}

		Map<String, SmPersonnel> byNric = new HashMap<>();
		for (SmPersonnel per : appl.getSmPersonnels()){
			byNric.put(per.getIdentificationNo(), per);
		}

		for (SmApplDocFile docFile : epfDoc.getSmApplDocFiles()){
			applyEpfValidation(docFile, byNric);
		}
	}

	// Reset the AI transients on every personnel before re-deriving from the EPF statement.
	private static void clearPersonnelAiFields(SmAppl appl){
		if (EntityHelper.isCollectionEmpty(appl.getSmPersonnels())){
			return;
		}
		for (SmPersonnel per : appl.getSmPersonnels()){
			per.setEpfNoAI(null);
			per.setNameAI(null);
			per.setIcAI(null);
		}
	}

	private static SmApplSupportDoc findEpfDoc(SmAppl appl){
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (isEpfDoc(suppDoc.getPmDocument().getDocumentId())){
				return suppDoc;
			}
		}
		return null;
	}

	// One EPF file: match extracted employees to personnel by NRIC and light the EPF No./name AI icons.
	private static void applyEpfValidation(SmApplDocFile docFile, Map<String, SmPersonnel> byNric){
		if (docFile.getFileName() == null || docFile.getFileName().trim().isEmpty()){
			return;
		}
		AIApiResponse response = SmServiceLookup.getAIApiProcessSO()
				.getAIApiResponse(docFile.getAiApiResponseId());
		if (response == null){
			return;
		}
		AiEpfResultDTO ee = AiDocParser.toEpf(AiDocParser.parseFields(response.getJsonResponse()));
		for (AiEpfResultDTO.AiEmployeeDTO emp : ee.getEmployees()){
			SmPersonnel per = byNric.get(emp.getNricNumber());
			if (per == null){
				continue;
			}
			per.setEpfNoAI(emp.getKwspNumber());

			String aiName = emp.getEmployeeName();
			if (aiName != null && per.getName() != null
					&& per.getName().trim().equalsIgnoreCase(aiName.trim())){
				per.setNameAI(aiName);
				per.setIcAI(emp.getNricNumber());
			}
		}
	}
	
	/**
	 * Get Employee Names from EPF thru AIApiResponse.
	 * Returns an empty set when AI does not apply / no EPF doc / no response, so the caller skips the
	 * check and manual adds stay unrestricted (as-is behaviour).
	 * @param appl
	 * @return trimmed extracted employee names
	 */
	public static Set<String> getAiEpfEmployeeNames(SmAppl appl){
		Set<String> names = new HashSet<>();
		if (!isAIApplicable(appl) || appl == null
				|| EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return names;
		}
		SmApplSupportDoc epfDoc = null;
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (suppDoc.getPmDocument() != null && isEpfDoc(suppDoc.getPmDocument().getDocumentId())){
				epfDoc = suppDoc;
				break;
			}
		}
		if (epfDoc == null || EntityHelper.isCollectionEmpty(epfDoc.getSmApplDocFiles())){
			return names;
		}
		for (SmApplDocFile docFile : epfDoc.getSmApplDocFiles()){
			collectEpfEmployeeNames(docFile, names);
		}
		return names;
	}

	// collect trimmed AI-extracted employee names from one EPF doc file into names
	private static void collectEpfEmployeeNames(SmApplDocFile docFile, Set<String> names){
		if (docFile.getFileName() == null || docFile.getFileName().trim().isEmpty()){
			return;
		}
		AIApiResponse response = SmServiceLookup.getAIApiProcessSO()
				.getAIApiResponse(docFile.getAiApiResponseId());
		if (response == null){
			return;
		}
		AiEpfResultDTO ee = AiDocParser.toEpf(AiDocParser.parseFields(response.getJsonResponse()));
		for (AiEpfResultDTO.AiEmployeeDTO emp : ee.getEmployees()){
			if (emp.getEmployeeName() != null && !emp.getEmployeeName().trim().isEmpty()){
				names.add(emp.getEmployeeName().trim());
			}
		}
	}

	/**
	 * Manually-added NEW personnel (personnelId == null) must exist by name in the
	 * uploaded EPF statement when AI applies. Existing/saved personnel (non-null id) and name edits on
	 * them are untouched; no EPF uploaded => no restriction. Throws to abort the add before it saves.
	 * @param appl
	 * @param personnel the row being added
	 */
	public static void validateManualPersonnelAgainstEpf(SmAppl appl, SmPersonnel personnel){
		if (!isAIApplicable(appl) || personnel == null || personnel.getPersonnelId() != null){
			return;
		}
		Set<String> aiNames = getAiEpfEmployeeNames(appl);
		if (aiNames.isEmpty()){
			return;
		}
		for (String name : aiNames){
			if (AiDocValidator.normalizedEquals(name, personnel.getName())){
				return;
			}
		}
		throw new EpException(SmCommonUtil.getMessage("lbl.sm.ai.epf.personnel.notexist", null));
	}

	/**
	 * E4 - an EPF or Tenancy doc uploaded via the AI Assistant dialog cannot be deleted from the
	 * Supporting Documents listing (replace it in the AI Assistant). Docs uploaded directly there carry
	 * no marker and stay deletable. Bank stays deletable.
	 * @param doc the support-doc group
	 * @param file the uploaded file row
	 * @return true when the trash control must be hidden
	 */
	public static boolean isAiSuppDocDeleteLocked(SmApplSupportDoc doc, SmApplDocFile file){
		if (file == null || !isAiAssistantUpload(file.getFileName())
				|| doc == null || doc.getPmDocument() == null){
			return false;
		}
		Long docId = doc.getPmDocument().getDocumentId();
		return isEpfDoc(docId) || isTenancyDoc(docId);
	}

	// true when the filename carries the AI Assistant marker (last token before the extension)
	private static boolean isAiAssistantUpload(String fileName){
		if (fileName == null){
			return false;
		}
		String name = fileName.trim();
		int dot = name.lastIndexOf('.');
		if (dot > 0){
			name = name.substring(0, dot);
		}
		return name.endsWith(AI_UPLOAD_MARKER);
	}

	/**
	 * EPF displays the neutral AI icon (validatedAI 3)
	 * @param appl
	 */
	public static void markEpfAiDocs(SmAppl appl){
		if (!isAIApplicable(appl) || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (suppDoc.getPmDocument() == null || !isEpfDoc(suppDoc.getPmDocument().getDocumentId())
					|| EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())){
				continue;
			}
			for (SmApplDocFile docFile : suppDoc.getSmApplDocFiles()){
				if (docFile.getAiApiResponseId() != null){
					docFile.setValidatedAI(3);
					docFile.setMappedFieldAI(buildEpfLabel(appl, docFile));
				}
			}
		}
	}

	// EPF tooltip for one doc file; null when the response is gone, so the icon renders without a tooltip.
	private static String buildEpfLabel(SmAppl appl, SmApplDocFile docFile){
		AIApiResponse response = SmServiceLookup.getAIApiProcessSO()
				.getAIApiResponse(docFile.getAiApiResponseId());
		if (response == null){
			return null;
		}
		AiEpfResultDTO ee = AiDocParser.toEpf(AiDocParser.parseFields(response.getJsonResponse()));
		AiDocValidator.validateEpf(ee, appl.getSmSupplier().getCompanyName());
		return ee.getLabel();
	}

	private static void validateBankEntity(SmAppl appl, SmApplSupportDoc suppDoc, List<AiBankDocDTO> docEntities){
		if (EntityHelper.isCollectionEmpty(docEntities)){
			return;
		}
		AiDocValidator.validateBank(appl, docEntities);
		for (SmApplDocFile file : suppDoc.getSmApplDocFiles()){
			for (AiBankDocDTO entity : docEntities){
				if (entity.getApiId() != null && entity.getApiId().equals(file.getAiApiResponseId())){
					file.setMappedFieldAI(entity.getLabel());
					file.setValidatedAI(entity.hasError() ? 2 : 1); //1-ok, 2-err
				}
			}
		}
	}
	
	private static void showErrorMessageBank(List<AiBankDocDTO> docEntities, String clientId){
		boolean hasWrongCompanyName = false;
		StringBuilder sb = new StringBuilder();

		for (AiBankDocDTO doc : docEntities){
			if (EntityHelper.isCollectionEmpty(doc.getBankStatements())){
				continue;
			}
			// per-bank mismatch rows, labels from the bundle
			if (appendBankMismatchRows(doc.getBankStatements(), sb)){
				hasWrongCompanyName = true;
			}
		}

		if (hasWrongCompanyName){
			// hard block: mismatch disables Declare.
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					AiDocParser.label("lbl.sm.ai.bank.company.mismatch"));
			FacesContext.getCurrentInstance().addMessage(clientId, msg);
		}
		if (!StringUtils.isEmpty(sb.toString())){
			// static prefix from the bundle, then per-bank rows (shown only if wrong).
			sb.insert(0, AiDocParser.label("lbl.sm.ai.bank.account.mismatch"));
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, sb.toString().replace("\n", "<br/>"));
			FacesContext.getCurrentInstance().addMessage(clientId, msg);
		}
	}

	// Append a mismatch row per invalid bank field; returns true if any account-holder name mismatched.
	private static boolean appendBankMismatchRows(List<AiBankDocDTO.AiBankStatementDTO> banks, StringBuilder sb){
		boolean hasWrongCompanyName = false;
		for (AiBankDocDTO.AiBankStatementDTO bank : banks){
			if (!bank.isValidAccountHolderName()){
				hasWrongCompanyName = true;
			}
			if (!bank.isValidBankName()){
				sb.append("\n").append(AiDocParser.label("lbl.gfmas.bank.desc")).append(": ")
					.append(bank.getBankName());
			}
			if (!bank.isValidAccountNo()){
				sb.append("\n").append(AiDocParser.label("lbl.fl.bank.acc.no")).append(": ")
					.append(bank.getAccountNo());
			}
			if (!bank.isValidBankName() || !bank.isValidAccountNo()){
				sb.append("\n");
			}
		}
		return hasWrongCompanyName;
	}
	
	/**
	 * Block submit when Bank Info mismatch
	 * @param appl
	 * @return
	 */
	public static boolean isBankAiMismatch(SmAppl appl) {
		if (!isAIApplicable(appl)) {
			return false;
		}
		if (appl == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())) {
			return false;
		}
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()) {
			if (suppDoc.getPmDocument() == null || EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())
					|| !isBankDoc(suppDoc.getPmDocument().getDocumentId())) {
				continue;
			}
			for (SmApplDocFile file : suppDoc.getSmApplDocFiles()) {
				if (file.getValidatedAI() == 2) {
					emitBankMismatchMessages(appl);
					return true;
				}
			}
		}
		return false;
	}

	/** Re-derive the bank AI verification and emit mismatch messages. */
	private static void emitBankMismatchMessages(SmAppl appl){
		if (appl == null || appl.getSmSupplier() == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		SmApplSupportDoc bankStatementDoc = null;
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (isBankDoc(suppDoc.getPmDocument().getDocumentId())){
				bankStatementDoc = suppDoc;
				break;
			}
		}
		if (bankStatementDoc == null || EntityHelper.isCollectionEmpty(bankStatementDoc.getSmApplDocFiles())){
			return;
		}
		List<AiBankDocDTO> entities = new ArrayList<>();
		for (SmApplDocFile docFile : bankStatementDoc.getSmApplDocFiles()){
			if (docFile.getFileName() == null || docFile.getFileName().trim().isEmpty()){
				continue;
			}
			AIApiResponse response = SmServiceLookup.getAIApiProcessSO()
					.getAIApiResponse(docFile.getAiApiResponseId());
			if (response != null){
				entities.add(AiDocParser.toBankDoc(response.getApiResponseId(),
						AiDocParser.parseFields(response.getJsonResponse())));
			}
		}
		SmFieldChangeUtil.setBankFieldChangeStatus(appl);
		validateBankEntity(appl, bankStatementDoc, entities);
		showErrorMessageBank(entities, null);
	}

	/**
	 * Recompute the TaskList AI flag ({@code SmAppl.aiProcessed}): true iff any document has AI response
	 * @param appl
	 */
	public static void recomputeAiProcessed(SmAppl appl){
		if (appl == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())){
				continue;
			}
			for (SmApplDocFile file : suppDoc.getSmApplDocFiles()){
				if (file.getAiApiResponseId() != null
						&& file.getFileName() != null && !file.getFileName().trim().isEmpty()){
					appl.setAiProcessed(2);
					return;
				}
			}
		}
	}
	
	private static boolean showErrorMessage(String docType, List<JsonWrapper> jsonWrappers){
		if (EntityHelper.isCollectionEmpty(jsonWrappers)){
			return false;
		}
		String err = null;
		for (JsonWrapper json : jsonWrappers){
			int statusCode = AiDocParser.getStatusCode(json.rawContent);
			if (statusCode == 415 || statusCode == 422){
				switch(docType){
				case TYPE_EPF:
					err = "lbl.sm.ai.epf.invalid";
					break;
				case TYPE_TENANCY:
					err = "lbl.sm.ai.tnc.invalid";
					break;
				case TYPE_BANK:
				default:
					err = "lbl.sm.ai.bank.invalid";
					break;
				}
			} else if (statusCode == 500){
				err = "lbl.sm.ai.doctype.invalid";

			} else if (statusCode >= 400){
				err = LBL_AI_UNAVAILABLE;
				addMessageOnce(FacesMessage.SEVERITY_WARN, err);
				return true;
			}
		}
		if (err != null){
			addMessageOnce(FacesMessage.SEVERITY_ERROR, err);
			return true;

		}
		return false;
	}
		
	/**
	 * @param jsonWrappers
	 * @return
	 */
	public static List<AiBankDocDTO> processBank(List<JsonWrapper> jsonWrappers){
		if (EntityHelper.isCollectionEmpty(jsonWrappers)){
			return new ArrayList<>();
		}
		List<AiBankDocDTO> docEnts = new ArrayList<>();
		for (JsonWrapper json : jsonWrappers){
			docEnts.add(AiDocParser.toBankDoc(json.apiId, json.entities));
		}
		return docEnts;
	}

	/**
	 * @param entities
	 * @param appl
	 */
	public void processTenancyAgreement(List<JsonWrapper> entities, SmAppl appl){
		if (EntityHelper.isCollectionEmpty(entities)){
			return;
		}
		for (JsonWrapper entityWrapper : entities){
			tenancy = AiDocParser.toTenancy(entityWrapper.entities);
		}
		if (tenancy == null){
			return;
		}
		String companyName = "";
		if (appl != null && appl.getSmSupplier() != null){
			companyName = appl.getSmSupplier().getCompanyName();
		}
		
		if (!StringUtils.isEmpty(tenancy.getTenantsName()) 
				&& !AiDocValidator.normalizedEquals(companyName, tenancy.getTenantsName())){
			blockSubmit = true;
			addMessageOnce(FacesMessage.SEVERITY_ERROR, "lbl.sm.ai.tnc.mismatch");
		}
	}

	/**
	 * @param entities
	 * @param appl
	 * @param docType
	 */
	public void processEPF(List<JsonWrapper> entities, SmAppl appl){
		if (epfs == null){
			epfs = new ArrayList<>();
		}
		if (EntityHelper.isCollectionEmpty(entities)){
			return;
		}
		
		String companyName = "";
		if (appl != null && appl.getSmSupplier() != null){
			companyName = appl.getSmSupplier().getCompanyName();
		}
		boolean hasWrongCompanyName = false;
		
		for (JsonWrapper entityWrapper : entities){
			AiEpfResultDTO ee = AiDocParser.toEpf(entityWrapper.entities);
			epfs.add(ee);

			// employer name matches exactly, case-insensitive; no employer name extracted still hard-blocks.
			boolean employerMatches = ee.getEmployerName() != null
					&& AiDocValidator.normalizedEquals(companyName, ee.getEmployerName());
			if (!employerMatches){
				hasWrongCompanyName = true;
			}
		}
		if(hasWrongCompanyName){
			blockSubmit = true;
			addMessageOnce(FacesMessage.SEVERITY_ERROR, "lbl.sm.ai.epf.mismatch");
		}
	}
	
	
	public List<SmApplDocFile> getDocs() {
		return docs;
	}
	
	/**
	 * @param doc
	 */
	public void deleteDoc(SmApplDocFile doc) {
		docs.remove(doc);
		detachFromSupportDoc(doc);
		selectedEmployees = null;
		if (TYPE_TENANCY.equals(docType)){
			validateTenancy(smAppl);
		} else if (TYPE_EPF.equals(docType)){
			validateEPF(smAppl);
		}
		if (EntityHelper.isCollectionEmpty(docs)){
			epfs = null;
			tenancy = null;
			postAI = false;
			postAIMap.put(docType, Boolean.FALSE);
			blockSubmit = false;
		} else {
			processDoc(docType, smAppl);
		}
		recomputeAiProcessed(smAppl);
	}

	private void detachFromSupportDoc(SmApplDocFile doc){
		if (doc == null){
			return;
		}
		SmApplSupportDoc suppDoc = doc.getSmApplSupportDoc();
		if (suppDoc == null || EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())){
			return;
		}
		List<SmApplDocFile> files = suppDoc.getSmApplDocFiles();
		if (files.size() == 1){
			SmApplDocFile only = files.get(0);
			only.setFileName(" ");
			only.setFilePath(" ");
			only.setValidatedAI(0);
			only.setMappedFieldAI(null);
			only.setAiApiResponseId(null);
		} else {
			files.remove(doc);
		}
	}
	
	public List<AiEpfResultDTO.AiEmployeeDTO> getEmployees() {
		if (epfs == null){
			return new ArrayList<>();
		}
		Map<String, AiEpfResultDTO.AiEmployeeDTO> unique = new LinkedHashMap<>();
		for (AiEpfResultDTO epf : epfs){
			for (AiEpfResultDTO.AiEmployeeDTO emp : epf.getEmployees()){
				String key = emp.getNricNumber();
				if (StringUtils.isEmpty(key)){
					key = emp.getKwspNumber() + "|" + emp.getEmployeeName();
				}
				if (!unique.containsKey(key)){
					unique.put(key, emp);
				}
			}
		}
		return new ArrayList<>(unique.values());
	}
	
	public AiTenancyDTO getTenancy() {
		return tenancy;
	}

	public List<AiEpfResultDTO.AiEmployeeDTO> getSelectedEmployees() {
		return selectedEmployees;
	}

	public void setSelectedEmployees(List<AiEpfResultDTO.AiEmployeeDTO> selectedEmployees) {
		this.selectedEmployees = selectedEmployees;
	}

	public boolean isPostAI() {
		return postAI;
	}

	public void setPostAI(boolean postAI) {
		this.postAI = postAI;
	}
	
	public boolean isBlockSubmit() {
		return blockSubmit || hasErrorMessage();
	}

	private static class JsonWrapper {
		private JSONArray entities;
		private long timeTakenMs;
		private String rawContent;
		private String ocrType;
		private long apiId;
		
		private JsonWrapper(long startTime, String rawContent, String ocrType) {
			this.entities = AiDocParser.parseFields(rawContent);
			this.timeTakenMs = System.currentTimeMillis() - startTime;
			this.rawContent = rawContent;
			this.ocrType = ocrType;
		}
		
		private JsonWrapper(AIApiResponse res){
			this(res.getTimeTakenMs(), res.getJsonResponse(), res.getOcrType());
		}
	}
	
}