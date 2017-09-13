package telecom.executors.kostanay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import telecom.MyFTP;
import telecom.executors.Execute;
import telecom.Mailer;


public class ExecuteS12WRAMTS extends Execute{

	public ExecuteS12WRAMTS(String mode) throws IOException {
		super(mode);
	}
		
	private File compressFile(File file, String strFileNewName) throws IOException{
		String slash = System.getProperty("file.separator");
		
		byte[] buffer = new byte[1024];		
		
		String zipFile = file.getParentFile().getCanonicalPath() + slash + strFileNewName + ".zip";
		
		FileOutputStream fos = new FileOutputStream(zipFile);
				
		ZipOutputStream zos = new ZipOutputStream(fos);
		
		zos.setLevel(9);
		
		ZipEntry ze = new ZipEntry(strFileNewName + ".db");
		
		zos.putNextEntry(ze);
		
		FileInputStream in = new FileInputStream(file);

		int len;
		while ((len = in.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}

		in.close();
		zos.closeEntry();

     	zos.close();
		
		return new File(zipFile);
	}	
	
	private void uploadFilesToTC(File[] files) throws IOException {
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
			ftp.uploadFiles(files, remoteDir);
		} finally {
			ftp.disconnect();
		}		
		
	}

	protected void start() throws Exception {		 
		String subj = ini.getString("email","subject","");
		List <File> aFiles = new ArrayList<File>();
		try {
			File[] files = downloadFiles();
			if (files.length > 0) {
	         	uploadFilesToArch(files);
				uploadFilesToMD(files);
							
				for(File file : files) {
					String strFileNewName = "18_" + file.getName().substring(13,15) +
							file.getName().substring(10,12) + 
							file.getName().substring(7,9) +"_"+ file.getName().substring(21,22); 
					
					File zipFile = compressFile(file, strFileNewName);
					aFiles.add(zipFile);
					
					//AMTS.2017.08.24.0000.0
					//18_240817_0.zip
				}
				
				uploadFilesToTC(aFiles.toArray(new File[aFiles.size()]));
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
