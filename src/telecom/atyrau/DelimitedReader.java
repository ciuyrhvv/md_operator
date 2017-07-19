package telecom.atyrau;

import java.io.FileNotFoundException;

//-----------DelimitedReader

public class DelimitedReader extends TextReader {
  private char delimiter;
  
  public DelimitedReader(String path, char delimiter) throws FileNotFoundException{
    super(path);
    this.setDelimiter(delimiter);
  }

  protected int parseLine(String line) {
    StringBuffer s = new StringBuffer(line);
    int idx = 0, p = 0;
    
    clearItems(); //Проверить this

    while (!s.toString().equals("")) {
      p = s.indexOf(";");
      if (p == -1) 
        p = s.length();  
      putItem(idx, s.substring(0, p)); //"test;one"           
      s.delete(0, p + 1);       
      idx++;
    }
    return idx;
  }

public char getDelimiter() {
	return delimiter;
}

public void setDelimiter(char delimiter) {
	this.delimiter = delimiter;
}  
}  
