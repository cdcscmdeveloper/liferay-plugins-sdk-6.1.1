package my.ep.web.util.sc;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import my.ep.factory.EpLogger;

import org.apache.log4j.Logger;

/**
 * Utility class for managing action popup confirmation message keys. This class
 * provides centralized access to title and content keys for various action
 * confirmation dialogs.
 * 
 * Issue #34444
 */
public class ActionPopupMessageKeys {

	private static final Logger log = EpLogger.getLogger(ActionPopupMessageKeys.class);

	/** Action â† title key */
	private static final Map<String, String> TITLE_KEYS = new HashMap<>();

	/** Action â† content key */
	private static final Map<String, String> CONTENT_KEYS = new HashMap<>();

	private static String labelApproved = "lbl.sc.popup.confirm.approve.header";
	private static String labelSubmit = "lbl.sc.popup.confirm.submit.header";

	static {
		// ===== APPROVE =====
		TITLE_KEYS.put("APPROVE", labelApproved);
		CONTENT_KEYS.put("APPROVE", "lbl.sc.popup.confirm.approve.content");

		// ===== REVISE =====
		TITLE_KEYS.put("REVISE", "lbl.sc.popup.confirm.revise.header");
		CONTENT_KEYS.put("REVISE", "lbl.sc.popup.confirm.revise.content");

		// ===== ENDORSE =====
		TITLE_KEYS.put("ENDORSE", "lbl.sc.popup.confirm.endorse.header");
		CONTENT_KEYS.put("ENDORSE", "lbl.sc.popup.confirm.endorse.content");

		// ===== SIGNOFF =====
		TITLE_KEYS.put("SIGNOFF", "lbl.sc.popup.confirm.signOff.header");
		CONTENT_KEYS.put("SIGNOFF", "lbl.sc.popup.confirm.signOff.content");

		// ===== SUBMIT =====
		TITLE_KEYS.put("SUBMIT", labelSubmit);
		CONTENT_KEYS.put("SUBMIT", "lbl.sc.popup.confirm.submit.content");

		// ===== SUBMISSIONLOA =====
		TITLE_KEYS.put("SUBMISSIONLOA", labelSubmit);
		CONTENT_KEYS.put("SUBMISSIONLOA", "lbl.sc.popup.confirm.loa.submission.content");

		// ===== APPROVELOA =====
		TITLE_KEYS.put("APPROVELOA", labelApproved);
		CONTENT_KEYS.put("APPROVELOA", "lbl.sc.popup.confirm.loa.approve.content");

		// ===== SUBMISSIONLOI =====
		TITLE_KEYS.put("SUBMISSIONLOI", labelSubmit);
		CONTENT_KEYS.put("SUBMISSIONLOI", "lbl.sc.popup.confirm.loi.submission.content");

		// ===== APPROVELOI =====
		TITLE_KEYS.put("APPROVELOI", labelApproved);
		CONTENT_KEYS.put("APPROVELOI", "lbl.sc.popup.confirm.loi.approve.content");
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ActionPopupMessageKeys() {
		// Utility class - prevent instantiation
	}

	/**
	 * Get the title key for the given action.
	 * 
	 * @param action
	 *            the action name
	 * @return the title key, or null if not found
	 */
	public static String getTitleKey(String action) {
		return TITLE_KEYS.get(action);
	}

	/**
	 * Get the content key for the given action.
	 * 
	 * @param action
	 *            the action name
	 * @return the content key, or null if not found
	 */
	public static String getContentKey(String action) {
		return CONTENT_KEYS.get(action);
	}

	/**
	 * Get the title message for the given action using the provided resource
	 * bundle.
	 * 
	 * @param action
	 *            the action name
	 * @param resourceBundle
	 *            the resource bundle to use for message lookup
	 * @return the title message, or the key itself if not found in the bundle
	 */
	public static String getTitleMessage(String action, ResourceBundle resourceBundle) {

		String key = getTitleKey(action);

		if (key != null && resourceBundle != null) {
			try {
				return resourceBundle.getString(key);
			} catch (MissingResourceException e) {
				log.warn("Missing resource bundle key:" + key, e);
				return "Message unavailable";
			}
		}

		return key;
	}

	/**
	 * Get the content message for the given action using the provided resource
	 * bundle.
	 * 
	 * @param action
	 *            the action name
	 * @param resourceBundle
	 *            the resource bundle to use for message lookup
	 * @return the content message, or the key itself if not found in the bundle
	 */
	public static String getContentMessage(String action, ResourceBundle resourceBundle) {

		String key = getContentKey(action);

		if (key != null && resourceBundle != null) {
			try {
				return resourceBundle.getString(key);
			} catch (MissingResourceException e) {
				log.warn("Missing resource bundle key: " + key, e);
				return key;
			}
		}

		return key;
	}
}
