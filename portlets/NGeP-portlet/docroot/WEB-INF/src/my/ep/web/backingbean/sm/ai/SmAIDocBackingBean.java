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

import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import my.ep.constant.EpPropertiesConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;


/**
 * 
 * @author AI
 */
@ManagedBean(name = "smAIDocBackingBean")
@ViewScoped
public class SmAIDocBackingBean extends BaseBackingBean<BaseManagedBean> {

	private static final long serialVersionUID = 1L;
	public static final Logger log = Logger.getLogger(SmAIDocBackingBean.class);

	private static List<SmApplDocFile> docs = new ArrayList<>();
	private static String folderAI = Calendar.getInstance().get(Calendar.YEAR) + "/SM/APPLICATION/AI/";
	private static String mentionTextStr = "mentionText";
	private static String confidence = "confidence";
	private static String fieldsStr = "fields";
	private static String resultsStr = "results";
	private static String googleUrl = EpPropertiesUtils.getInstance().getProperty("google.doc.url");
	private static double minConfidence = 0.95;
	private static double minScoreFuzzyMatch = 0.8;
	
	private static EPFEntity epf = null;
	private static TenancyEntity tenancy = null;
	private List<EPFEntity.EmployeeInfo> selectedEmployees;
	
	private static String tempGoogleUrl = "http://192.168.62.17:5000/process";
	//172.23.114.72, 192.168.62.17 (dev), 192.168.120.238 (jenkins)"
	
	
	/**
	 * 
	 * @param appl
	 * @return
	 */
	public boolean showButton(SmAppl appl) {
		//check pg 3 for full scope
		if (appl == null || !SupplierManagementConst.APPL_TYPE_MOF.equals(appl.getApplType())) {
			return false;
		}
		return false;
	}


	/**
	 * @param event
	 */
	public void handleSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() == null) {
			return;
		}
		SmApplDocFile doc = new SmApplDocFile();
		doc.setFileName(StringUtils.replace(event.getFile().getFileName(), " ", "-"));
		doc.setFilePath(folderAI);
		log.info("...file:\t"+doc.getFilePath()+doc.getFileName());
		docs.add(doc);
		commonUploadFile(event, doc.getFilePath());
	}


	/**
	 * @param docType
	 */
	public static void processDoc(String docType){
		log.info("...wip\t"+docs.size() +"\t_" + docType);
		if (EntityHelper.isCollectionEmpty(docs)){
			return;
		}
		//process multiple docs
		List<JSONArray> entities = new ArrayList<>();
		for (SmApplDocFile doc : docs){
			JSONArray arr = sendDocumentOCR("bank", doc.getFilePath(), doc.getFileName());
			if (arr != null){
				entities.add(arr);
			}
		}
		
		if ("EPF".equals(docType)){
			processEPF(entities);
			
		} else if ("TNC".equals(docType)){
			processTenancyAgreement(entities);
			
		} else {
			processBank(entities);
		}
	}
	
	private static JSONArray sendDocumentOCR(String docType, String filepath, String filename){
		if (filename.endsWith(".json")){
			log.info("skip AI");
			return getFakeData(filepath + filename);
			
		} else if (filename.endsWith("ai_demo.pdf")){
			InputStream is;
			try {
				is = sendDocumentToExternalService(docType, filepath, filename);
				return getJsonEntitiesFromStream(is);
			} catch (IOException e) {
				log.error(" ", e);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param docType
	 * @param appl
	 * @param methodActionStr
	 */
	public void submit(String docType, SmAppl appl, String methodActionStr){
		if (StringUtils.isEmpty(docType) || appl == null){
			return;
		}
		log.info(docType+"\t"+appl.getApplNo());
		if ("EPF".equals(docType) && !EntityHelper.isCollectionEmpty(selectedEmployees)){
			submitEPF(appl);
			
		} else if ("TNC".equals(docType)){
			submitTenancy(appl);
		}
		//save smAppl
		if (StringUtils.isNotBlank(methodActionStr)) {
			MethodExpression methodExp = FacesScopes.createMethodExpression(
					FacesContext.getCurrentInstance(), methodActionStr, null, new Class[] {});
			methodExp.invoke(FacesContext.getCurrentInstance().getELContext(), new Object[] {});
		}
	}
	
	private void submitEPF(SmAppl appl){
		if (appl.getSmPersonnels() == null){
			appl.setSmPersonnels(new ArrayList<SmPersonnel>());
		}
		for (EPFEntity.EmployeeInfo employee : selectedEmployees) {
			SmPersonnel per = new SmPersonnel();
			per.setName(employee.getNama());
			per.setIdentificationNo(employee.getNoPengenalanDiri());
			per.setEpfNo(employee.getNoAhliKWSP());
			
			//SmApplicationUtil - setDefaultPersonnelValues()
			// or check SssmSO on how to save SmPersonnels
			per.setPersonnelId(null);
			per.setEpRole("");
			per.setShareAmt(new BigDecimal(0));
			per.setSharePercentage(new BigDecimal(0));
			per.setTitle(new PmSalutation());
			per.setRevNo(1);
			per.setSmAppl(appl);
			per.setBumiFlg(1);
			per.setMuslimFlg(1);
			
			appl.getSmPersonnels().add(per);
		}
	}
	
	//wip
	private void submitTenancy(SmAppl appl){
		SmFacility basicCompany = new SmFacility();
		if (!StringUtils.isEmpty(tenancy.namaPemilikPremis)){
			basicCompany.setOwnerName(tenancy.namaPemilikPremis);
		}
		if (!StringUtils.isEmpty(tenancy.sewaanBulanan)){
			try {
				BigDecimal rent = new BigDecimal(tenancy.sewaanBulanan);
				basicCompany.setRentalRate(rent);
			} catch (NumberFormatException e) {
				log.error("Invalid Rent", e);
			}
		}
	}
	

	/**
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
		List<JSONArray> entities = new ArrayList<>();
		for (SmApplDocFile doc : suppDoc.getSmApplDocFiles()){
			JSONArray arr = sendDocumentOCR("bank", doc.getFilePath(), doc.getFileName());
			if (arr != null){
				entities.add(arr);
			}
		}
		List<BankStatementEntity> banks = processBank(entities);
		
		// wip - need to map based on fields received in JSON
		suppDoc.setMappedFieldAI("Bank Info:<br/>Bank Name, Account No., Company Name");
		
		validateBankEntity(appl, banks);
	}
	
	private static void validateBankEntity(SmAppl appl, List<BankStatementEntity> banks){
		String companyNameSupp = appl.getSmSupplier().getCompanyName();
		log.info("...processBankStatement1\t..." + companyNameSupp);
		Map<String, String> supplierAccounts = new HashMap<>();
		for (SmSupplierBank suppBank : appl.getSmSupplierBanks()){
			supplierAccounts.put(suppBank.getAccountNo(), suppBank.getPmFinancialOrg().getFinOrgName());
		}
		List<String> supplierBanks = new ArrayList<>(supplierAccounts.values());
		
		boolean hasWrongCompanyName = false;
		StringBuilder errMsg = new StringBuilder();
		for (BankStatementEntity bank : banks){
			String companyNameJson = bank.accountHolderName;
			
			log.info("...processBankStatement\t..." + companyNameJson);
			String bankName = supplierAccounts.get(bank.accountNo);
			if (bankName == null){
				errMsg.append("\nAccount No.: " + bank.accountNo);
				
				bankName = fuzzyMatchList(bank.bankName, supplierBanks);
				if (StringUtils.isEmpty(bankName)){
					errMsg.append("\nBank Name: " + bank.bankName);
				}
				
			} else {
				double score =  fuzzyMatchJaroWinkler(bankName, bank.bankName);
				log.info("Score: "+score+"\tBank1: "+bankName+"\tBank2:"+bank.bankName);
				if (score < minScoreFuzzyMatch){
					errMsg.append("\nBank Name: " + bank.bankName);
				}
			}
			log.info("account no:"+bank.accountNo);
			log.info("match with bank:"+bankName);
			if (!StringUtils.equals(companyNameJson, companyNameSupp)){
				hasWrongCompanyName = true;
			}
			
		}
		if (hasWrongCompanyName){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					SmCommonUtil.getMessage("lbl.sm.ai.bank.verify", new String[] { "" }));
			FacesContext.getCurrentInstance().addMessage("ai", msg);
		}
		if (!StringUtils.isEmpty(errMsg.toString())){
			errMsg.insert(0, "The information in your bank statement(s) does not match with" +
					" any existing Bank Account Details in Bank Information section:");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, errMsg.toString());
			FacesContext.getCurrentInstance().addMessage("ai", msg);
		}
	}
	
	/**
	 * @param entities
	 * @return
	 */
	public static List<BankStatementEntity> processBank(List<JSONArray> entities){
		if (EntityHelper.isCollectionEmpty(entities)){
			return new ArrayList<>();
		}
		List<BankStatementEntity> banks = new ArrayList<>();
		for (JSONArray entity : entities){
			BankStatementEntity bank = new BankStatementEntity(entity);
			banks.add(bank);
			log.info("COMPANY NAME: "+bank.accountHolderName);
		}
		return banks;
	}

	/**
	 * @param entities
	 */
	public static void processTenancyAgreement(List<JSONArray> entities){
		if (EntityHelper.isCollectionEmpty(entities)){
			return;
		}
		List<TenancyEntity> tes = new ArrayList<>();
		for (JSONArray entity : entities){
			TenancyEntity te = new TenancyEntity(entity);
			tes.add(te);
			if (tenancy == null){
				tenancy = te;
			}
		}
		log.info(tenancy);
	}

	/**
	 * @param entities
	 */
	public static void processEPF(List<JSONArray> entities){
		if (EntityHelper.isCollectionEmpty(entities)){
			return;
		}
		List<EPFEntity> ees = new ArrayList<>();
		for (JSONArray entity : entities){
			EPFEntity ee = new EPFEntity(entity);
			ees.add(ee);
			if (epf == null){
				epf = ee;
			}
		}
		log.info(epf);
	}
	
	/**
	 * Clear uploaded documents
	 */
	public void clearDocs() {
		docs.clear();
	}
	
	public List<SmApplDocFile> getDocs() {
		return docs;
	}
	
	/**
	 * 
	 * @param doc
	 */
	public void deleteDoc(SmApplDocFile doc) {
		log.info(docs.size());
		docs.remove(doc);
		log.info(docs.size());
	}
	
	public List<EPFEntity.EmployeeInfo> getEmployees() {
		return epf != null ? epf.getEmployees() : new ArrayList<EPFEntity.EmployeeInfo>();
	}


	private static JSONArray getFakeData(String filepath) {
		if (!filepath.endsWith(".json")){
			return null;
		}
		try {
			//based on commonUploadFile()
			String tempDir = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/";
			tempDir += filepath;
			File file = new File(tempDir);
			String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
			return parseJsonEntities(content);
		} catch (IOException e) {
			log.error(" Error reading JSON file", e);
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

	private static JSONArray getJsonEntitiesFromStream(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			return parseJsonEntities(content.toString());
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

		int statusCode = conn.getResponseCode();
		log.info("API Response Code: " + statusCode);

		if (statusCode >= 200 && statusCode < 300) {
			return conn.getInputStream();
		}

		throw new IOException("HTTP Error " + statusCode + ": " + conn.getResponseMessage());
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
	 * Fuzzy match using Levenshtein, case-insensitive. Higher score == better match. 
	 * Jaro-Winkler is prob better for bank name matching use-case.
	 * @deprecated fuzzyMatchJaroWinkler more accurate
	 * @param st1
	 * @param st2
	 * @return score
	 */
	@Deprecated
	private static double fuzzyMatch(String st1, String st2) {
		if (StringUtils.isEmpty(st1) || StringUtils.isEmpty(st2)){
			return 0.0;
		}
		String s1 = st1.toLowerCase().trim();
		String s2 = st2.toLowerCase().trim();
		
		if (s1.equals(s2)){
			return 1.0;
		}
		int maxLen = Math.max(s1.length(), s2.length());
		if (maxLen == 0){
			return 1.0;
		}
		int distance = StringUtils.getLevenshteinDistance(s1, s2);
		return 1.0 - ((double) distance / maxLen);
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

	private static class BankStatementEntity {
		private String bankName;
		private String accountNo;
		private String accountHolderName;
		private String bankBranch;

		private BankStatementEntity(JSONArray entities) {
			try {
				for (int i = 0; i < entities.length(); i++) {
					JSONObject entity = entities.getJSONObject(i);
					String type = entity.optString("type", "");
					String text = entity.optString("value", "");
					double conf = entity.optDouble(confidence, 0.0);
					if (conf < minConfidence){
						log.info("Patut skip la kan? " + conf);
					}
					if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text)){
						continue;
					}
					switch (type) {
					case "Bank_Name":
						this.bankName = text.trim();
						break;
					case "Account_Number":
						this.accountNo = text.trim();
						break;
					case "Account_Holder_Name":
						this.accountHolderName = text.trim();
						break;
					case "Bank_Branch":
						this.bankBranch = text.trim();
						break;
					default:
					}	
				}
			} catch (JSONException e) {
				log.error("Error BankStatementEntity", e);
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (bankName != null) {
				sb.append("bankName: ").append(bankName).append("\n");
			}
			if (accountNo != null) {
				sb.append("accountNo: ").append(accountNo).append("\n");
			}
			if (accountHolderName != null) {
				sb.append("accountHolderName: ").append(accountHolderName).append("\n");
			}
			if (bankBranch != null) {
				sb.append("bankBranch: ").append(bankBranch).append("\n");
			}
			return sb.toString();
		}
	}

	/**
	 * JSON mapping for EPF document
	 * @author AI
	 */
	public static class EPFEntity {		//outdated format
		private EmployerInfo employer;
		private List<EmployeeInfo> employees;

		private static class EmployerInfo {
			private String noRujukan;
			private String nama;
			private String alamat;

			public EmployerInfo(String noRujukan, String nama, String alamat) {
				this.noRujukan = noRujukan;
				this.nama = nama;
				this.alamat = alamat;
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();
				if (nama != null) {
					sb.append("employer: ").append(nama).append("\n");
				}
				if (noRujukan != null) {
					sb.append("employer reference: ").append(noRujukan).append("\n");
				}
				if (alamat != null) {
					sb.append("employer address: ").append(alamat).append("\n");
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
			private String noAhliKWSP;
			private String noPengenalanDiri;
			private String nama;
			private String carumanMajikan;
			private String carumanPekerja;
			
			/**
			 * @param noAhliKWSP
			 * @param noPengenalanDiri
			 * @param nama
			 * @param carumanMajikan
			 * @param carumanPekerja
			 */
			public EmployeeInfo(String noAhliKWSP, String noPengenalanDiri, String nama, 
					String carumanMajikan, String carumanPekerja) {
				this.noAhliKWSP = noAhliKWSP;
				this.noPengenalanDiri = noPengenalanDiri;
				this.nama = nama;
				this.carumanMajikan = carumanMajikan;
				this.carumanPekerja = carumanPekerja;
			}

			public String getNoAhliKWSP() {
				return this.noAhliKWSP;
			}

			public String getNoPengenalanDiri() {
				return this.noPengenalanDiri;
			}

			public String getNama() {
				return this.nama;
			}
		}

		private EPFEntity(JSONArray entities) {
			this.employees = new ArrayList<>();
			try {
				for (int i = 0; i < entities.length(); i++) {
					JSONObject entity = entities.getJSONObject(i);
					String type = entity.optString("type", "");
					String text = entity.optString(mentionTextStr, "");
					if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text)){
						continue;
					}
					if ("employer".equals(type)){
						parseEmployer(entity, text);
					} else if ("employee".equals(type)){
						parseEmployee(entity);
					}
				}

			} catch (JSONException e) {
				log.error("Error EPFEntity", e);
			}
		}
		
		public List<EmployeeInfo> getEmployees() {
			return employees != null ? employees : new ArrayList<EmployeeInfo>();
		}

		private void parseEmployer(JSONObject entity, String text) throws JSONException{
			String empNoRujukan = null;
			String empNama = null;
			String empAlamat = null;
			
			JSONArray properties = entity.optJSONArray("properties");
			if (properties != null) {
				for (int j = 0; j < properties.length(); j++) {
					JSONObject prop = properties.getJSONObject(j);
					String propType = prop.optString("type", "");
					String propText = prop.optString(mentionTextStr, "");

					if ("Alamat_Majikan".equals(propType)) {
						empAlamat = propText.trim();
					}
				}
			} else {
				// Fallback to mentionText if no properties
				empAlamat = text.trim();
			}
			this.employer = new EmployerInfo(empNoRujukan, empNama, empAlamat);
		}
		
		private void parseEmployee(JSONObject entity) throws JSONException{
			String noAhli = null;
			String noPengenalan = null;
			String nama = null;
			String carumanMaj = null;
			String carumanPek = null;

			JSONArray empProperties = entity.optJSONArray("properties");
			if (empProperties == null){
				return;
			}
			for (int j = 0; j < empProperties.length(); j++) {
				JSONObject prop = empProperties.getJSONObject(j);
				String propType = prop.optString("type", "");
				String propText = prop.optString(mentionTextStr, "");

				switch (propType) {
				case "No_Ahli_KWSP":
					noAhli = propText.trim();
					break;
				case "No_Pengenalan_Diri":
					noPengenalan = propText.trim();
					break;
				case "Nama_Pekerja":
					nama = propText.trim();
					break;
				case "Caruman_Majikan":
					carumanMaj = propText.trim();
					break;
				case "Caruman_Pekerja":
					carumanPek = propText.trim();
					break;
				default:
				}
			}
			this.employees.add(new EmployeeInfo(noAhli, noPengenalan, nama, carumanMaj, carumanPek));
		}
	}

	private static class TenancyEntity {	//outdated format
		private String alamatSewaanPremis;
		private String namaPemilikPremis;
		private String sewaanBulanan;
		private String postcode;
		private String country;
		private String state;
		private String district;
		private String city;
		private String normalizedAddress;

		public TenancyEntity(JSONArray entities) {
			try {
				for (int i = 0; i < entities.length(); i++) {
					JSONObject entity = entities.getJSONObject(i);
					String type = entity.optString("type", "");
					String text = entity.optString(mentionTextStr, "");
					if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text)){
						continue;
					}
					switch (type) {
					case "Alamat_Sewaan-Premis":
						this.alamatSewaanPremis = text.trim();
						setAddress(entity);
						break;
					case "Nama_Pemilik-Premis":
						this.namaPemilikPremis = text.trim();
						break;
					case "Sewaan_Bulanan":
						this.sewaanBulanan = text.trim();
						break;
					default:
					}
				}
			} catch (JSONException e) {
				log.error("Error TenancyEntity", e);
			}
		}
		
		private void setAddress(JSONObject entity){
			JSONObject addressComponents = entity.optJSONObject("addressComponents");
			if (addressComponents != null) {
				this.postcode = addressComponents.optString("postcode", "").trim();
				this.country = addressComponents.optString("country", "").trim();
				this.state = addressComponents.optString("state", "").trim();
				this.district = addressComponents.optString("district", "").trim();
				this.city = addressComponents.optString("city", "").trim();
				this.normalizedAddress = addressComponents.optString("normalized_Address", "").trim();
			}
		}
	}
}