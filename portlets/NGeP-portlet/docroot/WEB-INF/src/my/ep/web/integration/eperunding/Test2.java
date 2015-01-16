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

public class Test2 {

	public static void main(String[] args) {
		Test2 test2 = new Test2();
		test2.run();

	}

	public void run() {
		
		List<EquityOwner> list = new ArrayList<EquityOwner>();
		
		EquityOwner equityOwner = new EquityOwner();

		equityOwner.setMOFNo("465-00002043");
		
		Owner owner = new Owner();
		owner.setIsEquityOwner("Y");
		owner.setName("MAT JUSOH BIN MAMAT");
		owner.setNewICNo("671216035251");
		owner.setPassport("N/A");
		owner.setIsBumi("Y");
		owner.setCitizenOf(131);
		owner.setEquityValue("460000");
		owner.setEquityPercentage("92");
		owner.setRace("MELAYU");
		owner.setDesignation("PENGARAH URUSAN");
		equityOwner.setOwner(owner);
		
		Qualification qualification = new Qualification();
		qualification.setMajorIn("BACHELOR SCIENCE OF ENGINEERING ( CIVIL)");
		qualification.setQualification("TIDAK BERKENAAN");
		qualification.setQualificationLevel(3);
		qualification.setInstitute("BUCKNELL UNIVERSITY, PENNSYLVANIA, USA");
		qualification.setYear("1990");
		qualification.setEducationField(2);
		equityOwner.setQualification(qualification);

		ProfBodyDetail profBodyDetail = new ProfBodyDetail();
		profBodyDetail.setProfBody("1");
		profBodyDetail.setProfBodyNo("11124");
		profBodyDetail.setProfBodyExpiry(new Date());
		equityOwner.setProfBodyDetail(profBodyDetail);

		Expertise expertise = new Expertise();
		expertise.setCategoryClassification(null);
		equityOwner.setExpertise(expertise);
		
		list.add(equityOwner);
		
		equityOwnerOutPutStream(list,"jhdgsf");
		
		

	}

	public void equityOwnerOutPutStream(List<EquityOwner> content,
			String filePath) {

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath), "utf-8"));

			StringBuffer line = new StringBuffer();
			for (EquityOwner eOwner : content) {

				line.append(formatString(eOwner.getMOFNo()));

				line.append(formatString(eOwner.getOwner().getIsEquityOwner()));
				line.append(formatString(eOwner.getOwner().getName()));
				line.append(formatString(eOwner.getOwner().getNewICNo()));
				line.append(formatString(eOwner.getOwner().getPassport()));
				line.append(formatString(eOwner.getOwner().getIsBumi()));
				line.append(formatInt(eOwner.getOwner().getCitizenOf()));
				line.append(formatString(eOwner.getOwner().getEquityValue()));
				line.append(formatString(eOwner.getOwner().getEquityPercentage()));
				line.append(formatString(eOwner.getOwner().getRace()));
				line.append(formatString(eOwner.getOwner().getDesignation()));
				

				line.append(formatString(eOwner.getQualification().getMajorIn()));
				line.append(formatString(eOwner.getQualification().getQualification()));
				line.append(formatInt(eOwner.getQualification()
						.getQualificationLevel()));
				line.append(formatString(eOwner.getQualification()
						.getInstitute()));
				line.append(formatString(eOwner.getQualification().getYear()));
				line.append(formatInt(eOwner.getQualification()
						.getEducationField()));

				line.append(formatString(eOwner.getProfBodyDetail()
						.getProfBody()));
				line.append(formatString(eOwner.getProfBodyDetail()
						.getProfBodyNo()));
				line.append(formatDate(eOwner.getProfBodyDetail()
						.getProfBodyExpiry()));

				line.append(String.format("%s", eOwner.getExpertise()
						.getCategoryClassification()));

				line.append(System.getProperty("line.separator"));
				// writer.write(line.toString());
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

		if (input == null)
			return "NULL|";
		if (input.isEmpty() && input.length() == 0)
			return "|";
		return String.format("%s|", input.trim());
	}

	public String formatDate(Date date) {

		if (date == null)
			return "|";

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
