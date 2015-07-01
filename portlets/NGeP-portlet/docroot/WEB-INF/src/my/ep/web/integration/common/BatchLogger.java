package my.ep.web.integration.common;

/*
 * @author P Ganga prasad
 */

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class BatchLogger {

	private static BatchLogger instance;

	private BatchLogger() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("batch_log4j.properties");
		PropertyConfigurator.configure(url);
	}

	@SuppressWarnings("rawtypes")
	public static Logger getLogger(Class clazz) {
		if (instance == null) {
			instance = new BatchLogger();
		}
		return Logger.getLogger(clazz);

	}

}
