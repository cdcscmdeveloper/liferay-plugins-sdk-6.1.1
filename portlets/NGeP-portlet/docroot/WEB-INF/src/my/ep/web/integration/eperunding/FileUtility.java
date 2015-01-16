package my.ep.web.integration.eperunding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import my.ep.web.integration.common.IntegrationConstants;

public class FileUtility {
	
	public void consultantProfileFileOutPutStream(
			List<ConsultantProfile> content, String filePath) {

		String fileName = "ConsultantProfiles.txt";
		Writer writer = null;

		try {

			// Using OutputStreamWriter you don't have to convert the String to
			// byte[]
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			StringBuffer line = new StringBuffer();
			writer.write("         " + content.size() + " Rows Selected");
			// line.append(System.getProperty("line.separator"));
			for (ConsultantProfile cProfile : content) {

				line.append(System.getProperty("line.separator"));

				line.append(formatString(cProfile.getCompanyName()));
				line.append(formatString(cProfile.getSSMNo()));
				line.append(formatString(cProfile.getMOFNo()));
				line.append(formatString(cProfile.getCompanyType()));
				line.append(formatString(cProfile.getAddress1()));
				line.append(formatString(cProfile.getAddress2()));
				line.append(formatString(cProfile.getAddress3()));
				line.append(formatString(cProfile.getPostcode()));
				line.append(formatString(cProfile.getCity()));
				line.append(formatString(cProfile.getDistrict()));
				line.append(formatString(cProfile.getState()));
				line.append(formatString(cProfile.getCountry()));
				line.append(formatString(cProfile.getTelCountryCode()));
				line.append(formatString(cProfile.getTelAreaCode()));
				line.append(formatString(cProfile.getTelNo()));
				line.append(formatString(cProfile.getFaxCountryCode()));
				line.append(formatString(cProfile.getFaxAreaCode()));
				line.append(formatString(cProfile.getFaxNo()));
				line.append(formatFloat(cProfile.getAccumulatedCapital()));
				line.append(formatString(cProfile.getAuthorisedCapital()));
				line.append(formatString(cProfile.getPaidUpCapital()));
				line.append(formatFloat(cProfile.getAccumulatedShare()));
				line.append(formatFloat(cProfile.getBumiputeraPercentage()));
				line.append(formatFloat(cProfile.getNonBumiPercentage()));
				line.append(formatInt(cProfile.getRegStatus()));
				line.append(formatDate(cProfile.getEffectDateTo()));
				line.append(formatDate(cProfile.getEstablishedDate()));
				line.append(formatDate(cProfile.getEffectDateFrom()));
				line.append(formatString(cProfile.getDisciplinary()));
				line.append(formatString(cProfile.getIsAssociatedCompany()));
				line.append(formatDate(cProfile.getProfileUpdateDate()));

				line.append(formatString(cProfile.getProfBodyDetail()
						.getProfBody()));
				line.append(formatString(cProfile.getProfBodyDetail()
						.getProfBodyNo()));
				line.append(formatDate(cProfile.getProfBodyDetail()
						.getProfBodyExpiry()));

				line.append(formatDate(cProfile.getRegistrationHistory()
						.getDateStart()));
				line.append(formatDate(cProfile.getRegistrationHistory()
						.getDateEnd()));

				line.append(formatString(cProfile.getCapitalProportion()
						.getBumi()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getNonBumi()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getNonMalaysian()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getGovAgency()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getGovEquity()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getLocalCo()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getForeignCo()));
				line.append(formatFloat(cProfile.getCapitalProportion()
						.getTotalMember()));
				line.append(String.format("%.0f", cProfile
						.getCapitalProportion().getTotalShare()));

				// line.append(System.getProperty("line.separator"));
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

	public void signatoryFileOutPutStream(List<Signatory> content,
			String filePath) {
		String fileName = "Signatory.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			StringBuffer line = new StringBuffer();
			writer.write("         " + content.size() + " Rows Selected");
			// line.append(System.getProperty("line.separator"));
			for (Signatory signatory : content) {
				line.append(System.getProperty("line.separator"));
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

				// line.append(System.getProperty("line.separator"));
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

	public void manageProfileFileOutPutStream(
			List<ManagementPersonnel> content, String filePath) {

		String fileName = "ManagementPersonnel.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			StringBuffer line = new StringBuffer();
			writer.write("         " + content.size() + " Rows Selected");
			// line.append(System.getProperty("line.separator"));
			for (ManagementPersonnel mPersonnel : content) {
				line.append(System.getProperty("line.separator"));

				line.append(formatString(mPersonnel.getMOFNo()));

				line.append(formatString(mPersonnel.getPersonnel()
						.getIsManagement()));
				line.append(formatString(mPersonnel.getPersonnel()
						.getIsProfessional()));
				line.append(formatString(mPersonnel.getPersonnel()
						.getIsSemiProf()));
				line.append(formatString(mPersonnel.getPersonnel()
						.getIsClerical()));
				line.append(formatString(mPersonnel.getPersonnel().getName()));
				line.append(formatString(mPersonnel.getPersonnel().getNewICNo()));
				line.append(formatString(mPersonnel.getPersonnel()
						.getPassport()));
				line.append(formatString(mPersonnel.getPersonnel().getIsBumi()));
				line.append(formatInt(mPersonnel.getPersonnel().getCitizenOf()));
				line.append(formatString(mPersonnel.getPersonnel().getRace()));
				line.append(formatString(mPersonnel.getPersonnel()
						.getDesignation()));
				line.append(formatInt(mPersonnel.getPersonnel()
						.getWorkExperience()));

				line.append(formatString(mPersonnel.getQualification()
						.getMajorIn()));
				line.append(formatString(mPersonnel.getQualification()
						.getQualification()));
				line.append(formatInt(mPersonnel.getQualification()
						.getQualificationLevel()));
				line.append(formatString(mPersonnel.getQualification()
						.getInstitute()));
				line.append(formatString(mPersonnel.getQualification()
						.getYear()));
				line.append(formatInt(mPersonnel.getQualification()
						.getEducationField()));

				line.append(formatString(mPersonnel.getProfBodyDetail()
						.getProfBody()));
				line.append(formatString(mPersonnel.getProfBodyDetail()
						.getProfBodyNo()));
				line.append(formatDate(mPersonnel.getProfBodyDetail()
						.getProfBodyExpiry()));

				line.append(String.format("%s", mPersonnel.getExpertise()
						.getCategoryClassification()));

				// line.append(System.getProperty("line.separator"));
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

	public void boardOfDirectorOutPutStream(List<BoardOfDirector> content,
			String filePath) {

		String fileName = "BoardOfDirector.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			StringBuffer line = new StringBuffer();
			writer.write("         " + content.size() + " Rows Selected");
			// line.append(System.getProperty("line.separator"));
			for (BoardOfDirector bDirector : content) {

				line.append(System.getProperty("line.separator"));

				line.append(formatString(bDirector.getMOFNo()));

				line.append(formatString(bDirector.getDirector().getIsBOD()));
				line.append(formatString(bDirector.getDirector().getIsCEO()));
				line.append(formatString(bDirector.getDirector().getName()));
				line.append(formatString(bDirector.getDirector().getNewICNo()));
				line.append(formatString(bDirector.getDirector().getPassport()));
				line.append(formatString(bDirector.getDirector().getIsBumi()));
				line.append(formatInt(bDirector.getDirector().getCitizenOf()));
				line.append(formatString(bDirector.getDirector().getRace()));
				line.append(formatString(bDirector.getDirector()
						.getDesignation()));
				line.append(formatInt(bDirector.getDirector()
						.getWorkExperience()));

				line.append(formatString(bDirector.getQualification()
						.getMajorIn()));
				line.append(formatString(bDirector.getQualification()
						.getQualification()));
				line.append(formatInt(bDirector.getQualification()
						.getQualificationLevel()));
				line.append(formatString(bDirector.getQualification()
						.getInstitute()));
				line.append(formatString(bDirector.getQualification().getYear()));
				line.append(formatInt(bDirector.getQualification()
						.getEducationField()));

				line.append(formatString(bDirector.getProfBodyDetail()
						.getProfBody()));
				line.append(formatString(bDirector.getProfBodyDetail()
						.getProfBodyNo()));
				line.append(formatDate(bDirector.getProfBodyDetail()
						.getProfBodyExpiry()));

				line.append(String.format("%s", bDirector.getExpertise()
						.getCategoryClassification()));

				// line.append(System.getProperty("line.separator"));
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

	public void equityOwnerOutPutStream(List<EquityOwner> content,
			String filePath) {
		String fileName = "EquityOwner.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			StringBuffer line = new StringBuffer();
			writer.write("         " + content.size() + " Rows Selected");
			// line.append(System.getProperty("line.separator"));
			for (EquityOwner eOwner : content) {

				line.append(System.getProperty("line.separator"));

				line.append(formatString(eOwner.getMOFNo()));

				line.append(formatString(eOwner.getOwner().getIsEquityOwner()));
				line.append(formatString(eOwner.getOwner().getName()));
				line.append(formatString(eOwner.getOwner().getNewICNo()));
				line.append(formatString(eOwner.getOwner().getPassport()));
				line.append(formatString(eOwner.getOwner().getIsBumi()));
				line.append(formatInt(eOwner.getOwner().getCitizenOf()));
				line.append(formatString(eOwner.getOwner().getEquityValue()));
				line.append(formatString(eOwner.getOwner()
						.getEquityPercentage()));
				line.append(formatString(eOwner.getOwner().getRace()));
				line.append(formatString(eOwner.getOwner().getDesignation()));

				line.append(formatString(eOwner.getQualification().getMajorIn()));
				line.append(formatString(eOwner.getQualification()
						.getQualification()));
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

				// line.append(System.getProperty("line.separator"));
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

	public String createDirectory(Date date,String path) {

		SimpleDateFormat dt1 = new SimpleDateFormat("ddMMyyyy");

		File file = new File(path + dt1.format(date));
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		} else {
			System.out.println("Directory is already exists!");
		}

		System.out.println("file.getAbsoluteFile() " + file.getAbsolutePath());

		return file.getAbsolutePath();

	}
	
	public boolean createEPerundingFileInLocal(List<ConsultantProfile> ConsProfileList,List<Signatory> signatoryList,List<ManagementPersonnel> mPersonnelList,
			List<BoardOfDirector> bDirectorList,List<EquityOwner> eOwnerList){
		String path = createDirectory(new Date(), IntegrationConstants.FILE_PATH)+"/";
		consultantProfileFileOutPutStream(ConsProfileList,path);
		signatoryFileOutPutStream(signatoryList,path);
		manageProfileFileOutPutStream(mPersonnelList,path);
		boardOfDirectorOutPutStream(bDirectorList,path);
		equityOwnerOutPutStream(eOwnerList,path);
		return true;
	}

}
