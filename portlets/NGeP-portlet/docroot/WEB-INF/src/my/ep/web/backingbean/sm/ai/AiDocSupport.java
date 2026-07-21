package my.ep.web.backingbean.sm.ai;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import my.ep.constant.EpPropertiesConst;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;

/**
 * Support types for AI-assisted supporting-document extraction
 */
public final class AiDocSupport {

	private static final String BR = "<br/>";

	// AI doc extraction master switch from ngep.properties; absent or anything but "1" is off.
	private static final String AI_ONOFF = EpPropertiesUtils.getInstance().getProperty("google.doc.ai.onoff");

	private AiDocSupport() {
	}

	public static boolean isAiEnabled() {
		return AI_ONOFF != null && AI_ONOFF.trim().equalsIgnoreCase("1");
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
					emp.setNricNumber(value);
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
				for (int i = 0; i < entities.length(); i++) {
					JSONObject bankEntity = entities.getJSONObject(i);
					String entityId = bankEntity.getString("entity");
					JSONArray properties = bankEntity.getJSONArray(PROPERTIES);
					dto.getBankStatements().add(toBankStatement(properties, entityId));
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

		public static final double MIN_FUZZY_SCORE = 0.8;

		/**
		 * Verify a tenancy extraction against the rental info keyed in Premises Type; sets the per-field verdict
		 * flags on the DTO. A field absent from either side is excluded from the verdict (left valid). Landlord's
		 * Name keeps Jaro-Winkler; Company / Monthly Rental match exactly.
		 */
		public static void validateTenancy(AiTenancyDTO te, String landlordName, String companyName,
				BigDecimal rentalRate, SmAddress addr) {
			if (te == null) {
				return;
			}
			te.setValidLandlordsName(StringUtils.isBlank(landlordName) || StringUtils.isBlank(te.getLandlordsName())
					|| fuzzyMatchJaroWinkler(landlordName, te.getLandlordsName()) >= MIN_FUZZY_SCORE);
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
			String suppAddr = addr.getAddress1() + addr.getAddress2() + addr.getAddress3();
			String jsonAddr = te.getAddr1() + te.getAddr2() + te.getAddr3();
			double score = fuzzyMatchJaroWinkler(suppAddr, jsonAddr);
			if (score < MIN_FUZZY_SCORE) {
				return;
			}
			te.setValidAddress(true);
		}

		/**
		 * Verify an EPF extraction's employer name against the registered company name; sets the verdict flag
		 * on the DTO. A blank employer name is excluded from the verdict (left valid), as with the other docs.
		 */
		public static void validateEpf(AiEpfResultDTO ee, String companyName) {
			if (ee == null) {
				return;
			}
			ee.setValidEmployerName(StringUtils.isBlank(ee.getEmployerName())
					|| normalizedEquals(companyName, ee.getEmployerName()));
		}

		/**
		 * Group extracted bank records by (bank name + account number), dedupe, then set each record's valid*
		 * flags against the newly-added/modified supplier banks. Mutates the DTOs in place.
		 * @param appl
		 * @param docEntities
		 */
		public static void validateBank(SmAppl appl, List<AiBankDocDTO> docEntities) {
			if (EntityHelper.isCollectionEmpty(docEntities)) {
				return;
			}
			List<AiBankDocDTO.AiBankStatementDTO> bankEntities = dedupeBankStatements(docEntities);

			// Compare with new/modified banks only.
			Map<String, String> supplierAccounts = collectChangedSupplierAccounts(appl);
			List<String> supplierBanks = new ArrayList<>(supplierAccounts.values());
			String companyNameSupp = appl.getSmSupplier().getCompanyName();

			for (AiBankDocDTO.AiBankStatementDTO bank : bankEntities) {
				validateBankRecord(bank, supplierAccounts, supplierBanks, companyNameSupp);
			}
		}

		// Flatten every response's statements and keep the first per (bank name + account number).
		private static List<AiBankDocDTO.AiBankStatementDTO> dedupeBankStatements(List<AiBankDocDTO> docEntities) {
			Map<String, AiBankDocDTO.AiBankStatementDTO> uniqueBanks = new LinkedHashMap<>();
			for (AiBankDocDTO entity : docEntities) {
				for (AiBankDocDTO.AiBankStatementDTO be : entity.getBankStatements()) {
					String key = (be.getBankName() == null ? "" : be.getBankName().trim().toLowerCase()) + "|"
							+ (be.getAccountNo() == null ? "" : be.getAccountNo().trim());
					if (!uniqueBanks.containsKey(key)) {
						uniqueBanks.put(key, be);
					}
				}
			}
			return new ArrayList<>(uniqueBanks.values());
		}

		// Account number -> financial-org name, for new/modified supplier banks only.
		private static Map<String, String> collectChangedSupplierAccounts(SmAppl appl) {
			Map<String, String> supplierAccounts = new HashMap<>();
			for (SmSupplierBank suppBank : appl.getSmSupplierBanks()) {
				if (!StringUtils.isEmpty(suppBank.getChangedFieldStatus())) {
					supplierAccounts.put(suppBank.getAccountNo(), suppBank.getPmFinancialOrg().getFinOrgName());
				}
			}
			return supplierAccounts;
		}

		private static void validateBankRecord(AiBankDocDTO.AiBankStatementDTO bank,
				Map<String, String> supplierAccounts, List<String> supplierBanks, String companyNameSupp) {
			String companyNameJson = bank.getAccountHolderName();
			String bankName = supplierAccounts.get(bank.getAccountNo());
			if (bankName == null) {
				bank.setValidAccountNo(false);

				bankName = fuzzyMatchList(bank.getBankName(), supplierBanks);
				if (StringUtils.isEmpty(bankName)) {
					bank.setValidBankName(false);
				}

			} else {
				double score = fuzzyMatchJaroWinkler(bankName, bank.getBankName());
				log.debug("Bank name fuzzy score=" + score + " supplier=" + bankName + " extracted=" + bank.getBankName());
				if (score < MIN_FUZZY_SCORE) {
					bank.setValidBankName(false);
				}
			}
			// Company / account-holder name matches exactly, case-insensitive; a missing holder name is excluded.
			if (!StringUtils.isBlank(companyNameJson) && !normalizedEquals(companyNameJson, companyNameSupp)) {
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
		
		public static String canonicalizeCompanyName(String name) {
			if (StringUtils.isBlank(name)) {
				return "";
			}
			String canonical = name.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
			return canonical.replaceAll("\\s+", " ").trim();
		}

		public static String fuzzyMatchList(String target, List<String> list) {
			if (StringUtils.isEmpty(target) || EntityHelper.isCollectionEmpty(list)) {
				return "";
			}
			double bestScore = 0;
			String bestResult = "";
			for (String s : list) {
				double score = fuzzyMatchJaroWinkler(target, s);
				if (score > bestScore && score > MIN_FUZZY_SCORE) {
					bestScore = score;
					bestResult = s;
				}
			}
			return bestResult;
		}
		
		/**
		 * Jaro-Winkler, better match for "maybank" and "maybank investment banking".
		 * @param st1
		 * @param st2
		 * @return
		 */
		public static double fuzzyMatchJaroWinkler(String st1, String st2) {
			if (StringUtils.isEmpty(st1) || StringUtils.isEmpty(st2)) {
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

		private static double jaroSimilarity(String st1, String st2) {
			if (st1 == null || st2 == null) {
				return 0.0;
			}
			if (st1.equals(st2)) {
				return 1.0;
			}

			String s1 = st1.toLowerCase();
			String s2 = st2.toLowerCase();

			int len1 = s1.length();
			int len2 = s2.length();

			if (len1 == 0 || len2 == 0) {
				return (len1 == 0 && len2 == 0) ? 1.0 : 0.0;
			}

			boolean[] s1Matches = new boolean[len1];
			boolean[] s2Matches = new boolean[len2];

			int matches = countMatches(s1, s2, s1Matches, s2Matches);
			if (matches == 0) {
				return 0.0;
			}
			int transpositions = countTranspositions(s1, s2, s1Matches, s2Matches);

			return ((double) matches / len1 + (double) matches / len2
					+ (matches - (double) transpositions / 2) / matches) / 3.0;
		}

		// Flag the matching characters within the Jaro match window; returns the match count.
		private static int countMatches(String s1, String s2, boolean[] s1Matches, boolean[] s2Matches) {
			int len1 = s1.length();
			int len2 = s2.length();
			int matchWindow = Math.max(0, Math.max(len1, len2) / 2 - 1);
			int matches = 0;
			for (int i = 0; i < len1; i++) {
				int start = Math.max(0, i - matchWindow);
				int end = Math.min(i + matchWindow + 1, len2);
				for (int j = start; j < end; j++) {
					if (!s2Matches[j] && s1.charAt(i) == s2.charAt(j)) {
						s1Matches[i] = true;
						s2Matches[j] = true;
						matches++;
						break;
					}
				}
			}
			return matches;
		}

		// Count out-of-order matched characters (twice the Jaro transposition count).
		private static int countTranspositions(String s1, String s2, boolean[] s1Matches, boolean[] s2Matches) {
			int transpositions = 0;
			int k = 0;
			for (int i = 0; i < s1.length(); i++) {
				if (!s1Matches[i]) {
					continue;
				}
				while (!s2Matches[k]) {
					k++;
				}
				if (s1.charAt(i) != s2.charAt(k)) {
					transpositions++;
				}
				k++;
			}
			return transpositions;
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
			}
		}

		// Set the tenancy AI transients so isXxxFromAI() lights the icon while the field still matches the
		// extraction.
		public static void setTenancyAiFields(SmFacility fac, AiTenancyDTO te) {
			if (fac == null || te == null) {
				return;
			}
			fac.setOwnerNameAI(te.getLandlordsName());
			fac.setRentalRateAI(te.getMonthlyRental());
			SmAddress addr = fac.getSmAddress();
			if (addr != null) {
				addr.setAddress1AI(te.getAddr1());
				addr.setAddress2AI(te.getAddr2());
				addr.setAddress3AI(te.getAddr3());
				addr.setPostcodeAI(te.getPostcode());
				PmCity city = te.getPmCity();
				if (city != null) {
					addr.setCityIdAI(city.getCityId());
					if (city.getPmDistrict() != null) {
						addr.setDistrictIdAI(city.getPmDistrict().getDistrictId());
						if (city.getPmDistrict().getPmState() != null) {
							addr.setStateIdAI(city.getPmDistrict().getPmState().getStateId());
						}
					}
				}
			}
		}
	}

	/** A parsed EPF Contribution Statement: the employer name and the employee rows. */
	public static class AiEpfResultDTO implements Serializable {

		private static final long serialVersionUID = 1L;

		/** Employee rows rendered in full by {@link #getLabel()}; the rest are summarised as a count. */
		private static final int MAX_LABEL_EMPLOYEES = 5;

		private String employerName;
		private List<AiEmployeeDTO> employees = new ArrayList<>();

		private boolean validEmployerName = true;

		/**
		 * Tooltip rows: the employer name with a tick/cross verdict (set by {@link AiDocValidator}), then the
		 * first {@value #MAX_LABEL_EMPLOYEES} employees' name / IC. Any remainder is summarised as a count.
		 * Employees carry no verdict, so they render unmarked.
		 */
		public String getLabel() {
			StringBuilder sb = new StringBuilder();
			if (employerName != null) {
				String s = AiDocParser.label("lbl.cm.company.name") + ": " + employerName + BR;
				if (!validEmployerName) {
					sb.append("<span style='color: red;'>").append("&#x2717; ").append(s).append("</span>");
				} else {
					sb.append("&#x2713; ").append(s);
				}
			}
			List<AiEmployeeDTO> emps = getEmployees();
			int shown = Math.min(emps.size(), MAX_LABEL_EMPLOYEES);
			for (int i = 0; i < shown; i++) {
				AiEmployeeDTO emp = emps.get(i);
				appendEmployeeField(sb, "lbl.sm.person.name", emp.getEmployeeName());
				appendEmployeeField(sb, "lbl.sm.person.icNo", emp.getNricNumber());
				sb.append(BR);
			}
			int rest = emps.size() - shown;
			if (rest > 0) {
				sb.append(ResourceBundleUtils.getMessage("lbl.sm.ai.epf.more.employees",
						new Object[] { rest }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())).append(BR);
			}
			return sb.toString();
		}

		// One unmarked labelled employee row; skips a field OCR left below the confidence threshold.
		private void appendEmployeeField(StringBuilder sb, String labelKey, String value) {
			if (value == null) {
				return;
			}
			sb.append(AiDocParser.label(labelKey)).append(": ").append(value).append(BR);
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

		/** One EPF employee record (KWSP No. / NRIC / name). */
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

		/** One bank-account record extracted by OCR, plus its per-field verification flags. */
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
