package telecom.atyrau;
public class TextReader {
  private FileReader fr;
  private BufferedReader buff;
  private String[] items;
 
  public TextReader(String path){
    fr = new FileReader(path);
    buff = new BuffedReader(fr);    
  }
  
  public close(){
    if (fr != null) {
      fr.close;
    }
    if (buff != null) {
      buff.close;
    }  
  }
      
  protected String getItem(int index){    
    String str;
    if (index >= 0) && (index < items.length) {
      str = items[index];     
    } else {
      str = "";
    }
    return = str;
  }
  
  protected int getItemCount(){
    return items.length;
  }

  public boolean nextLine(){
    boolean isOk = false;
    int n = 0;    

    String thisLine = br.readLine();

    if (thisLine != null) {
      n = parseLine(thisLine);
      isOk = True;
    }
  }
  
  private abstract parseLine(String line);
  
  private clearItems{
  	this.items = null;
  }

  private putItem(int index, String item){
  	this.with{
      if (items == null)
        items = new String[formatLength];
      items[index] = item;
  	}
  }
   
  //-----------DelimitedReader
  
  public class DelimitedReader extends TextReader {
    private String delimiter;
    public int formatLength;
    
    public DelimitedReader(String fileName, char delimiter){
      super(String path);
      this.delimiter = delimiter;
    }

    private int parseLine(String line) {
      StringBuffer s = new StringBuffer(line);
      int idx = 0, p = 0;
      
      clearItems; //Проверить this

      while (!s.toString().equals("")) {
        p = s.indexOf(";");
        if (p == -1) 
          p = s.length();  
        putItem(res, s.substring(0, p)); //"test;one"           
        s.delete(0, p + 1);       
        idx++;
      }
      return idx;
    }  
  }  

  public class Csr14Reader extends DelimitedReader{
    public final sourceFileId  =0;
    public final recordId	   =1;    
    public final sourceGroupId =2;    
    public final serviceDate   =3; 
    public final device        =4;
    public final detail        =5;
    public final factor        =6;
    public final serviceCount  =7;
    public final serviceCount1 =8 ;
    public final serviceCount2 =9;
    public final serviceCount3 =10;
    public final abonentId     =11;
    public final debit         =12;
    public final tdrGroupId    =13;
  }

  public class SipReader extends DelimitedReader{
    public final cplSourceId            =14;
    public final cplSourceTypeId        =15;
    public final sipUseridO          	=16;
    public final answerTime          	=17;
    public final answerIndicator     	=18;
    public final releaseTime  		 	=19;
    public final userId 			 	=20;
    public final userNumber 		 	=21;
    public final groupNumber 		 	=22;
    public final callingNumber 		 	=23;
    public final calledNumber 		 	=24;
    public final dialedDigits 		 	=25;
    public final callCategory 	     	=26;
    public final route 				 	=27;
    public final serviceProvider     	=28;
    public final networkCallType     	=29;
    public final networkTranslatedNumbe =30;
  }	


 




} 
  
