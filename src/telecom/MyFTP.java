package telecom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

public class MyFTP {

	private FTPClient ftp;
	private String hostname, username, password;

	// private String slash;

	public MyFTP(String hostname, String username, String password) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		// this.slash = System.getProperty("file.separator");
	}

	public void connect() throws IOException {
		ftp = new FTPClient();
		ftp.setBufferSize(1024000);
		ftp.connect(hostname);

		int reply;
		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new IOException("FTP server refused connection");
		}
		if (!ftp.login(username, password)) {
			ftp.logout();
			throw new IOException("Wrong Username or Password");
		}
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.enterLocalPassiveMode();
		ftp.setBufferSize(1024000);
	}

	public void disconnect() {
		if (ftp.isConnected()) {
			try {
				ftp.disconnect();
			} catch (IOException f) {
				// do nothing
			}
		}
	}

	public void uploadFiles(String localDir, String remoteDir, String fileMask)
			throws IOException {
		if (!remoteDir.equals("")) {
			// ftp.makeDirectory(remoteDir);
			if (!makeDirectories(remoteDir))
				throw new IOException("Could't create a folder");
			ftp.changeWorkingDirectory(remoteDir);
		}
		File[] files = (new File(localDir)).listFiles(getFileFilter(fileMask));
		for (File file : files) {
			InputStream input = new FileInputStream(file.getCanonicalPath());
			ftp.storeFile(file.getName(), input);
			input.close();
		}
	}
	
	public void uploadFiles(File[] aFiles, String remoteDir)
			throws IOException {
		if (!remoteDir.equals("")) {
			// ftp.makeDirectory(remoteDir);
			if (!makeDirectories(remoteDir))
				throw new IOException("Could't create a folder");
			ftp.changeWorkingDirectory(remoteDir);
		}

		for (File file : aFiles) {
			InputStream input = new FileInputStream(file.getCanonicalPath());
			ftp.storeFile(file.getName(), input);
			input.close();
		}
	}	

	public void downloadFiles(String localDir, String remoteDir, String fileMask)
			throws IOException {
		if (remoteDir != "")
			ftp.changeWorkingDirectory(remoteDir);
		FTPFile[] ftpFiles = ftp.listFiles("", getFTPFileFilter(fileMask));
		for (FTPFile ftpFile : ftpFiles) {
			OutputStream output = new FileOutputStream(localDir
					+ ftpFile.getName());
			ftp.retrieveFile(ftpFile.getName(), output);
			output.close();
		}
	}

	public void downloadFiles(String localDir, String remoteDir,
			String fileMask, int daysBack) throws IOException {
		if (remoteDir != "")
			ftp.changeWorkingDirectory(remoteDir);
		FTPFile[] ftpFiles = ftp.listFiles("",
				getFTPFileFilter(fileMask, daysBack));
		for (FTPFile ftpFile : ftpFiles) {
			OutputStream output = new FileOutputStream(localDir
					+ ftpFile.getName());
			ftp.retrieveFile(ftpFile.getName(), output);
			output.close();
		}
	}

	public File[] downloadFiles(String localDir, String remoteDir,
			String fileMask, ArrayList<String> exceptFiles) throws IOException {
		if (remoteDir != "")
			ftp.changeWorkingDirectory(remoteDir);

		ArrayList<File> aFiles = new ArrayList<File>();
		FTPFile[] ftpFiles = ftp.listFiles("", getFTPFileFilter(fileMask));
		Boolean exists;
		for (FTPFile ftpFile : ftpFiles) {
			exists = false;
			for (String ex : exceptFiles) {
				if (ex.equals(ftpFile))
					exists = true;
			}
			if (!exists) {
				String strFile = localDir + ftpFile.getName();
				
				OutputStream output = new FileOutputStream(strFile);
				ftp.retrieveFile(ftpFile.getName(), output);
				output.close();
				
				File file = new File(strFile);
				aFiles.add(file);
			}
		}		
		return aFiles.toArray(new File[aFiles.size()]);
	}

	public File[] downloadFiles(String localDir, String remoteDir,
			String fileMask, int daysBack, ArrayList<String> exceptFiles)
			throws IOException {
		if (!remoteDir.equals(""))
			ftp.changeWorkingDirectory(remoteDir);
		ArrayList<File> aFiles = new ArrayList<File>();
		
		FTPFile[] ftpFiles = ftp.listFiles("",
				getFTPFileFilter(fileMask, daysBack));
		Boolean exists;
		for (FTPFile ftpFile : ftpFiles) {
			exists = false;
			for (String exceptFile : exceptFiles) {
				if (ftpFile.getName().equals(exceptFile)) { 
					exists = true;
					break;
				}	
			}
			if (!exists) {
				String strFile = localDir + ftpFile.getName();
				
				OutputStream output = new FileOutputStream(strFile);
				ftp.retrieveFile(ftpFile.getName(), output);
				output.close();
				
				File file = new File(strFile);
				aFiles.add(file);

			}
		}
		return aFiles.toArray(new File[aFiles.size()]);		
	}

	private boolean makeDirectories(String dirPath) throws IOException {
		String[] pathElements = dirPath.split("/");
		if (pathElements != null && pathElements.length > 0) {
			// System.out.println(pathElements);
			for (String singleDir : pathElements) {
				if (singleDir.equals(""))
					singleDir += "/";
				boolean existed = ftp.changeWorkingDirectory(singleDir);
				if (!existed) {
					boolean created = ftp.makeDirectory(singleDir);
					if (created) {
						ftp.changeWorkingDirectory(singleDir);
					} else {
						// System.out.println("Не удалось создать папку: " +
						// singleDir);
						return false;
					}
				}
			}
		}
		return true;
	}

	private FTPFileFilter getFTPFileFilter(final String mask) {
		FTPFileFilter filter = new FTPFileFilter() {
			@Override
			public boolean accept(FTPFile ftpFile) {
				// return (ftpFile.isFile() &&
				// ftpFile.getName().startsWith(mask));
				return Pattern.compile(mask).matcher(ftpFile.getName())
						.matches();
			}
		};
		return filter;
	}

	private FTPFileFilter getFTPFileFilter(final String mask, final int daysBack) {
		FTPFileFilter filter = new FTPFileFilter() {
			@Override
			public boolean accept(FTPFile ftpFile) {
				Calendar cFrom = Calendar.getInstance();
				cFrom.add(Calendar.DAY_OF_MONTH, daysBack);
				Calendar cFile = ftpFile.getTimestamp();
				
				//String fname = ftpFile.getName();
				//boolean n = Pattern.compile(mask).matcher(ftpFile.getName()).matches();
				
				 boolean m = Pattern.compile(mask).matcher(ftpFile.getName())
						.matches()
						&& cFile.after(cFrom);
				 return m;
			}
		};
		return filter;
	}

	private FilenameFilter getFileFilter(final String mask) {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				// return filename.startsWith(mask);
				return Pattern.compile(mask).matcher(filename).matches();
			}
		};
		return filter;
	}
}