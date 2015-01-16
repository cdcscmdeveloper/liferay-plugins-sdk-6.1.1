package my.ep.web.integration.eperunding;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test1 {
	public static void main(String args[]) {
		Test1 test = new Test1();
		test.run();
	}

	public void run() {
		List<Signatory> list1 = new ArrayList<Signatory>();
		Signatory signatory = new Signatory();
		signatory.setMOFNo("465-00002043");

		List<AuthorizedSignatory> list = new ArrayList<AuthorizedSignatory>();
		AuthorizedSignatory authorizedSignatory = new AuthorizedSignatory();

		authorizedSignatory.setName("YAZID  BIN JANTAN");
		authorizedSignatory.setNewICNo("570627045507");
		authorizedSignatory.setPassport("N/A");
		authorizedSignatory.setIsBumi("Y");
		authorizedSignatory.setDesignation("DIRECTOR");
		list.add(authorizedSignatory);
		signatory.setAuthorizedSignatoryList(list);
		list1.add(signatory);

		signatoryFileOutPutStream(list1, "jsbh");

	}

	public void signatoryFileOutPutStream(List<Signatory> content,
			String filePath) {

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath), "utf-8"));

			StringBuffer line = new StringBuffer();
			for (Signatory signatory : content) {
				line.append(formatString(signatory.getMOFNo()));
				line.append(formatString(signatory.getAuthorizedSignatoryList()
						.get(0).getName()));
				line.append(formatString(signatory.getAuthorizedSignatoryList()
						.get(0).getNewICNo()));
				line.append(formatString(signatory.getAuthorizedSignatoryList()
						.get(0).getPassport()));
				line.append(formatString(signatory.getAuthorizedSignatoryList()
						.get(0).getIsBumi()));
				line.append(String.format("%s", signatory
						.getAuthorizedSignatoryList().get(0).getDesignation()));

				line.append(System.getProperty("line.separator"));
				writer.write(line.toString());
			}
			System.out.println("line :" + line.toString());

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public String formatString(String input) {

		return String.format("%s|", input.trim());
	}

	public String formatDate(Date date) {

		SimpleDateFormat dt1 = new SimpleDateFormat("yyyMMdd");

		return String.format("%s|", dt1.format(date));
	}

	public String formatFloat(float value) {

		return String.format("%.0f|", value);
	}

	public String formatInt(int value) {

		return String.format("%d|", value);

	}

}
