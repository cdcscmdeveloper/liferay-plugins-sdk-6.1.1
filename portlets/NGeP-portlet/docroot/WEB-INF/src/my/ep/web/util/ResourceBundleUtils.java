/**
 * 
 */
package my.ep.web.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * ResourceBundleUtils
 * 
 * @author Kenny
 * 
 */
public abstract class ResourceBundleUtils {

	/* Begin Constants to represent bundle files */
	public static final int BUNDLE_COMMON_LABELS = 1;

	/* End Constants to represent bundle files */

	public static String getMessage(String key, int bundleName, Locale locale) {
		ResourceBundle bundle = getBundle(bundleName, locale, null);
		String msg = null;
		
		if (bundle != null) {
			try {
				msg = bundle.getString(key);
			} catch (MissingResourceException e) {
				msg = key;
			}

		}
		
		return msg;
	}

	public static String getMessage(String key, Object params[],
			int bundleName, Locale locale) {
		String msg = getMessage(key, bundleName, locale);
		
		if (msg != null) {
			MessageFormat mf = new MessageFormat(msg, locale);
			msg = mf.format(params, new StringBuffer(), null).toString();
		}
		
		return msg;
	}

	private static ResourceBundle getBundle(int bundleName, Locale locale,
			ClassLoader classLoader) {
		ResourceBundle bundle = null;
		String strBundleName = "";
		
		switch (bundleName) {
			case BUNDLE_COMMON_LABELS:
				strBundleName = "messageprovider-config.bundles.labels.labels";
				break;

			default:
				break;
		}

		if (!"".equals(strBundleName)) {
			if (classLoader == null) {
				bundle = ResourceBundle.getBundle(strBundleName, locale);
			} else {
				bundle = ResourceBundle.getBundle(strBundleName, locale,
						classLoader);
			}
		}

		return bundle;
	}

}
