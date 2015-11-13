
package main.java.nl.tue.ieis.is.correlation.utility;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;


public class MailUtil {
	
public static void sendMail(User user) throws MalformedURLException, EmailException {  
	   
	   // load your HTML email template
	   String htmlEmailTemplate = "<html><body><p>Please confirm your email address.</p></body></html>";
	   
	   // define you base URL to resolve relative resource locations
	   URL url = new URL("http://www.apache.org");

	   // create the email message
	   HtmlEmail email = new ImageHtmlEmail();
	   ((ImageHtmlEmail) email).setDataSourceResolver(new DataSourceUrlResolver(url));
	   email.setSmtpPort(465);
	   email.setAuthenticator(new SimpleAuthentication("shaya.pourmirza@gmail.com","Shapoor13681989!"));
	   email.setDebug(false);
	   email.setHostName("smtp.gmail.com");
	   email.setStartTLSEnabled(true);
	   email.setFrom("shaya.pourmirza@gmail.com", "Correlation Miner Team");
	   email.setSubject("[CorrelationMiner] Email Confirmation");
	   email.addTo(user.getEmail(), user.getFirstName() + " " + user.getLastName());
	   email.setTLS(true);
	   email.setSSL(true);
	   email.setHtmlMsg(htmlEmailTemplate);
	   email.setTextMsg("Your email client does not support HTML messages");

	   email.send();
   }
}