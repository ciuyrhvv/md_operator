package telecom.executors.kostanay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import telecom.Mailer;
import telecom.executors.Execute;

public class ExecuteS12 extends Execute {

	public ExecuteS12(String mode) throws IOException {
		super(mode);
		this.fileMask = ini.getString(this.mode, "filemask", "-1")  + "|" + ini.getString(this.mode, "reportmask", "-1");

	}

	private File[] unZipFile(File file) throws IOException {
		
		String slash = System.getProperty("file.separator");
		ArrayList<File> fileList = new ArrayList<>();
		FileInputStream fin = new FileInputStream(file);
		ZipInputStream zin = new ZipInputStream(fin);
		ZipEntry ze = null;

		try {
			byte[] buffer = new byte[1024];
			while ((ze = zin.getNextEntry()) != null) {

				File outFile = new File(file.getCanonicalFile().getParent() + slash + ze.getName());

				if (outFile.exists())
					throw new IOException(outFile.getCanonicalPath() + " is allready exists. UNZIP error.");

				FileOutputStream fout = new FileOutputStream(outFile);
				
				for (int c = zin.read(buffer); c > 0; c = zin.read(buffer)) {
					fout.write(buffer, 0, c);
				}
				zin.closeEntry();
				fout.close();

				fileList.add(outFile);
			}
			
		} finally {
			zin.close();
		}

		return fileList.toArray(new File[fileList.size()]);
	}

	protected void start() throws Exception {
		String slash = System.getProperty("file.separator");

		String subj = ini.getString("email", "subject", "");
		try {
			File[] files = downloadFiles();
			if (files.length > 0) {
				
				uploadFilesToArch(files);

				String filemask = ini.getString(this.mode, "filemask", "qqq");
				
				
	         	List <File> aFiles = new ArrayList<File>();	         		         	
         	         	
				for (File zipFile : files) {

					if (Pattern.compile(filemask).matcher(zipFile.getName()).matches()) {					
						
						File[] unzFiles = unZipFile(zipFile);
						
						for (File unzFile : unzFiles) {
							
							String strUnzDir = unzFile.getCanonicalFile().getParent() + slash;
							String strUnzNewFile = 									
									 zipFile.getName().substring(0,
											zipFile.getName().lastIndexOf(getFileExtension(zipFile.getName())))
									+ "_" + unzFile.getName();
							
							
							File unzNewFile = new File(strUnzDir + strUnzNewFile);
							
							unzFile.renameTo(unzNewFile);
							
							aFiles.add(unzNewFile);																				

						}
					}					
				}
				
				uploadFilesToMD(aFiles.toArray(new File[aFiles.size()]));				
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
			(new Mailer(ini.getString("email", "to", ""), ini.getString("email", "from", ""),
					ini.getString("email", "host", ""), subj, this.log.getAllMessages())).send();
		}
	}

	private String getFileExtension(String filename) {
		if (filename == null) {
			return null;
		}
		int lastUnixPos = filename.lastIndexOf('/');
		int lastWindowsPos = filename.lastIndexOf('\\');
		int indexOfLastSeparator = Math.max(lastUnixPos, lastWindowsPos);
		int extensionPos = filename.lastIndexOf('.');
		int lastSeparator = indexOfLastSeparator;
		int indexOfExtension = lastSeparator > extensionPos ? -1 : extensionPos;
		int index = indexOfExtension;
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index);
		}
	}

}
