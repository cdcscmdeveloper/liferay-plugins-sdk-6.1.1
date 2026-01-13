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
 * REST endpoint for property management with enhanced security and search capabilities.
 * 
 * Security Features:
 * - API key authentication via header
 * - Property key whitelist/blacklist filtering
 * - Sensitive property masking
 * 
 * Features:
 * - Get single property value
 * - List all properties
 * - Search properties by pattern (LIKE)
 * - JSON response format
 */
@Path("/properties")
public class EPerolehanCommonRest {
    
    public static Logger log = Logger.getLogger(EPerolehanCommonRest.class);
    
    // Security configuration - should be externalized to properties file
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String VALID_API_KEY = "test-api-key-123"; // TODO: Move to properties
    
    // List of sensitive properties that should be masked in output
    private static final String[] SENSITIVE_PATTERNS = {
        "password", "pwd", "secret", "key", "token", "credential"
    };
    
    // Whitelist approach: only these properties can be accessed
    // Comment out if you prefer blacklist approach
    // private static final String[] ALLOWED_PROPERTIES = {
    //     "molpay.url", "app.version", "system.name"
    // };
    
    // Blacklist approach: these properties cannot be accessed
    private static final String[] BLOCKED_PROPERTIES = {
        "db.password", "admin.password", "encryption.key"
    };
    
    /**
     * Get a single property value by key
     * 
     * @param key Property key
     * @param apiKey API key for authentication (from header)
     * @return JSON response with property information
     * 
     * Example URL: /rest/properties/single/molpay.url
     * Header: X-API-Key: your-api-key
     */
    @GET
    @Path("/single/{key}")
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
            
            log.info("Get Single Property - Key: " + key);
            
            // Get node information
            String portalNode = getNodeInfo();
            String ejbNode = SmServiceLookup.getConfigGeneralSO().getNodeInfo();
            
            // Get property values
            String portalValue = EpPropertiesUtils.getInstance().getProperty(key);
            String ejbValue = SmServiceLookup.getConfigGeneralSO().getPropertyValue(key);
            
            // Mask sensitive values
            boolean isSensitive = isSensitiveProperty(key);
            if (isSensitive) {
                portalValue = maskValue(portalValue);
                ejbValue = maskValue(ejbValue);
            }
            
            // Build response
            Map<String, Object> response = new HashMap<String, Object>();
            response.put("success", true);
            response.put("key", key);
            response.put("sensitive", isSensitive);
            
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
            
            log.info("End Get Single Property - Key: " + key);
            
            return Response.status(200)
                    .entity(toJson(response))
                    .build();
            
        } catch (Exception e) {
            log.error("Error getting property: " + key, e);
            return createErrorResponse(e);
        }
    }
    
    /**
     * List all properties (with optional filtering)
     * 
     * @param apiKey API key for authentication
     * @param includeSensitive Include sensitive properties (default: false)
     * @param source Filter by source: portal, ejb, or both (default: both)
     * @return JSON response with all properties
     * 
     * Example URL: /rest/properties/list?includeSensitive=false&source=portal
     * Header: X-API-Key: your-api-key
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllProperties(
            @HeaderParam(API_KEY_HEADER) String apiKey,
            @QueryParam("includeSensitive") @DefaultValue("false") boolean includeSensitive,
            @QueryParam("source") @DefaultValue("both") String source) {
        
        try {
            // Validate API key
            if (!isValidApiKey(apiKey)) {
                return createUnauthorizedResponse();
            }
            
            log.info("List All Properties - Include Sensitive: " + includeSensitive + ", Source: " + source);
            
            List<Map<String, Object>> properties = new ArrayList<Map<String, Object>>();
            
            // Get node information
            String portalNode = getNodeInfo();
            String ejbNode = SmServiceLookup.getConfigGeneralSO().getNodeInfo();
            
            // Get all properties from Portal
            if ("portal".equals(source) || "both".equals(source)) {
                Properties portalProps = EpPropertiesUtils.getInstance().getAllProperties();
                for (String key : portalProps.stringPropertyNames()) {
                    if (!isPropertyAllowed(key)) continue;
                    if (!includeSensitive && isSensitiveProperty(key)) continue;
                    
                    Map<String, Object> prop = new HashMap<String, Object>();
                    prop.put("key", key);
                    prop.put("source", "portal");
                    prop.put("node", portalNode);
                    prop.put("value", isSensitiveProperty(key) ? 
                            maskValue(portalProps.getProperty(key)) : 
                            portalProps.getProperty(key));
                    prop.put("sensitive", isSensitiveProperty(key));
                    properties.add(prop);
                }
            }
            
            // Get all properties from EJB
            if ("ejb".equals(source) || "both".equals(source)) {
                Properties ejbProps = SmServiceLookup.getConfigGeneralSO().getAllProperties();
                for (String key : ejbProps.stringPropertyNames()) {
                    if (!isPropertyAllowed(key)) continue;
                    if (!includeSensitive && isSensitiveProperty(key)) continue;
                    
                    Map<String, Object> prop = new HashMap<String, Object>();
                    prop.put("key", key);
                    prop.put("source", "ejb");
                    prop.put("node", ejbNode);
                    prop.put("value", isSensitiveProperty(key) ? 
                            maskValue(ejbProps.getProperty(key)) : 
                            ejbProps.getProperty(key));
                    prop.put("sensitive", isSensitiveProperty(key));
                    properties.add(prop);
                }
            }
            
            Map<String, Object> response = new HashMap<String, Object>();
            response.put("success", true);
            response.put("count", properties.size());
            response.put("properties", properties);
            
            log.info("End List All Properties - Count: " + properties.size());
            
            return Response.status(200)
                    .entity(toJson(response))
                    .build();
            
        } catch (Exception e) {
            log.error("Error listing properties", e);
            return createErrorResponse(e);
        }
    }
    
    /**
     * Search properties using LIKE pattern
     * 
     * @param pattern Search pattern (SQL LIKE style: % for wildcard)
     * @param apiKey API key for authentication
     * @param includeSensitive Include sensitive properties (default: false)
     * @return JSON response with matching properties
     * 
     * Example URL: /rest/properties/search?pattern=molpay%&includeSensitive=false
     * Header: X-API-Key: your-api-key
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchProperties(
            @QueryParam("pattern") String pattern,
            @HeaderParam(API_KEY_HEADER) String apiKey,
            @QueryParam("includeSensitive") @DefaultValue("false") boolean includeSensitive) {
        
        try {
            // Validate API key
            if (!isValidApiKey(apiKey)) {
                return createUnauthorizedResponse();
            }
            
            if (pattern == null || pattern.trim().isEmpty()) {
                return createBadRequestResponse("Pattern parameter is required");
            }
            
            log.info("Search Properties - Pattern: " + pattern + ", Include Sensitive: " + includeSensitive);
            
            // Get node information
            String portalNode = getNodeInfo();
            String ejbNode = SmServiceLookup.getConfigGeneralSO().getNodeInfo();
            
            // Convert SQL LIKE pattern to Java regex
            String regex = pattern.replace("%", ".*").replace("_", ".");
            
            List<Map<String, Object>> matchedProperties = new ArrayList<Map<String, Object>>();
            
            // Search Portal properties
            Properties portalProps = EpPropertiesUtils.getInstance().getAllProperties();
            for (String key : portalProps.stringPropertyNames()) {
                if (!isPropertyAllowed(key)) continue;
                if (!includeSensitive && isSensitiveProperty(key)) continue;
                
                if (key.matches(regex)) {
                    Map<String, Object> prop = new HashMap<String, Object>();
                    prop.put("key", key);
                    prop.put("source", "portal");
                    prop.put("node", portalNode);
                    prop.put("value", isSensitiveProperty(key) ? 
                            maskValue(portalProps.getProperty(key)) : 
                            portalProps.getProperty(key));
                    prop.put("sensitive", isSensitiveProperty(key));
                    matchedProperties.add(prop);
                }
            }
            
            // Search EJB properties
            Properties ejbProps = SmServiceLookup.getConfigGeneralSO().getAllProperties();
            for (String key : ejbProps.stringPropertyNames()) {
                if (!isPropertyAllowed(key)) continue;
                if (!includeSensitive && isSensitiveProperty(key)) continue;
                
                if (key.matches(regex)) {
                    Map<String, Object> prop = new HashMap<String, Object>();
                    prop.put("key", key);
                    prop.put("source", "ejb");
                    prop.put("node", ejbNode);
                    prop.put("value", isSensitiveProperty(key) ? 
                            maskValue(ejbProps.getProperty(key)) : 
                            ejbProps.getProperty(key));
                    prop.put("sensitive", isSensitiveProperty(key));
                    matchedProperties.add(prop);
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
     * Check if property is allowed to be accessed
     */
    private boolean isPropertyAllowed(String key) {
        // Blacklist check
        for (String blocked : BLOCKED_PROPERTIES) {
            if (key.equalsIgnoreCase(blocked)) {
                return false;
            }
        }
        
        // Whitelist check (uncomment if using whitelist approach)
        // if (ALLOWED_PROPERTIES != null && ALLOWED_PROPERTIES.length > 0) {
        //     for (String allowed : ALLOWED_PROPERTIES) {
        //         if (key.equalsIgnoreCase(allowed)) {
        //             return true;
        //         }
        //     }
        //     return false;
        // }
        
        return true;
    }
    
    /**
     * Check if property contains sensitive information
     */
    private boolean isSensitiveProperty(String key) {
        String lowerKey = key.toLowerCase();
        for (String pattern : SENSITIVE_PATTERNS) {
            if (lowerKey.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Mask sensitive value
     */
    private String maskValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        if (value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
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
