package my.ep.web.backingbean.sm.ai;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import my.ep.constant.EpPropertiesConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmRace;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
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
	private static String mentionText = "mentionText";
	private static String confidence = "confidence";
	private static String googleSkipAI = EpPropertiesUtils.getInstance().getProperty("google.doc.skip");
	private static String googleUrl = EpPropertiesUtils.getInstance().getProperty("google.doc.url");
	private static double minConfidence = 0.95;
	
	private static EPFEntity epf = null;
	private static TenancyEntity tenancy = null;
	private List<EPFEntity.EmployeeInfo> selectedEmployees;
	
	private static String tempGoogleUrl = "http://172.23.114.72:5000/process"; //http://192.168.62.17:5000/process";
	
	
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
	 * @throws InterruptedException
	 */
	public void processDoc(String docType) throws InterruptedException{
		log.info("...wip\t"+docs.size() +"\t_" + docType);
		if (EntityHelper.isCollectionEmpty(docs)){
			return;
		}
		if (StringUtils.isEmpty(googleUrl)){
			googleUrl = tempGoogleUrl;
		}
		
		if ("EPF".equals(docType)){
			processEPF(docs.get(0).getFilePath(), docs.get(0).getFileName());
			
		} else if ("TNC".equals(docType)){
			processTenancyAgreement(docs.get(0).getFilePath(), docs.get(0).getFileName());
			
		} else {
			processBank(docs.get(0).getFilePath(), docs.get(0).getFileName());
		}
	}
	
	/**
	 * 
	 * @param docType
	 * @param appl
	 */
	public void submit(String docType, SmAppl appl){
		if (StringUtils.isEmpty(docType) || appl == null){
			return;
		}
		log.info(docType+"\t"+appl.getApplNo());
		if ("EPF".equals(docType) && !EntityHelper.isCollectionEmpty(selectedEmployees)){
			submitEPF(appl);
			
		} else if ("TNC".equals(docType)){
			log.info("woah, tenancy");
			submitTenancy(appl);
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
			per.setEpRole("");
			per.setShareAmt(new BigDecimal(0));
			per.setSharePercentage(new BigDecimal(0));
			per.setTitle(new PmSalutation());
			per.setRevNo(1);
			per.setSmAppl(appl);
			per.setBumiFlg(1);
			per.setMuslimFlg(1);
			per.setPmCountry(new PmCountry());
			per.setPmRace(new PmRace());
			per.setIdentificationType(new PmParameter());
			per.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
			per.setSmStaffAcademics(new ArrayList<SmStaffAcademic>());
			per.setSmStaffExperiences(new ArrayList<SmStaffExperience>());
			per.setSmRelatedCompanies(new ArrayList<SmRelatedCompany>());
			per.setSmAssociationBodies(new ArrayList<SmAssociationBody>());
			per.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
			
			appl.getSmPersonnels().add(per);
		}
	}
	
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
	 * 
	 * @param doc
	 */
	public static void processBankStatement(SmApplSupportDoc doc){
		if (doc == null || doc.getPmDocument() == null || doc.getSmAppl() == null
				|| doc.getSmAppl().getSmSupplier() == null){
			return;
		}
		//PENYATA AKAUN SEMASA YANG TERKINI ATAS NAMA SYARIKAT / SLIP DEPOSIT (all zones)
		List<Long> bankStatementIds = Arrays.asList(529L, 633L, 676L);
		if (!bankStatementIds.contains(doc.getPmDocument().getDocumentId())){
			log.info("...not bank statement");
			return;
		}
		if (StringUtils.isEmpty(googleUrl)){
			googleUrl = tempGoogleUrl;
		}
		//wip - need to combine all docs  & convert to base64
		SmApplDocFile docFile = doc.getSmApplDocFiles().get(0);
		String filepath = docFile.getFilePath() + docFile.getFileName();

		//send to Google
		JSONArray entities = null;
		if (docFile.getFileName().endsWith(".json")){
			log.info("skip AI");
			byte[] fileData = getFakeData(filepath);
			entities = getJsonEntities(fileData); 
		} else if (docFile.getFileName().endsWith("ai_demo.pdf")){
			log.info("use google url: "+googleUrl);
			InputStream is;
			try {
				String doc64 = getBase64(doc);
				is = sendDocumentToExternalService(doc64, "bank", docFile.getFileName());
				entities = getJsonEntitiesFromStream(is);
			} catch (IOException e) {
				log.error(" ", e);
			} catch (DocumentException e) {
				log.error(" ", e);
			}
		}
		
		if (entities == null){
			return;
		}
		log.info(entities);
		BankStatementEntity bankStatement = new BankStatementEntity(entities);
		if (StringUtils.isEmpty(bankStatement.namaSyarikat)){
			return;
		}

		String companyNameJson = bankStatement.namaSyarikat;
		String companyNameSupp = doc.getSmAppl().getSmSupplier().getCompanyName();
		log.info("...processBankStatement1\t..." + companyNameJson);
		log.info("...processBankStatement2\t..." + companyNameSupp);

		// wip - need to map based on fields received in JSON
		//doc.setMappedFieldAI("Bank Info:<br/>Bank Name, Account No.");

		if (!StringUtils.equals(companyNameJson, companyNameSupp)){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					SmCommonUtil.getMessage("lbl.sm.ai.bank.verify", new String[] { "" }));
			FacesContext.getCurrentInstance().addMessage("ai", msg);
		}
	}
	
	/**
	 * @param filepath
	 * @param filename
	 */
	public static void processBank(String filepath, String filename){
		JSONArray entities = null;
		if (filename.endsWith(".json")){
			log.info("skip AI");
			byte[] fileData = getFakeData(filepath + filename);
			entities = getJsonEntities(fileData);
			
		} else if (filename.endsWith("ai_demo.pdf")){
			log.info("use google url: "+googleUrl);
			InputStream is;
			try {
				String doc64 = getBase64(filepath);
				is = sendDocumentToExternalService(doc64, "tenancy", filename);
				entities = getJsonEntitiesFromStream(is);
			} catch (IOException e) {
				log.error(" ", e);
			}
		}
		if (entities == null){
			return;
		}
		log.info(entities);
		BankStatementEntity bank = new BankStatementEntity(entities);
		log.info(bank);
		log.info("COMPANY NAME: "+bank.namaSyarikat);
	}

	/**
	 * @param filepath
	 * @param filename
	 */
	public static void processTenancyAgreement(String filepath, String filename){
		JSONArray entities = null;
		if (filename.endsWith(".json")){
			log.info("skip AI");
			byte[] fileData = getFakeData(filepath + filename);
			entities = getJsonEntities(fileData);
			
		} else if (filename.endsWith("ai_demo.pdf")){
			log.info("use google url: "+googleUrl);
			InputStream is;
			try {
				String doc64 = getBase64(filepath);
				is = sendDocumentToExternalService(doc64, "tenancy", filename);
				entities = getJsonEntitiesFromStream(is);
			} catch (IOException e) {
				log.error(" ", e);
			}
		}
		if (entities == null){
			return;
		}
		log.info("entities");
		tenancy = new TenancyEntity(entities);
		log.info(tenancy);
	}

	/**
	 * @param filepath
	 * @param filename
	 */
	public static void processEPF(String filepath, String filename){
		JSONArray entities = null;
		if (filename.endsWith(".json")){
			log.info("skip AI");
			byte[] fileData = getFakeData(filepath + filename);
			entities = getJsonEntities(fileData);
			
		} else if (filename.endsWith("ai_demo.pdf")){
			log.info("use google url: "+googleUrl);
			InputStream is;
			try {
				String doc64 = getBase64(filepath + filename);
				is = sendDocumentToExternalService(doc64, "epf", filename);
				entities = getJsonEntitiesFromStream(is);
			} catch (IOException e) {
				log.error(" ", e);
			}
		}
		if (entities == null){
			return;
		}
		log.info("entities");
		epf = new EPFEntity(entities);
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


	private static byte[] getFakeData(String filepath) {
		if (!filepath.endsWith(".json")){
			return new byte[0];
		}
		try {
			//based on commonUploadFile()
			String tempDir = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/";
			tempDir += filepath;
			File file = new File(tempDir);
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			log.error(" Error parsing JSON", e);
		}
		return new byte[0];
	}

	private static JSONArray getJsonEntities(byte[] fileData) {
		if (fileData == null || fileData.length < 1){
			return null;
		}
		try {
			String content = new String(fileData, StandardCharsets.UTF_8);
			JSONObject root = new JSONObject(content);
			if (!root.has("extractedData")) {
				log.warn("  No extractedData found in JSON");
				return null;
			}
			JSONObject extractedData = root.getJSONObject("extractedData");
			if (!extractedData.has("entities")) {
				log.warn("  No entities found in extractedData");
				return null;
			}
			JSONArray entities = extractedData.getJSONArray("entities");
			if (entities.length() == 0) {
				log.warn("  No entities found");
				return null;
			}
			return entities;
		} catch (JSONException e) {
			log.error(" Error parsing JSON", e);
		}
		return null;
	}

	private static JSONArray getJsonEntitiesFromStream(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			
			JSONObject root = new JSONObject(content.toString());
			if (!root.has("extractedData")) {
				log.warn("  No extractedData found in JSON");
				return null;
			}
			JSONObject extractedData = root.getJSONObject("extractedData");
			if (!extractedData.has("entities")) {
				log.warn("  No entities found in extractedData");
				return null;
			}
			JSONArray entities = extractedData.getJSONArray("entities");
			if (entities.length() == 0) {
				log.warn("  No entities found");
				return null;
			}
			return entities;
		} catch (IOException e) {
			log.error(" Error reading InputStream", e);
		} catch (JSONException e) {
			log.error(" Error parsing JSON from InputStream", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.warn("Error closing reader", e);
				}
			}
		}
		return null;
	}
	

	public List<EPFEntity.EmployeeInfo> getSelectedEmployees() {
		return selectedEmployees;
	}

	public void setSelectedEmployees(List<EPFEntity.EmployeeInfo> selectedEmployees) {
		this.selectedEmployees = selectedEmployees;
	}

	
	private static String getBase64(String filepath) throws IOException{
		String tempDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/"+filepath;
		File file = new File(tempDir);
		byte[] pdf = Files.readAllBytes(file.toPath());
		return DatatypeConverter.printBase64Binary(pdf);
	}

	private static String getBase64(SmApplSupportDoc doc) throws IOException, DocumentException {
		if (doc == null || EntityHelper.isCollectionEmpty(doc.getSmApplDocFiles())) {
			return "";
		}
		//based on commonUploadFile()
		String tempDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/";
		List<String> pdfPaths = new ArrayList<>();
		for (SmApplDocFile file : doc.getSmApplDocFiles()) {
			if (!file.getFileName().endsWith("pdf")) {
				continue;
			}
			pdfPaths.add(tempDir + file.getFilePath() + file.getFileName());
		}
		byte[] combinedPDF = combinePDF(pdfPaths);
		return DatatypeConverter.printBase64Binary(combinedPDF);
	}

	private static InputStream sendDocumentToExternalService(String base64, 
			String docType, String filename) throws IOException {
		
		String payload = String.format("{\"document\":\"%s\",\"doc_type\":\"%s\",\"filename\":\"%s\"}", 
				base64, docType, filename);

		URL url = new URL(googleUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		conn.setConnectTimeout(30*1000);
		conn.setReadTimeout(120*1000);

		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
		out.write(payload);
		out.flush();
		out.close();

		int statusCode = conn.getResponseCode();
		log.info("External Service Response Status: " + statusCode);

		if (statusCode >= 200 && statusCode < 300) {
			return conn.getInputStream();
		}

		throw new IOException("HTTP Error " + statusCode + ": " + conn.getResponseMessage());
	}

	/**
	 * 
	 * @param pdfPaths
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	private static byte[] combinePDF(List<String> pdfPaths) throws IOException, DocumentException {
		if (pdfPaths.size() == 1){
			File file = new File(pdfPaths.get(0));
			return Files.readAllBytes(file.toPath());
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, baos);
		document.open();
		PdfContentByte cb = writer.getDirectContent();

		for (String pdfPath : pdfPaths) {
			PdfReader reader = new PdfReader(pdfPath);
			int pageCount = reader.getNumberOfPages();

			for (int i = 1; i <= pageCount; i++) {
				document.newPage();
				PdfImportedPage page = writer.getImportedPage(reader, i);
				cb.addTemplate(page, 0, 0);
			}
			reader.close();
		}
		document.close();
		return baos.toByteArray();
	}

	private static class BankStatementEntity {
		private String namaBank;
		private String noAkaun;
		private String namaSyarikat;
		private String alamatBank;
		private String cawanganBank;

		private BankStatementEntity(JSONArray entities) {
			try {
				for (int i = 0; i < entities.length(); i++) {
					JSONObject entity = entities.getJSONObject(i);
					String type = entity.optString("type", "");
					String text = entity.optString(mentionText, "");
					double conf = entity.optDouble(confidence, 0.0);
					if (conf < minConfidence){
						log.info("Patut skip la kan? " + conf);
					}
					if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text)){
						continue;
					}
					switch (type) {
					case "Nama_Bank":
						this.namaBank = text.trim();
						break;
					case "No_Akaun":
						this.noAkaun = text.trim();
						break;
					case "Nama_Syarikat":
						this.namaSyarikat = text.trim();
						break;
					case "Alamat_Bank":
						this.alamatBank = text.trim();
						break;
					case "Cawangan_Bank":
						this.cawanganBank = text.trim();
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
			if (namaBank != null) {
				sb.append("Nama_Bank: ").append(namaBank).append("\n");
			}
			if (noAkaun != null) {
				sb.append("No_Akaun: ").append(noAkaun).append("\n");
			}
			if (namaSyarikat != null) {
				sb.append("Nama_Syarikat: ").append(namaSyarikat).append("\n");
			}
			if (alamatBank != null) {
				sb.append("Alamat_Bank: ").append(alamatBank).append("\n");
			}
			if (cawanganBank != null) {
				sb.append("Cawangan_Bank: ").append(cawanganBank).append("\n");
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
					String text = entity.optString(mentionText, "");
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
					String propText = prop.optString(mentionText, "");

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
				String propText = prop.optString(mentionText, "");

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

	private static class TenancyEntity {
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
					String text = entity.optString(mentionText, "");
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
			// Parse address components if available
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