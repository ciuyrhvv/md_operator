package telecom;
// File Name SendEmail.java

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Mailer {
   private String to;
   private String from;
   private String host;
   private String subj;
   private String mess;


   public Mailer(String to, String from, String host, String subj, String mess){
     this.to = to;
     this.from = from;
     this.host = host;
     this.subj = subj;
     this.mess = mess;

   }

   public void send() throws MessagingException {    

      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty("mail.smtp.host", this.host);
      properties.setProperty("mail.smtp.port", "25");
      //properties.setProperty("mail.debug", "true");

      // Get the default Session object.
      Session session = Session.getDefaultInstance(properties);

      try {
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(this.from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(this.to));

         // Set Subject: header field
         message.setSubject(this.subj);

         // Now set the actual message
         message.setText(this.mess);

         // Send message
         Transport.send(message);
         
      }catch (MessagingException mex) {
         mex.printStackTrace();
         throw mex;
      }
   }
}