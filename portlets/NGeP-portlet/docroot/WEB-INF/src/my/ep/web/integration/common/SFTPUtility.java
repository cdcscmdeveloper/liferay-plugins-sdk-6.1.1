package my.ep.web.integration.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

/**
 * The class SFTPUtil containing uploading, downloading, checking if file exists
 * and deleting functionality using Apache Commons VFS (Virtual File System)
 * Library
 * 
 * @author Ganga prasad
 * 
 */
public class SFTPUtility {

	String hostName = IntegrationConstants.SFTP_HOST;
	String username = IntegrationConstants.SFTP_USER;
	String password = IntegrationConstants.SFTP_PASS;

	
	  public static void main(String[] args) {
		  /* String remoteFilePath = "/batch/1GFMAS/IN/1002APOVE40000120150102005.GPG"; 
	  String localFilePath = "/batch/1GFMAS/ARCHIVE/1002APOVE40000120150102005.GPG"; 
	  String localFilePath ="C:/Integration/18012014/1000APOVE40000120141003001.GPG"; 
	   String	  remoteTempFilePath = "/FakeRemoteTempPath/FakeRemoteTempFile.txt"; */
	  SFTPUtility sftpUtility = new SFTPUtility();
	//  System.out.println("File Name :"+sftpUtility.findFileNameInDirectory(IntegrationConstants.PHIS_UPLOAD_DIR, "PHS-030"));
	 /* sftpUtility.upload(localFilePath, remoteFilePath); 
	  sftpUtility.exist(remoteFilePath); 
	  sftpUtility.download(localFilePath,remoteFilePath); 
	   move(hostName, username, password,remoteFilePath, remoteTempFilePath); 
	   delete(hostName, username,password, remoteFilePath); }
	  sftpUtility.listFilesInDirectory("");*/
	  
	 // sftpUtility.move(remoteFilePath, localFilePath);
	 
	  }
	  
	  
	/**
	 * Method to upload a file in Remote server
	 * 
	 */
	
	public void upload(String localFilePath, String remoteFilePath) {
		System.out.println("inside file upload...");

		File file = new File(localFilePath);
		if (!file.exists())
			throw new RuntimeException("Error. Local file not found");

		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {
			manager.init();

			// Create local file object
			FileObject localFile = manager.resolveFile(file.getAbsolutePath());

			// Create remote file object
			FileObject remoteFile = manager.resolveFile(
					createConnectionString(hostName, username, password,
							remoteFilePath), createDefaultOptions());
			/*
			 * use createDefaultOptions() in place of fsOptions for all default
			 * options - Ashok.
			 */

			// Copy local file to sftp server
			remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);

			System.out.println("File upload success "+"[ hostName :"+hostName+" username :"+username+" password :"+password+" ]");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			manager.close();
		}
	}

	public boolean move(String remoteSrcFilePath, String remoteDestFilePath) {
		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {
			manager.init();

			// Create remote object
			FileObject remoteFile = manager.resolveFile(
					createConnectionString(hostName, username, password,
							remoteSrcFilePath), createDefaultOptions());
			FileObject remoteDestFile = manager.resolveFile(
					createConnectionString(hostName, username, password,
							remoteDestFilePath), createDefaultOptions());

			if (remoteFile.exists()) {
				remoteFile.moveTo(remoteDestFile);
				;
				System.out.println("Move remote file success");
				return true;
			} else {
				System.out.println("Source file doesn't exist");
				return false;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			manager.close();
		}
	}

	/**
	 * Method to download the file from remote server location
	 * 
	 */
	public boolean download(String localFilePath, String remoteFilePath) {

		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {
			manager.init();

			// Append _downlaod_from_sftp to the given file name.
			// String downloadFilePath = localFilePath.substring(0,
			// localFilePath.lastIndexOf(".")) + "_downlaod_from_sftp" +
			// localFilePath.substring(localFilePath.lastIndexOf("."),
			// localFilePath.length());

			// Create local file object. Change location if necessary for new
			// downloadFilePath
			FileObject localFile = manager.resolveFile(localFilePath);

			// Create remote file object
			FileObject remoteFile = manager.resolveFile(
					createConnectionString(hostName, username, password,
							remoteFilePath), createDefaultOptions());

			// Copy local file to sftp server
			localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);

			System.out.println("File download success");
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			manager.close();
		}
	}

	/**
	 * Method to delete the specified file from the remote system
	 * 
	 */
	public void delete(String remoteFilePath) {
		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {
			manager.init();

			// Create remote object
			FileObject remoteFile = manager.resolveFile(
					createConnectionString(hostName, username, password,
							remoteFilePath), createDefaultOptions());

			if (remoteFile.exists()) {
				remoteFile.delete();
				System.out.println("Delete remote file success");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			manager.close();
		}
	}

	/**
	 * Method to check if the remote file exists in the specified remote
	 */
	public boolean exist(
			String remoteFilePath) {
		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {
			manager.init();

			// Create remote object
			FileObject remoteFile = manager.resolveFile(
					createConnectionString(hostName, username, password,
							remoteFilePath), createDefaultOptions());

			System.out.println("File exist: " + remoteFile.exists());

			return remoteFile.exists();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			manager.close();
		}
	}

	/**
	 * Generates SFTP URL connection String
	 * 
	 */
	public String createConnectionString(String hostName,
			String username, String password, String remoteFilePath) {
		return "sftp://" + username + ":" + password + "@" + hostName + "/"
				+ remoteFilePath;
	}

	/**
	 * Method to setup default SFTP config
	 * 
	 * @return the FileSystemOptions object containing the specified
	 *         configuration options
	 * @throws FileSystemException
	 */
	public static FileSystemOptions createDefaultOptions()
			throws FileSystemException {
		// Create SFTP options
		FileSystemOptions opts = new FileSystemOptions();

		// SSH Key checking
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
				opts, "no");

		/*
		 * Using the following line will cause VFS to choose File System's Root
		 * as VFS's root. If I wanted to use User's home as VFS's root then set
		 * 2nd method parameter to "true"
		 */
		// Root directory set to user home
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

		// Timeout is count by Milliseconds
		SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

		return opts;
	}

	public void createDirectory(String directoryName) {

		File dir = new File(directoryName);

		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {
			manager.init();

			// Create remote file object
			FileObject remoteFile = manager.resolveFile(
					createConnectionString(hostName, username, password,
							directoryName), createDefaultOptions());
			if (remoteFile.exists()) {
				throw new RuntimeException("Remote file exists..");
			} else {
				remoteFile.createFolder();
				System.out.println("directory creation successful :");
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			manager.close();
		}
	}

	public void listFilesInDirectory(String directoryName) {

		File dir = new File("/batch/eperunding/IN/" + directoryName);

		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {
			manager.init();

			// Create remote file object
			FileObject remoteFile = manager.resolveFile(
					createConnectionString(hostName, username, password,
							directoryName), createDefaultOptions());

			if (remoteFile.exists()) {
				FileObject exists[] = remoteFile.getChildren();
				for (int i = 0; i < exists.length; i++) {

					exists[i].getName();
					System.out.println(" Names :" + exists[i].getName());
				}
			}
			// remoteFile.createFolder();

			System.out.println("directory creation successful :"+ remoteFile.exists());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			manager.close();
		}
	}
	
	public String findFileNameInDirectory(String directoryName,String serviceId) {

			StandardFileSystemManager manager = new StandardFileSystemManager();
			String fileName = null;

			try {
				manager.init();

				FileObject remoteFile = manager.resolveFile(
						createConnectionString(hostName, username, password,
								directoryName), createDefaultOptions());
                
				if (remoteFile.exists()) {
					FileObject exists[] = remoteFile.getChildren();
					
					for (int i = 0; i < exists.length; i++) {

						fileName = exists[i].getName().toString();
						String subFile = fileName.substring(53);
						
						if(subFile.contains(serviceId )){
							System.out.println("subFile :"+subFile);
							subFile.contains(getDateStringValue());
							return subFile;
						}
					}
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				manager.close();
			}
			return null;
		}
	
	public String getDateStringValue(){
		
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyMMdd");

		String DATE = dt1.format(new Date());
		return DATE;
	}
	
}
