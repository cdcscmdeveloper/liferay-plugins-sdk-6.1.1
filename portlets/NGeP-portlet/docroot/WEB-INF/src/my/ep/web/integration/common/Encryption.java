package my.ep.web.integration.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import my.ep.biz.service.integration.common.JAXBUtility;
import my.ep.biz.service.integration.jms.IRemoteClientSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.jms.common.JMSReqDTO;
import my.ep.dto.jms.common.JMSResDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@SuppressWarnings("rawtypes")
public class Encryption {
	
	private static Logger log = EpLogger.getLogger(Encryption.class);

	JAXBUtility jaxbUtility = new JAXBUtility();
	
	IRemoteClientSO remoteClientSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.MDB, IRemoteClientSO.class);

	public void encryptGmas(BatchReqDTO reqDTO) {
		log.info("inside encryptGmas....");
	
		File inFile = new File(reqDTO.getFilePath() + reqDTO.getFileName());
		File outFile = new File(reqDTO.getEncryptFilePath() + reqDTO.getFileName());
	
		URL resource = getClass().getResource(".");
		String path = resource.getPath();
	
		log.info("path value with (.) :" + path);
	
		log.info("user.dir "+System.getenv("user.dir"));
		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));
		
//		substring = "C:/ePerolehanDemo/liferay-portal-6.1.1-ce-ga2/jboss-7.1.1/standalone/deployments/";
	
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
		
//		String transId =  UUID.randomUUID().toString();
//		reqDTO.setRemoteFilePath(IntegrationConstants.GMAS_UPLOAD + reqDTO.getFileName());
//		reqDTO.setLocatFilePath(reqDTO.getEncryptFilePath() + reqDTO.getFileName());
//		reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_1GFMAS);
//		reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
//		reqDTO.setTransId(transId);
//		sftpUtility.upload(reqDTO);
		
		// call osb service for transfering from host to 1GFMAS
//		osbUtility.osbServiceCallOutBound(reqDTO.getServiceCode(),reqDTO.getFileName());
		
//		reqDTO.setQueueMsgStatus(fileUtility.constructQueueMessage(reqDTO.getServiceCode(), reqDTO.getFileName(), transId));
		
//		fileUtility.setValuesToPmInterface(reqDTO);
	
	}

	// decrypt gmass
	public void decryptGmas(String inFilePath, String outFilePath) {
		log.info("inside decryptGmas");

		File inFile = new File(inFilePath);
		File outFile = new File(outFilePath);

		URL resource = getClass().getResource(".");
		String path = resource.getPath();

		log.info("path value with (.) :" + path);

		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));

		String encryptKey = substring + IntegrationConstants.GMAS_PRIVATE_KEY_PATH;

		try {
			String decrypted = PGPProcessor.decryptContent(
					readFileContents(inFile), encryptKey,
					IntegrationConstants.GMAS_PASS_PHASE);

			createFile(outFile, decrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public BatchReqDTO encryptPhis(BatchReqDTO reqDTO) {

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

		String transId =  UUID.randomUUID().toString();
		reqDTO.setLocatFilePath(reqDTO.getEncryptFilePath() + reqDTO.getFileName());
		reqDTO.setRemoteFilePath(IntegrationConstants.PHIS_UPLOAD_DIR + reqDTO.getFileName());
		reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_PHIS);
		reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
		reqDTO.setTransId(transId);
	
//		sftpUtility.upload(reqDTO);

		// once upload is done cal web service osb integration to transfer file.
//		osbUtility.osbServiceCallOutBound(reqDTO.getServiceCode(), reqDTO.getFileName());
		
//		reqDTO.setQueueMsgStatus(fileUtility.constructQueueMessage(reqDTO.getServiceCode(), reqDTO.getFileName(), transId));
		
//		fileUtility.setValuesToPmInterface(reqDTO);
		
		JMSReqDTO jmsReqDTO = new JMSReqDTO();
		jmsReqDTO.setQueueName("queue/Batch_Outbound_Queue");
		jmsReqDTO.setTextMessage(true);
		jmsReqDTO.setTextMessage(jaxbUtility.getMarshalMessageOutbound(reqDTO.getServiceCode(),reqDTO.getFileName(), transId));
		
		JMSResDTO jmsResDTO = remoteClientSO.sendMessageToJBossQueue(jmsReqDTO);
		
		if(jmsResDTO.isSuccessful()){
			reqDTO.setQueueMsgStatus(1);
		}else{
			reqDTO.setQueueMsgStatus(0);
		}

		return reqDTO;
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
			br = new BufferedReader(new FileReader(inFilePath));
			String line = br.readLine();
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
	
	
	public static void main(String args[]){
		
		Encryption encryption = new Encryption();
		BatchReqDTO dto = new BatchReqDTO<>();
		dto.setFileName("1000APIVE40000120160310002.GPG");
		dto.setFilePath("C:/Integration/batch/Temp/");
		dto.setEncryptFilePath("C:/Integration/batch/1GFMAS/OUT/");
		
		
		encryption.encryptGmas(dto);
		
	}
}
