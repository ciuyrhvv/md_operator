package telecom;

import java.io.FileNotFoundException;

public class SipReader extends DelimitedReader{

	public SipReader(String path, char delimiter) throws FileNotFoundException {
		super(path, delimiter);
	}


  public final int cplSourceId            =14;
  public final int cplSourceTypeId        =15;
  public final int sipUseridO          	=16;
  public final int answerTime          	=17;
  public final int answerIndicator     	=18;
  public final int releaseTime  		 	=19;
  public final int userId 			 	=20;
  public final int userNumber 		 	=21;
  public final int groupNumber 		 	=22;
  public final int callingNumber 		 	=23;
  public final int calledNumber 		 	=24;
  public final int dialedDigits 		 	=25;
  public final int callCategory 	     	=26;
  public final int route 				 	=27;
  public final int serviceProvider     	=28;
  public final int networkCallType     	=29;
  public final int networkTranslatedNumbe =30;
}	
