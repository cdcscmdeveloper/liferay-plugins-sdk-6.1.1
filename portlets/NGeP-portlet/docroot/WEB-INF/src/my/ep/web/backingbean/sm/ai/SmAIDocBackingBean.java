package my.ep.web.backingbean.sm.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import my.ep.constant.EpPropertiesConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.ai.AIApiResponse;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.sm.helper.SmFieldChangeUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;


/**
 * 
 * @author AI
 */
@ManagedBean(name = "smAIDocBackingBean")
@ViewScoped
public class SmAIDocBackingBean extends BaseBackingBean<BaseManagedBean> {

	private static final long serialVersionUID = 1L;
	public static final Logger log = Logger.getLogger(SmAIDocBackingBean.class);

	private static Map<String, List<SmApplDocFile>> docsMap = new HashMap<>();
	private static Map<String, Boolean> postAIMap = new HashMap<>();
	private List<SmApplDocFile> docs;
	

	private static String valueStr = "value";
	private static String confidence = "confidence";
	private static String fieldsStr = "fields";
	private static String resultsStr = "results";
	private static String propertiesStr = "properties";
	private static String normalizedValueStr = "normalizedValue";
	private static String googleUrl = EpPropertiesUtils.getInstance().getProperty("google.doc.url");
	private static double minConfidence = 0.95;
	private static double minScoreFuzzyMatch = 0.8;
	private static final String TYPE_EPF = "epf";
	private static final String TYPE_TENANCY = "tenancy";
	private static final String TYPE_BANK = "bank";
	
	private static List<EPFEntity> epfs;
	private static TenancyEntity tenancy;
	private static List<EPFEntity.EmployeeInfo> selectedEmployees;
	private boolean postAI = false;
	private String docType;
	private SmAppl smAppl;
	
	private static String tempGoogleUrl = "http://192.168.62.17:5000/process";
	//172.23.114.72, 192.168.62.17 (dev)"
	
	private SmAppl testAppl;
	/**
	 * temp test
	 * @return
	 */
	public SmAppl test(){
		if (testAppl == null){
			MofRegistrationReqDTO dto = new MofRegistrationReqDTO();
			dto.setPoSdoUser(false);
			dto.setApplId(1895956l); //KU-12092025-0001
			dto.setCurrentPage("");
			testAppl = SmServiceLookup.getMofRegistrationSO().retrieveCurrentDraftApplication(dto).getSmAppl();
		}
		return testAppl;
	}


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
		
		if (!postAIMap.containsKey(docType)) {
			postAIMap.put(docType, Boolean.FALSE);
		}
		
		docs = docsMap.get(docType);
		postAI = postAIMap.get(docType);
		if (docs.size() < 1){
			epfs = null;
			tenancy = null;
			postAI = false;
		}
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
		doc.setFileName(StringUtils.replace(event.getFile().getFileName(), " ", "-"));
		doc.setFilePath(filePath);
		
		SmApplSupportDoc suppDoc = getSmApplSupportDoc(docType, smAppl);
		if (suppDoc.getSmApplDocFiles() == null){
			suppDoc.setSmApplDocFiles(new ArrayList<SmApplDocFile>());
		}
		
		docs.add(doc);
		commonUploadFile(event, doc.getFilePath());
		
		if (docType != null) {
			processDoc(docType, smAppl);
			postAI = true;
			postAIMap.put(docType, Boolean.TRUE);
		}
	}
	
	private static SmApplSupportDoc getSmApplSupportDoc(String docType, SmAppl appl){
		SmApplSupportDoc newDoc = new SmApplSupportDoc();
		newDoc.setSmApplDocFiles(new ArrayList<SmApplDocFile>());
		if(appl == null || EntityHelper.isCollectionEmpty(appl.getSmApplSupportDocs())){
			return newDoc;
		}
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			Long docId = suppDoc.getPmDocument().getDocumentId();
			if (TYPE_EPF.equals(docType) && docId == 768l){
				return suppDoc;
			}
			boolean isTenancyDoc = docId == 679l || docId == 662l || docId == 654l;
			if (TYPE_TENANCY.equals(docType) && isTenancyDoc){
				return suppDoc;
			}
		}
		return newDoc;
	}

	/**
	 * Test
	 * @param type
	 * @param appl
	 */
	public static void processDoc(String type, SmAppl appl){
		if (StringUtils.isEmpty(type)){
			return;
		}
		String docType = type.trim();
		List<SmApplDocFile> docs = docsMap.get(docType);
		if (EntityHelper.isCollectionEmpty(docs)){
			return;
		}
		log.info("...wip\t"+docs.size() +"\t_" + docType);
		
		List<JsonWrapper> jsonWrappers = new ArrayList<>();
		for (SmApplDocFile doc : docs){
			// Skip already processed files
			if (doc.getAiApiResponseId() != null) {
				AIApiResponse res = SmServiceLookup.getAIApiProcessSO()
						.getAIApiResponse(doc.getAiApiResponseId());
				jsonWrappers.add(new JsonWrapper(res));
				continue;
			}
			
			JsonWrapper result = sendDocumentOCR(docType, doc.getFilePath(), doc.getFileName());
			if (appl == null && result != null){	//temp
				jsonWrappers.add(result);
				
			} else if (result != null){
				long apiId = saveResponse(appl, result);
				doc.setAiApiResponseId(apiId);
				result.apiId = apiId;
				jsonWrappers.add(result);
			}
		}
		boolean hasError = showErrorMessage(docType, jsonWrappers);
		try {
			if (TYPE_EPF.equals(docType)){
				processEPF(jsonWrappers, appl, docType);
				
			} else if (TYPE_TENANCY.equals(docType)){
				processTenancyAgreement(jsonWrappers, docType);
				
			} else {
				processBank(jsonWrappers);
			}
			RequestContext.getCurrentInstance().addCallbackParam("success", true);
		} catch(NullPointerException e){
			if (!hasError){
				String err = "Please upload a valid EPF Contribution Statement for AI data extraction";
				if (TYPE_TENANCY.equals(docType)){
					err = "Please upload a valid Stamped Premise Rental Agreement for AI data extraction";
				}
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, err);
				FacesContext.getCurrentInstance().addMessage("ai", msg);
			}
		}
	}
	
	//do parallel request using ExecutorService
	private static JsonWrapper sendDocumentOCR(String docType, String filepath, String filename){
		///temporary demo filter - start
		if (filename.endsWith(".json")){
			log.info("skip AI");
			String content = getFakeData(filepath, filename);
			return new JsonWrapper(System.currentTimeMillis(), content, docType);
		}
		if (!filename.endsWith("ai_demo.pdf")){
			return null;
		}
		///temporary demo filter - end
		
		String sessionId = ((PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest()).getRequestedSessionId(); 
		long startTime = System.currentTimeMillis();
		
		try {
			InputStream is = sendDocumentToExternalService(docType, filepath, filename);
			SLAUtils.getInstance().logSLASubInfo("SM", "AI", sessionId, "null",
					"SmAIDocbackingBean.sendDocumentOCR", "1:"+filename, startTime);
			String content = getRawContent(is);
			return new JsonWrapper(startTime, content, docType);
		} catch (IOException e) {
			log.error(" ", e);
			
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, null,
					"AI processing is temporarily unavailable. Your document has been uploaded successfully," +
							" and you can still proceed with your application as usual.");
			FacesContext.getCurrentInstance().addMessage("ai", msg);
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
		apiResp.setResponseStatus(getErrorCode(jsonWrapper.rawContent));
		
		return SmServiceLookup.getAIApiProcessSO().saveAIApiResponse(apiResp).getApiResponseId();
	}
	
	private static int getErrorCode(String rawContent){
		try {
			return new JSONObject(rawContent).getJSONObject("error").getInt("code");
		} catch (JSONException e){
			return 200;
		}
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
		log.info(docType+"\t"+appl.getApplNo());
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
		for (EPFEntity.EmployeeInfo employee : selectedEmployees) {
			SmPersonnel per = personnels.get(employee.getNricNumber());
			if (per != null){
				per.setEpfNo(employee.getKwspNumber());
				continue;
			}
			per = new SmPersonnel();
			per.setName(employee.getEmployeeName());
			per.setIdentificationNo(employee.getNricNumber());
			per.setEpfNo(employee.getKwspNumber());
			
			//SmApplicationUtil - setDefaultPersonnelValues()
			// or check SssmSO on how to save SmPersonnels
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
		List<Long> bankStatementIds = Arrays.asList(529L, 633L, 676L);
		if (!bankStatementIds.contains(suppDoc.getPmDocument().getDocumentId())){
			log.info("...not bank statement");
			return;
		}
		List<JsonWrapper> jsonWrappers = new ArrayList<>();
		for (SmApplDocFile doc : suppDoc.getSmApplDocFiles()){
			if (doc.getAiApiResponseId() != null){
				continue;
			}
			JsonWrapper result = sendDocumentOCR(TYPE_BANK, doc.getFilePath(), doc.getFileName());
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
			List<DocEntity> docEntities = processBank(jsonWrappers);
			validateBankEntity(appl, suppDoc, docEntities);
			showErrorMessageBank(docEntities);
		}
		
		SLAUtils.getInstance().logSLASubInfo("SM", "AI", sessionId, "null",
				"SmAIDocbackingBean.sendDocumentOCR", "2", startTime);
	}
	
	//better to put this in SO, when initializing the docs for supplier/SDO/PO etc
	/**
	 * @param appl
	 */
	public static void validateBank(SmAppl appl){
		if (appl == null || appl.getSmSupplier() == null){
			return;
		}
		//PENYATA AKAUN SEMASA YANG TERKINI ATAS NAMA SYARIKAT / SLIP DEPOSIT (all zones)
		List<Long> bankStatementIds = Arrays.asList(529L, 633L, 676L);
		SmApplSupportDoc bankStatementDoc = null;
		for (SmApplSupportDoc suppDoc : appl.getSmApplSupportDocs()){
			if (bankStatementIds.contains(suppDoc.getPmDocument().getDocumentId())){
				bankStatementDoc = suppDoc;
				break;
			}
		}
		if (bankStatementDoc == null || EntityHelper.isCollectionEmpty(bankStatementDoc.getSmApplDocFiles())){
			log.info("x jumpa bank statement?");
			return;
		}
		
		List<DocEntity> entities = new ArrayList<>();
		for (SmApplDocFile docFile : bankStatementDoc.getSmApplDocFiles()){
			if (docFile.getFileName() == null ||docFile.getFileName().trim().isEmpty()){
				continue;
			}
			AIApiResponse response = SmServiceLookup.getAIApiProcessSO()
					.getAIApiResponse(docFile.getAiApiResponseId());
			if (response != null){
				DocEntity entity = new DocEntity(response.getApiResponseId(), response.getJsonResponse());
				entities.add(entity);
			}
		}
		SmFieldChangeUtil.setBankFieldChangeStatus(appl);
		validateBankEntity(appl, bankStatementDoc, entities);
	}
	
	private static void validateBankEntity(SmAppl appl, SmApplSupportDoc suppDoc, List<DocEntity> docEntities){
		if (EntityHelper.isCollectionEmpty(docEntities)){
			return;
		}
		//init bank statements
		List<BankStatementEntity> bankEntities = new ArrayList<>();
		for (DocEntity entity : docEntities){
			bankEntities.addAll(entity.bankStatements);
		}
		
		//compare with new/modified banks only
		Map<String, String> supplierAccounts = new HashMap<>();	
		for (SmSupplierBank suppBank : appl.getSmSupplierBanks()){
			if (!StringUtils.isEmpty(suppBank.getChangedFieldStatus())){
				supplierAccounts.put(suppBank.getAccountNo(), suppBank.getPmFinancialOrg().getFinOrgName());	
			}
		}
		List<String> supplierBanks = new ArrayList<>(supplierAccounts.values());
		String companyNameSupp = appl.getSmSupplier().getCompanyName();
		
		for (BankStatementEntity bank : bankEntities){
			String companyNameJson = bank.accountHolderName;
			String bankName = supplierAccounts.get(bank.accountNo);
			log.info("account no:"+bank.accountNo);
			log.info("match with bank:"+bankName);
			if (bankName == null){
				bank.validAccountNo = false;
				
				bankName = fuzzyMatchList(bank.bankName, supplierBanks);
				if (StringUtils.isEmpty(bankName)){
					bank.validBankName = false;
				}
			
			//account no. exists
			} else {
				double score = fuzzyMatchJaroWinkler(bankName, bank.bankName);
				log.info("Score: "+score+"\tBank1: "+bankName+"\tBank2:"+bank.bankName);
				if (score < minScoreFuzzyMatch){
					bank.validBankName = false;
				}
			}
			if (!StringUtils.equals(companyNameJson, companyNameSupp)){
				bank.validAccountHolderName = false;
			}
		}
		
		for (SmApplDocFile file : suppDoc.getSmApplDocFiles()){
			for (DocEntity entity : docEntities){
				if (entity.apiId.equals(file.getAiApiResponseId())){
					file.setMappedFieldAI(entity.getLabel());
					file.setValidatedAI(entity.hasError() ? 2 : 1); //1-ok, 2-err
				}
			}
		}
	}
	
	private static void showErrorMessageBank(List<DocEntity> docEntities){
		boolean hasWrongCompanyName = false;
		StringBuilder sb = new StringBuilder();
		
		for (DocEntity doc : docEntities){
			if (EntityHelper.isCollectionEmpty(doc.bankStatements)){
				continue;
			}
			for (BankStatementEntity bank : doc.bankStatements){
				if (!bank.validAccountHolderName){
					hasWrongCompanyName = true;
				}
				if (!bank.validBankName){
					sb.append("\nBank Name: ").append(bank.bankName);
				}
				if (!bank.validAccountNo){
					sb.append("\nAccount No.: ").append(bank.accountNo);
				}
				if (!bank.validBankName || ! bank.validAccountNo){
					sb.append("\n");
				}
			}
		}
		
		if (hasWrongCompanyName){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, null,
					"The company name in your bank statement(s) does not match with the registered Business/Company Name" +
					" in Company Information section. Your application can still be submitted as-is, " +
					"or you may update the Business/Company Name before proceeding.");
			FacesContext.getCurrentInstance().addMessage("ai", msg);
		}
		if (!StringUtils.isEmpty(sb.toString())){
			sb.insert(0, "Your bank statement(s) does not match with any newly added/updated bank in" +
					" Bank Information section. Your application can still be submitted as-is, " +
					"or you may update the Bank Information section before proceeding:");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, null, sb.toString().replace("\n", "<br/>"));
			FacesContext.getCurrentInstance().addMessage("ai", msg);
		}
	}
	
	private static boolean showErrorMessage(String docType, List<JsonWrapper> jsonWrappers){
		if (EntityHelper.isCollectionEmpty(jsonWrappers)){
			return false;
		}
		String err = null;
		for (JsonWrapper json : jsonWrappers){
			int statusCode = getErrorCode(json.rawContent);
			if (statusCode == 415 || statusCode == 422){
				switch(docType){
				case TYPE_EPF:
					err = "Please upload a valid EPF Contribution Statement for AI data extraction";
					break;
				case TYPE_TENANCY:
					err = "Please upload a valid Stamped Premise Rental Agreement for AI data extraction";
					break;
				case TYPE_BANK:
				default:
					err = "Please upload a valid Bank Statement for AI data extraction";
					break;
				}
			}
			if (statusCode == 500){
				err = "AI processing is temporarily unavailable. Your document has been uploaded successfully, " +
						"and you can still proceed with your application as usual.";
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, null, err);
				FacesContext.getCurrentInstance().addMessage("ai", msg);
				return true;
			}
		}
		if (err != null){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, err);
			FacesContext.getCurrentInstance().addMessage("ai", msg);
			return true;
			
		}
		return false;
	}
		
	/**
	 * @param jsonWrappers
	 * @return
	 */
	public static List<DocEntity> processBank(List<JsonWrapper> jsonWrappers){
		if (EntityHelper.isCollectionEmpty(jsonWrappers)){
			return new ArrayList<>();
		}
		List<DocEntity> docs = new ArrayList<>();
		for (JsonWrapper json : jsonWrappers){
			DocEntity doc = new DocEntity(json.apiId, json.entities);
			docs.add(doc);
		}
		return docs;
	}

	/**
	 * @param entities
	 * @param docType
	 */
	public static void processTenancyAgreement(List<JsonWrapper> entities, String docType){
		if (EntityHelper.isCollectionEmpty(entities)){
			return;
		}
		List<TenancyEntity> tes = new ArrayList<>();
		TenancyEntity te = null;
		for (JsonWrapper entityWrapper : entities){
			te = new TenancyEntity(entityWrapper.entities);
			tes.add(te);
			tenancy = te;
		}
	}

	/**
	 * @param entities
	 * @param appl
	 * @param docType
	 */
	public static void processEPF(List<JsonWrapper> entities, SmAppl appl, String docType){
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
			EPFEntity ee = new EPFEntity(entityWrapper.entities);
			epfs.add(ee);
			
			double score=0;
			if (ee.employer != null){
				score = fuzzyMatchJaroWinkler(companyName, ee.employer.employerName);
			}
			if (score < minScoreFuzzyMatch){
				hasWrongCompanyName = true;
			}
		}
		if(hasWrongCompanyName){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, null,
					"The company name in your EPF(s) does not match with the registered Business/Company Name" +
					" in Company Information section.");
			FacesContext.getCurrentInstance().addMessage("ai", msg);
		}
		// wip: need to save the doc to SmApplDocFile with PmDocument ID: 768, 864
		//PENYATA CARUMAN KWSP (BORANG A) IAITU SENARAI NAMA PEKERJA YANG DICARUM SERTA RESIT
	}
	
	/**
	 * Clear uploaded documents
	 */
	public void resetDoc() {
		log.info("test");
	}
	
	public List<SmApplDocFile> getDocs() {
		return docs;
	}
	
	/**
	 * 
	 * @param doc
	 */
	public void deleteDoc(SmApplDocFile doc) {
		docs.remove(doc);
		if(docs.size() < 1){
			epfs = null;
			tenancy = null;
			postAI = false;
		}
	}
	
	public List<EPFEntity.EmployeeInfo> getEmployees() {
		if (epfs == null){
			return new ArrayList<>();
		}
		List <EPFEntity.EmployeeInfo> employees = new ArrayList<>();
		for (EPFEntity epf : epfs){
			employees.addAll(epf.getEmployees());
		}
		return employees;
	}
	
	public TenancyEntity getTenancy() {
		return tenancy;
	}

	private static String getFakeData(String filepath, String filename) {
		if (!filename.endsWith(".json")){
			return null;
		}
		try {
			Thread.sleep(2500);
			//based on commonUploadFile()
			String tempDir = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/";
			tempDir += filepath + filename;
			File file = new File(tempDir);
			return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.error(" Error reading JSON file", e);
		} catch (InterruptedException e){
			Thread.currentThread().interrupt();
		}
		return null;
	}
	
	private static JSONArray parseJsonEntities(String jsonContent) {
		if (StringUtils.isEmpty(jsonContent)) {
			return null;
		}
		try {
			JSONObject root = new JSONObject(jsonContent);
			if (!root.has("data")) {
				log.warn("  No data found in JSON");
				return null;
			}
			JSONObject data = root.getJSONObject("data");
			if (!data.has(resultsStr)) {
				log.warn("  No results found in data");
				return null;
			}
			JSONObject results = data.getJSONObject(resultsStr);
			if (!results.has(fieldsStr)) {
				log.warn("  No fields found in results");
				return null;
			}
			JSONArray fields = results.getJSONArray(fieldsStr);
			if (fields.length() == 0) {
				log.warn("  No fields found");
				return null;
			}
			return fields;
		} catch (JSONException e) {
			log.error(" Error parsing JSON", e);
		}
		return null;
	}

	private static String getRawContent(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			return content.toString();
		} catch (IOException e) {
			log.error(" Error reading InputStream", e);
		}
		return null;
	}
	

	public List<EPFEntity.EmployeeInfo> getSelectedEmployees() {
		return selectedEmployees;
	}

	public void setSelectedEmployees(List<EPFEntity.EmployeeInfo> selectedEmployees) {
		this.selectedEmployees = selectedEmployees;
	}

	public boolean isPostAI() {
		return postAI;
	}

	public void setPostAI(boolean postAI) {
		this.postAI = postAI;
	}


	private static InputStream sendDocumentToExternalService(String docType, String filepath, String filename) 
			throws IOException {
		
		if (StringUtils.isEmpty(googleUrl)) {
			googleUrl = tempGoogleUrl;
		}
		log.info("use google url: " + googleUrl);
		
		String tempDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR) + "/" + filepath + filename;
		File file = new File(tempDir);
		byte[] fileBytes = Files.readAllBytes(file.toPath());
		
		// Create boundary for multipart
		String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
		String crlf = "\r\n";
		
		URL url = new URL(googleUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		conn.setDoOutput(true);
		conn.setConnectTimeout(30 * 1000); // 30 sec
		conn.setReadTimeout(120 * 1000); // 2 min
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("--").append(boundary).append(crlf);
		sb.append("Content-Disposition: form-data; name=\"documentType\"").append(crlf);
		sb.append(crlf);
		sb.append(docType).append(crlf);
		
		sb.append("--").append(boundary).append(crlf);
		sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
			.append(filename).append("\"").append(crlf);
		sb.append("Content-Type: application/pdf").append(crlf);
		sb.append(crlf);
		
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
		out.write(sb.toString());
		out.flush();
		
		conn.getOutputStream().write(fileBytes);
		
		out.write(crlf + "--" + boundary + "--" + crlf);
		out.flush();
		out.close();
		
		try {
			log.info("sendDocumentToExternalService-status: " + conn.getResponseCode() + " " 
					+ conn.getResponseMessage());
			return conn.getInputStream();
		} catch (IOException e){
			log.error("Response error",e);
			return conn.getErrorStream();
		}
	}
	
	private static String fuzzyMatchList(String target, List<String> list){
		if (StringUtils.isEmpty(target) || EntityHelper.isCollectionEmpty(list)){
			return "";
		}
		double bestScore = 0;
		String bestResult = "";
		for (String s : list){
			double score = fuzzyMatchJaroWinkler(target, s);
			if (score > bestScore && score > minScoreFuzzyMatch){
				bestScore = score;
				bestResult = s;
			}
		}
		return bestResult;
	}
	
	
	/**
	 * Fuzzy match using Jaro-Winkler, better matching for "maybank" and "maybank investment banking"
	 * @param st1
	 * @param st2
	 * @return
	 */
	private static double fuzzyMatchJaroWinkler(String st1, String st2) {
		if (StringUtils.isEmpty(st1) || StringUtils.isEmpty(st2)){
			return 0.0;
		}
		String s1 = st1.toLowerCase().trim();
		String s2 = st2.toLowerCase().trim();
		double jaroSim = jaroSimilarity(s1, s2);
		if (jaroSim < 0.7) {
			return jaroSim;
		}
		int prefixLength = 0;
        int maxPrefix = Math.min(4, Math.min(s1.length(), s2.length()));
        
        for (int i = 0; i < maxPrefix; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                prefixLength++;
            } else {
                break;
            }
        }
        
        return jaroSim + (0.1 * prefixLength * (1 - jaroSim));
	}
	
	/**
	 * New commons-lang.jar can just use StringUtils.getJaroWinkler
	 * @param s1
	 * @param s2
	 * @return
	 */
    private static double jaroSimilarity(String st1, String st2) {
        if (st1 == null || st2 == null){
        	return 0.0;
        }
        if (st1.equals(st2)){
        	return 1.0;
        }
        
        String s1 = st1.toLowerCase();
        String s2 = st2.toLowerCase();
        
        int len1 = s1.length();
        int len2 = s2.length();
        
        if (len1 == 0 && len2 == 0){
        	return 1.0;
        }
        if (len1 == 0 || len2 == 0){
        	return 0.0;
        }
        
        int matchWindow = Math.max(len1, len2) / 2 - 1;
        if (matchWindow < 0){
        	matchWindow = 0;
        }
        
        boolean[] s1Matches = new boolean[len1];
        boolean[] s2Matches = new boolean[len2];
        
        int matches = 0;
        int transpositions = 0;
        
        // Find matches
        for (int i = 0; i < len1; i++) {
            int start = Math.max(0, i - matchWindow);
            int end = Math.min(i + matchWindow + 1, len2);
            
            for (int j = start; j < end; j++) {
                if (s2Matches[j] || s1.charAt(i) != s2.charAt(j)){
                	continue;
                }
                s1Matches[i] = true;
                s2Matches[j] = true;
                matches++;
                break;
            }
        }
        
        if (matches == 0){
        	return 0.0;
        }
        
        // Count transpositions
        int k = 0;
        for (int i = 0; i < len1; i++) {
            if (!s1Matches[i]){
            	continue;
            }
            while (!s2Matches[k]){
            	k++;
            }
            if (s1.charAt(i) != s2.charAt(k)){
            	transpositions++;
            }
            k++;
        }
        
        return ((double) matches / len1 + (double) matches / len2 + 
                (double) (matches - transpositions / 2) / matches) / 3.0;
    }

	private static class JsonWrapper {
		private JSONArray entities;
		private long timeTakenMs;
		private String rawContent;
		private String ocrType;
		private long apiId;
		
		private JsonWrapper(long startTime, String rawContent, String ocrType) {
			this.entities = parseJsonEntities(rawContent);
			this.timeTakenMs = System.currentTimeMillis() - startTime;
			this.rawContent = rawContent;
			this.ocrType = ocrType;
		}
		
		private JsonWrapper(AIApiResponse res){
			this(res.getTimeTakenMs(), res.getJsonResponse(), res.getOcrType());
		}
	}
	
	private static class DocEntity {
		private Long apiId;
		private List<BankStatementEntity> bankStatements;
		private boolean error;
		
		private DocEntity(Long apiId, String rawContent){
			this(apiId, parseJsonEntities(rawContent));
		}
		
		private DocEntity(Long apiId, JSONArray entities){
			this.apiId = apiId;
			this.bankStatements = new ArrayList<>();
			if (entities == null || entities.length() == 0){
				this.error = true;
				return;
			}
			try {
				for (int i=0; i < entities.length(); i++){
					JSONObject bankEntity = entities.getJSONObject(i);
					String entityId = bankEntity.getString("entity");
					JSONArray properties = bankEntity.getJSONArray(propertiesStr);
					BankStatementEntity bankStatement = new BankStatementEntity(
							properties, entityId);
					this.bankStatements.add(bankStatement);
				}
			} catch (JSONException e){
				log.error("Error DocEntity", e);
				this.error = true;
			}
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (BankStatementEntity bank : this.bankStatements){
				sb.append(bank.entityId).append(":\n");
				sb.append(bank.toString()).append("\n");
			}
			return sb.toString();
		}
		
		private String getLabel() {
			StringBuilder sb = new StringBuilder();
			if (this.error){
				return "Please upload valid document.";
			}
			if (this.bankStatements.size() == 1){
				return bankStatements.get(0).getLabel().replace("\n", "<br/>");
			}
			for (BankStatementEntity bank : this.bankStatements){
				sb.append("Bank " + bank.entityId.substring(5)).append(":\n");
				sb.append(bank.getLabel()).append("\n");
			}
			return sb.toString().replace("\n", "<br/>");
		}
		
		private boolean hasError() {
			if (this.error){
				return true;
			}
			if (EntityHelper.isCollectionEmpty(bankStatements)){
				return false;
			}
			for (BankStatementEntity bank : bankStatements){
				if (!bank.validAccountHolderName || !bank.validAccountNo || !bank.validBankName){
					return true;
				}
			}
			return false;
		}
	}

	private static class BankStatementEntity {
		private String bankName;
		private String accountNo;
		private String accountHolderName;
		private String entityId;
		private boolean validBankName = true;
		private boolean validAccountNo = true;
		private boolean validAccountHolderName = true;

		private BankStatementEntity(JSONArray properties, String entityId) throws JSONException {
			this.entityId = entityId;
			
			for (int i = 0; i < properties.length(); i++) {
				JSONObject property = properties.getJSONObject(i);
				String type = property.optString("type", "");
				String text = property.optString(valueStr, "");
				String value = property.optString(normalizedValueStr, text);
				double conf = property.optDouble(confidence, 0.0);
				if (conf < minConfidence){
					log.info("Patut skip la kan? " + conf);
				}
				if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text)){
					continue;
				}
				switch (type) {
				case "Bank_Name":
					this.bankName = value.trim();
					break;
				case "Account_Number":
					this.accountNo = value.trim();
					break;
				case "Account_Holder_Name":
					this.accountHolderName = value.trim();
					break;
				default:
				}	
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (accountHolderName != null) {
				sb.append("Company: ").append(accountHolderName).append("\n");
			}
			if (bankName != null) {
				sb.append("Bank Name: ").append(bankName).append("\n");
			}
			if (accountNo != null) {
				sb.append("Account No.: ").append(accountNo).append("\n");
			}
			return sb.toString();
		}
		
		public String getLabel() {
			StringBuilder sb = new StringBuilder();
			String err1 = "<span style='color: red;'>";
			String err2 = "</span>";
			String yes = "&#x2713; ";
			String no = "&#x2717; ";
			if (accountHolderName != null) {
				String s = "Company: " + accountHolderName + "\n";
				if (!validAccountHolderName){
					sb.append(err1).append(no).append(s).append(err2);
				} else {
					sb.append(yes).append(s);
				}
			}
			if (bankName != null) {
				String s = "Bank name: " + bankName + "\n";
				if (!validBankName){
					sb.append(err1).append(no).append(s).append(err2);
				} else {
					sb.append(yes).append(s);
				}
			}
			if (accountNo != null) {
				String s = "Account No.: " + accountNo + "\n";
				if (!validAccountNo){
					sb.append(err1).append(no).append(s).append(err2);
				} else {
					sb.append(yes).append(s);
				}
			}
			return sb.toString();
		}
	}

	/**
	 * JSON mapping for EPF document
	 * @author AI
	 */
	public static class EPFEntity {
		private EmployerInfo employer;
		private List<EmployeeInfo> employees;

		private static class EmployerInfo {
			private String employerName;
			private String referenceNumber;
			private String employerAddress;
			private String entityId;
			private String postcode;
			private String country;
			private String state;
			private String district;
			private String city;
			private String normalizedAddress;

			private EmployerInfo(JSONArray properties, String entityId) throws JSONException {
				this.entityId = entityId;

				for (int i = 0; i < properties.length(); i++) {
					JSONObject property = properties.getJSONObject(i);
					String type = property.optString("type", "");
					String text = property.optString(valueStr, "");
					String value = property.optString(normalizedValueStr, text);
					double conf = property.optDouble(confidence, 0.0);
					if (conf < minConfidence) {
						log.info("Low confidence for employer field: " + conf);
					}
					if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text)) {
						continue;
					}
					switch (type) {
					case "employer_name":
						this.employerName = value.trim();
						break;
					case "reference_number":
						this.referenceNumber = value.trim();
						break;
					case "employer_address":
						this.employerAddress = value.trim();
						JSONObject addressComponents = property.optJSONObject("addressComponents");
						if (addressComponents != null) {
							this.postcode = addressComponents.optString("postcode", "").trim();
							this.country = addressComponents.optString("country", "").trim();
							this.state = addressComponents.optString("state", "").trim();
							this.district = addressComponents.optString("district", "").trim();
							this.city = addressComponents.optString("city", "").trim();
							this.normalizedAddress = addressComponents.optString("normalized_Address", "").trim();
						}
						break;
					default:
					}
				}
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();
				if (employerName != null) {
					sb.append("Employer: ").append(employerName).append("\n");
				}
				if (referenceNumber != null) {
					sb.append("Reference Number: ").append(referenceNumber).append("\n");
				}
				if (employerAddress != null) {
					sb.append("Address: ").append(employerAddress).append("\n");
				}
				return sb.toString();
			}
		}

		/**
		 * JSON mapping for EPF document (Employees)
		 * @author AI
		 */
		public static class EmployeeInfo implements Serializable {

			private static final long serialVersionUID = 1L;
			private String kwspNumber;
			private String nricNumber;
			private String employeeName;
			private String employeeContribution;
			private String employerContribution;
			private String entityId;

			private EmployeeInfo(JSONArray properties, String entityId) throws JSONException {
				this.entityId = entityId;

				for (int i = 0; i < properties.length(); i++) {
					JSONObject property = properties.getJSONObject(i);
					String type = property.optString("type", "");
					String text = property.optString(valueStr, "");
					String value = property.optString(normalizedValueStr, text);
					double conf = property.optDouble(confidence, 0.0);
					if (conf < minConfidence) {
						log.info("Low confidence for employee field: " + conf);
					}
					if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text)) {
						continue;
					}
					value = value.trim();
					switch (type) {
					case "KWSP_number":
						this.kwspNumber = value;
						break;
					case "NRIC_number":
						this.nricNumber = value;
						break;
					case "employee_name":
						this.employeeName = value;
						break;
					case "employee_contribution":
						this.employeeContribution = value;
						break;
					case "employer_contribution":
						this.employerContribution = value;
						break;
					default:
					}
				}
			}

			public String getKwspNumber() {
				return this.kwspNumber;
			}

			public String getNricNumber() {
				return this.nricNumber;
			}

			public String getEmployeeName() {
				return this.employeeName;
			}
		}

		private EPFEntity(JSONArray entities) {
			this.employees = new ArrayList<>();
			try {
				for (int i = 0; i < entities.length(); i++) {
					JSONObject entityObj = entities.getJSONObject(i);
					String entityId = entityObj.getString("entity");
					JSONArray properties = entityObj.getJSONArray(propertiesStr);

					if (entityId.startsWith("employee-")) {
						EmployeeInfo employee = new EmployeeInfo(properties, entityId);
						this.employees.add(employee);
					} else if (entityId.startsWith("employer-")) {
						this.employer = new EmployerInfo(properties, entityId);
					}
				}

			} catch (JSONException e) {
				log.error("Error EPFEntity", e);
			}
		}

		public List<EmployeeInfo> getEmployees() {
			return employees != null ? employees : new ArrayList<EmployeeInfo>();
		}
	}

	public static class TenancyEntity {
		public String agreementDate;
		public String landlordsName;
		public BigDecimal monthlyRental;
		public String premisesAddress;
		public String rentalDuration;
		public String tenantsName;
		public String propertyType;
		public String postcode;
		public String country;
		public String state;
		public String district;
		public String city;
		public String normalizedAddress;
		
		public String codeState;
		public String codeDistrict;
		public String codeCity;
		public PmCity pmCity;

		public TenancyEntity(JSONArray entities) {
			try {
				for (int i = 0; i < entities.length(); i++) {
					JSONObject entity = entities.getJSONObject(i);
					String type = entity.optString("type", "");
					String text = entity.optString(valueStr, "");
					String value = entity.optString(normalizedValueStr, text);
					double conf = entity.optDouble(confidence, 0.0);

					if (conf < minConfidence) {
						log.info("Low confidence for tenancy field: " + conf);
					}
					if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text)){
						continue;
					}
					value = value.trim();
					switch (type) {
					case "agreement_date":
						this.agreementDate = value;
						break;
					case "landlords_name":
						this.landlordsName = value;
						break;
					case "monthly_rental":
						this.monthlyRental = new BigDecimal(value);
						break;
					case "premises_address":
						this.premisesAddress = value;
						setAddress(entity.optJSONObject("addressComponents"));
						break;
					case "rental_duration":
						this.rentalDuration = value;
						break;
					case "tenants_name":
						this.tenantsName = value;
						break;
					case "property_type":
						this.propertyType = value;
						break;
					default:
					}
				}
			} catch (JSONException | NumberFormatException e) {
				log.error("Error TenancyEntity", e);
			}
		}
		
		public void setAddress(JSONObject addr){
			if (addr != null) {
				this.postcode = addr.optString("postcode", "").trim();
				this.country = addr.optString("country", "").trim();
				this.state = addr.optString("state", "").trim();
				this.district = addr.optString("district", "").trim();
				this.city = addr.optString("city", "").trim();
				String fullAddr = addr.optString("normalized_Address", "").trim();
				this.normalizedAddress = truncateAddressAtPostcode(fullAddr, this.postcode);
				this.codeState = addr.optString("state_code", "").trim();
				this.codeDistrict = addr.optString("district_code", "").trim();
				this.codeCity = addr.optString("city_code", "").trim();
			}
			if (!StringUtils.isEmpty(codeCity)){
				this.pmCity = SmServiceLookup.getCitySO().getCityByCode(codeCity).getPmCity();
			}
			
		}
		
        /**
         * Truncates the address string at the first occurrence of the postcode.
         * @param normalizedAddress
         * @param postcode the postcode
         * @return the truncated address
         */
		private String truncateAddressAtPostcode(String normalizedAddress, String postcode) {
			if (StringUtils.isEmpty(normalizedAddress) || StringUtils.isEmpty(postcode)) {
				return normalizedAddress;
    		}
			int postcodeIndex = normalizedAddress.indexOf(postcode);
			if (postcodeIndex == -1) {
				return normalizedAddress;
			}
			String truncated = normalizedAddress.substring(0, postcodeIndex).trim();
			return truncated.replaceAll(",\\s*$", "").trim();
        }

		public String getLandlordsName(){
			return this.landlordsName;
		}
		
		public BigDecimal getMonthlyRental(){
			return this.monthlyRental;
		}
		
		public String getPremisesAddress(){
			return this.premisesAddress;
		}
	}
}