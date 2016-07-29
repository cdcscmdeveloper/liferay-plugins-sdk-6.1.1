package my.ep.web.backingbean.report;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;

public class InterruptThread implements Runnable {

    HttpURLConnection con;
    private static final Logger log = EpLogger
			.getLogger(InterruptThread.class);
    
    public InterruptThread(HttpURLConnection con) {
        this.con = con;
    }

    public void run() {
    	log.info("@@@ start of Run method");
        try {
            Thread.sleep(306000); // or Thread.sleep(con.getConnectTimeout())
        } catch (InterruptedException e) {
			throw new EpException(MessageConst.ERROR_REPORT_780020);
        }	
        log.info("@@@ End of Run method");
        con.disconnect();
    }
}
