package my.ep.web.integration.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import my.ep.pgp.PGPProcessor;

public class Encryption {

	public void encryptGmas(String inFilePath, String outFilePath,String fileName,String processId) {

		File inFile = new File(inFilePath+fileName);
		File outFile = new File(outFilePath+fileName);
		
		
		URL resource = getClass().getResource("/");
		String path = resource.getPath();
		
		System.out.println("path -----------"+path);
		//path.replace("modules/sun/jdk/main/service-loader-resources/", "standalone/deployments/NGeP-portlet.war/encryptionKeys/1gfmasdev_pub.id");
		String replacePath = path;
		replacePath.replaceAll("modules/sun/jdk/main/service-loader-resources/", "standalone/deployments/NGeP-portlet.war/encryptionKeys/1gfmasdev_pub.id");
		
		System.out.println("replacePath :"+replacePath);
		
		path = "/C:/ePerolehanDemo/liferay-portal-6.1.1-ce-ga2/jboss-7.1.1/standalone/deployments/NGeP-portlet.war/encryptionKeys/1gfmasdev_pub.id";
		System.out.println("path ==========="+path);
		
		// encript the content
		String encrypted;
		try {
			encrypted = PGPProcessor.encryptContent(
					readFileContents(inFile),
					path);

			createFile(outFile, encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//upload the encripted file to sftp server
		
				SFTPUtility sftpUtility = new SFTPUtility();
				sftpUtility.upload(outFilePath+fileName, IntegrationConstants.GMAS_UPLOAD+fileName);
				
	    // call osb service	for transfering from host to 1GFMAS
				 OsbUtility osbUtility = new OsbUtility();
				 osbUtility.osbServiceCall(processId);
		
	}

	// decrypt gmass
	public void decryptGmas(File inFilePath, File outFilePath) {

		try {
			String decrypted = PGPProcessor.decryptContent(
					readFileContents(inFilePath),
					IntegrationConstants.GMAS_PRIVATE_KEY_PATH,
					IntegrationConstants.GMAS_PASS_PHASE);

			createFile(outFilePath, decrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	// encrypt phis
	public void encryptPhis(String inFilePath, String outFilePath,String fileName,String processId) {
		
		File inFile = new File(inFilePath+fileName);
		File outFile = new File(outFilePath+fileName);
		
		System.out.println("inFile  :"+inFile.getPath());
		System.out.println("readFileContents :"+readFileContents(inFile));

		// encript the content
		String encrypted;
		try {
			encrypted = PGPProcessor.encryptContent(
					readFileContents(inFile),
					IntegrationConstants.PHIS_PUBLIC_KEY_PATH);

			createFile(outFile, encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//upload the encripted file to sftp server
		
		SFTPUtility sftpUtility = new SFTPUtility();
		sftpUtility.upload(outFilePath+fileName, IntegrationConstants.PHIS_UPLOAD_DIR+fileName);
		
		//once upload is done cal web service osb integration to transfer file.
		 OsbUtility osbUtility = new OsbUtility();
		 osbUtility.osbServiceCall(processId);
		
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
			System.out.println("line :"+line);

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
