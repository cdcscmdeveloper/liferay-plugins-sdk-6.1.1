package my.ep.web.backingbean.sm.ai;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import my.ep.constant.EpPropertiesConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

/**
 * Support types for AI-assisted supporting-document extraction
 */
public final class AiDocSupport {

	private static final String BR = "<br/>";
	private static final String AI_ONOFF = EpPropertiesUtils.getInstance().getProperty("google.doc.ai.onoff");
	private static final String AI_BANK_OCR = EpPropertiesUtils.getInstance().getProperty("google.doc.bank.co.name.check.onoff");
	// this filename will always go to API
	private static final String DEMO_AI_FILE = "demo_ai_40112";

	private AiDocSupport() {
	}

	public static boolean isAiEnabled() {
		return AI_ONOFF != null && AI_ONOFF.trim().equalsIgnoreCase("1");
	}

	/**
	 * Bank statements reach the OCR API when the company-name check is on; with it off only the demo
	 * file does, so nothing else carries an extracted name that could block Declare.
	 * @param fileName
	 * @return
	 */
	public static boolean isBankOcrAllowed(String fileName) {
		if (AI_BANK_OCR != null && AI_BANK_OCR.trim().equalsIgnoreCase("1")) {
			return true;
		}
		return fileName != null && fileName.contains(DEMO_AI_FILE);
	}

	/**
	 * Fields the applicant edited since extraction, from AI_API_RESPONSE.USER_EDIT.
	 */
	public static final class AiUserEdit {

		public static final String KEY_TENANCY = "hq";
		// never an NRIC: marks the applicant's EPF tick/untick decision as recorded
		public static final String KEY_EPF_DECIDED = "_decided";
		public static final String F_DECIDED = "decided";
		public static final String F_EPF_NO = "epf_no";
		public static final String F_NAME = "name";
		public static final String F_IC = "ic";
		public static final String F_OWNER_NAME = "owner_name";
		public static final String F_RENTAL_RATE = "rental_rate";
		public static final String F_ADDRESS1 = "address1";
		public static final String F_ADDRESS2 = "address2";
		public static final String F_ADDRESS3 = "address3";
		public static final String F_POSTCODE = "postcode";
		public static final String F_COUNTRY = "country";
		public static final String F_STATE = "state";
		public static final String F_DISTRICT = "district";
		public static final String F_CITY = "city";

		private static final Logger log = Logger.getLogger(AiUserEdit.class);

		private AiUserEdit() {
		}

		public static Map<String, Set<String>> parse(String userEdit) {
			Map<String, Set<String>> edits = new HashMap<>();
			if (StringUtils.isEmpty(userEdit)) {
				return edits;
			}
			try {
				JSONObject root = new JSONObject(userEdit);
				for (Iterator<?> keys = root.keys(); keys.hasNext();) {
					String entityKey = String.valueOf(keys.next());
					JSONArray fields = root.optJSONArray(entityKey);
					if (fields == null) {
						continue;
					}
					Set<String> names = new HashSet<>();
					for (int i = 0; i < fields.length(); i++) {
						names.add(fields.getString(i));
					}
					edits.put(entityKey, names);
				}
			} catch (JSONException | NullPointerException e) {
				log.error("Error parsing USER_EDIT", e);
			}
			return edits;
		}

		public static String toJson(Map<String, Set<String>> edits) {
			JSONObject root = new JSONObject();
			try {
				for (Map.Entry<String, Set<String>> entry : edits.entrySet()) {
					if (!entry.getValue().isEmpty()) {
						root.put(entry.getKey(), new JSONArray(entry.getValue()));
					}
				}
			} catch (JSONException e) {
				log.error("Error building USER_EDIT", e);
			}
			return root.toString();
		}

		public static Set<String> fields(Map<String, Set<String>> edits, String entityKey) {
			Set<String> names = entityKey == null ? null : edits.get(entityKey);
			return names == null ? new HashSet<String>() : names;
		}

		public static boolean add(Map<String, Set<String>> edits, String entityKey, String field) {
			if (entityKey == null) {
				return false;
			}
			Set<String> names = edits.get(entityKey);
			if (names == null) {
				names = new HashSet<>();
				edits.put(entityKey, names);
			}
			return names.add(field);
		}

		public static boolean clear(Map<String, Set<String>> edits, String entityKey) {
			return entityKey != null && edits.remove(entityKey) != null;
		}
	}

	/**
	 * All OCR-JSON parsing, the confidence threshold and the supplier-facing label lookup
	 */
	public static final class AiDocParser {

		private AiDocParser() {
		}

		public static final Logger log = Logger.getLogger(AiDocParser.class);
		private static final String GOOGLE_CONSENT = EpPropertiesUtils.getInstance().getProperty("google.doc.consent");
		private static final String VALUE = "value";
		private static final String CONFIDENCE = "confidence";
		private static final String FIELDS = "fields";
		private static final String RESULTS = "results";
		private static final String PROPERTIES = "properties";
		private static final String NORM_VALUE = "normalizedValue";

		private static final double DEFAULT_MIN_CONFIDENCE = 0.8;
		public static final double MIN_CONFIDENCE = resolveMinConfidence();

		// Minimum OCR field confidence, from ngep.properties ai.min.confidence; default when missing/unparseable.
		private static double resolveMinConfidence() {
			String prop = EpPropertiesUtils.getInstance().getProperty("google.ai.min.confidence");
			if (StringUtils.isEmpty(prop)) {
				return DEFAULT_MIN_CONFIDENCE;
			}
			try {
				return Double.parseDouble(prop.trim());
			} catch (NumberFormatException e) {
				log.warn("Invalid ai.min.confidence='" + prop + "', using default " + DEFAULT_MIN_CONFIDENCE);
				return DEFAULT_MIN_CONFIDENCE;
			}
		}

		// Supplier-facing label from the common bundle in the user's locale.
		public static String label(String key) {
			return ResourceBundleUtils.getMessage(key, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		}
		
		/**
		 * Extract OCR data.results.fields array; null when absent/empty
		 * @param jsonContent
		 * @return
		 */
		public static JSONArray parseFields(String jsonContent) {
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
				if (!data.has(RESULTS)) {
					log.warn("  No results found in data");
					return null;
				}
				JSONObject results = data.getJSONObject(RESULTS);
				if (!results.has(FIELDS)) {
					log.warn("  No fields found in results");
					return null;
				}
				JSONArray fields = results.getJSONArray(FIELDS);
				if (fields.length() == 0) {
					log.warn("  No fields found");
					return null;
				}
				return fields;
			} catch (JSONException | NullPointerException e) {
				log.error(" Error parsing JSON", e);
			}
			return null;
		}
		
		/**
		 * OCR error/status code from the raw response body; 200 when there is no error.code.
		 * @param rawContent
		 * @return
		 */
		public static int getStatusCode(String rawContent) {
			try {
				return new JSONObject(rawContent).getJSONObject("error").getInt("code");
			} catch (JSONException e) {
				return 200;
			}
		}
		
		/**
		 * Tenancy object
		 * @param entities
		 * @return
		 */
		public static AiTenancyDTO toTenancy(JSONArray entities) {
			AiTenancyDTO dto = new AiTenancyDTO();
			try {
				for (int i = 0; i < entities.length(); i++) {
					JSONObject entity = entities.getJSONObject(i);
					String type = entity.optString("type", "");
					String text = entity.optString(VALUE, "");
					String value = entity.optString(NORM_VALUE, text);
					double conf = entity.optDouble(CONFIDENCE, 0.0);

					if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text) || conf < MIN_CONFIDENCE) {
						continue;
					}
					value = value.trim();
					if ("landlords_name".equals(type)) {
						dto.setLandlordsName(value);
					} else if ("tenants_name".equals(type)) {
						dto.setTenantsName(value);
					} else if ("monthly_rental".equals(type)) {
						dto.setMonthlyRental(new BigDecimal(value));
					} else if ("premises_address".equals(type)) {
						dto.setPremisesAddress(value);
						setTenancyAddress(dto, entity.optJSONObject("addressComponents"));
					}
				}
			} catch (JSONException | NumberFormatException e) {
				log.error("Error TenancyEntity", e);
				dto.setError(true);
			} catch (NullPointerException e) {
				log.error("Error TenancyEntity-NPE");
				dto.setError(true);
			}
			if (dto.getLandlordsName() == null && dto.getTenantsName() == null
					&& dto.getMonthlyRental() == null && dto.getPremisesAddress() == null) {
				dto.setError(true);
			}
			return dto;
		}

		private static void setTenancyAddress(AiTenancyDTO dto, JSONObject addr) {
			if (addr != null) {
				dto.setPostcode(addr.optString("postcode", "").trim());
				String fullAddr = addr.optString("normalized_Address", "").trim();
				truncateAddressAtPostcode(dto, fullAddr, dto.getPostcode());
				dto.setCodeCity(addr.optString("city_code", "").trim());
			}
			if (!StringUtils.isEmpty(dto.getCodeCity())) {
				PmCity city = SmServiceLookup.getCitySO().getCityByCode(dto.getCodeCity()).getPmCity();
				dto.setPmCity(city);
			}
		}

		// Truncate the normalized address at the postcode and split into addr1/2/3.
		private static void truncateAddressAtPostcode(AiTenancyDTO dto, String addr, String postcode) {
			dto.setAddr1(addr);
			dto.setAddr2("");
			dto.setAddr3("");
			if (StringUtils.isEmpty(addr) || StringUtils.isEmpty(postcode)) {
				return;
			}
			int postcodeIndex = addr.indexOf(postcode);
			if (postcodeIndex == -1) {
				return;
			}
			String truncated = addr.substring(0, postcodeIndex).trim();
			String result = truncated.replaceAll(",\\s*$", "").trim();

			Pattern pattern = Pattern.compile(",|\\s+(?=(?:jalan|jln|taman|tmn|blok|seksyen)\\b)",
					Pattern.CASE_INSENSITIVE);
			String[] parts = pattern.split(result, 3);

			if (parts.length > 0) {
				dto.setAddr1(parts[0].trim());
			}
			if (parts.length > 1) {
				dto.setAddr2(parts[1].trim());
			}
			if (parts.length > 2) {
				dto.setAddr3(parts[2].trim());
			}
		}
		
		/**
		 * EPF object
		 * @param entities
		 * @return
		 */
		public static AiEpfResultDTO toEpf(JSONArray entities) {
			AiEpfResultDTO dto = new AiEpfResultDTO();
			List<AiEpfResultDTO.AiEmployeeDTO> employees = new ArrayList<>();
			try {
				for (int i = 0; i < entities.length(); i++) {
					JSONObject entityObj = entities.getJSONObject(i);
					String entityId = entityObj.getString("entity");
					JSONArray properties = entityObj.getJSONArray(PROPERTIES);

					if (entityId.startsWith("employee-")) {
						employees.add(toEmployee(properties));
					} else if (entityId.startsWith("employer-")) {
						dto.setEmployerName(toEmployerName(properties));
					}
				}
			} catch (JSONException e) {
				log.error("Error EPFEntity", e);
			} catch (NullPointerException e) {
				log.error("Error EPFEntity-NPE");
			}
			dto.setEmployees(employees);
			return dto;
		}

		private static AiEpfResultDTO.AiEmployeeDTO toEmployee(JSONArray properties) throws JSONException {
			AiEpfResultDTO.AiEmployeeDTO emp = new AiEpfResultDTO.AiEmployeeDTO();
			for (int i = 0; i < properties.length(); i++) {
				JSONObject property = properties.getJSONObject(i);
				String type = property.optString("type", "");
				String text = property.optString(VALUE, "");
				String value = property.optString(NORM_VALUE, text);
				double conf = property.optDouble(CONFIDENCE, 0.0);
				if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text) || conf < MIN_CONFIDENCE) {
					continue;
				}
				value = value.trim();
				if ("KWSP_number".equals(type)) {
					emp.setKwspNumber(value);
				} else if ("NRIC_number".equals(type)) {
					// the statement prints the NRIC dashed or spaced; eP stores the 12 digits
					emp.setNricNumber(AiDocValidator.normalizeNric(value));
				} else if ("employee_name".equals(type)) {
					emp.setEmployeeName(value);
				}
			}
			return emp;
		}

		private static String toEmployerName(JSONArray properties) throws JSONException {
			String employerName = null;
			for (int i = 0; i < properties.length(); i++) {
				JSONObject property = properties.getJSONObject(i);
				String type = property.optString("type", "");
				String text = property.optString(VALUE, "");
				String value = property.optString(NORM_VALUE, text);
				double conf = property.optDouble(CONFIDENCE, 0.0);
				if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text) || conf < MIN_CONFIDENCE) {
					continue;
				}
				if ("employer_name".equals(type)) {
					employerName = value.trim();
				}
			}
			return employerName;
		}

		// One OCR response = possibly several bank-account records; null/empty entities yields an error DTO.
		public static AiBankDocDTO toBankDoc(Long apiId, JSONArray entities) {
			AiBankDocDTO dto = new AiBankDocDTO();
			dto.setApiId(apiId);
			if (entities == null || entities.length() == 0) {
				dto.setError(true);
				return dto;
			}
			try {
				Set<String> seen = new HashSet<>();
				for (int i = 0; i < entities.length(); i++) {
					JSONObject bankEntity = entities.getJSONObject(i);
					String entityId = bankEntity.getString("entity");
					JSONArray properties = bankEntity.getJSONArray(PROPERTIES);
					AiBankDocDTO.AiBankStatementDTO bank = toBankStatement(properties, entityId);
					// one row per bank name + account number, per FRS 2.4
					String key = AiDocValidator.bankAccountKey(bank);
					if (key == null || seen.add(key)) {
						dto.getBankStatements().add(bank);
					}
				}
			} catch (JSONException e) {
				log.error("Error DocEntity", e);
				dto.setError(true);
			} catch (NullPointerException e) {
				log.error("Error DocEntity-NPE");
				dto.setError(true);
			}
			return dto;
		}

		private static AiBankDocDTO.AiBankStatementDTO toBankStatement(JSONArray properties, String entityId)
				throws JSONException {
			AiBankDocDTO.AiBankStatementDTO bank = new AiBankDocDTO.AiBankStatementDTO();
			bank.setEntityId(entityId);
			for (int i = 0; i < properties.length(); i++) {
				JSONObject property = properties.getJSONObject(i);
				String type = property.optString("type", "");
				String text = property.optString(VALUE, "");
				String value = property.optString(NORM_VALUE, text);
				double conf = property.optDouble(CONFIDENCE, 0.0);
				if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text) || conf < MIN_CONFIDENCE) {
					continue;
				}
				if ("Bank_Name".equals(type)) {
					bank.setBankName(value.trim());
				} else if ("Account_Number".equals(type)) {
					bank.setAccountNo(value.trim());
				} else if ("Account_Holder_Name".equals(type)) {
					bank.setAccountHolderName(value.trim());
				}
			}
			return bank;
		}

		/**
		 * Build the OCR request body (JSON) from the uploaded file. The biz-sm SO performs the HTTP transport.
		 * @throws IOException if the file is missing/empty or the payload can't be built
		 * @param docType
		 * @param filepath
		 * @param filename
		 * @param clientRequestId
		 * @return
		 * @throws IOException
		 */
		public static String buildOcrRequestBody(String docType, String filepath, String filename, String clientRequestId)
				throws IOException {

			String tempDir = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR) + "/" + filepath + filename;
			File file = new File(tempDir);

			if (!file.exists()) {
				String errorMsg = "File not found: " + tempDir;
				log.error(errorMsg);
				throw new IOException(errorMsg);
			}
			if (file.length() == 0) {
				String errorMsg = "File is empty (0 bytes): " + tempDir;
				log.error(errorMsg);
				throw new IOException(errorMsg);
			}

			log.info("Uploading file: " + filename + " (" + file.length() + " bytes from " + tempDir + ")");

			byte[] fileBytes = Files.readAllBytes(file.toPath());
			String base64Content = DatatypeConverter.printBase64Binary(fileBytes);

			String consent = StringUtils.isEmpty(GOOGLE_CONSENT) ? "true" : GOOGLE_CONSENT;
			JSONObject jsonPayload = new JSONObject();
			try {
				jsonPayload.put("base64Content", base64Content);
				jsonPayload.put("consent", consent);
				jsonPayload.put("documentType", docType);
				jsonPayload.put("fileName", filename);
				jsonPayload.put("mimeType", "application/pdf");
				jsonPayload.put("clientRequestId", clientRequestId);

			} catch (JSONException | NullPointerException e) {
				log.error(e);
				throw new IOException("Error building JSON payload");
			}

			return jsonPayload.toString();
		}

		// Temporary demo harness: reads a .json fixture straight from the doc repository, bypassing OCR.
		public static String getFakeData(String filepath, String filename) {
			if (!filename.endsWith(".json")) {
				return null;
			}
			try {
				Thread.sleep(2500);
				String tempDir = EpPropertiesUtils.getInstance().getProperty(
						EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR) + "/";
				tempDir += filepath + filename;
				File file = new File(tempDir);
				return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
			} catch (IOException e) {
				log.error(" Error reading JSON file", e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return null;
		}
	}

	/**
	 * AI comparison/verification: hand-rolled Jaro-Winkler fuzzy matching, tenancy field comparison and
	 * bank grouping/dedupe with per-bank mismatch flags. Pure functions over DTOs and entities.
	 */
	public static final class AiDocValidator {

		private AiDocValidator() {
		}

		public static final Logger log = Logger.getLogger(AiDocValidator.class);

		public static final double MIN_BANK_MATCH_SCORE = 0.4;
		public static final double MIN_BANK_MATCH_MARGIN = 0.15;
		public static final double MIN_TEXT_MATCH_SCORE = 0.6;

		/**
		 * Verify a tenancy extraction against the rental info keyed in Premises Type; sets the per-field verdict
		 * flags on the DTO. A field absent from either side is excluded from the verdict (left valid). Landlord's
		 * Name / Company / Monthly Rental match exactly; the address is matched on tokens.
		 */
		public static void validateTenancy(AiTenancyDTO te, String landlordName, String companyName,
				BigDecimal rentalRate, SmAddress addr) {
			if (te == null) {
				return;
			}
			te.setValidLandlordsName(StringUtils.isBlank(landlordName) || StringUtils.isBlank(te.getLandlordsName())
					|| normalizedEquals(landlordName, te.getLandlordsName()));
			te.setValidTenantsName(StringUtils.isBlank(te.getTenantsName())
					|| normalizedEquals(companyName, te.getTenantsName()));
			te.setValidMonthlyRental(rentalRate == null || te.getMonthlyRental() == null
					|| rentalRate.compareTo(te.getMonthlyRental().setScale(2)) == 0);
			compareAddress(te, addr);
		}

		private static void compareAddress(AiTenancyDTO te, SmAddress addr) {
			// Address absent on either side is excluded from the verdict.
			if (te.getPremisesAddress() == null || addr == null || addr.getPmCity() == null || te.getPmCity() == null) {
				te.setValidAddress(true);
				return;
			}
			te.setValidAddress(false);
			if (!te.getPmCity().getCityId().equals(addr.getPmCity().getCityId())) {
				return;
			}
			if (te.getPostcode() == null || !te.getPostcode().equals(addr.getPostcode())) {
				return;
			}
			String suppAddr = joinParts(addr.getAddress1(), addr.getAddress2(), addr.getAddress3());
			String jsonAddr = joinParts(te.getAddr1(), te.getAddr2(), te.getAddr3());
			double score = tokenSimilarity(suppAddr, jsonAddr);
			if (score < MIN_TEXT_MATCH_SCORE) {
				return;
			}
			te.setValidAddress(true);
		}

		/**
		 * Verify an EPF extraction's employer name against the registered company name; sets the verdict flag
		 * on the DTO. Unlike the other docs, a blank employer name counts as a mismatch: its employees are
		 * dropped from the list and light no icons.
		 */
		public static void validateEpf(AiEpfResultDTO ee, String companyName) {
			if (ee == null) {
				return;
			}
			// unlike tenancy, an employer name the OCR never read counts as a mismatch
			ee.setValidEmployerName(normalizedEquals(companyName, ee.getEmployerName()));
		}

		/**
		 * Set every extracted bank record's valid* flags against the supplier's Bank Information.
		 * Mutates the DTOs in place.
		 * @param appl
		 * @param docEntities
		 */
		public static void validateBank(SmAppl appl, List<AiBankDocDTO> docEntities) {
			if (EntityHelper.isCollectionEmpty(docEntities)) {
				return;
			}
			Map<String, String> supplierAccounts = collectSupplierAccounts(appl);
			Set<String> supplierOrgNames = new HashSet<>(supplierAccounts.values());
			List<PmFinancialOrg> candidates = candidateFinancialOrgs(appl);
			String companyNameSupp = appl.getSmSupplier().getCompanyName();

			// the flags live on each record, so a repeated account still has to be verified on its own file
			for (AiBankDocDTO entity : docEntities) {
				for (AiBankDocDTO.AiBankStatementDTO bank : entity.getBankStatements()) {
					validateBankRecord(bank, supplierAccounts, supplierOrgNames, candidates, companyNameSupp);
				}
			}
		}

		// Account number -> chosen bank's canonical name, over every bank keyed in at Bank Information.
		private static Map<String, String> collectSupplierAccounts(SmAppl appl) {
			Map<String, String> supplierAccounts = new HashMap<>();
			if (EntityHelper.isCollectionEmpty(appl.getSmSupplierBanks())) {
				return supplierAccounts;
			}
			for (SmSupplierBank suppBank : appl.getSmSupplierBanks()) {
				String accountNo = normalizeAccountNo(suppBank.getAccountNo());
				// a half-keyed row has nothing to compare against; so has one with no digits at all
				if (StringUtils.isEmpty(accountNo) || suppBank.getPmFinancialOrg() == null) {
					continue;
				}
				supplierAccounts.put(accountNo, canonicalizeCompanyName(suppBank.getPmFinancialOrg().getFinOrgName()));
			}
			return supplierAccounts;
		}

		// The banks the supplier could have picked, taken from the same service types the Bank Information
		// dropdown offers, so a match can only ever land on something selectable.
		private static List<PmFinancialOrg> candidateFinancialOrgs(SmAppl appl) {
			Set<String> serviceTypes = new HashSet<>();
			if (!EntityHelper.isCollectionEmpty(appl.getSmSupplierBanks())) {
				for (SmSupplierBank suppBank : appl.getSmSupplierBanks()) {
					addBankServiceTypes(serviceTypes, suppBank.getAccountPurpose());
				}
			}
			if (serviceTypes.isEmpty()) {
				addBankServiceTypes(serviceTypes, null);
			}
			// keyed by canonical name, not id: PM_FINANCIAL_ORG holds the same bank under several ids,
			// and identical names would otherwise cancel each other out on the runner-up margin below
			Map<String, PmFinancialOrg> candidates = new HashMap<>();
			for (String serviceType : serviceTypes) {
				FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
				reqDTO.setServiceType(serviceType);
				FinancialOrgResDTO resDTO = SmServiceLookup.getFinancialOrgSO()
						.retrieveFinancialOrgListByServiceType(reqDTO);
				if (resDTO == null || EntityHelper.isCollectionEmpty(resDTO.getFinancialOrgList())) {
					continue;
				}
				for (PmFinancialOrg org : resDTO.getFinancialOrgList()) {
					candidates.put(canonicalizeCompanyName(org.getFinOrgName()), org);
				}
			}
			return new ArrayList<>(candidates.values());
		}

		private static void addBankServiceTypes(Set<String> serviceTypes, String accountPurpose) {
			if ("P".equalsIgnoreCase(accountPurpose)) {
				serviceTypes.add(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
			} else if ("R".equalsIgnoreCase(accountPurpose)) {
				serviceTypes.add(ParameterConst.BANK_SERVICES_REGISTRATION);
			} else {
				serviceTypes.add(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
				serviceTypes.add(ParameterConst.BANK_SERVICES_REGISTRATION);
			}
		}

		private static void validateBankRecord(AiBankDocDTO.AiBankStatementDTO bank,
				Map<String, String> supplierAccounts, Set<String> supplierOrgNames,
				List<PmFinancialOrg> candidates, String companyNameSupp) {
			String companyNameJson = bank.getAccountHolderName();
			String resolvedOrgName = resolveFinancialOrgName(bank.getBankName(), candidates);
			String chosenOrgName = supplierAccounts.get(normalizeAccountNo(bank.getAccountNo()));
			// a field the OCR did not read is not a mismatch, so it keeps its valid verdict
			boolean hasBankName = !StringUtils.isBlank(bank.getBankName());
			boolean hasAccountNo = !StringUtils.isBlank(bank.getAccountNo());
			if (chosenOrgName == null) {
				if (hasAccountNo) {
					bank.setValidAccountNo(false);
				}
				// no account to anchor on, so the name only has to be one of the banks the supplier keyed in
				if (hasBankName && (resolvedOrgName == null || !supplierOrgNames.contains(resolvedOrgName))) {
					bank.setValidBankName(false);
				}
			} else if (hasBankName && !chosenOrgName.equals(resolvedOrgName)) {
				bank.setValidBankName(false);
			}
			// only a name that was actually extracted can mismatch the registered one
			if (!StringUtils.isBlank(companyNameJson) && !StringUtils.isBlank(companyNameSupp)
					&& !normalizedEquals(companyNameJson, companyNameSupp)) {
				bank.setValidAccountHolderName(false);
			}
		}
		
		/**
		 * Lower-case, strip punctuation (keep letters/digits/whitespace). 
		 * "AS Eng Sdn Bhd" == "AS ENG SDN. BHD."
		 * @param st1
		 * @param st2
		 * @return
		 */
		public static boolean normalizedEquals(String st1, String st2) {
			String c1 = canonicalizeCompanyName(st1);
			String c2 = canonicalizeCompanyName(st2);
			if (c1.isEmpty() || c2.isEmpty()) {
				
				return false;
			}
			return c1.equals(c2);
		}
		
		/**
		 * Grouping key for an extracted bank record: bank name + account number, per FRS 2.4.
		 * @param bank
		 * @return null when neither is present, so the record is never treated as a duplicate
		 */
		public static String bankAccountKey(AiBankDocDTO.AiBankStatementDTO bank) {
			if (bank == null || (StringUtils.isBlank(bank.getBankName())
					&& StringUtils.isBlank(bank.getAccountNo()))) {
				return null;
			}
			return canonicalizeCompanyName(bank.getBankName()) + "|"
					+ normalizeAccountNo(bank.getAccountNo());
		}

		public static String canonicalizeCompanyName(String name) {
			if (StringUtils.isBlank(name)) {
				return "";
			}
			String canonical = name.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
			return canonical.replaceAll("\\s+", " ").trim();
		}

		/**
		 * Account numbers are numeric; the dashes and spaces are only how they get keyed in
		 * or printed on the statement, so compare on the digits alone.
		 * @param accountNo
		 * @return digits only, empty when there are none
		 */
		public static String normalizeAccountNo(String accountNo) {
			if (StringUtils.isBlank(accountNo)) {
				return "";
			}
			return accountNo.replaceAll("[^0-9]", "");
		}

		/**
		 * A Malaysian NRIC is 12 digits; the dashes and spaces are only how it gets printed on
		 * the statement, so match on the digits alone.
		 * @param nric
		 * @return digits only, empty when there are none
		 */
		public static String normalizeNric(String nric) {
			if (StringUtils.isBlank(nric)) {
				return "";
			}
			return nric.replaceAll("[^0-9]", "");
		}

		public static List<String> tokenize(String name) {
			List<String> tokens = new ArrayList<>();
			String canonical = canonicalizeCompanyName(name);
			if (canonical.isEmpty()) {
				return tokens;
			}
			for (String token : canonical.split(" ")) {
				if (!token.isEmpty()) {
					tokens.add(token);
				}
			}
			return tokens;
		}

		public static String joinParts(String... parts) {
			StringBuilder sb = new StringBuilder();
			for (String part : parts) {
				if (StringUtils.isNotBlank(part)) {
					sb.append(part).append(" ");
				}
			}
			return sb.toString().trim();
		}

		/**
		 * Share of tokens the two strings have in common, for free text with no reference list behind it.
		 * @param st1
		 * @param st2
		 * @return 0.0 when either side has no tokens
		 */
		public static double tokenSimilarity(String st1, String st2) {
			Set<String> shared = new HashSet<>(tokenize(st1));
			Set<String> other = new HashSet<>(tokenize(st2));
			if (shared.isEmpty() || other.isEmpty()) {
				return 0.0;
			}
			Set<String> union = new HashSet<>(shared);
			union.addAll(other);
			shared.retainAll(other);
			return (double) shared.size() / union.size();
		}

		// Skim Perbankan Islam entries are separate legal entities, so they never match a conventional name.
		public static boolean isSpiName(String name) {
			return tokenize(name).contains("spi")
					|| canonicalizeCompanyName(name).contains("skim perbankan islam");
		}

		/**
		 * Resolve an extracted bank name to one candidate bank's canonical name. Tokens are weighted by how rare
		 * they are across the candidate list, so shared boilerplate such as BANK or BERHAD cannot carry a match on
		 * its own. The verdict is the name rather than the id, because one bank owns several PM_FINANCIAL_ORG rows.
		 * @param extractedName
		 * @param candidates
		 * @return the canonical bank name, or null when nothing clears both the floor and the runner-up
		 */
		public static String resolveFinancialOrgName(String extractedName, List<PmFinancialOrg> candidates) {
			Set<String> queryTokens = new HashSet<>(tokenize(extractedName));
			if (queryTokens.isEmpty() || EntityHelper.isCollectionEmpty(candidates)) {
				return null;
			}
			boolean spi = isSpiName(extractedName);
			List<PmFinancialOrg> pool = new ArrayList<>();
			for (PmFinancialOrg org : candidates) {
				if (isSpiName(org.getFinOrgName()) == spi) {
					pool.add(org);
				}
			}
			if (pool.isEmpty()) {
				return null;
			}
			Map<String, Integer> docFreq = documentFrequency(pool);
			double best = 0;
			double runnerUp = 0;
			PmFinancialOrg bestOrg = null;
			for (PmFinancialOrg org : pool) {
				double score = weightedTokenScore(queryTokens, new HashSet<>(tokenize(org.getFinOrgName())),
						docFreq, pool.size());
				if (score > best) {
					runnerUp = best;
					best = score;
					bestOrg = org;
				} else if (score > runnerUp) {
					runnerUp = score;
				}
			}
			log.debug("Bank name resolve extracted=" + extractedName + " best="
					+ (bestOrg == null ? null : bestOrg.getFinOrgName()) + " score=" + best
					+ " runnerUp=" + runnerUp);
			if (bestOrg == null || best < MIN_BANK_MATCH_SCORE || best - runnerUp < MIN_BANK_MATCH_MARGIN) {
				return null;
			}
			return canonicalizeCompanyName(bestOrg.getFinOrgName());
		}

		private static Map<String, Integer> documentFrequency(List<PmFinancialOrg> pool) {
			Map<String, Integer> docFreq = new HashMap<>();
			for (PmFinancialOrg org : pool) {
				for (String token : new HashSet<>(tokenize(org.getFinOrgName()))) {
					Integer freq = docFreq.get(token);
					docFreq.put(token, freq == null ? 1 : freq + 1);
				}
			}
			return docFreq;
		}

		private static double weightedTokenScore(Set<String> queryTokens, Set<String> nameTokens,
				Map<String, Integer> docFreq, int poolSize) {
			if (nameTokens.isEmpty()) {
				return 0.0;
			}
			Set<String> union = new HashSet<>(queryTokens);
			union.addAll(nameTokens);
			double shared = 0;
			double total = 0;
			for (String token : union) {
				double weight = inverseDocFrequency(token, docFreq, poolSize);
				total += weight;
				if (queryTokens.contains(token) && nameTokens.contains(token)) {
					shared += weight;
				}
			}
			return total == 0 ? 0.0 : shared / total;
		}

		private static double inverseDocFrequency(String token, Map<String, Integer> docFreq, int poolSize) {
			Integer freq = docFreq.get(token);
			return Math.log((poolSize + 1.0) / ((freq == null ? 0 : freq) + 1.0)) + 1.0;
		}

		// The basic-company (headquarters) facility that the Premises Type form binds to.
		public static SmFacility getHeadquartersFacility(SmAppl appl) {
			if (appl == null || EntityHelper.isCollectionEmpty(appl.getSmFacilities())) {
				return null;
			}
			for (SmFacility fac : appl.getSmFacilities()) {
				if (fac.isHeadquarters()) {
					return fac;
				}
			}
			return null;
		}

		// Clear-then-populate: null out the tenancy AI transients before re-deriving.
		public static void clearTenancyAiFields(SmFacility fac) {
			if (fac == null) {
				return;
			}
			fac.setOwnerNameAI(null);
			fac.setRentalRateAI(null);
			SmAddress addr = fac.getSmAddress();
			if (addr != null) {
				addr.setAddress1AI(null);
				addr.setAddress2AI(null);
				addr.setAddress3AI(null);
				addr.setPostcodeAI(null);
				addr.setStateIdAI(null);
				addr.setDistrictIdAI(null);
				addr.setCityIdAI(null);
				addr.setCountryIdAI(null);
			}
		}

		// display AI icons when values match
		public static void setTenancyAiFields(SmFacility fac, AiTenancyDTO te, Set<String> edited) {
			if (fac == null || te == null) {
				return;
			}
			if (!edited.contains(AiUserEdit.F_OWNER_NAME)) {
				fac.setOwnerNameAI(te.getLandlordsName());
			}
			if (!edited.contains(AiUserEdit.F_RENTAL_RATE)) {
				fac.setRentalRateAI(te.getMonthlyRental());
			}
			SmAddress addr = fac.getSmAddress();
			if (addr != null) {
				if (!edited.contains(AiUserEdit.F_ADDRESS1)) {
					addr.setAddress1AI(te.getAddr1());
				}
				if (!edited.contains(AiUserEdit.F_ADDRESS2)) {
					addr.setAddress2AI(te.getAddr2());
				}
				if (!edited.contains(AiUserEdit.F_ADDRESS3)) {
					addr.setAddress3AI(te.getAddr3());
				}
				if (!edited.contains(AiUserEdit.F_POSTCODE)) {
					addr.setPostcodeAI(te.getPostcode());
				}
				PmCity city = te.getPmCity();
				if (city != null) {
					if (!edited.contains(AiUserEdit.F_CITY)) {
						addr.setCityIdAI(city.getCityId());
					}
					if (!edited.contains(AiUserEdit.F_COUNTRY)) {
						addr.setCountryIdAI(SmCommonUtil.getMyCountryId());
					}
					if (city.getPmDistrict() != null) {
						if (!edited.contains(AiUserEdit.F_DISTRICT)) {
							addr.setDistrictIdAI(city.getPmDistrict().getDistrictId());
						}
						if (city.getPmDistrict().getPmState() != null
								&& !edited.contains(AiUserEdit.F_STATE)) {
							addr.setStateIdAI(city.getPmDistrict().getPmState().getStateId());
						}
					}
				}
			}
		}
	}

	/**
	 * A parsed EPF Contribution Statement: the employer name and the employee rows.
	 */
	public static class AiEpfResultDTO implements Serializable {

		private static final long serialVersionUID = 1L;

		private Long apiId;
		private String employerName;
		private List<AiEmployeeDTO> employees = new ArrayList<>();

		private boolean validEmployerName = true;

		public Long getApiId() {
			return apiId;
		}

		public void setApiId(Long apiId) {
			this.apiId = apiId;
		}

		/**
		 * Tooltip row: the employer name with a tick/cross verdict (set by {@link AiDocValidator}). The
		 * employee rows carry no verdict of their own and are not rendered here.
		 */
		public String getLabel() {
			// nothing mapped out of this file, same verdict as Bank and Tenancy report
			if (employerName == null && getEmployees().isEmpty()) {
				return AiDocParser.label("lbl.sm.ai.doc.invalid");
			}
			StringBuilder sb = new StringBuilder();
			if (employerName != null) {
				String s = AiDocParser.label("lbl.cm.company.name") + ": " + employerName + BR;
				if (!validEmployerName) {
					sb.append("<span style='color: red;'>").append("&#x2717; ").append(s).append("</span>");
				} else {
					sb.append("&#x2713; ").append(s);
				}
			}
			return sb.toString();
		}

		public boolean isValidEmployerName() {
			return validEmployerName;
		}

		public void setValidEmployerName(boolean validEmployerName) {
			this.validEmployerName = validEmployerName;
		}

		public String getEmployerName() {
			return employerName;
		}

		public void setEmployerName(String employerName) {
			this.employerName = employerName;
		}

		public List<AiEmployeeDTO> getEmployees() {
			return employees != null ? employees : new ArrayList<AiEmployeeDTO>();
		}

		public void setEmployees(List<AiEmployeeDTO> employees) {
			this.employees = employees;
		}

		/**
		 * One EPF employee record (KWSP No. / NRIC / name).
		 */
		public static class AiEmployeeDTO implements Serializable {

			private static final long serialVersionUID = 1L;

			private String kwspNumber;
			private String nricNumber;
			private String employeeName;

			public String getKwspNumber() {
				return kwspNumber;
			}

			public void setKwspNumber(String kwspNumber) {
				this.kwspNumber = kwspNumber;
			}

			public String getNricNumber() {
				return nricNumber;
			}

			public void setNricNumber(String nricNumber) {
				this.nricNumber = nricNumber;
			}

			public String getEmployeeName() {
				return employeeName;
			}

			public void setEmployeeName(String employeeName) {
				this.employeeName = employeeName;
			}
		}
	}

	/**
	 * A Tenancy Agreement extracted by OCR: the raw fields and the per-field verdicts (set by
	 * {@link AiDocValidator}). Two render helpers.
	 */
	public static class AiTenancyDTO implements Serializable {

		private static final long serialVersionUID = 1L;

		private String landlordsName;
		private String tenantsName;
		private BigDecimal monthlyRental;
		private String premisesAddress;
		private String postcode;
		private String addr1;
		private String addr2;
		private String addr3;
		private String codeCity;
		private PmCity pmCity;

		private boolean validLandlordsName;
		private boolean validTenantsName;
		private boolean validMonthlyRental;
		private boolean validAddress;

		private boolean error;

		// Field labels from the common bundle (locale-sensitive at render time).
		public String getLabel() {
			if (this.error) {
				return AiDocParser.label("lbl.sm.ai.doc.invalid");
			}
			StringBuilder sb = new StringBuilder();
			appendField(sb, "lbl.cm.company.name", tenantsName, validTenantsName);
			appendField(sb, "lbl.sm.facility.landLordName", landlordsName, validLandlordsName);
			appendField(sb, "lbl.sm.facility.monthRent", monthlyRental, validMonthlyRental);
			appendField(sb, "lbl.sm.facility.address", premisesAddress, validAddress);
			return sb.toString();
		}

		// One labelled field row: a green tick when valid, red cross wrapped in red when not; skips null values.
		private void appendField(StringBuilder sb, String labelKey, Object value, boolean valid) {
			if (value == null) {
				return;
			}
			String s = AiDocParser.label(labelKey) + ": " + value + BR;
			if (!valid) {
				sb.append("<span style='color: red;'>").append("&#x2717; ").append(s).append("</span>");
			} else {
				sb.append("&#x2713; ").append(s);
			}
		}

		public boolean hasError() {
			return this.error || !this.validLandlordsName || !this.validMonthlyRental
					|| !this.validTenantsName || !this.validAddress;
		}

		public String getLandlordsName() {
			return landlordsName;
		}

		public void setLandlordsName(String landlordsName) {
			this.landlordsName = landlordsName;
		}

		public String getTenantsName() {
			return tenantsName;
		}

		public void setTenantsName(String tenantsName) {
			this.tenantsName = tenantsName;
		}

		public BigDecimal getMonthlyRental() {
			return monthlyRental;
		}

		public void setMonthlyRental(BigDecimal monthlyRental) {
			this.monthlyRental = monthlyRental;
		}

		public String getPremisesAddress() {
			return premisesAddress;
		}

		public void setPremisesAddress(String premisesAddress) {
			this.premisesAddress = premisesAddress;
		}

		public String getPostcode() {
			return postcode;
		}

		public void setPostcode(String postcode) {
			this.postcode = postcode;
		}

		public String getAddr1() {
			return addr1;
		}

		public void setAddr1(String addr1) {
			this.addr1 = addr1;
		}

		public String getAddr2() {
			return addr2;
		}

		public void setAddr2(String addr2) {
			this.addr2 = addr2;
		}

		public String getAddr3() {
			return addr3;
		}

		public void setAddr3(String addr3) {
			this.addr3 = addr3;
		}

		public String getCodeCity() {
			return codeCity;
		}

		public void setCodeCity(String codeCity) {
			this.codeCity = codeCity;
		}

		public PmCity getPmCity() {
			return pmCity;
		}

		public void setPmCity(PmCity pmCity) {
			this.pmCity = pmCity;
		}

		public boolean isValidLandlordsName() {
			return validLandlordsName;
		}

		public void setValidLandlordsName(boolean validLandlordsName) {
			this.validLandlordsName = validLandlordsName;
		}

		public boolean isValidTenantsName() {
			return validTenantsName;
		}

		public void setValidTenantsName(boolean validTenantsName) {
			this.validTenantsName = validTenantsName;
		}

		public boolean isValidMonthlyRental() {
			return validMonthlyRental;
		}

		public void setValidMonthlyRental(boolean validMonthlyRental) {
			this.validMonthlyRental = validMonthlyRental;
		}

		public boolean isValidAddress() {
			return validAddress;
		}

		public void setValidAddress(boolean validAddress) {
			this.validAddress = validAddress;
		}

		public boolean isError() {
			return error;
		}

		public void setError(boolean error) {
			this.error = error;
		}
	}

	/**
	 * One OCR bank-statement response, which may carry several bank-account records. The valid* flags on each
	 * {@link AiBankStatementDTO} are set by {@link AiDocValidator}; {@link #getLabel()} renders the tooltip.
	 */
	public static class AiBankDocDTO implements Serializable {

		private static final long serialVersionUID = 1L;

		private Long apiId;
		private List<AiBankStatementDTO> bankStatements = new ArrayList<>();
		private boolean error;

		public Long getApiId() {
			return apiId;
		}

		public void setApiId(Long apiId) {
			this.apiId = apiId;
		}

		public List<AiBankStatementDTO> getBankStatements() {
			return bankStatements;
		}

		public void setBankStatements(List<AiBankStatementDTO> bankStatements) {
			this.bankStatements = bankStatements;
		}

		public boolean isError() {
			return error;
		}

		public void setError(boolean error) {
			this.error = error;
		}

		public String getLabel() {
			StringBuilder sb = new StringBuilder();
			if (this.error) {
				return AiDocParser.label("lbl.sm.ai.doc.invalid");
			}
			if (this.bankStatements.size() == 1) {
				return bankStatements.get(0).getLabel();
			}
			for (AiBankStatementDTO bank : this.bankStatements) {
				sb.append("Bank " + bank.getEntityId().substring(5)).append(":<br/>");
				sb.append(bank.getLabel()).append(BR);
			}
			return sb.toString();
		}

		public boolean hasError() {
			if (this.error) {
				return true;
			}
			if (bankStatements == null || bankStatements.isEmpty()) {
				return false;
			}
			for (AiBankStatementDTO bank : bankStatements) {
				if (!bank.isValidAccountHolderName() || !bank.isValidAccountNo() || !bank.isValidBankName()) {
					return true;
				}
			}
			return false;
		}

		/**
		 * One bank-account record extracted by OCR, plus its per-field verification flags.
		 */
		public static class AiBankStatementDTO implements Serializable {

			private static final long serialVersionUID = 1L;

			private String bankName;
			private String accountNo;
			private String accountHolderName;
			private String entityId;
			private boolean validBankName = true;
			private boolean validAccountNo = true;
			private boolean validAccountHolderName = true;

			public String getLabel() {
				StringBuilder sb = new StringBuilder();
				String err1 = "<span style='color: red;'>";
				String err2 = "</span>";
				String yes = "&#x2713; ";
				String no = "&#x2717; ";
				if (accountHolderName != null) {
					String s = AiDocParser.label("lbl.cm.company.name") + ": " + accountHolderName + BR;
					if (!validAccountHolderName) {
						sb.append(err1).append(no).append(s).append(err2);
					} else {
						sb.append(yes).append(s);
					}
				}
				if (bankName != null) {
					String s = AiDocParser.label("lbl.gfmas.bank.desc") + ": " + bankName + BR;
					if (!validBankName) {
						sb.append(err1).append(no).append(s).append(err2);
					} else {
						sb.append(yes).append(s);
					}
				}
				if (accountNo != null) {
					String s = AiDocParser.label("lbl.fl.bank.acc.no") + ": " + accountNo + BR;
					if (!validAccountNo) {
						sb.append(err1).append(no).append(s).append(err2);
					} else {
						sb.append(yes).append(s);
					}
				}
				return sb.toString();
			}

			public String getBankName() {
				return bankName;
			}

			public void setBankName(String bankName) {
				this.bankName = bankName;
			}

			public String getAccountNo() {
				return accountNo;
			}

			public void setAccountNo(String accountNo) {
				this.accountNo = accountNo;
			}

			public String getAccountHolderName() {
				return accountHolderName;
			}

			public void setAccountHolderName(String accountHolderName) {
				this.accountHolderName = accountHolderName;
			}

			public String getEntityId() {
				return entityId;
			}

			public void setEntityId(String entityId) {
				this.entityId = entityId;
			}

			public boolean isValidBankName() {
				return validBankName;
			}

			public void setValidBankName(boolean validBankName) {
				this.validBankName = validBankName;
			}

			public boolean isValidAccountNo() {
				return validAccountNo;
			}

			public void setValidAccountNo(boolean validAccountNo) {
				this.validAccountNo = validAccountNo;
			}

			public boolean isValidAccountHolderName() {
				return validAccountHolderName;
			}

			public void setValidAccountHolderName(boolean validAccountHolderName) {
				this.validAccountHolderName = validAccountHolderName;
			}
		}
	}
}
