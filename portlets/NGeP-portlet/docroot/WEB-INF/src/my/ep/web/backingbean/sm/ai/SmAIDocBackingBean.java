package my.ep.web.backingbean.sm.ai;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
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

import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
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
import my.ep.web.backingbean.sm.ai.AiDocSupport.AiUserEdit;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
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
	private static final String AI_UPLOAD_MARKER = "_ai";
	private static final String EL_BACKING_BEAN = "#{backingBean}";
	private static final String LBL_DOC_INVALID = "lbl.sm.ai.doc.invalid";
	private static final String LBL_EPF_MISMATCH = "lbl.sm.ai.epf.mismatch";
	private static final String RA_DIALOG_MESSAGES = "smAiDialogMessages";

	private List<AiEpfResultDTO> epfs;
	private AiTenancyDTO tenancy;
	private Map<Long, AiTenancyDTO> tenancies = new HashMap<>();
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
		tenancies.clear();
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
			bean.tenancies.clear();
			bean.initContext(docType, appl);
		}
	}

	/**
	 * Block Submit when the extracted company name does not match the registered one. EPF is not
	 * blocked - a wrong-company statement simply contributes no employees to the table.
	 * @param docType
	 * @param appl
	 */
	private void recomputeBlockSubmit(String docType, SmAppl appl) {
		blockSubmit = false;
		String companyName = (appl != null && appl.getSmSupplier() != null)
				? appl.getSmSupplier().getCompanyName() : "";
		if (TYPE_TENANCY.equals(docType) && tenancy != null 
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
	
	// PM_DOCUMENT.AI_TYPE names the OCR type, so the document name is no longer matched
	public static boolean isEpfDoc(PmDocument doc) {
		return doc != null && TYPE_EPF.equalsIgnoreCase(StringUtils.trim(doc.getAiType()));
	}

	public static boolean isTenancyDoc(PmDocument doc) {
		return doc != null && TYPE_TENANCY.equalsIgnoreCase(StringUtils.trim(doc.getAiType()));
	}

	public static boolean isBankDoc(PmDocument doc) {
		return doc != null && TYPE_BANK.equalsIgnoreCase(StringUtils.trim(doc.getAiType()));
	}

	/**
	 * Show the AI upload control on a Supporting Documents row.
	 * @param doc
	 * @return
	 */
	public static boolean isAiUploadDocRow(PmDocument doc) {
		return isBankDoc(doc) || isTenancyDoc(doc);
	}

	/**
	 * Decide from the version gate and the AI toggle whether AI is on, then re-derive every verdict.
	 * @param appl
	 * @return whether AI is on for this application
	 */
	public static boolean initAiState(SmAppl appl) {
		if (appl == null) {
			return false;
		}
		if (SmApplicationUtil.getAppVersionDetailsWithRedmineNo(appl,
				SupplierManagementConst.APP_VERSION_REDMINE_NO_40112, SupplierManagementConst.PHASE_1)) {
			return false;
		}
		// nothing extracted yet + AI off: hide everything. Already extracted ignores the toggle
		if (!AiDocSupport.isAiEnabled()
				&& !SmServiceLookup.getAIApiProcessSO().hasExtraction(appl.getApplId())) {
			return false;
		}
		validateBank(appl);
		validateTenancy(appl);
		validateEPF(appl);
		markEpfAiDocs(appl);
		return true;
	}

	/**
	 * OCR a document just uploaded on the Supporting Documents page.
	 * @param appl
	 * @param suppDoc
	 */
	public static void processUploadedSupportDoc(SmAppl appl, SmApplSupportDoc suppDoc) {
		if (appl == null || suppDoc == null || suppDoc.getPmDocument() == null) {
			return;
		}
		// the dialog fires close on dismiss too: nothing to OCR when no new file came in
		if (!hasPendingAiUpload(suppDoc)) {
			return;
		}
		if (isTenancyDoc(suppDoc.getPmDocument())) {
			processTenancyStatement(appl, suppDoc);
		} else {
			processBankStatement(appl, suppDoc);
		}
	}

	// a file still without its AI_API_RESPONSE id is one this close event has to process
	private static boolean hasPendingAiUpload(SmApplSupportDoc suppDoc) {
		if (EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())) {
			return false;
		}
		for (SmApplDocFile doc : suppDoc.getSmApplDocFiles()) {
			if (doc.getAiApiResponseId() == null && doc.getFileName() != null
					&& !doc.getFileName().trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public static void revalidateAfterDelete(SmAppl appl) {
		if (appl == null) {
			return;
		}
		validateTenancy(appl);
		validateEPF(appl);
		markEpfAiDocs(appl);
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
		// marker last before the extension so the delete-lock can find it
		return oriFilename + "_" + timestamp + AI_UPLOAD_MARKER + extension;
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
	 * The Supporting Documents row an AI type projects onto.
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
					|| (TYPE_TENANCY.equals(ocrType) && isTenancyDoc(doc))
					|| (TYPE_BANK.equals(ocrType) && isBankDoc(doc))){
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
		tenancies.clear();
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
		///temporary demo filter - end
		
		String sessionId = ((PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest()).getRequestedSessionId(); 
		long startTime = System.currentTimeMillis();
		
		try {
			String requestBody = AiDocParser.buildOcrRequestBody(docType, filepath, filename, clientRequestId);
			AiOcrClient.OcrResponse result = AiOcrClient.callDocumentOcr(requestBody, docType);
			SLAUtils.getInstance().logSLASubInfo("SM", "AI", sessionId, "null",
					"SmAIDocbackingBean.sendDocumentOCR", "1:"+filename, startTime);
			if (result == null) {
				addMessageOnce(FacesMessage.SEVERITY_WARN, LBL_AI_UNAVAILABLE);
				return null;
			}
			return new JsonWrapper(startTime, result.body, docType, result.status);
		} catch (IOException e) {
			log.error("OCR call failed for " + filename, e);

			addMessageOnce(FacesMessage.SEVERITY_WARN, LBL_AI_UNAVAILABLE);
		}

		return null;
	}
	
	/**
	 * The DB is WE8MSWIN1252, which stores anything outside it as 0xBF and loses it for good.
	 * Escape those as JSON backslash-u units so the stored JSON still parses to what the AI sent.
	 * @param json
	 * @return
	 */
	private static String escapeNonDbChars(String json) {
		if (json == null) {
			return null;
		}
		CharsetEncoder dbCharset = Charset.forName("windows-1252").newEncoder();
		StringBuilder sb = new StringBuilder(json.length());
		for (char c : json.toCharArray()) {
			if (dbCharset.canEncode(c)) {
				sb.append(c);
			} else {
				sb.append(String.format("\\u%04x", (int) c));
			}
		}
		return sb.toString();
	}

	private static AIApiResponse saveResponse(SmAppl smAppl, JsonWrapper jsonWrapper, String fileName, String filePath){
		AIApiResponse apiResp = new AIApiResponse();

		apiResp.setSupplierId(smAppl.getSmSupplier().getSupplierId());
		apiResp.setDocType("SM");
		apiResp.setDocId(smAppl.getApplId());
		apiResp.setDocNo(smAppl.getApplNo());
		apiResp.setFileName(fileName);
		apiResp.setFilePath(filePath);

		apiResp.setJsonResponse(escapeNonDbChars(jsonWrapper.rawContent));
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
		// Supporting Documents has no dialog, so its messages stay with the page
		if (!isSupportDocPage()){
			List<FacesMessage> msgs = dialogMessages();
			for (FacesMessage existing : msgs){
				if (existing.getSeverity() == severity
						&& StringUtils.equals(existing.getDetail(), detail)){
					return;
				}
			}
			msgs.add(new FacesMessage(severity, null, detail));
			return;
		}
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

	/**
	 * The AI Assistant dialog's messages. They are kept in the request, not the FacesContext, because
	 * any page-level p:messages without for/globalOnly would repeat a FacesContext message outside the dialog.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<FacesMessage> dialogMessages(){
		Map<String, Object> req = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		List<FacesMessage> msgs = (List<FacesMessage>) req.get(RA_DIALOG_MESSAGES);
		if (msgs == null){
			msgs = new ArrayList<>();
			req.put(RA_DIALOG_MESSAGES, msgs);
		}
		return msgs;
	}

	/**
	 * Dialog message texts of one severity, "error" or "warn".
	 * @param severity
	 * @return
	 */
	public List<String> getDialogMessages(String severity){
		FacesMessage.Severity sev = "warn".equals(severity) ? FacesMessage.SEVERITY_WARN : FacesMessage.SEVERITY_ERROR;
		List<String> details = new ArrayList<>();
		for (FacesMessage msg : dialogMessages()){
			if (msg.getSeverity() == sev){
				details.add(msg.getDetail());
			}
		}
		return details;
	}

	// docType is re-seeded per page but never cleared, so it cannot say where we are
	private static boolean isSupportDocPage(){
		FacesContext fc = FacesContext.getCurrentInstance();
		SmApplicationBackingBean bb = fc.getApplication().evaluateExpressionGet(fc,
				EL_BACKING_BEAN, SmApplicationBackingBean.class);
		return bb != null && bb.getManagedBean() != null
				&& SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS
						.equalsIgnoreCase(bb.getManagedBean().getCurrentPageType());
	}

	private static boolean hasErrorMessage(){
		String soft = AiDocParser.label(LBL_EPF_MISMATCH);
		for (FacesMessage msg : dialogMessages()){
			if (msg.getSeverity() == FacesMessage.SEVERITY_ERROR
					&& !StringUtils.equals(msg.getDetail(), soft)){
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
			String key = TYPE_EPF.equals(docType) ? LBL_EPF_MISMATCH : "lbl.sm.ai.tnc.mismatch";
			addMessageOnce(FacesMessage.SEVERITY_ERROR, key);
			return;
		}
		if (hasErrorMessage()){
			return;
		}
		log.info(docType+"\t"+appl.getApplNo());
		if (TYPE_EPF.equals(docType)){
			if (!EntityHelper.isCollectionEmpty(selectedEmployees)){
				submitEPF(appl);
			} else {
				// nobody ticked, so nobody was accepted - re-derive to drop any icon still showing
				validateEPF(appl);
			}
			// the acceptance is recorded, so a reopened dialog starts unticked
			selectedEmployees = null;
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
			personnels.put(AiDocValidator.normalizeIdNo(per.getIdentificationNo()), per);
		}

		//check for dupe IC/passport
		for (AiEpfResultDTO.AiEmployeeDTO employee : selectedEmployees) {
			SmPersonnel per = personnels.get(employee.getIdNo());
			if (per != null){
				// the applicant applied all three fields from the dialog, so all three get the icon
				// tenancy accepts every extracted field on submit the same way
				acceptEpfUserEdit(appl, employee.getIdNo(),
						AiUserEdit.F_EPF_NO, AiUserEdit.F_NAME, AiUserEdit.F_IC);
				// applying the statement overwrites the eP name as well as the EPF No.
				per.setName(employee.getEmployeeName());
				per.setNameAI(employee.getEmployeeName());
				per.setEpfNo(employee.getKwspNumber());
				per.setEpfNoAI(employee.getKwspNumber());
				continue;
			}
			// a new row is wholly AI-populated, so all three fields are accepted
			acceptEpfUserEdit(appl, employee.getIdNo(),
					AiUserEdit.F_EPF_NO, AiUserEdit.F_NAME, AiUserEdit.F_IC);
			per = new SmPersonnel();
			per.setName(employee.getEmployeeName());
			per.setIdentificationNo(employee.getIdNo());
			if (employee.isPassport()){
				per.setIdentificationTypeId(SmApplicationUtil.retrievePmParameterByTypeAndCode(
						ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_PASSPORT)
						.getParameterId());
			} else {
				per.setIdentificationTypeId(SmApplicationUtil.retrievePmParameterByTypeAndCode(
						ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_IC)
						.getParameterId());
			}
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
		
		SmApplicationBackingBean bb = saveAppl();
		if (bb != null){
			validateEPF(bb.getManagedBean().getSmAppl());
		}
	}

	// save through the page's own Save; the reload swaps in a fresh appl graph
	private SmApplicationBackingBean saveAppl(){
		FacesContext fc = FacesContext.getCurrentInstance();
		SmApplicationBackingBean bb = fc.getApplication().evaluateExpressionGet(fc, 
				EL_BACKING_BEAN, SmApplicationBackingBean.class);
		if (bb != null){
			bb.saveOrUpdateAppl();
			SmApplicationUtil.sortPersonnelByRoleInCompany(bb.getManagedBean().getSmAppl());
			// the reload drops the @Transient roleName, same as every other personnel mutator
			bb.calcPersonnelListCount();
			smAppl = bb.getManagedBean().getSmAppl();
		}
		return bb;
	}


	private void submitTenancy(){
		FacesContext fc = FacesContext.getCurrentInstance();
		SmApplicationBackingBean bb = fc.getApplication().evaluateExpressionGet(fc, 
				EL_BACKING_BEAN, SmApplicationBackingBean.class);
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
			if (!AiDocSupport.isBankOcrAllowed(doc.getFileName())){
				log.info("google.doc.bank.co.name.check.onoff is off, skipping AI for " + doc.getFileName());
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
		
		// a rejected file gets its verdict icon too; only the mismatch messages are skipped
		List<AiBankDocDTO> docEntities = processBank(jsonWrappers);
		validateBankEntity(appl, suppDoc, docEntities);
		if (!hasError){
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
		showErrorMessage(TYPE_TENANCY, jsonWrappers);
		validateTenancy(appl);
	}
	
	public static void validateBank(SmAppl appl){
		validateBank(appl, false);
	}

	// emitMessages: the submit-time re-check also raises the mismatch messages, the page-load pass stays silent
	private static void validateBank(SmAppl appl, boolean emitMessages){
		if (appl == null || appl.getSmSupplier() == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		// any supporting document may carry a bank statement, so every matching row is validated
		boolean hasBankDoc = false;
		List<AiBankDocDTO> entities = new ArrayList<>();
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
				entities.add(AiDocParser.toBankDoc(response));
			}
		}
		return entities;
	}
	
	public static void validateTenancy(SmAppl appl){
		if (appl == null){
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
		for (AIApiResponse response : responses){
			AiTenancyDTO te = validatedTenancy(appl, hqFacility, response);
			markProjectedFile(appl, response, te.getLabel(), te.getVerdict());
			// nothing read from this file: it must not blank the AI fields an earlier file set
			if (te.isUnavailable() || te.isError()){
				continue;
			}
			Map<String, Set<String>> edits = AiUserEdit.parse(response.getUserEdit());
			Set<String> accepted = new HashSet<>(AiUserEdit.accepted(edits, AiUserEdit.KEY_TENANCY));
			accepted.removeAll(AiUserEdit.fields(edits, AiUserEdit.KEY_TENANCY));
			AiDocValidator.setTenancyAiFields(hqFacility, te, accepted);
		}
	}

	// one tenancy file scored against the supplier's own premises values
	private static AiTenancyDTO validatedTenancy(SmAppl appl, SmFacility hqFacility, AIApiResponse response){
		String landlordsNameSupp = "";
		BigDecimal rentalRate = null;
		SmAddress addr = null;
		if (hqFacility != null){
			landlordsNameSupp = hqFacility.getOwnerName();
			if (hqFacility.getRentalRate() != null){
				rentalRate = hqFacility.getRentalRate().setScale(2);
			}
			addr = hqFacility.getSmAddress();
		}
		AiTenancyDTO te = AiDocParser.toTenancy(response);
		AiDocValidator.validateTenancy(te, landlordsNameSupp, appl.getSmSupplier().getCompanyName(),
				rentalRate, addr);
		return te;
	}

	public static void validateEPF(SmAppl appl){
		if (appl == null){
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
			byNric.put(AiDocValidator.normalizeIdNo(per.getIdentificationNo()), per);
		}

		String companyName = appl.getSmSupplier() != null ? appl.getSmSupplier().getCompanyName() : null;
		for (AIApiResponse response : responses){
			applyEpfValidation(response, companyName, byNric);
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
	private static void applyEpfValidation(AIApiResponse response, String companyName,
			Map<String, SmPersonnel> byNric){
		JSONArray fields = AiDocParser.parseFields(response.getJsonResponse());
		if (fields == null){
			return;
		}
		AiEpfResultDTO ee = AiDocParser.toEpf(response.getApiResponseId(), fields);
		// a statement issued to another company lights nobody, as in the AI Assistant table
		AiDocValidator.validateEpf(ee, companyName);
		if (!ee.isValidEmployerName()){
			return;
		}
		Map<String, Set<String>> edits = AiUserEdit.parse(response.getUserEdit());
		for (AiEpfResultDTO.AiEmployeeDTO emp : ee.getEmployees()){
			applyEpfEmployee(emp, byNric.get(emp.getIdNo()), edits);
		}
	}

	private static void applyEpfEmployee(AiEpfResultDTO.AiEmployeeDTO emp, SmPersonnel per,
			Map<String, Set<String>> edits){
		if (per == null){
			return;
		}
		// #40112 - the icon means the applicant submitted this field from the dialog. A personnel
		// added by hand matches the statement by IC/passport but was never accepted, so it stays blank.
		Set<String> accepted = AiUserEdit.accepted(edits, emp.getIdNo());
		if (accepted.isEmpty()){
			return;
		}
		Set<String> edited = AiUserEdit.fields(edits, emp.getIdNo());
		if (accepted.contains(AiUserEdit.F_EPF_NO) && !edited.contains(AiUserEdit.F_EPF_NO)){
			per.setEpfNoAI(emp.getKwspNumber());
		}

		// the NRIC is what matched this employee to the personnel, so it is AI-verified on its
		// own; nesting it under the name check let an edited name switch the IC icon off too
		if (accepted.contains(AiUserEdit.F_IC) && !edited.contains(AiUserEdit.F_IC)){
			per.setIcAI(per.getIdentificationNo());
		}

		String aiName = emp.getEmployeeName();
		// both sides are whitespace-collapsed before the ignore-case compare
		if (accepted.contains(AiUserEdit.F_NAME) && !edited.contains(AiUserEdit.F_NAME)
				&& AiDocValidator.personNameEquals(per.getName(), aiName)){
			per.setNameAI(per.getName());
		}
	}
	
	/**
	 * Record every AI-populated field the applicant has changed. Call before saving.
	 * @param appl
	 */
	public static void latchUserEdit(SmAppl appl){
		if (appl == null){
			return;
		}
		for (AIApiResponse response : findResponses(appl, TYPE_EPF)){
			latchEpfUserEdit(appl, response);
		}
		SmFacility hqFacility = AiDocValidator.getHeadquartersFacility(appl);
		for (AIApiResponse response : findResponses(appl, TYPE_TENANCY)){
			latchTenancyUserEdit(hqFacility, response);
		}
	}

	private static void latchEpfUserEdit(SmAppl appl, AIApiResponse response){
		if (EntityHelper.isCollectionEmpty(appl.getSmPersonnels())){
			return;
		}
		JSONArray fields = AiDocParser.parseFields(response.getJsonResponse());
		if (fields == null){
			return;
		}
		Map<String, AiEpfResultDTO.AiEmployeeDTO> extracted = new HashMap<>();
		for (AiEpfResultDTO.AiEmployeeDTO emp : AiDocParser.toEpf(response.getApiResponseId(), fields).getEmployees()){
			extracted.put(emp.getIdNo(), emp);
		}
		Map<String, Set<String>> edits = AiUserEdit.parse(response.getUserEdit());
		boolean changed = false;
		for (SmPersonnel per : appl.getSmPersonnels()){
			String key = AiDocValidator.normalizeIdNo(
					per.getIcAI() != null ? per.getIcAI() : per.getIdentificationNo());
			AiEpfResultDTO.AiEmployeeDTO emp = extracted.get(key);
			if (emp == null){
				continue;
			}
			// the AI transients are re-derived on every page load and the form clears them on edit, so
			// the saved value against the statement is the only lasting evidence that it was typed over
			Set<String> accepted = AiUserEdit.accepted(edits, key);
			if (accepted.contains(AiUserEdit.F_EPF_NO) && !StringUtils.equals(
					StringUtils.trimToEmpty(per.getEpfNo()), StringUtils.trimToEmpty(emp.getKwspNumber()))){
				changed |= AiUserEdit.add(edits, key, AiUserEdit.F_EPF_NO);
			}
			if (accepted.contains(AiUserEdit.F_NAME)
					&& !AiDocValidator.personNameEquals(per.getName(), emp.getEmployeeName())){
				changed |= AiUserEdit.add(edits, key, AiUserEdit.F_NAME);
			}
		}
		persistUserEdit(response, edits, changed);
	}

	private static void latchTenancyUserEdit(SmFacility fac, AIApiResponse response){
		if (fac == null){
			return;
		}
		Map<String, Set<String>> edits = AiUserEdit.parse(response.getUserEdit());
		boolean changed = false;
		if (fac.getOwnerNameAI() != null && !fac.isOwnerNameFromAI()){
			changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_OWNER_NAME);
		}
		if (fac.getRentalRateAI() != null && !fac.isRentalRateFromAI()){
			changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_RENTAL_RATE);
		}
		SmAddress addr = fac.getSmAddress();
		if (addr != null){
			if (addr.getAddress1AI() != null && !addr.isAddress1FromAI()){
				changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_ADDRESS1);
			}
			if (addr.getAddress2AI() != null && !addr.isAddress2FromAI()){
				changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_ADDRESS2);
			}
			if (addr.getAddress3AI() != null && !addr.isAddress3FromAI()){
				changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_ADDRESS3);
			}
			if (addr.getPostcodeAI() != null && !addr.isPostcodeFromAI()){
				changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_POSTCODE);
			}
			if (addr.getCountryIdAI() != null && !addr.isCountryFromAI()){
				changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_COUNTRY);
			}
			if (addr.getStateIdAI() != null && !addr.isStateFromAI()){
				changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_STATE);
			}
			if (addr.getDivisionIdAI() != null && !addr.isDivisionFromAI()){
				changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_DIVISION);
			}
			if (addr.getDistrictIdAI() != null && !addr.isDistrictFromAI()){
				changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_DISTRICT);
			}
			if (addr.getCityIdAI() != null && !addr.isCityFromAI()){
				changed |= AiUserEdit.add(edits, AiUserEdit.KEY_TENANCY, AiUserEdit.F_CITY);
			}
		}
		persistUserEdit(response, edits, changed);
	}

	/**
	 * Record which of one employee's fields the applicant accepted by ticking and submitting; only
	 * these ever light an AI icon. Re-ticking an employee edited earlier also drops the edit marks,
	 * making those fields AI-populated again.
	 * @param appl
	 * @param nric
	 * @param acceptedFields
	 */
	public static void acceptEpfUserEdit(SmAppl appl, String nric, String... acceptedFields){
		for (AIApiResponse response : findResponses(appl, TYPE_EPF)){
			Map<String, Set<String>> edits = AiUserEdit.parse(response.getUserEdit());
			boolean changed = AiUserEdit.clear(edits, nric);
			for (String field : acceptedFields){
				changed |= AiUserEdit.accept(edits, nric, field);
			}
			persistUserEdit(response, edits, changed);
		}
	}

	/**
	 * A deleted personnel takes its accepted and edited fields with it, so the same IC added back
	 * by hand carries no AI icon.
	 * @param appl
	 * @param identificationNo
	 */
	public static void forgetEpfUserEdit(SmAppl appl, String identificationNo){
		String nric = AiDocValidator.normalizeIdNo(identificationNo);
		for (AIApiResponse response : findResponses(appl, TYPE_EPF)){
			Map<String, Set<String>> edits = AiUserEdit.parse(response.getUserEdit());
			persistUserEdit(response, edits, AiUserEdit.forget(edits, nric));
		}
	}

	/**
	 * Applying an extracted tenancy statement accepts every field it carries, and re-applying makes
	 * fields edited since AI-populated again.
	 * @param appl
	 */
	public static void acceptTenancyUserEdit(SmAppl appl){
		for (AIApiResponse response : findResponses(appl, TYPE_TENANCY)){
			Map<String, Set<String>> edits = AiUserEdit.parse(response.getUserEdit());
			boolean changed = AiUserEdit.clear(edits, AiUserEdit.KEY_TENANCY);
			for (String field : AiUserEdit.F_TENANCY_ALL){
				changed |= AiUserEdit.accept(edits, AiUserEdit.KEY_TENANCY, field);
			}
			persistUserEdit(response, edits, changed);
		}
	}

	private static void persistUserEdit(AIApiResponse response, Map<String, Set<String>> edits, boolean changed){
		if (!changed){
			return;
		}
		String json = AiUserEdit.toJson(edits);
		response.setUserEdit(json);
		SmServiceLookup.getAIApiProcessSO().updateUserEdit(response.getApiResponseId(), json);
	}

	/**
	 * Extracted employee IC/Passport Nos., empty when nothing was extracted so the caller leaves manual adds
	 * unrestricted. Wrong-company statements offer no one, the same set the AI Assistant table shows.
	 * @param appl
	 * @return
	 */
	public static Set<String> getAiEpfEmployeeNrics(SmAppl appl){
		Set<String> nrics = new HashSet<>();
		if (appl == null){
			return nrics;
		}
		String companyName = appl.getSmSupplier() != null ? appl.getSmSupplier().getCompanyName() : null;
		for (AIApiResponse response : findResponses(appl, TYPE_EPF)){
			collectEpfEmployeeNrics(response, companyName, nrics);
		}
		return nrics;
	}

	// collect trimmed AI-extracted employee IC/Passport Nos. from one EPF doc file into nrics
	private static void collectEpfEmployeeNrics(AIApiResponse response, String companyName, Set<String> nrics){
		JSONArray fields = AiDocParser.parseFields(response.getJsonResponse());
		if (fields == null){
			return;
		}
		AiEpfResultDTO ee = AiDocParser.toEpf(response.getApiResponseId(), fields);
		// a statement issued to another company contributes nobody, as in the AI Assistant table
		AiDocValidator.validateEpf(ee, companyName);
		if (!ee.isValidEmployerName()){
			return;
		}
		for (AiEpfResultDTO.AiEmployeeDTO emp : ee.getEmployees()){
			nrics.add(emp.getIdNo());
		}
	}

	/**
	 * A manually added new personnel must appear by IC No. in the uploaded EPF statement; throws to abort
	 * the add. Matched on IC No., not name - a matching name with a different IC is a different person.
	 * Only IC holders are checked - passport and any other identification type are left unrestricted.
	 * @param appl
	 * @param personnel
	 */
	public static void validateManualPersonnelAgainstEpf(SmAppl appl, SmPersonnel personnel){
		if (appl == null || personnel == null || personnel.getPersonnelId() != null){
			return;
		}
		if (personnel.getIdentificationType() == null){
			return;
		}
		String idType = personnel.getIdentificationType().getParameterCode();
		if (!ParameterConst.IDENTIFICATION_TYPES_IC.equalsIgnoreCase(idType)){
			return;
		}
		Set<String> aiNrics = getAiEpfEmployeeNrics(appl);
		if (aiNrics.isEmpty()){
			return;
		}
		if (personnel.getIdentificationNo() != null
				&& aiNrics.contains(AiDocValidator.normalizeIdNo(personnel.getIdentificationNo()))){
			return;
		}
		throw new EpException(SmCommonUtil.getMessage("lbl.sm.ai.epf.personnel.notexist", null));
	}

	/**
	 * An EPF or Tenancy doc uploaded via the AI Assistant is deleted in that dialog, not from the Supporting
	 * Documents listing. The _ai marker answers "uploaded from the AI Assistant dialog?" and is not
	 * replaceable by the response id; the AI verdict answers "is its response still active?", because a
	 * verdict is only ever set from an active response - otherwise the file could be deleted nowhere.
	 * @param doc
	 * @param file
	 * @return
	 */
	public static boolean isAiSuppDocDeleteLocked(SmApplSupportDoc doc, SmApplDocFile file){
		if (file == null || file.getAiApiResponseId() == null
				|| !isAiAssistantUpload(file.getFileName())
				|| doc == null || doc.getPmDocument() == null){
			return false;
		}
		return (isEpfDoc(doc.getPmDocument()) || isTenancyDoc(doc.getPmDocument()))
				&& file.getValidatedAI() != 0;
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
	 * EPF rows carry the same tick/cross verdict as Bank and Tenancy, on the employer name alone.
	 * @param appl
	 */
	public static void markEpfAiDocs(SmAppl appl){
		if (appl == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return;
		}
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (suppDoc.getPmDocument() == null || !isEpfDoc(suppDoc.getPmDocument())
					|| EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())){
				continue;
			}
			for (SmApplDocFile docFile : suppDoc.getSmApplDocFiles()){
				if (docFile.getAiApiResponseId() != null){
					markEpfDocFile(appl, docFile);
				}
			}
		}
	}

	// EPF verdict and tooltip for one doc file; a response that is gone leaves the row unmarked, as for Bank and Tenancy.
	private static void markEpfDocFile(SmAppl appl, SmApplDocFile docFile){
		AIApiResponse response = SmServiceLookup.getAIApiProcessSO()
				.getById(docFile.getAiApiResponseId());
		if (response == null){
			return;
		}
		AiEpfResultDTO ee = validatedEpf(appl, response);
		docFile.setMappedFieldAI(ee.getLabel());
		docFile.setValidatedAI(ee.getVerdict());
	}

	// one EPF file scored against the registered company name
	private static AiEpfResultDTO validatedEpf(SmAppl appl, AIApiResponse response){
		AiEpfResultDTO ee = AiDocParser.toEpf(response);
		AiDocValidator.validateEpf(ee, appl.getSmSupplier().getCompanyName());
		return ee;
	}

	/**
	 * Tooltip for the Supporting Documents AI column, resolved on every render so it follows the
	 * language switch. mappedFieldAI froze the sentence in the locale of the upload, so it is only
	 * the fallback for a row this method cannot re-derive. Reads no dialog state: the SDO review
	 * screen calls it on a bean instance that never ran an upload.
	 * @param file
	 * @return
	 */
	public String getSuppDocLabel(SmApplDocFile file){
		return getSuppDocLabel(file, file.getSmApplSupportDoc().getSmAppl());
	}
	
	public String getSuppDocLabel(SmApplDocFile file, SmAppl appl){
		if (file == null){
			return null;
		}
		SmApplSupportDoc suppDoc = file.getSmApplSupportDoc();
		if (file.getAiApiResponseId() == null || appl == null || appl.getSmSupplier() == null
				|| suppDoc.getPmDocument() == null){
			return file.getMappedFieldAI();
		}
		AIApiResponse response = SmServiceLookup.getAIApiProcessSO()
				.getById(file.getAiApiResponseId());
		if (response == null){
			return file.getMappedFieldAI();
		}
		PmDocument pmDoc = suppDoc.getPmDocument();
		if (isBankDoc(pmDoc)){
			// validateBank scores the whole list, so this file goes in on its own
			List<AiBankDocDTO> docEntities = new ArrayList<>();
			docEntities.add(AiDocParser.toBankDoc(response));
			AiDocValidator.validateBank(appl, docEntities);
			return docEntities.get(0).getLabel();
		}
		if (isTenancyDoc(pmDoc)){
			return validatedTenancy(appl, AiDocValidator.getHeadquartersFacility(appl), response).getLabel();
		}
		if (isEpfDoc(pmDoc)){
			return validatedEpf(appl, response).getLabel();
		}
		return file.getMappedFieldAI();
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
					file.setValidatedAI(entity.getVerdict());
				}
			}
		}
	}
	
	private static void showErrorMessageBank(List<AiBankDocDTO> docEntities, String clientId){
		boolean hasWrongCompanyName = false;
		boolean hasUnreadable = false;
		StringBuilder sb = new StringBuilder();
		// the same bank may arrive on several statements; it is listed once, per FRS 2.4
		Set<String> seen = new HashSet<>();

		for (AiBankDocDTO doc : docEntities){
			// nothing parsed out of this file: it blocks submit too, but it is not a mismatch
			if (doc.isUnavailable()){
				continue;
			}
			if (doc.isError()){
				hasUnreadable = true;
				continue;
			}
			if (EntityHelper.isCollectionEmpty(doc.getBankStatements())){
				continue;
			}
			// per-bank mismatch rows, labels from the bundle
			if (appendBankMismatchRows(doc.getBankStatements(), sb, seen)){
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
	// seen carries the account numbers already listed, across every statement.
	private static boolean appendBankMismatchRows(List<AiBankDocDTO.AiBankStatementDTO> banks, StringBuilder sb,
			Set<String> seen){
		boolean hasWrongCompanyName = false;
		for (AiBankDocDTO.AiBankStatementDTO bank : banks){
			// the same key the parser de-duped on, or the two would disagree on how many banks there are
			String key = AiDocValidator.normalizeAccountNo(bank.getAccountNo());
			if (!key.isEmpty() && !seen.add(key)){
				continue;
			}
			if (!bank.isValidAccountHolderName()){
				hasWrongCompanyName = true;
			}
			boolean rowAppended = appendBankMismatchRow(sb, "lbl.gfmas.bank.desc", bank.getBankName(),
					bank.isValidBankName());
			rowAppended |= appendBankMismatchRow(sb, "lbl.fl.bank.acc.no", bank.getAccountNo(),
					bank.isValidAccountNo());
			if (rowAppended){
				sb.append("\n");
			}
		}
		return hasWrongCompanyName;
	}

	// One labelled mismatch row; skipped when the field is valid or was never extracted, the way the
	// tenancy tooltip leaves out what the OCR did not read. Returns true when a row was written.
	private static boolean appendBankMismatchRow(StringBuilder sb, String labelKey, String value, boolean valid){
		if (valid || StringUtils.isBlank(value)){
			return false;
		}
		sb.append("\n").append(AiDocParser.label(labelKey)).append(": ").append(value);
		return true;
	}
	
	/**
	 * emitMessages: the submit-time check raises the mismatch messages, the Declare button gate stays silent.
	 * @param appl
	 * @param emitMessages
	 * @return
	 */
	public static boolean isBankAiMismatch(SmAppl appl, boolean emitMessages) {
		if (appl == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())) {
			return false;
		}
		// validatedAI is transient and only initAiState sets it, so recompute before reading it
		validateBank(appl, emitMessages);
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()) {
			if (suppDoc.getPmDocument() == null || EntityHelper.isCollectionEmpty(suppDoc.getSmApplDocFiles())
					|| !isBankDoc(suppDoc.getPmDocument())) {
				continue;
			}
			for (SmApplDocFile file : suppDoc.getSmApplDocFiles()) {
				if (file.getValidatedAI() == 2) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean showErrorMessage(String docType, List<JsonWrapper> jsonWrappers){
		if (EntityHelper.isCollectionEmpty(jsonWrappers)){
			return false;
		}
		String err = null;
		boolean unavailable = false;
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
			} else if (AiDocParser.isUnavailable(json.apiId, statusCode, json.rawContent)){
				unavailable = true;
			}
		}
		// a warning only: the other files in the batch are still checked
		if (unavailable){
			addMessageOnce(FacesMessage.SEVERITY_WARN, LBL_AI_UNAVAILABLE);
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
			// null entities yields an error DTO, so a wrong document is crossed rather than skipped
			docEnts.add(AiDocParser.toBankDoc(json.apiId, json.statusCode, json.rawContent));
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
			tenancy = AiDocParser.toTenancy(entityWrapper.apiId, entityWrapper.entities);
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
			// every file gets a result, so its icon and tooltip come from the DTO alone
			AiEpfResultDTO ee = AiDocParser.toEpf(entityWrapper.apiId, entityWrapper.statusCode,
					entityWrapper.rawContent);
			// employer name matches exactly, case-insensitive; no employer name extracted counts as wrong
			AiDocValidator.validateEpf(ee, companyName);
			epfs.add(ee);

			// a file the AI never read, or read nothing from, is reported by showErrorMessage, not as a mismatch
			if (entityWrapper.entities != null && !ee.isUnavailable() && !ee.isValidEmployerName()){
				hasWrongCompanyName = true;
			}
		}
		if(hasWrongCompanyName){
			addMessageOnce(FacesMessage.SEVERITY_ERROR, LBL_EPF_MISMATCH);
		}
	}
	
	
	public List<AIApiResponse> getDocs() {
		return docs;
	}

	/**
	 * Company-name verdict for one uploaded EPF file, mirroring the Supporting Documents icons.
	 * 1 = matches the registered name, 2 = wrong or never extracted, 3 = the AI never read it.
	 * @param doc
	 * @return
	 */
	public int getEpfDocVerdict(AIApiResponse doc){
		AiEpfResultDTO ee = findEpfResult(doc);
		return ee == null ? 2 : ee.getVerdict();
	}

	/**
	 * Verdict icon for one uploaded file, scored the same way as its Supporting Documents row.
	 * 1 = mapped, 2 = mismatch or nothing mappable, 3 = read only partly or never read, 0 = nothing to show.
	 * @param doc
	 * @return
	 */
	public int getDocVerdict(AIApiResponse doc){
		if (TYPE_EPF.equals(docType)){
			return getEpfDocVerdict(doc);
		}
		if (TYPE_TENANCY.equals(docType)){
			AiTenancyDTO te = findTenancyResult(doc);
			return te == null ? 2 : te.getVerdict();
		}
		return 0;
	}

	/**
	 * Tooltip for that icon: why the file was crossed, blank when it mapped cleanly.
	 * @param doc
	 * @return
	 */
	public String getDocLabel(AIApiResponse doc){
		if (TYPE_EPF.equals(docType)){
			return getEpfDocLabel(doc);
		}
		if (TYPE_TENANCY.equals(docType)){
			AiTenancyDTO te = findTenancyResult(doc);
			return te == null ? AiDocParser.label(LBL_DOC_INVALID) : te.getLabel();
		}
		return "";
	}

	/**
	 * Tooltip for that column: the extracted company name with its own tick or cross.
	 * @param doc
	 * @return
	 */
	public String getEpfDocLabel(AIApiResponse doc){
		AiEpfResultDTO ee = findEpfResult(doc);
		return ee == null ? AiDocParser.label(LBL_DOC_INVALID) : ee.getLabel();
	}

	private boolean isDocUnavailable(AIApiResponse doc){
		if (TYPE_EPF.equals(docType)){
			AiEpfResultDTO ee = findEpfResult(doc);
			return ee != null && ee.isUnavailable();
		}
		if (TYPE_TENANCY.equals(docType)){
			AiTenancyDTO te = findTenancyResult(doc);
			return te != null && te.isUnavailable();
		}
		return false;
	}

	// scored once per file: the area lookups are EJB calls and EL asks for each row several times
	private AiTenancyDTO findTenancyResult(AIApiResponse doc){
		if (doc == null || doc.getApiResponseId() == null || smAppl == null || smAppl.getSmSupplier() == null){
			return null;
		}
		if (!tenancies.containsKey(doc.getApiResponseId())){
			tenancies.put(doc.getApiResponseId(),
					validatedTenancy(smAppl, AiDocValidator.getHeadquartersFacility(smAppl), doc));
		}
		return tenancies.get(doc.getApiResponseId());
	}

	private AiEpfResultDTO findEpfResult(AIApiResponse doc){
		if (doc == null || doc.getApiResponseId() == null || EntityHelper.isCollectionEmpty(epfs)){
			return null;
		}
		for (AiEpfResultDTO ee : epfs){
			if (doc.getApiResponseId().equals(ee.getApiId())){
				return ee;
			}
		}
		return null;
	}

	public void deleteDoc(AIApiResponse doc) {
		deleteResponse(doc);
		docs.remove(doc);
		afterDelete();
	}

	/**
	 * Delete every crossed file, and every file the AI never read, when the dialog closes, whether by
	 * Submit, Cancel or the X.
	 */
	public void deleteInvalidDocs() {
		boolean deleted = false;
		for (AIApiResponse doc : new ArrayList<>(docs)){
			if (getDocVerdict(doc) == 2 || isDocUnavailable(doc)){
				log.info("Deleting invalid " + docType + " doc on dialog close: " + doc.getFileName());
				deleteResponse(doc);
				docs.remove(doc);
				deleted = true;
			}
		}
		if (deleted){
			afterDelete();
		}
	}

	private void afterDelete() {
		selectedEmployees = null;
		// the response is gone already, so save now or its file outlives it until some later Save
		saveAppl();
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
			// a statement issued to another company contributes no employees
			if (!epf.isValidEmployerName()){
				continue;
			}
			for (AiEpfResultDTO.AiEmployeeDTO emp : epf.getEmployees()){
				if (!unique.containsKey(emp.getIdNo())){
					unique.put(emp.getIdNo(), emp);
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

		private JsonWrapper(long startTime, String rawContent, String ocrType, int httpStatus) {
			this(startTime, rawContent, ocrType);
			// a gateway error page is not the service's JSON: keep the status the wire reported
			if (httpStatus < 200 || httpStatus >= 300) {
				this.statusCode = httpStatus;
			}
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