package telecom.atyrau;

import java.io.FileNotFoundException;

public class CSR14Reader extends DelimitedReader{

    public CSR14Reader(String path, char delimiter) throws FileNotFoundException {
		super(path, delimiter);
		// TODO Auto-generated constructor stub
	}
    
	public final int sourceFileId  =0;
    public final int recordId	   =1;    
    public final int sourceGroupId =2;    
    public final int serviceDate   =3; 
    public final int device        =4;
    public final int detail        =5;
    public final int factor        =6;
    public final int serviceCount  =7;
    public final int serviceCount1 =8 ;
    public final int serviceCount2 =9;
    public final int serviceCount3 =10;
    public final int abonentId     =11;
    public final int debit         =12;
    public final int tdrGroupId    =13;
  }

