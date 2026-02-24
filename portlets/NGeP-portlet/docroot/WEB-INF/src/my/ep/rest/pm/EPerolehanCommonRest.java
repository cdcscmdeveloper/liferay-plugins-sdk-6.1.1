package my.ep.rest.pm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import my.ep.util.EpPropertiesUtils;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

/**
 * REST endpoint for system health check and property validation with enhanced security.
 * 
 * Security Features:
 * - API key authentication via header
 * - Whitelist-based property access control
 * - Only registered properties can be accessed
 * 
 * Features:
 * - Get single property value (if whitelisted)
 * - List all whitelisted properties
 * - Search properties by pattern (within whitelist)
 * - JSON response format
 */
@Path("/health")
public class EPerolehanCommonRest {
    
    public static Logger log = Logger.getLogger(EPerolehanCommonRest.class);
    
    // Security configuration - should be externalized to properties file
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String VALID_API_KEY = "nGeP_H34lth_Ch3ck_K3y_2026_Pr0d_S3cur3!"; // TODO: Move to properties
    
    // Property name in properties file that contains the whitelist
    // Format: whitelist.prop=prop1,prop2,prop3
    private static final String WHITELIST_PROPERTY_NAME = "whitelist.prop";
    
    // Fallback whitelist if property is not configured
    // These are the allowed properties that can be accessed via API
    private static final String[] DEFAULT_WHITELIST = {
        "mygpki.new.roaming",
        "sqTimeValidity", 
        "molpay.gov.merchantid",
        "mygpki.modeUsed"
    };
    
    /**
     * Get a single property value by key (only if whitelisted)
     * 
     * @param key Property key (must be in whitelist)
     * @param apiKey API key for authentication (from header)
     * @return JSON response with property information
     * 
     * Example URL: /rest/health/check/molpay.gov.merchantid
     * Header: X-API-Key: your-api-key
     */
    @GET
    @Path("/check/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleProperty(
            @PathParam("key") String key,
            @HeaderParam(API_KEY_HEADER) String apiKey) {
        
        try {
            // Validate API key
            if (!isValidApiKey(apiKey)) {
                return createUnauthorizedResponse();
            }
            
            // Check if property is allowed
            if (!isPropertyAllowed(key)) {
                return createForbiddenResponse(key);
            }
            
            log.info("Check Property - Key: " + key);
            
            // Get node information
            String portalNode = getNodeInfo();
            String ejbNode = SmServiceLookup.getConfigGeneralSO().getNodeInfo();
            
            // Get property values (no masking needed since sensitive props are blocked)
            String portalValue = EpPropertiesUtils.getInstance().getProperty(key);
            String ejbValue = SmServiceLookup.getConfigGeneralSO().getPropertyValue(key);
            
            // Build response
            Map<String, Object> response = new HashMap<String, Object>();
            response.put("success", true);
            response.put("key", key);
            
            Map<String, Object> portalInfo = new HashMap<String, Object>();
            portalInfo.put("node", portalNode);
            portalInfo.put("value", portalValue);
            response.put("portal", portalInfo);
            
            Map<String, Object> ejbInfo = new HashMap<String, Object>();
            ejbInfo.put("node", ejbNode);
            ejbInfo.put("value", ejbValue);
            response.put("ejb", ejbInfo);
            
            // Check if values match
            boolean valuesMatch = (portalValue != null && portalValue.equals(ejbValue)) ||
                                 (portalValue == null && ejbValue == null);
            response.put("valuesMatch", valuesMatch);
            
            log.info("End Check Property - Key: " + key);
            
            return Response.status(200)
                    .entity(toJson(response))
                    .build();
            
        } catch (Exception e) {
            log.error("Error getting property: " + key, e);
            return createErrorResponse(e);
        }
    }
    
    /**
     * List all whitelisted properties
     * 
     * @param apiKey API key for authentication
     * @param source Filter by source: portal, ejb, or both (default: both)
     * @return JSON response with all whitelisted properties
     * 
     * Example URL: /rest/health/whitelist?source=portal
     * Header: X-API-Key: your-api-key
     */
    @GET
    @Path("/whitelist")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllProperties(
            @HeaderParam(API_KEY_HEADER) String apiKey,
            @QueryParam("source") @DefaultValue("both") String source) {
        
        try {
            // Validate API key
            if (!isValidApiKey(apiKey)) {
                return createUnauthorizedResponse();
            }
            
            log.info("List Whitelisted Properties - Source: " + source);
            
            List<Map<String, Object>> properties = new ArrayList<Map<String, Object>>();
            
            // Get node information
            String portalNode = getNodeInfo();
            String ejbNode = SmServiceLookup.getConfigGeneralSO().getNodeInfo();
            
            // Get whitelist
            String[] whitelist = getWhitelist();
            
            // Get properties from Portal (only whitelisted)
            if ("portal".equals(source) || "both".equals(source)) {
                Properties portalProps = EpPropertiesUtils.getInstance().getAllProperties();
                for (String key : whitelist) {
                    String value = portalProps.getProperty(key);
                    if (value != null) {
                        Map<String, Object> prop = new HashMap<String, Object>();
                        prop.put("key", key);
                        prop.put("source", "portal");
                        prop.put("node", portalNode);
                        prop.put("value", value);
                        properties.add(prop);
                    }
                }
            }
            
            // Get properties from EJB (only whitelisted)
            if ("ejb".equals(source) || "both".equals(source)) {
                Properties ejbProps = SmServiceLookup.getConfigGeneralSO().getAllPropertiesValue();
                for (String key : whitelist) {
                    String value = ejbProps.getProperty(key);
                    if (value != null) {
                        Map<String, Object> prop = new HashMap<String, Object>();
                        prop.put("key", key);
                        prop.put("source", "ejb");
                        prop.put("node", ejbNode);
                        prop.put("value", value);
                        properties.add(prop);
                    }
                }
            }
            
            Map<String, Object> response = new HashMap<String, Object>();
            response.put("success", true);
            response.put("count", properties.size());
            response.put("properties", properties);
            
            log.info("End List Whitelisted Properties - Count: " + properties.size());
            
            return Response.status(200)
                    .entity(toJson(response))
                    .build();
            
        } catch (Exception e) {
            log.error("Error listing properties", e);
            return createErrorResponse(e);
        }
    }
    
    /**
     * Search properties using LIKE pattern (only within whitelist)
     * 
     * @param pattern Search pattern (SQL LIKE style: % for wildcard)
     * @param apiKey API key for authentication
     * @return JSON response with matching whitelisted properties
     * 
     * Example URL: /rest/health/search?pattern=molpay%
     * Header: X-API-Key: your-api-key
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchProperties(
            @QueryParam("pattern") String pattern,
            @HeaderParam(API_KEY_HEADER) String apiKey) {
        
        try {
            // Validate API key
            if (!isValidApiKey(apiKey)) {
                return createUnauthorizedResponse();
            }
            
            if (pattern == null || pattern.trim().isEmpty()) {
                return createBadRequestResponse("Pattern parameter is required");
            }
            
            log.info("Search Properties - Pattern: " + pattern);
            
            // Get node information
            String portalNode = getNodeInfo();
            String ejbNode = SmServiceLookup.getConfigGeneralSO().getNodeInfo();
            
            // Convert SQL LIKE pattern to Java regex
            String regex = pattern.replace("%", ".*").replace("_", ".");
            
            List<Map<String, Object>> matchedProperties = new ArrayList<Map<String, Object>>();
            
            // Get whitelist
            String[] whitelist = getWhitelist();
            
            // Search only within whitelisted properties from Portal
            Properties portalProps = EpPropertiesUtils.getInstance().getAllProperties();
            for (String key : whitelist) {
                if (key.matches(regex)) {
                    String value = portalProps.getProperty(key);
                    if (value != null) {
                        Map<String, Object> prop = new HashMap<String, Object>();
                        prop.put("key", key);
                        prop.put("source", "portal");
                        prop.put("node", portalNode);
                        prop.put("value", value);
                        matchedProperties.add(prop);
                    }
                }
            }
            
            // Search only within whitelisted properties from EJB
            Properties ejbProps = SmServiceLookup.getConfigGeneralSO().getAllPropertiesValue();
            for (String key : whitelist) {
                if (key.matches(regex)) {
                    String value = ejbProps.getProperty(key);
                    if (value != null) {
                        Map<String, Object> prop = new HashMap<String, Object>();
                        prop.put("key", key);
                        prop.put("source", "ejb");
                        prop.put("node", ejbNode);
                        prop.put("value", value);
                        matchedProperties.add(prop);
                    }
                }
            }
            
            Map<String, Object> response = new HashMap<String, Object>();
            response.put("success", true);
            response.put("pattern", pattern);
            response.put("count", matchedProperties.size());
            response.put("properties", matchedProperties);
            
            log.info("End Search Properties - Pattern: " + pattern + ", Count: " + matchedProperties.size());
            
            return Response.status(200)
                    .entity(toJson(response))
                    .build();
            
        } catch (Exception e) {
            log.error("Error searching properties: " + pattern, e);
            return createErrorResponse(e);
        }
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Validate API key
     */
    private boolean isValidApiKey(String apiKey) {
        // TODO: Implement more sophisticated validation
        // - Check against database
        // - Validate expiry
        // - Check rate limits
        return VALID_API_KEY.equals(apiKey);
    }
    
    /**
     * Get whitelist of allowed properties from properties file or use default
     */
    private String[] getWhitelist() {
        try {
            String whitelistValue = EpPropertiesUtils.getInstance().getProperty(WHITELIST_PROPERTY_NAME);
            if (whitelistValue != null && !whitelistValue.trim().isEmpty()) {
                // Split by comma and trim each entry
                String[] whitelist = whitelistValue.split(",");
                for (int i = 0; i < whitelist.length; i++) {
                    whitelist[i] = whitelist[i].trim();
                }
                return whitelist;
            }
        } catch (Exception e) {
            log.warn("Error reading whitelist from properties, using default: " + e.getMessage());
        }
        
        // Return default whitelist if property not configured
        return DEFAULT_WHITELIST;
    }
    
    /**
     * Check if property is in the whitelist
     */
    private boolean isPropertyAllowed(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        String[] whitelist = getWhitelist();
        
        // Check if property is in whitelist
        for (String allowedProp : whitelist) {
            if (key.equalsIgnoreCase(allowedProp)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get current node information
     */
    private String getNodeInfo() {
        String nodeName = System.getProperty("jboss.node.name");
        String serverName = System.getProperty("jboss.server.name");
        return nodeName != null ? nodeName : serverName;
    }
    
    /**
     * Convert Map to JSON string manually (no external library needed)
     */
    private String toJson(Map<String, Object> map) {
        if (map == null) {
            return "{}";
        }
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            
            String key = entry.getKey();
            Object value = entry.getValue();
            
            json.append("\"").append(escapeJson(key)).append("\":");
            json.append(toJsonValue(value));
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Convert object to JSON value
     */
    @SuppressWarnings("unchecked")
    private String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeJson((String) value) + "\"";
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Map) {
            return toJson((Map<String, Object>) value);
        } else if (value instanceof List) {
            return toJsonArray((List<?>) value);
        } else {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
    }
    
    /**
     * Convert List to JSON array
     */
    private String toJsonArray(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        boolean first = true;
        for (Object item : list) {
            if (!first) {
                json.append(",");
            }
            first = false;
            
            if (item instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) item;
                json.append(toJson(map));
            } else {
                json.append(toJsonValue(item));
            }
        }
        
        json.append("]");
        return json.toString();
    }
    
    /**
     * Escape special characters for JSON
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (ch < ' ') {
                        String hex = Integer.toHexString(ch);
                        escaped.append("\\u");
                        for (int j = 0; j < 4 - hex.length(); j++) {
                            escaped.append('0');
                        }
                        escaped.append(hex);
                    } else {
                        escaped.append(ch);
                    }
            }
        }
        return escaped.toString();
    }
    
    /**
     * Create unauthorized response (401)
     */
    private Response createUnauthorizedResponse() {
        Map<String, Object> error = new HashMap<String, Object>();
        error.put("success", false);
        error.put("error", "Unauthorized");
        error.put("message", "Invalid or missing API key");
        return Response.status(401).entity(toJson(error)).build();
    }
    
    /**
     * Create forbidden response (403)
     */
    private Response createForbiddenResponse(String key) {
        Map<String, Object> error = new HashMap<String, Object>();
        error.put("success", false);
        error.put("error", "Forbidden");
        error.put("message", "Access to property '" + key + "' is not allowed");
        return Response.status(403).entity(toJson(error)).build();
    }
    
    /**
     * Create bad request response (400)
     */
    private Response createBadRequestResponse(String message) {
        Map<String, Object> error = new HashMap<String, Object>();
        error.put("success", false);
        error.put("error", "Bad Request");
        error.put("message", message);
        return Response.status(400).entity(toJson(error)).build();
    }
    
    /**
     * Create error response (500)
     */
    private Response createErrorResponse(Exception e) {
        Map<String, Object> error = new HashMap<String, Object>();
        error.put("success", false);
        error.put("error", "Internal Server Error");
        error.put("message", e.getMessage());
        return Response.status(500).entity(toJson(error)).build();
    }
}
