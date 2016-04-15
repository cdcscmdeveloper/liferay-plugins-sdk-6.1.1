package my.ep.web.backingbean.report;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;

public class InterruptThread implements Runnable {

    HttpURLConnection con;
    public InterruptThread(HttpURLConnection con) {
        this.con = con;
    }

    public void run() {
        try {
            Thread.sleep(300000); // or Thread.sleep(con.getConnectTimeout())
        } catch (InterruptedException e) {
			throw new EpException(MessageConst.ERROR_REPORT_780020);
        }		
        con.disconnect();
    }
}
