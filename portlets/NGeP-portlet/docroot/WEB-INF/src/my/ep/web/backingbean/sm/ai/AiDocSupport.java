package my.ep.web.backingbean.sm.ai;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.ai.AIApiResponse;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmState;
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
	private static final String LBL_DOC_INVALID = "lbl.sm.ai.doc.invalid";
	private static final String LBL_AI_UNAVAILABLE = "lbl.sm.ai.unavailable";
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
		// accepted fields hang off the same map under a prefixed key, so the CLOB keeps one shape
		private static final String ACCEPTED_PREFIX = "+";
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
		public static final String F_DIVISION = "division";
		public static final String F_DISTRICT = "district";
		public static final String F_CITY = "city";
		// submitting the dialog applies the whole extracted statement, so all of these are accepted at once
		static final String[] F_TENANCY_ALL = { F_OWNER_NAME, F_RENTAL_RATE, F_ADDRESS1,
				F_ADDRESS2, F_ADDRESS3, F_POSTCODE, F_COUNTRY, F_STATE, F_DIVISION, F_DISTRICT, F_CITY };

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

		// drops both the edit marks and the accepted set, as if the entity was never submitted
		public static boolean forget(Map<String, Set<String>> edits, String entityKey) {
			return clear(edits, entityKey)
					| clear(edits, entityKey == null ? null : ACCEPTED_PREFIX + entityKey);
		}

		/**
		 * Fields the applicant applied by submitting the AI Assistant dialog. Nothing outside this
		 * set ever carries an AI icon, whatever the typed value happens to be.
		 */
		public static Set<String> accepted(Map<String, Set<String>> edits, String entityKey) {
			return fields(edits, entityKey == null ? null : ACCEPTED_PREFIX + entityKey);
		}

		public static boolean accept(Map<String, Set<String>> edits, String entityKey, String field) {
			return add(edits, entityKey == null ? null : ACCEPTED_PREFIX + entityKey, field);
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

		private static String optString(JSONObject obj, String key, String defaultValue) {
			Object o = obj.opt(key);
			return o == null || JSONObject.NULL.equals(o) ? defaultValue : o.toString();
		}

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
				JSONObject root = new JSONObject(toDbChars(jsonContent));
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
			} catch (JSONException | NullPointerException | NumberFormatException e) {
				log.error(" Error parsing JSON", e);
			}
			return null;
		}

		// the DB is WE8MSWIN1252: what it can't store becomes the 0xBF it would store, raw or backslash-u escaped
		private static String toDbChars(String json) {
			CharsetEncoder dbCharset = Charset.forName("windows-1252").newEncoder();
			StringBuilder sb = new StringBuilder(json.length());
			int i = 0;
			while (i < json.length()) {
				char c = json.charAt(i);
				if (c == '\\' && i + 1 < json.length()) {
					char next = json.charAt(i + 1);
					if (next == 'u' && i + 5 < json.length()
							&& !dbCharset.canEncode((char) Integer.parseInt(json.substring(i + 2, i + 6), 16))) {
						sb.append('\u00bf');
						i += 6;
					} else {
						// any other escape is copied whole, so an escaped backslash is not read as an escape
						sb.append(c).append(next);
						i += 2;
					}
				} else {
					sb.append(dbCharset.canEncode(c) ? c : '\u00bf');
					i++;
				}
			}
			return sb.toString();
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
		 * The AI never read this file: unreachable, an error status, or a body that does not parse.
		 * 415/422 is the AI rejecting the document, which is a wrong document, not an outage.
		 * Icons and tooltips ask the DTO built by toTenancy/toEpf/toBankDoc instead; only the upload
		 * messages read it directly.
		 */
		static boolean isUnavailable(Long apiId, Integer status, String jsonContent) {
			if (status != null && (status == 415 || status == 422)) {
				return false;
			}
			return status == null || status != 200 || !isParseable(apiId, jsonContent);
		}

		private static boolean isParseable(Long apiId, String jsonContent) {
			try {
				new JSONObject(toDbChars(jsonContent));
				return true;
			} catch (JSONException | NullPointerException | NumberFormatException e) {
				log.error("AI_API_RESPONSE " + apiId + " unparseable: " + e);
				return false;
			}
		}

		// one stored response into its DTO, the outage folded in, so the DTO alone answers label and verdict
		public static AiTenancyDTO toTenancy(AIApiResponse res) {
			return toTenancy(res.getApiResponseId(), res.getResponseStatus(), res.getJsonResponse());
		}

		public static AiTenancyDTO toTenancy(Long apiId, Integer status, String jsonContent) {
			JSONArray fields = parseFields(jsonContent);
			AiTenancyDTO dto;
			if (fields == null) {
				dto = new AiTenancyDTO();
				dto.setError(true);
			} else {
				dto = toTenancy(apiId, fields);
			}
			if (isUnavailable(apiId, status, jsonContent)) {
				dto.setUnavailable(true);
			}
			return dto;
		}

		public static AiEpfResultDTO toEpf(AIApiResponse res) {
			return toEpf(res.getApiResponseId(), res.getResponseStatus(), res.getJsonResponse());
		}

		public static AiEpfResultDTO toEpf(Long apiId, Integer status, String jsonContent) {
			AiEpfResultDTO dto = toEpf(apiId, parseFields(jsonContent));
			dto.setApiId(apiId);
			if (isUnavailable(apiId, status, jsonContent)) {
				dto.setUnavailable(true);
			}
			return dto;
		}

		public static AiBankDocDTO toBankDoc(AIApiResponse res) {
			return toBankDoc(res.getApiResponseId(), res.getResponseStatus(), res.getJsonResponse());
		}

		public static AiBankDocDTO toBankDoc(Long apiId, Integer status, String jsonContent) {
			AiBankDocDTO dto = toBankDoc(apiId, parseFields(jsonContent));
			if (isUnavailable(apiId, status, jsonContent)) {
				dto.setUnavailable(true);
			}
			return dto;
		}

		/**
		 * Tenancy object
		 * @param entities
		 * @return
		 */
		public static AiTenancyDTO toTenancy(Long apiId, JSONArray entities) {
			AiTenancyDTO dto = new AiTenancyDTO();
			try {
				for (int i = 0; i < entities.length(); i++) {
					JSONObject entity = entities.getJSONObject(i);
					String type = entity.optString("type", "");
					String text = optString(entity, VALUE, "");
					String value = optString(entity, NORM_VALUE, text);
					double conf = entity.optDouble(CONFIDENCE, 0.0);

					if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text) || conf < MIN_CONFIDENCE) {
						continue;
					}
					value = value.trim();
					if ("landlords_name".equals(type)) {
						dto.setLandlordsName(StringUtils.left(value, 50).trim());	// form maxlength; OWNER_NAME is 200
					} else if ("tenants_name".equals(type)) {
						dto.setTenantsName(value);
					} else if ("monthly_rental".equals(type)) {
						try {
							// the rental input takes 2 decimals within the SM amount range
							BigDecimal rental = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
							if (rental.doubleValue() >= SupplierManagementConst.MIN_AMT_RANGE
									&& rental.doubleValue() <= SupplierManagementConst.MAX_AMT_RANGE) {
								dto.setMonthlyRental(rental);
							} else {
								log.error("Error TenancyEntity monthly_rental out of range '" + value + "'");
							}
						} catch (NumberFormatException e) {
							log.error("Error TenancyEntity monthly_rental '" + value + "'");
						}
					} else if ("premises_address".equals(type)) {
						dto.setPremisesAddress(value);
						setTenancyAddress(dto, entity.optJSONObject("addressComponents"));
					}
				}
			} catch (JSONException e) {
				log.error("Error TenancyEntity " + apiId, e);
				dto.setUnavailable(true);
			} catch (NullPointerException e) {
				log.error("Error TenancyEntity-NPE " + apiId);
				dto.setUnavailable(true);
			}
			if (dto.getLandlordsName() == null && dto.getTenantsName() == null
					&& dto.getMonthlyRental() == null && dto.getPremisesAddress() == null) {
				dto.setError(true);
			}
			return dto;
		}

		private static void setTenancyAddress(AiTenancyDTO dto, JSONObject addr) {
			if (addr != null) {
				// form maxlength; POSTCODE is 10, ADDRESS_1/2/3 are 255
				// the postcode input accepts 5 digits only; the address is cut at the postcode as printed
				String postcode = optString(addr, "postcode", "").trim();
				dto.setPostcode(StringUtils.left(postcode.replaceAll("[^0-9]", ""), 5));
				String fullAddr = optString(addr, "normalized_Address", "").trim();
				truncateAddressAtPostcode(dto, fullAddr, postcode);
				dto.setCodeCity(optString(addr, "city_code", "").trim());
				setTenancyArea(dto, optString(addr, "state_code", "").trim(),
						optString(addr, "district_code", "").trim());
			}
		}

		// Each code must exist under its resolved parent; a null or unknown code is not extracted.
		private static void setTenancyArea(AiTenancyDTO dto, String stateCode, String districtCode) {
			PmState state = StringUtils.isEmpty(stateCode) ? null
					: SmCommonUtil.getInstance().retrieveStateByCountryCodeAndStateCode(stateCode);
			if (state == null) {
				return;
			}
			dto.setPmState(state);
			PmDistrict district = null;
			if (state.getAdoptDivision()) {
				// AI sends no division code: take the division whose district list holds the district
				DivisionReqDTO divReq = new DivisionReqDTO();
				divReq.setStateId(state.getStateId());
				for (PmDivision div : SmServiceLookup.getDivisionSO().retriveDivisionListByState(divReq)
						.getDivisionList()) {
					DistrictReqDTO distReq = new DistrictReqDTO();
					distReq.setDivisionId(div.getDivisionId());
					district = findDistrict(SmServiceLookup.getDistrictSO()
							.retriveDistrictListByDivision(distReq).getDistrictList(), districtCode);
					if (district != null) {
						dto.setPmDivision(div);
						break;
					}
				}
			} else {
				DistrictReqDTO distReq = new DistrictReqDTO();
				distReq.setStateId(state.getStateId());
				district = findDistrict(SmServiceLookup.getDistrictSO()
						.retriveDistrictListByStateID(distReq).getDistrictList(), districtCode);
			}
			if (district == null) {
				return;
			}
			dto.setPmDistrict(district);
			CityReqDTO cityReq = new CityReqDTO();
			cityReq.setDistrictId(district.getDistrictId());
			for (PmCity c : SmServiceLookup.getCitySO().retriveCityListByDistrictId(cityReq).getCityList()) {
				if (StringUtils.isNotEmpty(dto.getCodeCity()) && dto.getCodeCity().equals(c.getCityCode())) {
					dto.setPmCity(c);
					return;
				}
			}
		}

		private static PmDistrict findDistrict(List<PmDistrict> districts, String districtCode) {
			if (StringUtils.isEmpty(districtCode) || districts == null) {
				return null;
			}
			for (PmDistrict d : districts) {
				if (districtCode.equals(d.getDistrictCode())) {
					return d;
				}
			}
			return null;
		}

		// Truncate the normalized address at the postcode and split into addr1/2/3.
		private static void truncateAddressAtPostcode(AiTenancyDTO dto, String addr, String postcode) {
			dto.setAddr1(StringUtils.left(addr, 100).trim());
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
				dto.setAddr1(StringUtils.left(parts[0].trim(), 100).trim());
			}
			if (parts.length > 1) {
				dto.setAddr2(StringUtils.left(parts[1].trim(), 100).trim());
			}
			if (parts.length > 2) {
				dto.setAddr3(StringUtils.left(parts[2].trim(), 100).trim());
			}
		}
		
		/**
		 * EPF object
		 * @param entities
		 * @return
		 */
		public static AiEpfResultDTO toEpf(Long apiId, JSONArray entities) {
			AiEpfResultDTO dto = new AiEpfResultDTO();
			List<AiEpfResultDTO.AiEmployeeDTO> employees = new ArrayList<>();
			if (entities == null) {
				return dto;
			}
			try {
				for (int i = 0; i < entities.length(); i++) {
					JSONObject entityObj = entities.getJSONObject(i);
					String entityId = entityObj.getString("entity");
					JSONArray properties = entityObj.getJSONArray(PROPERTIES);

					if (entityId.startsWith("employee-")) {
						AiEpfResultDTO.AiEmployeeDTO emp = toEmployee(properties);
						// a row the AI could not read a name, IC/passport or EPF No. for is dropped
						if (StringUtils.isNotBlank(emp.getEmployeeName()) && StringUtils.isNotBlank(emp.getIdNo())
								&& StringUtils.isNotBlank(emp.getKwspNumber())) {
							employees.add(emp);
						}
					} else if (entityId.startsWith("employer-")) {
						dto.setEmployerName(toEmployerName(properties));
					}
				}
			} catch (JSONException e) {
				log.error("Error EPFEntity " + apiId, e);
				dto.setUnavailable(true);
			} catch (NullPointerException e) {
				log.error("Error EPFEntity-NPE " + apiId);
				dto.setUnavailable(true);
			}
			dto.setEmployees(employees);
			return dto;
		}

		private static AiEpfResultDTO.AiEmployeeDTO toEmployee(JSONArray properties) throws JSONException {
			AiEpfResultDTO.AiEmployeeDTO emp = new AiEpfResultDTO.AiEmployeeDTO();
			for (int i = 0; i < properties.length(); i++) {
				JSONObject property = properties.getJSONObject(i);
				String type = property.optString("type", "");
				String text = optString(property, VALUE, "");
				String value = optString(property, NORM_VALUE, text);
				double conf = property.optDouble(CONFIDENCE, 0.0);
				if (StringUtils.isEmpty(type) || StringUtils.isEmpty(text) || conf < MIN_CONFIDENCE) {
					continue;
				}
				value = value.trim();
				if ("KWSP_number".equals(type)) {
					// the EPF No. input accepts digits only
					emp.setKwspNumber(StringUtils.left(value.replaceAll("[^0-9]", ""), 10));
				} else if ("NRIC_number".equals(type)) {
					// the statement prints the NRIC dashed or spaced; eP stores the 12 digits
					String nric = StringUtils.left(AiDocValidator.normalizeNric(value), 12);
					// IC wins over passport, whichever the AI sends first
					if (StringUtils.isNotBlank(nric)) {
						emp.setIdNo(nric);
						emp.setPassport(false);
					}
				} else if ("Passport_number".equals(type)) {
					setPassport(emp, StringUtils.left(AiDocValidator.normalizeIdNo(value), 12));
				} else if ("employee_name".equals(type)) {
					// the OCR keeps the statement's own line breaks inside a name
					emp.setEmployeeName(StringUtils.left(AiDocValidator.normalizePersonName(value), 80).trim());
				}
			}
			return emp;
		}

		private static void setPassport(AiEpfResultDTO.AiEmployeeDTO emp, String passport) {
			if (StringUtils.isNotBlank(passport) && (StringUtils.isBlank(emp.getIdNo()) || emp.isPassport())) {
				emp.setIdNo(passport);
				emp.setPassport(true);
			}
		}

		private static String toEmployerName(JSONArray properties) throws JSONException {
			String employerName = null;
			for (int i = 0; i < properties.length(); i++) {
				JSONObject property = properties.getJSONObject(i);
				String type = property.optString("type", "");
				String text = optString(property, VALUE, "");
				String value = optString(property, NORM_VALUE, text);
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
				List<AiBankDocDTO.AiBankStatementDTO> parsed = new ArrayList<>();
				for (int i = 0; i < entities.length(); i++) {
					JSONObject bankEntity = entities.getJSONObject(i);
					String entityId = bankEntity.getString("entity");
					JSONArray properties = bankEntity.getJSONArray(PROPERTIES);
					parsed.add(toBankStatement(properties, entityId));
				}
				// deduped in validateBank, so nothing is scored until the duplicates are gone
				dto.setBankStatements(parsed);
			} catch (JSONException e) {
				log.error("Error DocEntity " + apiId, e);
				dto.setUnavailable(true);
			} catch (NullPointerException e) {
				log.error("Error DocEntity-NPE " + apiId);
				dto.setUnavailable(true);
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
				String text = optString(property, VALUE, "");
				String value = optString(property, NORM_VALUE, text);
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

		/**
		 * Verify a tenancy extraction against the rental info keyed in Premises Type; sets the per-field verdict
		 * flags on the DTO. A field absent from either side is excluded from the verdict (left valid). Landlord's
		 * Name / Company / Monthly Rental / Address match exactly.
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
			if (!normalizedEquals(suppAddr, jsonAddr)) {
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
				// one row per account, per FRS 2.4; nothing is scored until the duplicates are gone
				entity.setBankStatements(dedupeBankStatements(entity.getBankStatements()));
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
		 * One row per account, per FRS 2.4. The OCR segments a statement per printed section, so the same
		 * account arrives once per section; and a section it read no account number from, carrying a company
		 * name that is already listed against an account, is that same statement re-read, not another account.
		 * A company name that is not already listed is kept, so a statement in someone else's name still
		 * mismatches. The bank name is not part of the identity: one response is one uploaded file, so the same
		 * account number twice is the same account however its sections spell the bank, and a spelling the
		 * resolver cannot place must not split it. The record that is kept is filled from the duplicates it
		 * absorbs, so a field one printed section lost is taken from another.
		 * @param banks every record parsed out of one response, in the order the OCR returned them
		 * @return the records to show, first occurrence first
		 */
		public static List<AiBankDocDTO.AiBankStatementDTO> dedupeBankStatements(
				List<AiBankDocDTO.AiBankStatementDTO> banks) {
			// collected over the whole list before anything is dropped, so the OCR's order cannot change the outcome
			Set<String> holdersWithAccount = new HashSet<>();
			for (AiBankDocDTO.AiBankStatementDTO bank : banks) {
				if (!normalizeAccountNo(bank.getAccountNo()).isEmpty()) {
					holdersWithAccount.add(canonicalizeCompanyName(bank.getAccountHolderName()));
				}
			}
			Map<String, AiBankDocDTO.AiBankStatementDTO> kept = new LinkedHashMap<>();
			for (AiBankDocDTO.AiBankStatementDTO bank : banks) {
				String accountNo = normalizeAccountNo(bank.getAccountNo());
				String holder = canonicalizeCompanyName(bank.getAccountHolderName());
				if (accountNo.isEmpty() && holdersWithAccount.contains(holder)) {
					continue;
				}
				// a record with no account number is identified by its company name instead
				String key = accountNo.isEmpty() ? "|" + holder : accountNo;
				AiBankDocDTO.AiBankStatementDTO seen = kept.get(key);
				if (seen == null) {
					kept.put(key, bank);
				} else {
					fillBlankFields(seen, bank);
				}
			}
			return new ArrayList<>(kept.values());
		}

		// a field one printed section lost is taken from another read of the same account
		private static void fillBlankFields(AiBankDocDTO.AiBankStatementDTO kept,
				AiBankDocDTO.AiBankStatementDTO duplicate) {
			if (StringUtils.isBlank(kept.getBankName())) {
				kept.setBankName(duplicate.getBankName());
			}
			if (StringUtils.isBlank(kept.getAccountNo())) {
				kept.setAccountNo(duplicate.getAccountNo());
			}
			if (StringUtils.isBlank(kept.getAccountHolderName())) {
				kept.setAccountHolderName(duplicate.getAccountHolderName());
			}
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

		/**
		 * Personnel identity key for an IC or a passport: upper-cased letters and digits only. An IC comes
		 * out the same as {@link #normalizeNric}; a passport keeps its letters.
		 * @param idNo
		 * @return empty when there is nothing left
		 */
		public static String normalizeIdNo(String idNo) {
			if (StringUtils.isBlank(idNo)) {
				return "";
			}
			return idNo.toUpperCase().replaceAll("[^A-Z0-9]", "");
		}

		/**
		 * Collapse every run of whitespace - including the line breaks the OCR keeps inside a
		 * name - to one space, so a name reads the same however the statement printed it.
		 * @param name
		 * @return
		 */
		public static String normalizePersonName(String name) {
			if (StringUtils.isBlank(name)) {
				return "";
			}
			return name.replaceAll("\\s+", " ").trim();
		}

		/**
		 * Same person's name, whatever the spacing and case. Both sides are sanitized first.
		 * @param st1
		 * @param st2
		 * @return
		 */
		public static boolean personNameEquals(String st1, String st2) {
			String c1 = normalizePersonName(st1);
			String c2 = normalizePersonName(st2);
			return !c1.isEmpty() && c1.equalsIgnoreCase(c2);
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
				addr.setDivisionIdAI(null);
				addr.setDistrictIdAI(null);
				addr.setCityIdAI(null);
				addr.setCountryIdAI(null);
			}
		}

		// #40112 - populate a shadow field only where the applicant submitted it and has not since
		// edited it; a value she typed herself carries no AI icon even when it equals the extraction
		public static void setTenancyAiFields(SmFacility fac, AiTenancyDTO te, Set<String> accepted) {
			if (fac == null || te == null) {
				return;
			}
			if (accepted.contains(AiUserEdit.F_OWNER_NAME)) {
				fac.setOwnerNameAI(te.getLandlordsName());
			}
			if (accepted.contains(AiUserEdit.F_RENTAL_RATE)) {
				fac.setRentalRateAI(te.getMonthlyRental());
			}
			setTenancyAddressAiFields(fac.getSmAddress(), te, accepted);
		}

		private static void setTenancyAddressAiFields(SmAddress addr, AiTenancyDTO te,
				Set<String> accepted) {
			if (addr == null) {
				return;
			}
			if (accepted.contains(AiUserEdit.F_ADDRESS1)) {
				addr.setAddress1AI(te.getAddr1());
			}
			if (accepted.contains(AiUserEdit.F_ADDRESS2)) {
				addr.setAddress2AI(te.getAddr2());
			}
			if (accepted.contains(AiUserEdit.F_ADDRESS3)) {
				addr.setAddress3AI(te.getAddr3());
			}
			if (accepted.contains(AiUserEdit.F_POSTCODE)) {
				addr.setPostcodeAI(te.getPostcode());
			}
			setTenancyAreaAiFields(addr, te, accepted);
		}

		private static void setTenancyAreaAiFields(SmAddress addr, AiTenancyDTO te,
				Set<String> accepted) {
			if (te.getPmState() == null) {
				return;
			}
			if (accepted.contains(AiUserEdit.F_COUNTRY)) {
				addr.setCountryIdAI(SmCommonUtil.getMyCountryId());
			}
			if (accepted.contains(AiUserEdit.F_STATE)) {
				addr.setStateIdAI(te.getPmState().getStateId());
			}
			if (te.getPmDivision() != null && accepted.contains(AiUserEdit.F_DIVISION)) {
				addr.setDivisionIdAI(te.getPmDivision().getDivisionId());
			}
			if (te.getPmDistrict() != null && accepted.contains(AiUserEdit.F_DISTRICT)) {
				addr.setDistrictIdAI(te.getPmDistrict().getDistrictId());
			}
			if (te.getPmCity() != null && accepted.contains(AiUserEdit.F_CITY)) {
				addr.setCityIdAI(te.getPmCity().getCityId());
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
		private boolean unavailable;

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
			if (unavailable) {
				return AiDocParser.label(LBL_AI_UNAVAILABLE);
			}
			// nothing mapped out of this file, same verdict as Bank and Tenancy report
			if (employerName == null && getEmployees().isEmpty()) {
				return AiDocParser.label(LBL_DOC_INVALID);
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

		// 1-ok, 2-wrong or no employer name, 3-the AI never read the file
		public int getVerdict() {
			if (unavailable) {
				return 3;
			}
			return validEmployerName ? 1 : 2;
		}

		public boolean isValidEmployerName() {
			return validEmployerName;
		}

		public void setValidEmployerName(boolean validEmployerName) {
			this.validEmployerName = validEmployerName;
		}

		public boolean isUnavailable() {
			return unavailable;
		}

		public void setUnavailable(boolean unavailable) {
			this.unavailable = unavailable;
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
			// IC or passport, as SM_PERSONNEL.IDENTIFICATION_NO; passport says which
			private String idNo;
			private boolean passport;
			private String employeeName;

			public String getIdNo() {
				return idNo;
			}

			public void setIdNo(String idNo) {
				this.idNo = idNo;
			}

			public boolean isPassport() {
				return passport;
			}

			public void setPassport(boolean passport) {
				this.passport = passport;
			}

			public String getKwspNumber() {
				return kwspNumber;
			}

			public void setKwspNumber(String kwspNumber) {
				this.kwspNumber = kwspNumber;
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
		private PmState pmState;
		private PmDivision pmDivision;
		private PmDistrict pmDistrict;
		private PmCity pmCity;

		private boolean validLandlordsName;
		private boolean validTenantsName;
		private boolean validMonthlyRental;
		private boolean validAddress;

		private boolean error;
		private boolean unavailable;

		// Field labels from the common bundle (locale-sensitive at render time).
		public String getLabel() {
			if (this.unavailable) {
				return AiDocParser.label(LBL_AI_UNAVAILABLE);
			}
			if (this.error) {
				return AiDocParser.label(LBL_DOC_INVALID);
			}
			StringBuilder sb = new StringBuilder();
			appendField(sb, "lbl.cm.company.name", tenantsName, validTenantsName);
			appendField(sb, "lbl.sm.facility.landLordName", landlordsName, validLandlordsName);
			appendField(sb, "lbl.sm.facility.monthRent", monthlyRental, validMonthlyRental);
			appendField(sb, "lbl.sm.companyaddress", premisesAddress, validAddress);
			return sb.toString();
		}

		// One labelled field row: a green tick when valid, red cross wrapped in red when not, and a
		// darkgoldenrod warning naming the field the OCR read nothing into, same as Bank.
		private void appendField(StringBuilder sb, String labelKey, Object value, boolean valid) {
			String label = AiDocParser.label(labelKey);
			if (value == null) {
				sb.append("<span style='color: darkgoldenrod;'>").append("&#x26A0; ").append(label)
						.append(": ").append(AiDocParser.label("lbl.sm.ai.doc.notextracted"))
						.append(BR).append("</span>");
				return;
			}
			String s = label + ": " + value + BR;
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

		// read only partly: a field the OCR missed, nothing contradicting; the caller tests hasError() first
		public boolean hasWarning() {
			return !this.error && (landlordsName == null || tenantsName == null || monthlyRental == null
					|| premisesAddress == null);
		}

		// 1-ok, 2-mismatch or wrong document, 3-read only partly; the AI never reading it warns, never blocks
		public int getVerdict() {
			if (unavailable) {
				return 3;
			}
			if (hasError()) {
				return 2;
			}
			return hasWarning() ? 3 : 1;
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

		public PmState getPmState() {
			return pmState;
		}

		public void setPmState(PmState pmState) {
			this.pmState = pmState;
		}

		public PmDivision getPmDivision() {
			return pmDivision;
		}

		public void setPmDivision(PmDivision pmDivision) {
			this.pmDivision = pmDivision;
		}

		public PmDistrict getPmDistrict() {
			return pmDistrict;
		}

		public void setPmDistrict(PmDistrict pmDistrict) {
			this.pmDistrict = pmDistrict;
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

		public boolean isUnavailable() {
			return unavailable;
		}

		public void setUnavailable(boolean unavailable) {
			this.unavailable = unavailable;
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
		private boolean unavailable;

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

		public boolean isUnavailable() {
			return unavailable;
		}

		public void setUnavailable(boolean unavailable) {
			this.unavailable = unavailable;
		}

		public String getLabel() {
			StringBuilder sb = new StringBuilder();
			if (this.unavailable) {
				return AiDocParser.label(LBL_AI_UNAVAILABLE);
			}
			if (this.error) {
				return AiDocParser.label(LBL_DOC_INVALID);
			}
			if (this.bankStatements.size() == 1) {
				return bankStatements.get(0).getLabel();
			}
			// number the rows that are shown; the OCR's own entity index skips whatever was de-duped
			int bankNo = 1;
			for (AiBankStatementDTO bank : this.bankStatements) {
				sb.append("Bank ").append(bankNo++).append(":<br/>");
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
		 * Whether the AI read this document only partly: no field contradicts the application, but at least
		 * one bank on it is missing a field. Ranks below {@link #hasError()}, which the caller tests first.
		 * @return true when a kept record is missing its bank name, account number or company name
		 */
		public boolean hasWarning() {
			if (this.error || bankStatements == null || bankStatements.isEmpty()) {
				return false;
			}
			for (AiBankStatementDTO bank : bankStatements) {
				if (bank.hasNotExtracted()) {
					return true;
				}
			}
			return false;
		}

		// 1-ok, 2-mismatch or wrong document, 3-read only partly; the AI never reading it warns, never blocks Declare
		public int getVerdict() {
			if (unavailable) {
				return 3;
			}
			if (hasError()) {
				return 2;
			}
			return hasWarning() ? 3 : 1;
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
				appendField(sb, "lbl.cm.company.name", accountHolderName, validAccountHolderName);
				appendField(sb, "lbl.gfmas.bank.desc", bankName, validBankName);
				appendField(sb, "lbl.fl.bank.acc.no", accountNo, validAccountNo);
				return sb.toString();
			}

			// One labelled field row: a green tick when valid, a red cross when it contradicts the
			// application, and a darkgoldenrod warning naming the field the OCR read nothing into.
			private void appendField(StringBuilder sb, String labelKey, String value, boolean valid) {
				String label = AiDocParser.label(labelKey);
				if (StringUtils.isBlank(value)) {
					sb.append("<span style='color: darkgoldenrod;'>").append("&#x26A0; ").append(label)
							.append(": ").append(AiDocParser.label("lbl.sm.ai.doc.notextracted"))
							.append(BR).append("</span>");
					return;
				}
				String s = label + ": " + value + BR;
				if (!valid) {
					sb.append("<span style='color: red;'>").append("&#x2717; ").append(s).append("</span>");
				} else {
					sb.append("&#x2713; ").append(s);
				}
			}

			// a field this statement carries no value for; nothing to verify, but nothing mapped either
			public boolean hasNotExtracted() {
				return StringUtils.isBlank(bankName) || StringUtils.isBlank(accountNo)
						|| StringUtils.isBlank(accountHolderName);
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
