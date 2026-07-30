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
import my.ep.persistence.entity.pm.common.PmDocument;
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

	// AI_API_RESPONSE is the record of an AI upload; the supporting document row is only a projection of it
	private List<AIApiResponse> docs = new ArrayList<>();

	private static final String TYPE_EPF = "epf";
	private static final String TYPE_TENANCY = "tenancy";
	private static final String TYPE_BANK = "bank";
	private static final String LBL_AI_UNAVAILABLE = "lbl.sm.ai.unavailable";
	private static final int SC_AI_UNAVAILABLE = 503;

	private List<AiEpfResultDTO> epfs;
	private AiTenancyDTO tenancy;
	private List<AiEpfResultDTO.AiEmployeeDTO> selectedEmployees;
	private boolean postAI = false;
	private boolean blockSubmit = false;
	private String docType;
	private SmAppl smAppl;

	/**
	 * Point the dialog at one document type, seeding it from AI_API_RESPONSE the first time.
	 * @param docType
	 * @param smAppl
	 */
	public void initContext(String docType, SmAppl smAppl) {
		this.smAppl = smAppl;
		// fileUpload re-renders this panel: keep what was just extracted, only a new type re-seeds
		if (StringUtils.equals(docType, this.docType)) {
			return;
		}
		this.docType = docType;
		docs = new ArrayList<>();
		epfs = null;
		tenancy = null;
		postAI = false;
		blockSubmit = false;

		seedFromPersisted(docType, smAppl);

		if (!docs.isEmpty()) {
			recomputeBlockSubmit(docType, smAppl);
		}
	}

	public static void loadTenancyDocs(SmAppl appl) {
		loadDocs(TYPE_TENANCY, appl);
	}

	public static void loadEpfDocs(SmAppl appl) {
		loadDocs(TYPE_EPF, appl);
	}

	// the file list is @ViewScoped state, so the page opening the dialog has to reach the bean instance
	private static void loadDocs(String docType, SmAppl appl) {
		FacesContext fc = FacesContext.getCurrentInstance();
		SmAIDocBackingBean bean = fc.getApplication()
				.evaluateExpressionGet(fc, "#{smAIDocBackingBean}", SmAIDocBackingBean.class);
		if (bean != null) {
			bean.initContext(docType, appl);
		}
	}

	/**
	 * Block Submit when the extracted company name does not match the registered one.
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
	 * Load the uploads already recorded against this application and their saved JSON.
	 * @param docType
	 * @param appl
	 */
	private void seedFromPersisted(String docType, SmAppl appl) {
		if (appl == null || appl.getApplId() == null) {
			return;
		}
		List<JsonWrapper> stored = new ArrayList<>();
		for (AIApiResponse res : SmServiceLookup.getAIApiProcessSO().getByApplId(appl.getApplId())) {
			if (!docType.equals(res.getOcrType())) {
				continue;
			}
			docs.add(res);
			stored.add(new JsonWrapper(res));
		}
		if (stored.isEmpty()) {
			return;
		}
		if (TYPE_EPF.equals(docType)) {
			processEPF(stored, appl);
		} else if (TYPE_TENANCY.equals(docType)) {
			processTenancyAgreement(stored, appl);
		}
		// a failed OCR is still recorded, so only claim a result when something actually parsed
		for (JsonWrapper json : stored) {
			if (json.entities != null) {
				postAI = true;
				return;
			}
		}
	}
	
	public static boolean isEpfDoc(PmDocument doc) {
		return doc != null && doc.getDocName() != null
				&& doc.getDocName().toUpperCase().contains("KWSP");
	}

	public static boolean isTenancyDoc(PmDocument doc) {
		return doc != null && doc.getDocName() != null
				&& doc.getDocName().toUpperCase().contains("SEWA")
				&& doc.getDocName().toUpperCase().contains("PREMIS");
	}

	public static boolean isBankDoc(PmDocument doc) {
		return doc != null && doc.getDocName() != null
				&& doc.getDocName().toUpperCase().contains("PENYATA AKAUN");
	}

	/**
	 * 0 = AI off for this application, 1 = on, 2 = something has already been extracted.
	 * @param appl
	 * @return
	 */
	public static boolean isAIApplicable(SmAppl appl) {
		return appl != null && appl.getAiProcessed() >= 1;
	}
	
	/**
	 * Show the AI upload control on a Supporting Documents row.
	 * @param appl
	 * @param doc
	 * @return
	 */
	public static boolean isAiUploadDocRow(SmAppl appl, PmDocument doc) {
		return isAIApplicable(appl) && (isBankDoc(doc) || isTenancyDoc(doc));
	}

	/**
	 * Set aiProcessed from the version gate and the AI toggle, then re-derive every verdict.
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
	 * OCR a document just uploaded on the Supporting Documents page.
	 * @param appl
	 * @param suppDoc
	 */
	public static void processUploadedSupportDoc(SmAppl appl, SmApplSupportDoc suppDoc) {
		if (!isAIApplicable(appl) || suppDoc == null || suppDoc.getPmDocument() == null) {
			return;
		}
		if (isTenancyDoc(suppDoc.getPmDocument())) {
			processTenancyStatement(appl, suppDoc);
		} else {
			processBankStatement(appl, suppDoc);
		}
		recomputeAiProcessed(appl);
	}

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
	 * Upload, OCR and record one file chosen in the AI Assistant dialog.
	 * @param event
	 */
	public void handleSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() == null) {
			return;
		}
		
		String filePath = Calendar.getInstance().get(Calendar.YEAR)
				+ "/SM/APPLICATION/" + smAppl.getApplNo() + "/SD/";
		String fileName = generateUniqueFilename(StringUtils.replace(event.getFile().getFileName(), " ", "-"));

		SmCommonUtil.processUpload(filePath, event, fileName);
		JsonWrapper result = sendDocumentOCR(docType, filePath, fileName, smAppl.getApplNo());
		if (result == null){
			// OCR never answered: still record the upload, the file belongs to the supplier either way
			result = new JsonWrapper(System.currentTimeMillis(), "", docType);
		}
		// Tenancy is single-file: a new upload REPLACES the previous doc, but only once this one is in
		if (TYPE_TENANCY.equals(docType)){
			for (AIApiResponse previous : docs){
				deleteResponse(previous);
			}
			docs.clear();
		}
		docs.add(saveResponse(smAppl, result, fileName, filePath));
		if (docType != null) {
			processDoc(docType, smAppl);
			if (result.entities != null){
				postAI = true;
			}
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
		return oriFilename + "_" + timestamp + extension;
	}

	/**
	 * Project responses uploaded before the document list existed onto their Supporting Documents row.
	 * @param appl
	 */
	public static void attachPendingAiFiles(SmAppl appl){
		// not gated on the AI toggle: an uploaded file stays the supplier's even if AI is switched off
		if (appl == null || appl.getApplId() == null
				|| EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		for (AIApiResponse res : SmServiceLookup.getAIApiProcessSO().getByApplId(appl.getApplId())){
			if (res.getFileName() == null || findProjectedFile(appl, res.getApiResponseId()) != null){
				continue;
			}
			SmApplSupportDoc suppDoc = findSupportDoc(appl, res.getOcrType());
			if (suppDoc == null){
				continue;
			}
			if (suppDoc.getSmApplDocFiles() == null){
				suppDoc.setSmApplDocFiles(new ArrayList<SmApplDocFile>());
			}
			removeBlankDocFiles(suppDoc);
			SmApplDocFile file = new SmApplDocFile();
			file.setFileName(res.getFileName());
			file.setFilePath(res.getFilePath());
			file.setAiApiResponseId(res.getApiResponseId());
			file.setSmApplSupportDoc(suppDoc);
			suppDoc.getSmApplDocFiles().add(file);
			log.info("*** Bound pending AI file ***" + res.getFileName());
		}
	}

	/**
	 * The active AI responses of one type; the supporting document row is only a projection of these.
	 * @param appl
	 * @param ocrType
	 * @return
	 */
	private static List<AIApiResponse> findResponses(SmAppl appl, String ocrType){
		List<AIApiResponse> found = new ArrayList<>();
		if (appl == null || appl.getApplId() == null){
			return found;
		}
		for (AIApiResponse res : SmServiceLookup.getAIApiProcessSO().getByApplId(appl.getApplId())){
			if (ocrType.equals(res.getOcrType())){
				found.add(res);
			}
		}
		return found;
	}

	/**
	 * The doc file this response was projected onto, null while it is still unbound.
	 * @param appl
	 * @param apiResponseId
	 * @return
	 */
	private static SmApplDocFile findProjectedFile(SmAppl appl, Long apiResponseId){
		if (apiResponseId == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return null;
		}
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())){
				continue;
			}
			for (SmApplDocFile file : suppDoc.getSmApplDocFiles()){
				if (apiResponseId.equals(file.getAiApiResponseId())){
					return file;
				}
			}
		}
		return null;
	}

	// the AI verdict shows on the Supporting Documents row, which exists only once projected
	private static void markProjectedFile(SmAppl appl, AIApiResponse res, String label, int validated){
		SmApplDocFile file = findProjectedFile(appl, res.getApiResponseId());
		if (file != null){
			file.setMappedFieldAI(label);
			file.setValidatedAI(validated);
		}
	}

	// drop the placeholder rows getDefaultSmApplSupportingDoc() seeds, same as an interactive upload
	private static void removeBlankDocFiles(SmApplSupportDoc suppDoc){
		for (int i = suppDoc.getSmApplDocFiles().size() - 1; i >= 0; i--){
			String filename = suppDoc.getSmApplDocFiles().get(i).getFileName();
			if (filename == null || filename.trim().isEmpty()){
				suppDoc.getSmApplDocFiles().remove(i);
			}
		}
	}

	/**
	 * The Supporting Documents row an AI type projects onto; bank already has its own row.
	 * @param appl
	 * @param ocrType
	 * @return
	 */
	private static SmApplSupportDoc findSupportDoc(SmAppl appl, String ocrType){
		if (appl == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return null;
		}
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			PmDocument doc = suppDoc.getPmDocument();
			if ((TYPE_EPF.equals(ocrType) && isEpfDoc(doc))
					|| (TYPE_TENANCY.equals(ocrType) && isTenancyDoc(doc))){
				return suppDoc;
			}
		}
		return null;
	}

	/**
	 * Re-derive the dialog's extracted values from the docs it currently holds.
	 * @param type
	 * @param appl
	 */
	public void processDoc(String type, SmAppl appl){
		if (StringUtils.isEmpty(type)){
			return;
		}
		docType = type.trim();
		blockSubmit = false;
		epfs = null;
		tenancy = null;
		if (EntityHelper.isCollectionEmpty(docs)){
			return;
		}
		log.debug("processDoc: " + docs.size() + " document(s), type=" + docType);

		List<JsonWrapper> jsonWrappers = new ArrayList<>();
		for (AIApiResponse res : docs){
			jsonWrappers.add(new JsonWrapper(res));
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
	
	private static AIApiResponse saveResponse(SmAppl smAppl, JsonWrapper jsonWrapper, String fileName, String filePath){
		AIApiResponse apiResp = new AIApiResponse();

		apiResp.setSupplierId(smAppl.getSmSupplier().getSupplierId());
		apiResp.setDocType("SM");
		apiResp.setDocId(smAppl.getApplId());
		apiResp.setDocNo(smAppl.getApplNo());
		apiResp.setFileName(fileName);
		apiResp.setFilePath(filePath);

		apiResp.setJsonResponse(jsonWrapper.rawContent);
		apiResp.setTimeTakenMs(jsonWrapper.timeTakenMs);
		apiResp.setOcrType(jsonWrapper.ocrType);
		apiResp.setApiName("test");		//temp
		apiResp.setResponseStatus(jsonWrapper.statusCode);
		
		AIApiResponse saved = SmServiceLookup.getAIApiProcessSO().save(apiResp);
		jsonWrapper.apiId = saved.getApiResponseId();
		return saved;
	}

	// delete is a soft delete on the response; the projected doc file, if any, is dropped with it
	private void deleteResponse(AIApiResponse res){
		SmServiceLookup.getAIApiProcessSO().delete(res.getApiResponseId());
		detachFromSupportDoc(res);
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
	 * Apply the extracted values to the application.
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
	 * OCR and verify bank statements uploaded on the Supporting Documents page.
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
		if (!isBankDoc(suppDoc.getPmDocument())){
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
				doc.setAiApiResponseId(saveResponse(appl, result, doc.getFileName(), doc.getFilePath())
						.getApiResponseId());
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
	 * AI verification for a Tenancy Agreement uploaded in Supporting Documents, not the Premises dialog.
	 * @param appl
	 * @param suppDoc
	 */
	public static void processTenancyStatement(SmAppl appl, SmApplSupportDoc suppDoc){
		if (suppDoc == null || suppDoc.getPmDocument() == null || appl == null || appl.getSmSupplier() == null){
			return;
		}
		if (!isTenancyDoc(suppDoc.getPmDocument()) || EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())){
			return;
		}
		List<JsonWrapper> jsonWrappers = new ArrayList<>();
		for (SmApplDocFile doc : suppDoc.getSmApplDocFiles()){
			if (doc.getFileName() == null || doc.getFileName().trim().isEmpty() || doc.getAiApiResponseId() != null){
				continue;
			}
			JsonWrapper result = sendDocumentOCR(TYPE_TENANCY, doc.getFilePath(), doc.getFileName(), appl.getApplNo());
			if (result != null){
				doc.setAiApiResponseId(saveResponse(appl, result, doc.getFileName(), doc.getFilePath())
						.getApiResponseId());
				jsonWrappers.add(result);
			}
		}
		boolean hasError = showErrorMessage(TYPE_TENANCY, jsonWrappers);
		if (!hasError){
			validateTenancy(appl);
		}
	}
	
	public static void validateBank(SmAppl appl){
		validateBank(appl, false);
	}

	// emitMessages: the submit-time re-check also raises the mismatch messages, the page-load pass stays silent
	private static void validateBank(SmAppl appl, boolean emitMessages){
		if (!isAIApplicable(appl)){
			return;
		}
		if (appl == null || appl.getSmSupplier() == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		// any supporting document may carry a bank statement, so every matching row is validated
		boolean hasBankDoc = false;
		List<AiBankDocDTO> entities = new ArrayList<>();
		SmFieldChangeUtil.setBankFieldChangeStatus(appl);
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (!isBankDoc(suppDoc.getPmDocument()) || EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())){
				continue;
			}
			hasBankDoc = true;
			// each row marks its own files, so the responses stay grouped by row
			List<AiBankDocDTO> docEntities = collectBankResponses(suppDoc);
			validateBankEntity(appl, suppDoc, docEntities);
			entities.addAll(docEntities);
		}
		if (!hasBankDoc){
			log.info("No bank statement supporting document found for application " + appl.getApplNo());
			return;
		}
		if (emitMessages){
			showErrorMessageBank(entities, null);
		}
	}

	private static List<AiBankDocDTO> collectBankResponses(SmApplSupportDoc suppDoc){
		List<AiBankDocDTO> entities = new ArrayList<>();
		for (SmApplDocFile docFile : suppDoc.getSmApplDocFiles()){
			if (docFile.getFileName() == null || docFile.getFileName().trim().isEmpty()){
				continue;
			}
			AIApiResponse response = SmServiceLookup.getAIApiProcessSO()
					.getById(docFile.getAiApiResponseId());
			if (response != null){
				entities.add(AiDocParser.toBankDoc(response.getApiResponseId(),
						AiDocParser.parseFields(response.getJsonResponse())));
			}
		}
		return entities;
	}
	
	public static void validateTenancy(SmAppl appl){
		if (!isAIApplicable(appl)){
			return;
		}
		if (appl.getSmSupplier() == null){
			return;
		}
		SmFacility hqFacility = AiDocValidator.getHeadquartersFacility(appl);
		AiDocValidator.clearTenancyAiFields(hqFacility);

		List<AIApiResponse> responses = findResponses(appl, TYPE_TENANCY);
		if (EntityHelper.isCollectionEmpty(responses)
				|| EntityHelper.isCollectionEmpty(appl.getSmFacilities())){
			return;
		}
		validateTenancyFiles(appl, responses, hqFacility);
	}

	// read the supplier side off the same facility setTenancyAiFields() writes the verdict onto
	private static void validateTenancyFiles(SmAppl appl, List<AIApiResponse> responses, SmFacility hqFacility){
		String landlordsNameSupp = "";
		String companyName = appl.getSmSupplier().getCompanyName();

		BigDecimal rentalRate = null;
		SmAddress addr = null;
		if (hqFacility != null){
			landlordsNameSupp = hqFacility.getOwnerName();
			if (hqFacility.getRentalRate() != null){
				rentalRate = hqFacility.getRentalRate().setScale(2);
			}
			addr = hqFacility.getSmAddress();
		}

		for (AIApiResponse response : responses){
			JSONArray fields = AiDocParser.parseFields(response.getJsonResponse());
			if (fields == null){
				continue;
			}
			AiTenancyDTO te = AiDocParser.toTenancy(fields);
			AiDocValidator.validateTenancy(te, landlordsNameSupp, companyName, rentalRate, addr);

			markProjectedFile(appl, response, te.getLabel(), te.hasError() ? 2 : 1); //1-ok, 2-err
			AiDocValidator.setTenancyAiFields(hqFacility, te);
		}
	}

	public static void validateEPF(SmAppl appl){
		if (!isAIApplicable(appl)){
			return;
		}
		clearPersonnelAiFields(appl);

		List<AIApiResponse> responses = findResponses(appl, TYPE_EPF);
		if (EntityHelper.isCollectionEmpty(responses)
				|| EntityHelper.isCollectionEmpty(appl.getSmPersonnels())){
			return;
		}

		Map<String, SmPersonnel> byNric = new HashMap<>();
		for (SmPersonnel per : appl.getSmPersonnels()){
			byNric.put(per.getIdentificationNo(), per);
		}

		for (AIApiResponse response : responses){
			applyEpfValidation(response, byNric);
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

	// One EPF file: match extracted employees to personnel by NRIC and light the EPF No./name AI icons.
	private static void applyEpfValidation(AIApiResponse response, Map<String, SmPersonnel> byNric){
		JSONArray fields = AiDocParser.parseFields(response.getJsonResponse());
		if (fields == null){
			return;
		}
		AiEpfResultDTO ee = AiDocParser.toEpf(fields);
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
	 * Extracted employee names, empty when AI does not apply so the caller leaves manual adds unrestricted.
	 * @param appl
	 * @return
	 */
	public static Set<String> getAiEpfEmployeeNames(SmAppl appl){
		Set<String> names = new HashSet<>();
		if (!isAIApplicable(appl)){
			return names;
		}
		for (AIApiResponse response : findResponses(appl, TYPE_EPF)){
			collectEpfEmployeeNames(response, names);
		}
		return names;
	}

	// collect trimmed AI-extracted employee names from one EPF doc file into names
	private static void collectEpfEmployeeNames(AIApiResponse response, Set<String> names){
		JSONArray fields = AiDocParser.parseFields(response.getJsonResponse());
		if (fields == null){
			return;
		}
		AiEpfResultDTO ee = AiDocParser.toEpf(fields);
		for (AiEpfResultDTO.AiEmployeeDTO emp : ee.getEmployees()){
			if (emp.getEmployeeName() != null && !emp.getEmployeeName().trim().isEmpty()){
				names.add(emp.getEmployeeName().trim());
			}
		}
	}

	/**
	 * A manually added new personnel must appear by name in the uploaded EPF statement; throws to abort the add.
	 * @param appl
	 * @param personnel
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
	 * An EPF or Tenancy doc uploaded via the AI Assistant cannot be deleted from the Supporting Documents listing.
	 * @param doc
	 * @param file
	 * @return
	 */
	public static boolean isAiSuppDocDeleteLocked(SmApplSupportDoc doc, SmApplDocFile file){
		if (file == null || file.getAiApiResponseId() == null
				|| doc == null || doc.getPmDocument() == null){
			return false;
		}
		return isEpfDoc(doc.getPmDocument()) || isTenancyDoc(doc.getPmDocument());
	}

	/**
	 * EPF rows carry the neutral AI icon (validatedAI 3).
	 * @param appl
	 */
	public static void markEpfAiDocs(SmAppl appl){
		if (!isAIApplicable(appl) || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (suppDoc.getPmDocument() == null || !isEpfDoc(suppDoc.getPmDocument())
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
				.getById(docFile.getAiApiResponseId());
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
		boolean hasUnreadable = false;
		StringBuilder sb = new StringBuilder();

		for (AiBankDocDTO doc : docEntities){
			// nothing parsed out of this file: it blocks submit too, but it is not a mismatch
			if (doc.isError()){
				hasUnreadable = true;
				continue;
			}
			if (EntityHelper.isCollectionEmpty(doc.getBankStatements())){
				continue;
			}
			// per-bank mismatch rows, labels from the bundle
			if (appendBankMismatchRows(doc.getBankStatements(), sb)){
				hasWrongCompanyName = true;
			}
		}

		if (hasUnreadable){
			FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, null, AiDocParser.label("lbl.sm.ai.bank.invalid")));
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
	 * Block submit when a bank statement does not match Bank Info.
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
					|| !isBankDoc(suppDoc.getPmDocument())) {
				continue;
			}
			for (SmApplDocFile file : suppDoc.getSmApplDocFiles()) {
				if (file.getValidatedAI() == 2) {
					validateBank(appl, true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Recompute the task-list AI flag: 2 once any document has a parsed AI response.
	 * @param appl
	 */
	public static void recomputeAiProcessed(SmAppl appl){
		// 0 means the version gate turned AI off for this application, leave it alone
		if (appl == null || appl.getApplId() == null || appl.getAiProcessed() == 0){
			return;
		}
		int state = 1;
		for (AIApiResponse res : SmServiceLookup.getAIApiProcessSO().getByApplId(appl.getApplId())){
			// a recorded OCR failure is not an extraction
			if (AiDocParser.parseFields(res.getJsonResponse()) != null){
				state = 2;
				break;
			}
		}
		appl.setAiProcessed(state);
	}
	
	private static boolean showErrorMessage(String docType, List<JsonWrapper> jsonWrappers){
		if (EntityHelper.isCollectionEmpty(jsonWrappers)){
			return false;
		}
		String err = null;
		for (JsonWrapper json : jsonWrappers){
			int statusCode = json.statusCode;
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
		
	public static List<AiBankDocDTO> processBank(List<JsonWrapper> jsonWrappers){
		if (EntityHelper.isCollectionEmpty(jsonWrappers)){
			return new ArrayList<>();
		}
		List<AiBankDocDTO> docEnts = new ArrayList<>();
		for (JsonWrapper json : jsonWrappers){
			if (json.entities == null){
				continue;
			}
			docEnts.add(AiDocParser.toBankDoc(json.apiId, json.entities));
		}
		return docEnts;
	}

	public void processTenancyAgreement(List<JsonWrapper> entities, SmAppl appl){
		if (EntityHelper.isCollectionEmpty(entities)){
			return;
		}
		for (JsonWrapper entityWrapper : entities){
			if (entityWrapper.entities == null){
				continue;
			}
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
			if (entityWrapper.entities == null){
				continue;
			}
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
	
	
	public List<AIApiResponse> getDocs() {
		return docs;
	}

	public void deleteDoc(AIApiResponse doc) {
		deleteResponse(doc);
		docs.remove(doc);
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
			blockSubmit = false;
		} else {
			processDoc(docType, smAppl);
		}
		recomputeAiProcessed(smAppl);
	}

	private void detachFromSupportDoc(AIApiResponse res){
		if (res == null || res.getApiResponseId() == null || smAppl == null
				|| EntityHelper.isCollectionEmpty(smAppl.getSmApplSupportDocs())){
			return;
		}
		for (SmApplSupportDoc suppDoc : smAppl.getSmApplSupportDocs()){
			if (EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())){
				continue;
			}
			List<SmApplDocFile> files = suppDoc.getSmApplDocFiles();
			for (SmApplDocFile file : files){
				if (!res.getApiResponseId().equals(file.getAiApiResponseId())){
					continue;
				}
				// the row keeps one blank file when nothing is uploaded, same as the rest of the page
				if (files.size() == 1){
					file.setFileName(" ");
					file.setFilePath(" ");
					file.setValidatedAI(0);
					file.setMappedFieldAI(null);
					file.setAiApiResponseId(null);
				} else {
					files.remove(file);
				}
				return;
			}
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
		private int statusCode;

		private JsonWrapper(long startTime, String rawContent, String ocrType) {
			this.entities = AiDocParser.parseFields(rawContent);
			this.timeTakenMs = System.currentTimeMillis() - startTime;
			this.rawContent = rawContent;
			this.ocrType = ocrType;
			// nothing came back at all: the call never reached the service
			this.statusCode = StringUtils.isEmpty(rawContent)
					? SC_AI_UNAVAILABLE : AiDocParser.getStatusCode(rawContent);
		}

		private JsonWrapper(AIApiResponse res){
			this.entities = AiDocParser.parseFields(res.getJsonResponse());
			this.timeTakenMs = res.getTimeTakenMs() == null ? 0L : res.getTimeTakenMs();
			this.rawContent = res.getJsonResponse();
			this.ocrType = res.getOcrType();
			this.apiId = res.getApiResponseId() == null ? 0L : res.getApiResponseId();
			this.statusCode = res.getResponseStatus() == null ? SC_AI_UNAVAILABLE : res.getResponseStatus();
		}
	}
	
}