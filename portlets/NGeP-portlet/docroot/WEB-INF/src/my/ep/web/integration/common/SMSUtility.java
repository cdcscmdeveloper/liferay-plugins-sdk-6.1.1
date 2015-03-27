package my.ep.web.integration.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class SMSUtility {

	public void sendSMS(String mobileNo) {
		try {

			String user = "boyapativenu"; // user isms username
			String pass = "madhuri"; // user isms password
			String dstno = mobileNo; 
			//String dstno = "60183906102"; // You are going compose a message to
											// this destination number.
			String str = "Hi this is testing sms for cdc"; // Your message over
															// here
			String msg = str.replace(" ", "%20");
			int type = 2; // for unicode change to 2, normal will the 1.
			String sendid = "isms"; // Malaysia does not support sender id yet.
			// Send data
			URL myUrl = new URL("http://www.isms.com.my/isms_send.php?un="
					+ user + "&pwd=" + pass + "&dstno=" + dstno + "&msg=" + msg
					+ "&type=" + type + "&sendid=" + sendid);
			URLConnection conn = myUrl.openConnection();
			conn.setDoOutput(true);
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				// Print the response output...
				System.out.println(line);
			}
			rd.close();

			System.out.println(myUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
