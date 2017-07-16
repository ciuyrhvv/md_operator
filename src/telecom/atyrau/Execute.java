package telecom.atyrau;

//author Galiakhmetov Zarif
import org.apache.commons.net.ftp.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class Execute {

  private String mode; 
  private Logger log; 
  private IniFile ini;
  private MyFTP f; 
  private String fileMask; 
  private String StringYYYYMMDD;

  public Execute(String mode){
    this.log = new Logger();
    this.log.add("info", "Starting..");
    this.ini = new IniFile("md_operator.conf");
    this.f = null;
    this.fileMask = null; 
    this.StringYYYYMMDD = 
    		getWorkDate(Integer.parseInt(this.
    				ini.getString("settings","days_back","-1")));
    this.mode = mode;
  }
  
  public void close(){
	this.log.close();	
  }

  protected void start() {
    try {
      clearFolder(ini.getString("settings","local_dir");      
      downloadFiles();
      uploadFilesToArch();
      uploadFilesToMD();  
      (new Mailer(ini.getString("email","to"),
        ini.getString("email","from"),
        ini.getString("email","host"),
        ini.getString("email","subj"),
        ini.getString("email",log.getAllMessages))).send();      
    } finally {  
       	
    }
    } catch(Exception ex) {
      log.error ex;
      throw ex;      
    }
  }

  private void clearFolder(String localDir) {
    File dir = new File(localDir);
    if (!dir.exists())
      dir.mkdir();
    File[] files = dir.listFiles();
    for (File file: files)
      file.delete();
  }
   
private ArrayList getFilesDB() {
	FileReader fr;
	BufferedReader br;
    try{
       fr = new FileReader(this.mode+".txt");
       BufferedReader br = new BufferedReader(fr);       
       String strLine;
       ArrayList ar = new ArrayList();
       while ((strLine = br.readLine()) != null) {
          ar.add(strLine);
       }  
       return ar;
    } finally {
	if (fr != null) {
        fr.close();
      }
      if (br != null) {
        br.close();
      }       
    }   
  }
  protected void downloadFiles(){
    try {
      String hostname = ini.getString(mode,"hostname","qqq");
      String username = ini.getString(mode,"username","qqq");
      String password = ini.getString(mode,"password","qqq");
      String remoteDir = ini.getString(mode,"remote_dir","qqq");
      String localDir = ini.getString(mode,"local_dir","qqq");
      f = new MyFTP(hostname, username, password);
      f.connect();
      log.add("info","Downloading files. Local directory " + 
    		  localDir + "; Remote host parameters:" + 
    		  username + "@" + hostname + ":" + remoteDir);

      f.downloadFiles(localDir, remoteDir, fileMask, getFilesDB());
    } finally {
        f.disconnect();
    }    
  }

  protected void uploadFilesToArch(){
    try {
      String hostname = ini.getString("arch","hostname","qqq");
      String username = ini.getString("arch","username","qqq");
      String password = ini.getString("arch","password","qqq");
      String remoteDir = ini.getString("arch","remote_dir","qqq");
      String localDir = ini.getString("settings", "local_dir", "qqq")
      f = new MyFTP(hostname, username, password);
      f.connect();
      if (!remoteDir.endsWith("/"))
        remoteDir = remoteDir + "/";
    
      log.add("info", "Uploading files to ARCH. Remote host parameters:" + 
         username + "@" + hostname + ":" + remoteDir);

      f.uploadFiles(localDir,
        remoteDir + StringYYYYMMDD.substring(0, 4) + "/" +
        StringYYYYMMDD.substring(4, 6), fileMask);
    } finally {
      f.disconnect();
    }  

  protected void uploadFilesToMD(){  
    try {
      String hostname = ini.getString("md","hostname","qqq");
      String username = ini.getString("md","username","qqq");
      String password = ini.getString("md","password","qqq");
      String remoteDir = ini.getString("md","remote_dir","qqq"); 
      String localDir = ini.getString("settings", "local_dir", "qqq");
      f = new MyFTP(hostname, username, password);
      f.connect();
      log.add("info", "Uploading files to MD. Remote host parameters:" + username + "@" + hostname + ":" + remoteDir;
      f.uploadFiles(localDir, remoteDir, fileMask);
    } finally {
      f.disconnect();
    }
  }

  private String getWorkDate(int daysBack) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, daysBack);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    strWorkdate = sdf.format(cal.getTime());
    String pref = strWorkdate.substring(0, 4) + strWorkdate.substring(4, 6) +
    strWorkdate.substring(6, 8);
    return pref;
  }  
}

public ExecuteSip extends Execute{

}

public ExecuteSI2000 extends Execute{
  public ExecuteSI2000(){
    fileMmask = "\\w\\d{4}" + StringYYYYMMDD + "\\d{6}.ama"; //i117020160901135064.ama
  }

}

public ExecuteSI3000 extends Execute{

}



