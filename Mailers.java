import Values.*;
import Requirements.*;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
*	Class implementation to allow e-mail sending.
*	IMPORTANT: Uses JavaMailAPI, make sure it is installed before running this class.
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
*/
class Mailers {

	/** Host and server authentication variables */
    private static final String SMTP_HOST_NAME = "eusos.esicm.org";
    private static final String SMTP_AUTH_USER = "dataquery@eusos.esicm.org";
    private static final String SMTP_AUTH_PWD  = "eu505";
	
	/** Signature to be included in the end of the message */
	private final String signature = "Yours sincerely,\n Vasco Ramos & Rui Moreno\n On behalf of the EUSOS study group.";
	/** Disclaimer */
	private final String disclaimer = "\n\nThis message and their attachments were machine generated, please answer to dataquery@eusos.esicm.org until January 15th";
	
	/**
	* Creates a secure session to send the emails.
	*/
    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
           String username = SMTP_AUTH_USER;
           String password = SMTP_AUTH_PWD;
           return new PasswordAuthentication(username, password);
        }
    }
	
	/**
	* Initializes mailing properties. Uses authentication.
	* @return	Server properties
	*/
	private Properties initProperties(){
		// Sets new mailing properties
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
		return props;
	}
	
	/**
	* Returns the bodypart as an attachment.
	* @param	filename	The attachment's directory
	* @return	E-mail attachment
	*/
	private BodyPart addFile(String filename)  throws Exception{
		BodyPart messageBodyPart = new MimeBodyPart();
		FileDataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		return messageBodyPart;
	}

	/**
	* Sends out emails to all doctors which added a patient to the ICU that has no match in the OR.
	* @param	database	Contains patients database
	* @param	emailDatabase	Contains doctor's information
	*/
	public void mailORMissingForms(TreeMap<String, FieldsBox> database, ArrayList<Email> emailDatabase) throws Exception{
		// Initialize properties
		Properties props = initProperties();
		// Server requires authentication
        Authenticator auth = new SMTPAuthenticator();
        Session mailSession = Session.getDefaultInstance(props, auth);
        Transport transport = mailSession.getTransport();
		for(FieldsBox patient : database.values()){
			MimeMessage message = new MimeMessage(mailSession);
			// Begin setting fields of the header.
			//Sets FROM
			message.setFrom(new InternetAddress("dataquery@eusos.esicm.org"));
			// Adds own email to track progress
			message.addRecipient(Message.RecipientType.BCC,new InternetAddress("dataquery@eusos.esicm.org"));
			// Sets DATE
			message.setSentDate(new Date());
			// Ends setting fields
			String idPatient = ((TextValue) patient.getValue("EUSOS_ID")).getID();
			// Starts Writting email
			String email = "Dear Dr.(s) ";
			// Writes doctors names and adds email address
			for(Email e : emailDatabase){
				String idDoctor = e.getID();
				if(idPatient.startsWith(idDoctor)){
					//Set Doctors Address
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(e.getEmail()));
					email += (e.getName() +", ");
				}
				else if(idDoctor.startsWith(idPatient.substring(0,3)) && idDoctor.endsWith("000")){
					//Set Country Coordinators Address;
					message.addRecipient(Message.RecipientType.CC,new InternetAddress(e.getEmail()));
				}
			}
			//Writing the message
			email += "\n";
			email += "\tThank you very much for your participation in the EUSOS study, organized by  the ESICM and the ESA. We are now at the stage of checking the consistency of the data, in order to be sure that analysis will proceed as planned.\n";
			email += "\tIn revising the data from your centre, we have found a few apparent inconsistencies in some of your EuSOS forms that we would kindly ask you to review.\n";
			email += "\n\t Please double check patient " + idPatient + " has its correct fields, this is an ICU patient which we found no OR form for\n";
			email += "\n\t If possible, we would kindly ask you to provide us with the missing form him. In any event we would like you to provide some feedback wherever possible\n";
			email += signature;
			email += disclaimer;
			//Set Subject and Message
			message.setSubject("EuSOS Data Cleaning - Patient with ID: " + idPatient);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(email);
			//Create File and Set it
			String attachmentPath = generateAttachment(patient, "Missing Patient");
			// Set the File
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			multipart.addBodyPart(addFile(attachmentPath));

			// Send the complete message parts
			message.setContent(multipart );
			
			//Sending the message
			transport.connect();
			transport.send(message);
			transport.close();
		}	
	}

	/**
	*	Sends out an email to all doctors who added a patient to the OR,
	*	said they were admited into the ICU but there is no matching form.
	*	@param	database	File containing the patients' database
	*	@param	emailDatabase	File containing the doctor's information
	*/
	public void mailCCMissingForms(TreeMap<String, FieldsBox> database, ArrayList<Email> emailDatabase) throws Exception{
		// Initialize properties
		Properties props = initProperties();
		// Server requires authentication
        Authenticator auth = new SMTPAuthenticator();
        Session mailSession = Session.getDefaultInstance(props, auth);
        Transport transport = mailSession.getTransport();
		// Create a default MimeMessage object.
		for(FieldsBox patient : database.values()){
			MimeMessage message = new MimeMessage(mailSession);
			// Begin setting fields of the header.
			//Sets FROM
			message.setFrom(new InternetAddress("dataquery@eusos.esicm.org"));
			// Adds own email to track progress
			message.addRecipient(Message.RecipientType.BCC,new InternetAddress("dataquery@eusos.esicm.org"));
			//Sets DATE
			message.setSentDate(new Date());
			String idPatient = ((TextValue) patient.getValue("EUSOS_ID")).getID();
			// Starts writing email
			String email = "Dear Dr.(s) ";
			// Writes doctors names and adds email address
			for(Email e : emailDatabase){
				String idDoctor = e.getID();
				if(idPatient.startsWith(idDoctor)){
					//Set Doctors Address
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(e.getEmail()));
					email += (e.getName() +", ");
				}
				else if(idDoctor.startsWith(idPatient.substring(0,3)) && idDoctor.endsWith("000")){
					//Set Country Coordinators Address
					message.addRecipient(Message.RecipientType.CC,new InternetAddress(e.getEmail()));
				}
			}
			//Writing the message
			email += "\n";
			email += "\tThank you very much for your participation in the EUSOS study, organized by  the ESICM and the ESA. We are now at the stage of checking the consistency of the data, in order to be sure that analysis will proceed as planned.\n";
			email += "\tIn revising the data from your centre, we have found a few apparent inconsistencies in some of your EuSOS forms that we would kindly ask you to review.\n";
			email += "\n\t Please double check patient " + idPatient + " has its correct fields, this is an OR patient supposedly admitted into the ICU which didn't have a matching form\n";
			email += "\n\t If possible, we would kindly ask you to provide us with the missing form him. In any event we would like you to provide some feedback wherever possible.\n";
			email += signature;
			email += disclaimer;
			//Set Subject and Message
			message.setSubject("EuSOS Data Cleaning - Patient with ID: " + idPatient);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(email);
			//Create File and Set it
			String attachmentPath = generateAttachment(patient, "Missing Form");
			// Set the File
			Multipart multipart = new MimeMultipart();
			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			multipart.addBodyPart(addFile(attachmentPath));

			// Send the complete message parts
			message.setContent(multipart );
			
			//Sending the message
			transport.connect();
			transport.send(message);
			transport.close();
		}
	}
	
	/**
	* Sends out an email to all doctors who have the "survival to hospital discharge" variable missing
	* @param	database	File containing the patients' database
	* @param	emailDatabase	File containing thedoctor's information
	*/
	public void mailMissingSurvivalPatients(TreeMap<String, FieldsBox> database, ArrayList<Email> emailDatabase) throws Exception{
		// Initialize properties
		Properties props = initProperties();
		// Server requires authentication
        Authenticator auth = new SMTPAuthenticator();
        Session mailSession = Session.getDefaultInstance(props, auth);
        Transport transport = mailSession.getTransport();
		// Create a default MimeMessage object.
			
		for(FieldsBox patient : database.values()){
			BooleanValue admited = ((BooleanValue) patient.getValue("SURV_HOSP_DISCH"));
			if(admited.getBoolean() == null){
				MimeMessage message = new MimeMessage(mailSession);
				// Begin setting fields of the header.
				//Sets FROM
				message.setFrom(new InternetAddress("dataquery@eusos.esicm.org"));
				//Sets own address to track progress
				message.addRecipient(Message.RecipientType.BCC,new InternetAddress("dataquery@eusos.esicm.org"));
				//Sets DATE
				message.setSentDate(new Date());
				String idPatient = ((TextValue) patient.getValue("EUSOS_ID")).getID();
				String email = "Dear Dr.(s) ";
				// Writes doctors names and adds email address
				for(Email e : emailDatabase){
					String idDoctor = e.getID();
					if(idPatient.startsWith(idDoctor)){
						//Set Doctors Address
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(e.getEmail()));
						email += (e.getName() +", ");
					}
					else if(idDoctor.startsWith(idPatient.substring(0,3)) && idDoctor.endsWith("000")){
						//Set Country Coordinators Address;
						message.addRecipient(Message.RecipientType.CC,new InternetAddress(e.getEmail()));
					}
				}
				//Writing the message
				email += "\n";
				email += "\tThank you very much for your participation in the EUSOS study, organized by  the ESICM and the ESA. We are now at the stage of checking the consistency of the data, in order to be sure that analysis will proceed as planned.\n";
				email += "\tIn revising the data from your centre, we have found a few apparent inconsistencies in some of your EuSOS forms that we would kindly ask you to review.\n";
				email += "\n\t Please confirm patient " + idPatient + " survival to hospital discharge.\n";
				email += "\n   Thank you for you comprehension.\n";
				email += signature;
				email += disclaimer;
				//Set Subject and Message
				message.setSubject("EuSOS Data Cleaning - Patient with ID: " + idPatient);
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(email);
				//Create File and Set it
				String attachmentPath = generateAttachment(patient, "Missing Survival");
				// Set the File
				Multipart multipart = new MimeMultipart();

				// Set text message part
				multipart.addBodyPart(messageBodyPart);

				// Part two is attachment
				multipart.addBodyPart(addFile(attachmentPath));

				// Send the complete message parts
				message.setContent(multipart );
				
				//Sending the message
				transport.connect();
				transport.send(message);
				transport.close();
			}
		}
	}
	
	/**
	* Checks if patient was discharged before admission.
	* @param	admitance	Date of admission
	* @param	discharge	Date of discharge
	*/
	public boolean dischargedBeforeAdmited(DateValue admitance, DateValue discharge){
		if(admitance.getMonth() > discharge.getMonth())
			return true;
		else if(admitance.getMonth() == discharge.getMonth() && admitance.getDay() > discharge.getDay())
			return true;
		else
			return false;
	}
	
	/**
	* Sends out an email to all doctors who have patients that either have the date of discharge or admitance
	* missing, or their patient was discharged before the admitance date.
	* @param	database	File containing the patients' database
	* @param	emailDatabase	File containing the  doctor's information
	*/
	public void mailWrongDatePatients(TreeMap<String, FieldsBox> database, ArrayList<Email> emailDatabase) throws Exception{
		// Initialize properties
		Properties props = initProperties();
		// Server requires authentication
        Authenticator auth = new SMTPAuthenticator();
        Session mailSession = Session.getDefaultInstance(props, auth);
        Transport transport = mailSession.getTransport();
		// Create a default MimeMessage object.
			
		for(FieldsBox patient : database.values()){
			DateValue admitanceDate = ((DateValue) patient.getValue("DATE_CC_ADMIT"));
			DateValue dischargeDate = ((DateValue) patient.getValue("DATE_CRIT_DISCH"));
			String reason = "";
			if(dischargeDate.toString().equals("31-11-2")){
				reason += " is missing the discharge date, could you please confirm it.\n";
			}
			else if(admitanceDate.toString().equals("31-11-2")){
				reason += " is missing the admitance date, could you please confirm it.\n";
			}
			else if(dischargedBeforeAdmited(admitanceDate, dischargeDate)){
				reason += " was discharged before he was admitted, could you please clarify that.\n";
			}
			if(reason.equals("")){}
			else{
				MimeMessage message = new MimeMessage(mailSession);
				// Set fields of the header.
				message.setFrom(new InternetAddress("dataquery@eusos.esicm.org"));
				message.addRecipient(Message.RecipientType.BCC,new InternetAddress("dataquery@eusos.esicm.org"));
				message.setSentDate(new Date());
				String idPatient = ((TextValue) patient.getValue("EUSOS_ID")).getID();
				String email = "Dear Dr.(s) ";
				for(Email e : emailDatabase){
					String idDoctor = e.getID();
					if(idPatient.startsWith(idDoctor)){
						//Set Doctors Address
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(e.getEmail()));
						email += (e.getName() +", ");
					}
					else if(idDoctor.startsWith(idPatient.substring(0,3)) && idDoctor.endsWith("000")){
						//Set Country Coordinators Address;
						message.addRecipient(Message.RecipientType.CC,new InternetAddress(e.getEmail()));
					}
				}
				//Writing the message
				email += "\n";
				email += "\tThank you very much for your participation in the EUSOS study, organized by  the ESICM and the ESA. We are now at the stage of checking the consistency of the data, in order to be sure that analysis will proceed as planned.\n";
				email += "\tIn revising the data from your centre, we have found a few apparent inconsistencies in some of your EuSOS forms that we would kindly ask you to review.\n";
				email += "\n\t Please check patient with: " + idPatient + reason;
				email += "\n   Thank you for you comprehension.\n";
				email += signature;
				email += disclaimer;
				//Set Subject and Message
				message.setSubject("EuSOS Data Cleaning - Patient with ID: " + idPatient);
				BodyPart messageBodyPart = new MimeBodyPart();
				
				messageBodyPart.setText(email);
				//Create File and Set it
				String attachmentPath = generateAttachment(patient, "Invalid Date");
				// Set the File
				Multipart multipart = new MimeMultipart();

				// Set text message part
				multipart.addBodyPart(messageBodyPart);

				// Part two is attachment
				multipart.addBodyPart(addFile(attachmentPath));

				// Send the complete message parts
				message.setContent(multipart );
				
				//Sending the message
				transport.connect();
				transport.send(message);
				transport.close();
			}	
			
		}
	}
	
	/**
	* Sends out an email to all doctors who have duplicate patients
	* NOTE: Was abandoned due to change of requirements, and as such is NOT tested and 
	* currently NOT being used.
	* @param	database	File containing the patients'database
	* @param	emailDatabase	File containing the doctor's information
	*/
	public void mailDuplicatePatients(TreeMap<String, FieldsBox> database, ArrayList<Email> emailDatabase) throws Exception{
		// Initialize properties
		Properties props = initProperties();
		// Server requires authentication
        Authenticator auth = new SMTPAuthenticator();
        Session mailSession = Session.getDefaultInstance(props, auth);
        Transport transport = mailSession.getTransport();
		// Create a default MimeMessage object.
		
		for(FieldsBox patient : database.values()){
			MimeMessage message = new MimeMessage(mailSession);
			// Set fields of the header.
			message.setFrom(new InternetAddress("dataquery@eusos.esicm.org"));
			message.addRecipient(Message.RecipientType.BCC,new InternetAddress("dataquery@eusos.esicm.org"));
			message.setSentDate(new Date());
			String idPatient = ((TextValue) patient.getValue("EUSOS_ID")).getID();
			String email = "Dear Dr.(s) ";
			System.out.println("Trying to find: " + idPatient);
				for(Email e : emailDatabase){
					String idDoctor = e.getID();
					if(idPatient.startsWith(idDoctor)){
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(e.getEmail()));
						email += (e.getName() +", ");
					}
					else if(idDoctor.startsWith(idPatient.substring(0,3)) && idDoctor.endsWith("000")){
						message.addRecipient(Message.RecipientType.CC,new InternetAddress(e.getEmail()));
					}
				}
			
			
			//Writing the message
			email += "\n";
			email += "\tThank you very much for your participation in the EUSOS study, organized by  the ESICM and the ESA. We are now at the stage of checking the consistency of the data, in order to be sure that analysis will proceed as planned.\n";
			email += "\tIn revising the data from your centre, we have found a few apparent inconsistencies in some of your EuSOS forms that we would kindly ask you to review.\n";
			email += "\n\t Patient: " + idPatient + " is reported as Duplicate. Check the attached file.\n";
			email += "\n\t We would like you to double-check that your centre registered these patients to the database, and, if possible, to provide us with the right EUSOS_ID. In any event we would like you to provide some feedback wherever possible.\n";
			email += signature;
			//Set Subject and Message
			message.setSubject("EuSOS Data Cleaning - Patient with ID: " + idPatient);
			BodyPart messageBodyPart = new MimeBodyPart();
			
			messageBodyPart.setText(email);
			//Create File and Set it
			/*	## In order to finish the method "generateAttachmentDuplicatePatients(patient)" ##
				## Must be implemented. ##
				
			String attachmentPath = generateAttachmentDuplicatePatients(patient);
			// Set the File
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			multipart.addBodyPart(addFile(attachmentPath));

			// Send the complete message parts
			message.setContent(multipart );
			
			//Sending the message
			transport.connect();
			transport.send(message);
			transport.close();
			System.out.println("Done");
			*/
		}
	}
	
	/**
	* Sends out an email to all doctors who have patients with age/sex incongruencies.
	* NOTE: Was abandoned due to change of requirements, and as such is NOT tested and 
	* currently NOT being used.
	* @param 	database 		File containing the  patients database
	* @param 	emailDatabase 	File containing the  doctor's information
	*/
	public void mailAgeSexMismatchPatients(TreeMap<String, FieldsBox> database, ArrayList<Email> emailDatabase) throws Exception{
		// Initialize properties
		Properties props = initProperties();
		// Server requires authentication
        Authenticator auth = new SMTPAuthenticator();
        Session mailSession = Session.getDefaultInstance(props, auth);
        Transport transport = mailSession.getTransport();
		// Create a default MimeMessage object.
		
		for(FieldsBox patient : database.values()){
			MimeMessage message = new MimeMessage(mailSession);
			// Set fields of the header.
			message.setFrom(new InternetAddress("dataquery@eusos.esicm.org"));
			message.addRecipient(Message.RecipientType.BCC,new InternetAddress("dataquery@eusos.esicm.org"));
			message.setSentDate(new Date());
			String idPatient = ((TextValue) patient.getValue("EUSOS_ID")).getID();
			String email = "Dear Dr.(s) ";
			System.out.println("Trying to find: " + idPatient);
				for(Email e : emailDatabase){
					String idDoctor = e.getID();
					if(idPatient.startsWith(idDoctor)){
						//System.out.println("Found the doctor: " + e.getEmail() + " - "  + idDoctor + " " + e.getName()); 
						//Set Doctors Address
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(e.getEmail()));
						email += (e.getName() +", ");
					}
					else if(idDoctor.startsWith(idPatient.substring(0,3)) && idDoctor.endsWith("000")){
						//Set Country Coordinators Address
						System.out.println("Found the coordinator: " + e.getEmail());
						//InternetAddress add = new InternetAddress(e.getEmail());
						message.addRecipient(Message.RecipientType.CC,new InternetAddress(e.getEmail()));
					}
				}
			
			
			//Writing the message
			email += "\n";
			email += "\tThank you very much for your participation in the EUSOS study, organized by  the ESICM and the ESA. We are now at the stage of checking the consistency of the data, in order to be sure that analysis will proceed as planned.\n";
			email += "\tIn revising the data from your centre, we have found a few apparent inconsistencies in some of your EuSOS forms that we would kindly ask you to review.\n";
			email += "\n\t Patient: " + idPatient + " Sex and Age dont match with the other form. Check the attached file.\n";
			email += "\n\t We would like you to double-check that your centre registered these patients to the database, and, if possible, to provide us with the correct data. In any event we would like you to provide some feedback wherever possible.\n";
			email += signature;
			email += disclaimer;
			//Set Subject and Message
			message.setSubject("EuSOS Data Cleaning - Patient with ID: " + idPatient);
			BodyPart messageBodyPart = new MimeBodyPart();
			
			messageBodyPart.setText(email);
			//Create File and Set it
			/*	## In order to finish the method "generateAttachmentAgeSexMismatch(patient) ##
				## Must be implemented. ##
			String attachmentPath = generateAttachmentAgeSexMismatch(patient);
			// Set the File
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			multipart.addBodyPart(addFile(attachmentPath));

			// Send the complete message parts
			message.setContent(multipart );
			
			//Sending the message
			transport.connect();
			transport.send(message);
			transport.close();
			System.out.println("Done");*/
		}
	}
	
	
	
	/**
	* Generates an attachment specifying the patient's data.
	* @param	patient	Patient's Data
	* @param	reason		Reason for the attachment being generated
	* @return	directory	Directory of the output file
	*/
	public String generateAttachment(FieldsBox patient, String reason){
		// Verify type of database
		String databaseName = null;
		if(patient.getSize() < 60)
			databaseName = "Operating Room Database";
		else
			databaseName = "Intensive Care Unit Database";
		String finalFileName = "";
		try{
			// Open buffers
			File file;
			boolean exist;
			int fileNumber = 1;
			finalFileName = databaseName + " - " + ((TextValue) patient.getValue("EUSOS_ID")).getID() + " - " +  reason + ".rtf";
			file = new File("Outputs/Attachments/" + finalFileName);
			exist = file.createNewFile();
			FileWriter fstream = new FileWriter("Outputs/Attachments/" + finalFileName);
			BufferedWriter out = new BufferedWriter(fstream);

			// Write content of the attachment
			String patientId = ((TextValue) patient.getValue("EUSOS_ID")).getID();
			String text = databaseName + "\n";
			out.write(text, 0, text.length());
			int age = ((NumberValue)patient.getValue("AGE")).getAsInt();
			String sex = (((NumberValue)patient.getValue("SEX")).getAsInt() == 10 ? "MALE" : "FEMALE");
			text = "Patient " + patientId + ", a " + age + " year old " + sex + " is this the right patient?.\n";
			out.write(text, 0, text.length());
			text = "Patient Data:\n\n";
			out.write(text, 0, text.length());
			String spaces = "..............................";
			for(String fieldName : patient.getFieldNames()){
				text = fieldName + ":" + spaces.substring(fieldName.length()) + patient.getValue(fieldName) + "\n";
				out.write(text, 0, text.length());
			}
				
			out.close();
		} catch(IOException ioe){
			System.out.println("[generatePatientAttachmentDuplicatePatients]: Error creating file " + finalFileName);
		}
		return "Outputs/Attachments/" + finalFileName;
	}
	
	/**
	*	Generates e-mails and attachments and sends them to the respective doctors and coordinators.
	*	Please be careful in the execution of this class, as it will most likely
	*	spam the doctors involved in the study.
	*/
    public static void main(String[] args) throws Exception{
		Cleaners cleaner = new Cleaners();
		Readers reader  = new Readers();
		Fixers fixer = new Fixers();
		Mailers mailer = new Mailers();
		
		System.out.println("Welcome.");
		System.out.println("Reading Operating Room Utils...");
		TreeMap<String,Requirement> ORTypes = reader.readOperatingRoomUtils(new File("OperatingRoomUtil.csv"));
		System.out.println("Done");
		System.out.println("Reading Critical Care Room Utils...");
		TreeMap<String,Requirement> ICUTypes = reader.readCriticalCareRoomUtils(new File("CriticalCareRoomUtil.csv"));
		System.out.println("Done");
		System.out.println("Now reading CCR Pre-Cleaning database...");
		TreeMap<String, FieldsBox> ICUDatabase = reader.readDatabaseCriticalCareRoom(new File("Outputs/V2.0/ICU/Valid ICU 2.0.csv"), ICUTypes);
		System.out.println("Done");
		System.out.println("Now reading OR Pre-Cleaning database...");
		TreeMap<String, FieldsBox> ORDatabase = reader.readDatabaseOperatingRoom(new File("Outputs/V2.0/OR/Valid OR Patients 2.0.csv"), ORTypes);
		System.out.println("Done");
		System.out.println("Reading Mails");
		ArrayList<Email> mailDatabase = reader.readEmails(new File("sending mails.csv"));
		System.out.println("Done");
		//################################### ATTENTION #################################
		// These two lines must be un-commented to allow sending the generated emails
		// Please be careful in the execution of this class, as it will spam the doctors
		// involved in the study.
		//###############################################################################
		//mailer.mailWrongDatePatients(ICUDatabase, mailDatabase);
		//mailer.mailMissingSurvivalPatients(ORDatabase,mailDatabase);
    }
	
}