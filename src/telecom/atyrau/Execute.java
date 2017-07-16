package telecom.atyrau;

//author Galiakhmetov Zarif
import org.apache.commons.net.ftp.*;
import java.sql.*;
import java.util.*;


public class Execute {

  private  String mode; Logger log; IniFile ini; MyFTP f; String fileMask; String StringYYYYMMDD;

  public Execute(String mode){
    this.log = new Logger();
    this.log.add("info", "Starting..");
    this.ini = new IniFile("md_operator.conf");
    this.f = null;
    this.fileMask = null; 
    this.StringYYYYMMDD = 
    		getWorkDate(Integer.parseInt( this.ini.getString("settings","days_back","-1")));
    this.mode = mode;
  }
  
  public void close(){
	this.log.close();
	
  }

  protected start() {
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

  private clearFolder(String localDir) {
    File dir = new File(localDir);
    if (!dir.exists())
      dir.mkdir();
    File[] files = dir.listFiles();
    for (File file: files)
      file.delete();
  }
   
  private ArrayList getFilesDB() {
    try{
       FileReader fr = new FileReader(this.mode+".txt");
       BufferedReader br = new BuffedReader(fr);       
       String strLine;
       ArrayList ar = new ArrayList();
       while ((strLine = br.readLine()) != null) {
          ar.add(strLine);
       }  
       return ar;
    } finally {
      if (fs != null) {
        fs.close;
      }
      if (br != null) {
        br.close;
      }       
    }   
  }
  protected downloadFiles(){
    try {
      String hostname = ini.getString(mode,"hostname");
      String username = ini.getString(mode,"username");
      String password = ini.getString(mode,"password");
      String remoteDir = ini.getString(mode,"remote_dir");
      String localDir = ini.getString(mode,"local_dir");
      f = new MyFTP(hostname, username, password);
      f.connect();
      log.info("Downloading files. Local directory " + localDir + "; Remote host parameters:" + username + "@" + hostname + ":" + remoteDir);

      f.downloadFiles(localDir, remoteDir, fileMask, getFilesDB());
    } finally {
        f.disconnect();
    }    
  }

  protected uploadFilesToArch(){
    try {
      String hostname = ini.getString("arch","hostname");
      String username = ini.getString("arch","username");
      String password = ini.getString("arch","password");
      String remoteDir = ini.getString("arch","remote_dir");
      f = new MyFTP(hostname, username, password);
      f.connect();
      if (!remoteDir.endsWith("/"))
        remoteDir = remoteDir + "/";
    
      log.info "Uploading files to ARCH. Remote host parameters:" + username + "@" + hostname + ":" + remoteDir;

      f.uploadFiles(localDir,
        remoteDir + StringYYYYMMDD.substring(0, 4) + "/" +
        StringYYYYMMDD.substring(4, 6), fileMask);
    } finally {
      f.disconnect();
    }  

  protected uploadFilesToMD(){  
    try {
      String hostname = ini.getString("md","hostname");
      String username = ini.getString("md","username");
      String password = ini.getString("md","password");
      String remoteDir = ini.getString("md","remote_dir"); 
      f = new MyFTP(hostname, username, password);
      f.connect();
      log.info "Uploading files to MD. Remote host parameters:" + username + "@" + hostname + ":" + remoteDir;
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



