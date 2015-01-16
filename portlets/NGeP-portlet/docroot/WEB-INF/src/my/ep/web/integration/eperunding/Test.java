package my.ep.web.integration.eperunding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test {

	private String path = "C:/Integration/Upload/";
	
	//String localpath = createDirectory(new Date())+"/";

	public static void main(String args[]) {
		
		
		Test test = new Test();
//		 test.run();
//		 test.run1();
//		 test.run2();
//		 test.run3();
//		 test.run4();
		 test.run5();
		 
		//new SFTPUtility().createDirectory(IntegrationConstants.EPERUNDING_UPLOAD_DIR);
		 
//		 String path = "C:/Integration/Upload/30102014"; 
//		 
//		  String files;
//		  File folder = new File(path);
//		  File[] listOfFiles = folder.listFiles(); 
//		 
//		  for (int i = 0; i < listOfFiles.length; i++) 
//		  {
//		 
//		   if (listOfFiles[i].isFile()) 
//		   {
//		   files = listOfFiles[i].getName();
//		   System.out.println(files);
//		 //  SFTPUtility.upload("192.168.62.15", "oracle", "welcome1", "C:/Integration/Upload/30102014/"+files, "/batch/eperunding/IN/30102014/"+files);
//		   System.out.println(files);
//		      }
//		  }
//		  
//		  //call to webservice to integrate to SOA
		  
		  
	}
	
	public void run5(){
		
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
		
		//-------------------------------
		
		List<BoardOfDirector> list2 = new ArrayList<BoardOfDirector>();

		BoardOfDirector boardOfDirector = new BoardOfDirector();
		boardOfDirector.setMOFNo("465-00002043");

		Director director = new Director();
		director.setIsBOD("Y");
		director.setIsCEO("N");
		director.setName("MOHD RADZI BIN DAHALAN");
		director.setNewICNo("531016085495");
		director.setPassport("N/A");
		director.setIsBumi("Y");
		director.setCitizenOf(131);
		director.setRace("MELAYU");
		director.setDesignation("PENGARAH");
		director.setWorkExperience(34);
		boardOfDirector.setDirector(director);

		Qualification qualification1 = new Qualification();
		qualification1.setMajorIn("BACHELOR OF ENGINEERING (MECHANICAL)");
		qualification1.setQualification("TIDAK BERKENAAN");
		qualification1.setQualificationLevel(3);
		qualification1.setInstitute("QUEENSLAND UNIVERSITY, AUSTRALIA");
		qualification1.setYear("1976");
		qualification1.setEducationField(5);
		boardOfDirector.setQualification(qualification1);

		ProfBodyDetail profBodyDetail1 = new ProfBodyDetail();
		profBodyDetail1.setProfBody("1");
		profBodyDetail1.setProfBodyNo("7213");
		profBodyDetail1.setProfBodyExpiry(new Date());
		boardOfDirector.setProfBodyDetail(profBodyDetail1);

		Expertise expertise1 = new Expertise();
		expertise1.setCategoryClassification(null);
		boardOfDirector.setExpertise(expertise1);

		list2.add(boardOfDirector);
		//--------------------------------------------
		
		List<ManagementPersonnel> list3 = new ArrayList<ManagementPersonnel>();
		ManagementPersonnel managementPersonnel = new ManagementPersonnel();

		managementPersonnel.setMOFNo("465-00002043");

		Personnel personnel = new Personnel();
		personnel.setIsManagement("N");
		personnel.setIsProfessional("N");
		personnel.setIsSemiProf("N");
		personnel.setIsClerical("Y");
		personnel.setName("ABDUL HADI BIN RAHIL");
		personnel.setNewICNo("600804015451");
		personnel.setPassport("N/A");
		personnel.setIsBumi("Y");
		personnel.setCitizenOf(131);
		personnel.setRace("MELAYU");
		personnel.setDesignation("DRIVER");
		personnel.setWorkExperience(25);
		managementPersonnel.setPersonnel(personnel);

		Qualification qualification2 = new Qualification();
		qualification2.setMajorIn(null);
		qualification2.setQualification("");
		qualification2.setQualificationLevel(0);
		qualification2.setInstitute(null);
		qualification2.setYear("25");
		qualification2.setEducationField(0);
		managementPersonnel.setQualification(qualification2);

		ProfBodyDetail profBodyDetail2 = new ProfBodyDetail();
		profBodyDetail2.setProfBody("");
		profBodyDetail2.setProfBodyNo("");
		profBodyDetail2.setProfBodyExpiry(null);
		managementPersonnel.setProfBodyDetail(profBodyDetail2);

		Expertise expertise2 = new Expertise();
		expertise2.setCategoryClassification(null);
		managementPersonnel.setExpertise(expertise2);

		list3.add(managementPersonnel);
		
		//-----------------------------------
		
		List<Signatory> list1 = new ArrayList<Signatory>();
		Signatory signatory = new Signatory();
		signatory.setMOFNo("465-00002043");

		List<AuthorizedSignatory> list4 = new ArrayList<AuthorizedSignatory>();
		AuthorizedSignatory authorizedSignatory = new AuthorizedSignatory();

		authorizedSignatory.setName("YAZID  BIN JANTAN");
		authorizedSignatory.setNewICNo("570627045507");
		authorizedSignatory.setPassport("N/A");
		authorizedSignatory.setIsBumi("Y");
		authorizedSignatory.setDesignation("DIRECTOR");
		list4.add(authorizedSignatory);
		signatory.setAuthorizedSignatoryList(list4);
		list1.add(signatory);
		
		//-----------------------------------
		
		List<ConsultantProfile> list5 = new ArrayList<ConsultantProfile>();
		ConsultantProfile profile = new ConsultantProfile();

		profile.setCompanyName("THL CONSULTANTS     ");
		profile.setSSMNo("1086516-V");
		profile.setMOFNo("465-02032414");
		profile.setCompanyType("1");
		profile.setAddress1("2-2-2, JALAN SEMARAK API 3    ");
		profile.setAddress2("DIAMOND SQUARE");
		profile.setAddress3("     OFF JALAN GOMBAK");
		profile.setPostcode("53000");
		profile.setCity("KUALA LUMPUR");
		profile.setDistrict("1401");
		profile.setState("14");
		profile.setCountry("MY");
		profile.setTelCountryCode("+6");
		profile.setTelAreaCode("03");
		profile.setTelNo("40238289");
		profile.setFaxCountryCode("+6");
		profile.setFaxAreaCode("04");
		profile.setFaxNo("40211289");
		profile.setAccumulatedCapital(50000);
		profile.setAuthorisedCapital("0");
		profile.setPaidUpCapital("0");
		profile.setAccumulatedShare(0);

		profile.setBumiputeraPercentage(0);
		profile.setNonBumiPercentage(0);
		profile.setRegStatus(3);
		profile.setEffectDateTo(new Date());
		profile.setEstablishedDate(new Date());
		profile.setEffectDateFrom(new Date());
		profile.setDisciplinary("N");
		profile.setIsAssociatedCompany("N");
		profile.setProfileUpdateDate(new Date());

		ProfBodyDetail profBodyDetail4 = new ProfBodyDetail();
		profBodyDetail4.setProfBody("3");
		profBodyDetail4.setProfBodyNo("1997/FS00224");
		profBodyDetail4.setProfBodyExpiry(new Date());
		profile.setProfBodyDetail(profBodyDetail4);

		RegistrationHistory registrationHistory = new RegistrationHistory();
		registrationHistory.setDateStart(new Date());
		registrationHistory.setDateEnd(new Date());
		profile.setRegistrationHistory(registrationHistory);

		CapitalProportion capitalProportion = new CapitalProportion();
		capitalProportion.setBumi("0");
		capitalProportion.setNonBumi("50000");
		capitalProportion.setNonMalaysian("0");
		capitalProportion.setGovAgency("0");
		capitalProportion.setGovEquity("0");
		capitalProportion.setLocalCo("0");
		capitalProportion.setForeignCo("0");
		capitalProportion.setTotalMember(0);
		capitalProportion.setTotalShare(0);
		profile.setCapitalProportion(capitalProportion);
		list5.add(profile);
		
		FileUtility utility = new FileUtility();
		
		utility.createEPerundingFileInLocal(list5,list1,list3,list2,list);
		
		
	}

	/*public void run4() {
		
		//String path = createDirectory(new Date())+"/";
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

		equityOwnerOutPutStream(list, path);
	}

	public void run3() {
		
		//String path = createDirectory(new Date())+"/";

		List<BoardOfDirector> list = new ArrayList<BoardOfDirector>();

		BoardOfDirector boardOfDirector = new BoardOfDirector();
		boardOfDirector.setMOFNo("465-00002043");

		Director director = new Director();
		director.setIsBOD("Y");
		director.setIsCEO("N");
		director.setName("MOHD RADZI BIN DAHALAN");
		director.setNewICNo("531016085495");
		director.setPassport("N/A");
		director.setIsBumi("Y");
		director.setCitizenOf(131);
		director.setRace("MELAYU");
		director.setDesignation("PENGARAH");
		director.setWorkExperience(34);
		boardOfDirector.setDirector(director);

		Qualification qualification = new Qualification();
		qualification.setMajorIn("BACHELOR OF ENGINEERING (MECHANICAL)");
		qualification.setQualification("TIDAK BERKENAAN");
		qualification.setQualificationLevel(3);
		qualification.setInstitute("QUEENSLAND UNIVERSITY, AUSTRALIA");
		qualification.setYear("1976");
		qualification.setEducationField(5);
		boardOfDirector.setQualification(qualification);

		ProfBodyDetail profBodyDetail = new ProfBodyDetail();
		profBodyDetail.setProfBody("1");
		profBodyDetail.setProfBodyNo("7213");
		profBodyDetail.setProfBodyExpiry(new Date());
		boardOfDirector.setProfBodyDetail(profBodyDetail);

		Expertise expertise = new Expertise();
		expertise.setCategoryClassification(null);
		boardOfDirector.setExpertise(expertise);

		list.add(boardOfDirector);

		boardOfDirectorOutPutStream(list, path);
	}

	public void run2() {
	//	String path = createDirectory(new Date())+"/";
		List<ManagementPersonnel> list = new ArrayList<ManagementPersonnel>();
		ManagementPersonnel managementPersonnel = new ManagementPersonnel();

		managementPersonnel.setMOFNo("465-00002043");

		Personnel personnel = new Personnel();
		personnel.setIsManagement("N");
		personnel.setIsProfessional("N");
		personnel.setIsSemiProf("N");
		personnel.setIsClerical("Y");
		personnel.setName("ABDUL HADI BIN RAHIL");
		personnel.setNewICNo("600804015451");
		personnel.setPassport("N/A");
		personnel.setIsBumi("Y");
		personnel.setCitizenOf(131);
		personnel.setRace("MELAYU");
		personnel.setDesignation("DRIVER");
		personnel.setWorkExperience(25);
		managementPersonnel.setPersonnel(personnel);

		Qualification qualification = new Qualification();
		qualification.setMajorIn(null);
		qualification.setQualification("");
		qualification.setQualificationLevel(0);
		qualification.setInstitute(null);
		qualification.setYear("25");
		qualification.setEducationField(0);
		managementPersonnel.setQualification(qualification);

		ProfBodyDetail profBodyDetail = new ProfBodyDetail();
		profBodyDetail.setProfBody("");
		profBodyDetail.setProfBodyNo("");
		profBodyDetail.setProfBodyExpiry(null);
		managementPersonnel.setProfBodyDetail(profBodyDetail);

		Expertise expertise = new Expertise();
		expertise.setCategoryClassification(null);
		managementPersonnel.setExpertise(expertise);

		list.add(managementPersonnel);
		manageProfileFileOutPutStream(list, path);
	}

	public void run1() {
	//	String path = createDirectory(new Date())+"/";
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

		signatoryFileOutPutStream(list1, path);
	}

	public void run() {
	//	String path = createDirectory(new Date())+"/";

		List<ConsultantProfile> list = new ArrayList<ConsultantProfile>();
		ConsultantProfile profile = new ConsultantProfile();

		profile.setCompanyName("THL CONSULTANTS     ");
		profile.setSSMNo("1086516-V");
		profile.setMOFNo("465-02032414");
		profile.setCompanyType("1");
		profile.setAddress1("2-2-2, JALAN SEMARAK API 3    ");
		profile.setAddress2("DIAMOND SQUARE");
		profile.setAddress3("     OFF JALAN GOMBAK");
		profile.setPostcode("53000");
		profile.setCity("KUALA LUMPUR");
		profile.setDistrict("1401");
		profile.setState("14");
		profile.setCountry("MY");
		profile.setTelCountryCode("+6");
		profile.setTelAreaCode("03");
		profile.setTelNo("40238289");
		profile.setFaxCountryCode("+6");
		profile.setFaxAreaCode("04");
		profile.setFaxNo("40211289");
		profile.setAccumulatedCapital(50000);
		profile.setAuthorisedCapital("0");
		profile.setPaidUpCapital("0");
		profile.setAccumulatedShare(0);

		profile.setBumiputeraPercentage(0);
		profile.setNonBumiPercentage(0);
		profile.setRegStatus(3);
		profile.setEffectDateTo(new Date());
		profile.setEstablishedDate(new Date());
		profile.setEffectDateFrom(new Date());
		profile.setDisciplinary("N");
		profile.setIsAssociatedCompany("N");
		profile.setProfileUpdateDate(new Date());

		ProfBodyDetail profBodyDetail = new ProfBodyDetail();
		profBodyDetail.setProfBody("3");
		profBodyDetail.setProfBodyNo("1997/FS00224");
		profBodyDetail.setProfBodyExpiry(new Date());
		profile.setProfBodyDetail(profBodyDetail);

		RegistrationHistory registrationHistory = new RegistrationHistory();
		registrationHistory.setDateStart(new Date());
		registrationHistory.setDateEnd(new Date());
		profile.setRegistrationHistory(registrationHistory);

		CapitalProportion capitalProportion = new CapitalProportion();
		capitalProportion.setBumi("0");
		capitalProportion.setNonBumi("50000");
		capitalProportion.setNonMalaysian("0");
		capitalProportion.setGovAgency("0");
		capitalProportion.setGovEquity("0");
		capitalProportion.setLocalCo("0");
		capitalProportion.setForeignCo("0");
		capitalProportion.setTotalMember(0);
		capitalProportion.setTotalShare(0);
		profile.setCapitalProportion(capitalProportion);
		list.add(profile);

		consultantProfileFileOutPutStream(list, path);
	}

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

	public String createDirectory(Date date) {
		
		SimpleDateFormat dt1 = new SimpleDateFormat("ddMMyyyy");

		File file = new File(path+dt1.format(date));
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		} else {
			System.out.println("Directory is already exists!");
		}
		
			System.out.println("file.getAbsoluteFile() "+file.getAbsolutePath());
			
			return file.getAbsolutePath();

	}*/
}
