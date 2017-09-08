package telecom.executors.kostanay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import telecom.Mailer;
import telecom.executors.Execute;

public class ExecuteS12 extends Execute {

	public ExecuteS12(String mode) throws IOException {
		super(mode);
		this.fileMask = ini.getString(this.mode, "filemask", "-1")  + "|" + ini.getString(this.mode, "reportmask", "-1");

		// TODO Auto-generated constructor stub
	}

	private File[] unZipFile(File file) throws IOException {
		String slash = System.getProperty("file.separator");
		ArrayList<File> fileList = new ArrayList<>();
		FileInputStream fin = new FileInputStream(file.getCanonicalPath());
		ZipInputStream zin = new ZipInputStream(fin);
		ZipEntry ze = null;

		byte[] buffer = new byte[1024];
		while ((ze = zin.getNextEntry()) != null) {
			String strOutfile = file.getCanonicalFile().getParent() + slash + ze.getName();
			fileList.add(new File(strOutfile));
			FileOutputStream fout = new FileOutputStream(strOutfile);
			for (int c = zin.read(buffer); c > 0; c = zin.read(buffer)) {
				fout.write(buffer, 0, c);
			}
			zin.closeEntry();
			fout.close();
		}
		zin.close();

		return fileList.toArray(new File[fileList.size()]);
	}

	protected void start() throws Exception {
		String slash = System.getProperty("file.separator");

		String subj = ini.getString("email", "subject", "");
		try {
			downloadFiles();
			if (this.files.size() > 0) {
				uploadFilesToArch();

				String filemask = ini.getString(this.mode, "filemask", "qqq");
				// String reportmask = ini.getString(this.mode, "reportmask", "qqq");

				for (String fileName : files) {

					if (Pattern.compile(filemask).matcher(fileName).matches()) {
						
						File zipFile = new File(localDir + fileName);
						
						File[] unzFiles = unZipFile(zipFile);
						
						for (File unzFile : unzFiles) {
							
							String unzDir = unzFile.getCanonicalFile().getParent() + slash;
							String unzNewFile = 									
									 zipFile.getName().substring(0,
											zipFile.getName().lastIndexOf(getFileExtension(zipFile.getName())))
									+ "_" + unzFile.getName();
							
							unzFile.renameTo(new File(unzDir + unzNewFile));
							uploadFilesToMD(unzNewFile);
						}
					}					
				}
				
				
				setFilesDB(this.files);
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
