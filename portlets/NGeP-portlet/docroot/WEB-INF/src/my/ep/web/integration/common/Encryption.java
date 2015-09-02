package my.ep.web.integration.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import my.ep.factory.EpLogger;

import org.apache.log4j.Logger;

public class Encryption {
	
	private static Logger log = EpLogger.getLogger(EperundingIntegration.class);

	SFTPUtility sftpUtility = new SFTPUtility();
	OsbUtility osbUtility = new OsbUtility();

	public void encryptGmas(String inFilePath,String outFilePath,String fileName,String serviceCode,String processId) {
		
		log.info("inside encryptGmas....");
	
	//	FileUtility fileUtility = new FileUtility();
		File inFile = new File(inFilePath + fileName);
		File outFile = new File(outFilePath + fileName);
	
		URL resource = getClass().getResource(".");
		String path = resource.getPath();
	
		log.info("path value with (.) :" + path);
	
		// String substring =
		// path.substring(0,path.indexOf("NGeP-portlet.war"));
	
		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));
	
		String encryptKey = substring
				+ IntegrationConstants.GMAS_PUBLIC_KEY_PATH;
		log.info("encryptKey :" + encryptKey);
	
		// encript the content
		String encrypted;
		try {
			encrypted = PGPProcessor.encryptContent(readFileContents(inFile),
					encryptKey);
	
			createFile(outFile, encrypted);
			log.info("encryption successful..");
		} catch (Exception e) {
			
			log.info("Error Creating Encryption File");
	
			e.printStackTrace();
		}
		// upload the encripted file to sftp server
		
	
		sftpUtility.upload(outFilePath + fileName,IntegrationConstants.GMAS_UPLOAD +fileName,fileName,serviceCode,processId);
		
		// call osb service for transfering from host to 1GFMAS
		 osbUtility.osbServiceCallOutBound(processId,fileName);
	
	}
	
	
	public void encryptGmas(BatchReqDTO reqDTO) {
		log.info("inside encryptGmas....");
	
//		FileUtility fileUtility = new FileUtility();
		File inFile = new File(reqDTO.getFilePath() + reqDTO.getFileName());
		File outFile = new File(reqDTO.getEncryptFilePath() + reqDTO.getFileName());
	
		URL resource = getClass().getResource(".");
		String path = resource.getPath();
	
		log.info("path value with (.) :" + path);
	
		// String substring =
		// path.substring(0,path.indexOf("NGeP-portlet.war"));
	
		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));
	
		String encryptKey = substring
				+ IntegrationConstants.GMAS_PUBLIC_KEY_PATH;
		log.info("encryptKey :" + encryptKey);
	
		// encript the content
		String encrypted;
		try {
			encrypted = PGPProcessor.encryptContent(readFileContents(inFile),
					encryptKey);
	
			createFile(outFile, encrypted);
			log.info("encryption successful..");
		} catch (Exception e) {
			
			log.info("Error Creating Encryption File");
			e.printStackTrace();
		}
		// upload the encripted file to sftp server
		
//		reqDTO.setLocatFilePath(reqDTO.getEncryptFilePath() + reqDTO.getFileName());
		reqDTO.setRemoteFilePath(IntegrationConstants.GMAS_UPLOAD + reqDTO.getFileName());
		
		reqDTO.setLocatFilePath(reqDTO.getEncryptFilePath() + reqDTO.getFileName());
//		reqDTO.setRemoteFilePath(IntegrationConstants.GMAS_UPLOAD);
		reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_1GFMAS);
		reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
	
		sftpUtility.upload(reqDTO);
		
		// call osb service for transfering from host to 1GFMAS
		 osbUtility.osbServiceCallOutBound(reqDTO.getProcessId(),reqDTO.getFileName());
	
	}

	// decrypt gmass
	public void decryptGmas(String inFilePath, String outFilePath) {
		log.info("inside decryptGmas");
		log.info("inFilePath :" + inFilePath);
		log.info("outFilePath :" + outFilePath);

		File inFile = new File(inFilePath);
		File outFile = new File(outFilePath);

		URL resource = getClass().getResource(".");
		String path = resource.getPath();

		log.info("path value with (.) :" + path);

		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));

		String encryptKey = substring
				+ IntegrationConstants.GMAS_PRIVATE_KEY_PATH;

		log.info("decrpt :" + encryptKey);

		try {
			String decrypted = PGPProcessor.decryptContent(
					readFileContents(inFile), encryptKey,
					IntegrationConstants.GMAS_PASS_PHASE);

			createFile(outFile, decrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// encrypt phis
	public void encryptPhis(String inFilePath, String outFilePath,
			String fileName, String processId) {

		log.info("inside encryptPhis");
		File inFile = new File(inFilePath + fileName);
		File outFile = new File(outFilePath + fileName);

		URL resource = getClass().getResource(".");
		String path = resource.getPath();

		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));

		String encryptKey = substring
				+ IntegrationConstants.PHIS_PUBLIC_KEY_PATH;

		// encript the content
		String encrypted;
		try {
			encrypted = PGPProcessor.encryptContent(readFileContents(inFile),
					encryptKey);

			createFile(outFile, encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// upload the encripted file to sftp server

		sftpUtility.upload(outFilePath + fileName,
				IntegrationConstants.PHIS_UPLOAD_DIR + fileName, null, null, null);

		// once upload is done cal web service osb integration to transfer file.
//		osbUtility.osbServiceCallOutBound(processId, fileName);

	}
	
	public void encryptPhis(BatchReqDTO reqDTO) {

		log.info("inside encryptPhis");
		File inFile = new File(reqDTO.getFilePath() + reqDTO.getFileName());
		File outFile = new File(reqDTO.getEncryptFilePath() + reqDTO.getFileName());

		URL resource = getClass().getResource(".");
		String path = resource.getPath();

		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));
//		String substring = path.substring(0, path.indexOf("NGeP-portlet"));

		String encryptKey = substring
				+ IntegrationConstants.PHIS_PUBLIC_KEY_PATH;

		// encript the content
		String encrypted;
		try {
			encrypted = PGPProcessor.encryptContent(readFileContents(inFile),
					encryptKey);

			createFile(outFile, encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// upload the encripted file to sftp server

		reqDTO.setLocatFilePath(reqDTO.getEncryptFilePath() + reqDTO.getFileName());
		reqDTO.setRemoteFilePath(IntegrationConstants.PHIS_UPLOAD_DIR + reqDTO.getFileName());
		reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_PHIS);
		reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
		sftpUtility.upload(reqDTO);

		// once upload is done cal web service osb integration to transfer file.
		osbUtility.osbServiceCallOutBound(reqDTO.getServiceCode(), reqDTO.getFileName());

	}

	private void createFile(File outFilePath, String decrypted)
			throws IOException {
		if (!outFilePath.exists()) {
			outFilePath.createNewFile();
		}

		FileWriter fw = new FileWriter(outFilePath.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(decrypted);
		bw.close();
	}

	public String readFileContents(File inFilePath) {

		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {

			log.info("getAbsolutePath  :"
					+ inFilePath.getAbsolutePath());
			log.info("length  :" + inFilePath.length());
			br = new BufferedReader(new FileReader(inFilePath));
			String line = br.readLine();
			log.info("line :" + line);

			while (line != null) {

				sb.append(line);
				sb.append(System.getProperty("line.separator"));
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception ee) {

			}
		}

		return sb.toString();
	}
}
