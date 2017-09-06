package telecom.executors.kostanay;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import telecom.MyFTP;
import telecom.executors.Execute;
import telecom.Mailer;


public class ExecuteS12WRAMTS extends Execute{

	public ExecuteS12WRAMTS(String mode) throws IOException {
		super(mode);
		// TODO Auto-generated constructor stub
	}
	
	
	private String compressFile(String dir, String filename, String fileNewName) throws IOException{

		byte[] buffer = new byte[1024];
		
		String zipFile = dir + fileNewName + ".zip";
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		zos.setLevel(9);
		ZipEntry ze = new ZipEntry(fileNewName + ".db");
		zos.putNextEntry(ze);
		FileInputStream in = new FileInputStream(dir + filename);

		int len;
		while ((len = in.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}

		in.close();
		zos.closeEntry();

		// remember close it
		zos.close();
		
		return zipFile;
	}	
	
	private void uploadFilesToTC(String strFilemask) throws IOException {
		MyFTP ftp = null;
		try {
			String hostname = ini.getString("traffic_centre", "hostname", "qqq");
			String username = ini.getString("traffic_centre", "username", "qqq");
			String password = ini.getString("traffic_centre", "password", "qqq");
			String remoteDir = ini.getString("traffic_centre", "remote_dir", "qqq");

			ftp = new MyFTP(hostname, username, password);
			ftp.connect();

			if (!remoteDir.endsWith("/"))
				remoteDir = remoteDir + "/";
				
			log.add("info", "Uploading files to TrafficCentre. Remote host parameters:"
					+ username + "@" + hostname + ":" + remoteDir);
			ftp.uploadFiles(localDir, remoteDir, strFilemask);
		} finally {
			ftp.disconnect();
		}		
		
	}

	protected void start() throws Exception {		 
		String subj = ini.getString("email","subject","");
		//String filemask = ini.getString(this.mode, "filemask_tc", "qqq");
		String filemask = "^18.*\\.zip$";
		try {
			downloadFiles();
			if (this.files.size() > 0) {
	         	uploadFilesToArch();
				uploadFilesToMD(this.fileMask);
							
				String fileNewName ="";
				for(String fileName : files ) {
					fileNewName = "18_" + fileName.substring(13,15) +
							fileName.substring(10,12) + 
							fileName.substring(7,9) +"_"+ fileName.substring(21,22); 
					
					compressFile(localDir, fileName, fileNewName);
					//AMTS.2017.08.24.0000.0
					//18_010817_0.zip
				}
				
				uploadFilesToTC(filemask);
				setFilesDB(files);
			}			  		 			
		} catch (Exception ex) {
			this.log.add("error", ex.toString());
			throw ex;
		} finally {
			subj = subj + "(" + (this.mode) + ")";
			if (this.log.errCount > 0) { 
				  subj = subj + "[Error]";
				} else if (this.log.wrnCount > 0) {
					subj = subj + "[Warning]";
				}			
			(new Mailer(ini.getString("email","to",""),
					 ini.getString("email","from",""),
					 ini.getString("email","host",""),
					 subj,
					 this.log.getAllMessages())).send();			
		}
	}

}
