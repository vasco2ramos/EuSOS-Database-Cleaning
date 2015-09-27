import java.io.*;
import java.util.*;

import Values.*;
import Requirements.*;

/**
*	Class containing methods that read the different databases and check for input errors,
*	documenting them and creating a new, improved version of the database.
*	@author	Andre Alonso
*	@author	Vasco Ramos
*/

public class Cleaners{

	/**
	* Character field separator, we will use ';'.
	*/
	static private String _separator = ";";
	
	/**
	* Determines where the first column is to be read, since some columns are not part of the CRF.
	*/
	static private int _firstColumnOR = 7;
	static private int _firstColumnCC = 6;
	
	/**
	* Number of columns to be read in each database for the operating room and critical care respectively
	*/
	static private int _numberOfColumnsOR = 50;
	static private int _numberOfColumnsCC = 150;
	
	/**
	*	Reads and checks the database field names, removing the "_A1" suffix.
	*	@param	inputFile	Directory of the database's file
	*	@param	firstColumn	Number of the first column where to start analyzing
	*	@param	numberOfColumns	Number of Columns to analyze
	*	@param	inconsistencyFile	Name of the inconsistency file to be saved
	*	@param	databaseFile	Name of the new, corrected database to be saved
	*/
	public void correctFieldNames(File inputFile, int firstColumn, int numberOfColumns, String inconsistencyFile, String databaseFile){		

		try {
			// Creating the file writers
			FileWriter fstream = new FileWriter("Outputs/" + databaseFile + ".csv");
			BufferedWriter out = new BufferedWriter(fstream);
			
			FileWriter fstreamLog = new FileWriter("Outputs/" + inconsistencyFile + ".rtf");
			BufferedWriter outLog = new BufferedWriter(fstreamLog);
			
			String text;
			// Creating the Buffered reader for the input
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				// First line must have variable names, we assume there are no errors there
				String line = null;
				line = input.readLine();
				String[] firstLine = line.split(_separator);
				// Reading inputFile and correcting
				for(int i=0; i < (numberOfColumns + firstColumn);i++){
					String fieldName = firstLine[i];
					if(fieldName.endsWith("_A1")){
						int indexOf = fieldName.lastIndexOf("_A1");
						fieldName = fieldName.substring(0, indexOf);
						text = "[Line 1, Column " + i + "]: Replaced " + fieldName + "_A1 for " + fieldName+".\n";
						outLog.write(text, 0, text.length());
					}
					text = fieldName+";";
					out.write(text, 0, text.length()); 
				}
				text = "\n";
				out.write(text, 0, text.length()); 
				while ((line = input.readLine()) != null){ 
					text = line + "\n";
					out.write(text, 0, text.length()); 
				}
			}
			finally {
				out.close();
				outLog.close();
				input.close();
			}
		}
		catch (IOException ex){
		  System.out.println("[correctFieldNames] ERROR: input/output error.");
		  ex.printStackTrace();
		}
	}
	/**
	* Reads and checks the database for lines with missing values at the end of the line and adds an empty space.
	*	@param	inputFile	Directory of the database's file
	*	@param	firstColumn	Number of the first column where to start analyzing
	*	@param	numberOfColumns	Number of Columns to analyze
	*	@param	inconsistencyFile	Name of the inconsistency file to be saved
	*	@param	databaseFile	Name of the new, corrected database to be saved
	*/
	public void cleanMissingEndValues(File inputFile, int firstColumn, int numberOfColumns, String inconsistencyFile, String databaseFile){	//, TreeMap<String, Requirement> typesMap	
		
		try {
			// Creating the file writers
			FileWriter fstream = new FileWriter("Outputs/" + databaseFile + ".csv");
			BufferedWriter out = new BufferedWriter(fstream);
			
			FileWriter fstreamLog = new FileWriter("Outputs/" + inconsistencyFile + ".rtf");
			BufferedWriter outLog = new BufferedWriter(fstreamLog);
			
			String text;
			
			//Buffering reads one line at a time
			//FileReader assumes there are no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				
				// First line must have variable names, we assume there are no errors there
				String line = null;
				line = input.readLine();
				text = line + "\n";
				out.write(text, 0, text.length());
				int lineNumber = 1;
				while ((line = input.readLine()) != null){
					lineNumber++;
					String[] lineValues = line.split(_separator);
					// Checking types and creating correspondent class along the line					
					for(int i = 0; i < (numberOfColumns + firstColumn); i++){
						try{
							// Try to force an out of bounds exception,
							// meaning there are more columns than values
							text = lineValues[i];
							if(i != (numberOfColumns + firstColumn - 1))
								text += ";";
							out.write(text, 0, text.length());
						} catch (ArrayIndexOutOfBoundsException exc) {
							// As requested, all the missing values will be filled
							// with the "-1" value
							text = "-1";
							if(i != (numberOfColumns + firstColumn - 1))
								text = ";";
							out.write(text, 0, text.length());
							text = "[Line " + lineNumber + ", Column " + i + "]: Found an empty value at the end of the line.\n";
							outLog.write(text, 0, text.length());
						}
					}
					text = "\n";
					out.write(text, 0, text.length());
				}
			}
			finally {
				out.close();
				outLog.close();
				input.close();
			}
		}
		catch (IOException ex){
		  System.out.println("[cleanMissingEndValues] ERROR: input/output error.");
		  ex.printStackTrace();
		}
	}
	/**
	* Reads and checks the database for lines with missing values and fills the space with "-1"
	*	@param	inputFile	Directory of the database's file
	*	@param	firstColumn	Number of the first column where to start analyzing
	*	@param	numberOfColumns	Number of Columns to analyze
	*	@param	inconsistencyFile	Name of the inconsistency file to be saved
	*	@param	databaseFile	Name of the new, corrected database to be saved
	*/
	public void cleanAddMissingValues(File inputFile, int firstColumn, int numberOfColumns, String inconsistencyFile, String databaseFile){
		
		try {
			// Creating the file writers
			FileWriter fstream = new FileWriter("Outputs/" + databaseFile + ".csv");
			BufferedWriter out = new BufferedWriter(fstream);
			
			FileWriter fstreamLog = new FileWriter("Outputs/" + inconsistencyFile + ".rtf");
			BufferedWriter outLog = new BufferedWriter(fstreamLog);
			
			String text;
			
			//Buffering reads one line at a time
			//FileReader assumes there are no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				
				// First line must have variable names, we assume there are no errors there
				String line = null;
				line = input.readLine();
				text = line + "\n";
				out.write(text, 0, text.length());
				int lineNumber = 1;
				while ((line = input.readLine()) != null){
					lineNumber++;
					String field = "";
					String[] lineValues = line.split(_separator);
					// Checking types and creating correspondent class along the line					
					for(int i = 0; i < (numberOfColumns + firstColumn); i++){
						try{
							// Try to force an out of bounds exception,
							// meaning there are more columns than values
							field = lineValues[i];
							if(field.equals("")){
								text = 	"[Line " + lineNumber + ", Column " + i + "]: Found an empty value, replaced with \"-1\".\n";
								outLog.write(text, 0, text.length());
								text = "-1;";
							} else 
								text = field + ";";
							out.write(text, 0, text.length());
						} catch (ArrayIndexOutOfBoundsException exc) {
							System.out.println("[Line " + lineNumber + ", Column " + i + "]");
							System.out.println("The missing values cleaner is not prepared to deal with incomplete lines");
							System.out.println("Please run the Missing End Values cleaner beforehand");
						}
					}
					text = "\n";
					out.write(text, 0, text.length());
				}
			}
			finally {
				out.close();
				outLog.close();
				input.close();
			}
		}
		catch (IOException ex){
		  System.out.println("[cleanAddMissingValues] ERROR: input/output error.");
		  ex.printStackTrace();
		}
	}	
	/**
	* Reads and checks the database for spaces inside numeric fields.
	*	@param	inputFile	Directory of the database's file
	*	@param	firstColumn	Number of the first column where to start analyzing
	*	@param	numberOfColumns	Number of Columns to analyze
	*	@param	inconsistencyFile	Name of the inconsistency file to be saved
	*	@param	databaseFile	Name of the new, corrected database to be saved
	*/
	public void cleanNumberSpaces(File inputFile, int firstColumn, int numberOfColumns, String inconsistencyFile, String databaseFile, TreeMap<String, Requirement> typesMap){	
		try {
			// Creating the file writers
			FileWriter fstream = new FileWriter("Outputs/" + databaseFile + ".csv");
			BufferedWriter out = new BufferedWriter(fstream);
			
			FileWriter fstreamLog = new FileWriter("Outputs/" + inconsistencyFile + ".rtf");
			BufferedWriter outLog = new BufferedWriter(fstreamLog);
			
			String text;
			
			// Variable in which the column names will be stored
			ArrayList<String> names = new ArrayList<String>();
			
			//Buffering reads one line at a time
			//FileReader assumes there are no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				// First line must have variable names, we assume there are no errors there
				String line = input.readLine();				
				String[] firstLine = line.split(_separator);
				for(int i = 0; i < (numberOfColumns + firstColumn); i++){
					names.add(firstLine[i]);
					text = firstLine[i] + ";";
					out.write(text, 0, text.length());
				}
				text = "\n";
				out.write(text, 0, text.length());
				int lineNumber = 1;
				while ((line = input.readLine()) != null){
					lineNumber++;
					String field = "";
					String removedSpaces = "";
					String[] lineValues = line.split(_separator);
					// Checking types and creating correspondent class along the line
					for(int i = 0; i < firstColumn; i++){
						text = lineValues[i] + ";";
						out.write(text, 0, text.length());
					}
					
					for(int i = firstColumn; i < (numberOfColumns + firstColumn); i++){
						try{
							field = lineValues[i];
							// Fetch the name of the field
							String fieldName = names.get(i);
							// Fetch the type of the field
							Requirement type = typesMap.get(fieldName);
							// Fetches which class should be used to store each value
							String classType = type.getClassName();
							if(classType.equals("Number") || classType.equals("Range") || classType.equals("ValueArray")) {
								removedSpaces = field.replace(" ","");
								if(!removedSpaces.equals(field)){
									text = "[Line " + lineNumber + ", Column " + i + "]: Found and removed a space inside number \"" + field + "\".\n";
									outLog.write(text, 0, text.length());
									field = removedSpaces;
								}
							}
							text = field + ";";
							out.write(text, 0, text.length());

						} catch (ArrayIndexOutOfBoundsException exc) {
							System.out.println("[Line " + lineNumber + ", Column " + i + "]");
							System.out.println("The number spaces cleaner is not prepared to deal with incomplete lines");
							System.out.println("Please run the Missing End Values cleaner beforehand");
						}
					}
					text = "\n";
					out.write(text, 0, text.length());
				}
			}
			finally {
				out.close();
				outLog.close();
				input.close();
			}
		}
		catch (IOException ex){
		  System.out.println("[cleanNumberSpaces] ERROR: input/output error.");
		  ex.printStackTrace();
		}
	}
	/**
	* Reads and checks the atabase for inconsistent characters inside numbers.
	*	@param	inputFile	Directory of the database's file
	*	@param	firstColumn	Number of the first column where to start analyzing
	*	@param	numberOfColumns	Number of Columns to analyze
	*	@param	inconsistencyFile	Name of the inconsistency file to be saved
	*	@param	databaseFile	Name of the new, corrected database to be saved
	*/
	public void cleanNumberFormatting(File inputFile, int firstColumn, int numberOfColumns, String inconsistencyFile, String databaseFile, TreeMap<String, Requirement> typesMap){	
		try {
			// Creating the file writers
			FileWriter fstream = new FileWriter("Outputs/" + databaseFile + ".csv");
			BufferedWriter out = new BufferedWriter(fstream);
			
			FileWriter fstreamLog = new FileWriter("Outputs/" + inconsistencyFile + ".rtf");
			BufferedWriter outLog = new BufferedWriter(fstreamLog);
			
			String text;
			
			// Variable in which the column names will be stored
			ArrayList<String> names = new ArrayList<String>();
			
			//Buffering reads one line at a time
			//FileReader assumes there are no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				// First line must have variable names, we assume there are no errors there
				String line = input.readLine();				
				String[] firstLine = line.split(_separator);
				for(int i = 0; i < (numberOfColumns + firstColumn); i++){
					names.add(firstLine[i]);
					text = firstLine[i] + ";";
					out.write(text, 0, text.length());
				}
				text = "\n";
				out.write(text, 0, text.length());
				int lineNumber = 1;
				while ((line = input.readLine()) != null){
					lineNumber++;
					String field = "";
					String replacedCommasField;
					float fieldDecimal;
					int fieldInteger;
					String[] lineValues = line.split(_separator);
					// Checking types and creating correspondent class along the line
					for(int i = 0; i < firstColumn; i++){
						text = lineValues[i] + ";";
						out.write(text, 0, text.length());
					}
					
					for(int i = firstColumn; i < (numberOfColumns + firstColumn); i++){
						try{
							field = lineValues[i];
							// Fetch the name of the field
							String fieldName = names.get(i);
							// Fetch the type of the field
							Requirement type = typesMap.get(fieldName);
							// Fetches which class should be used to store each value
							String classType = type.getClassName();
							if(classType.equals("Number") || classType.equals("Range")) { //|| classType.equals("ValueArray")
								if(field.length() != 0){
									if(field.contains(".")) {
										// Decimal number
										fieldDecimal = Float.parseFloat(field);
										if(!field.equals(fieldDecimal + "")){
											text = "[Line " + lineNumber + ", Column " + i + "]: Corrected the format of \"" + field + "\"\t to \"" + fieldDecimal + "\".\n";
											outLog.write(text, 0, text.length());
											field = fieldDecimal + "";
										}
										// Integer number
									} else {
										fieldInteger = Integer.parseInt(field);
										if(!field.equals(fieldInteger + "")){
											text = "[Line " + lineNumber + ", Column " + i + "]: Corrected the format of \"" + field + "\"\t to \"" + fieldInteger + "\".\n";
											outLog.write(text, 0, text.length());
											field = fieldInteger + "";
										}
									}
								} else {
									// If we find a blank field, we have a strange character taking an empty space and we will fill it with -1
									field = "-1";
									text = "[Line " + lineNumber + ", Column " + i + "]: Found and removed a character taking up an empty number space.\n";
									outLog.write(text, 0, text.length());
								}
								//text = "[Line " + lineNumber + ", Column " + i + "]: Found and removed a space inside number \"" + field + "\".\n";
								//outLog.write(text, 0, text.length());
								//field = fieldNumber + "";
							}
							text = field + ";";
							out.write(text, 0, text.length());
						} catch (ArrayIndexOutOfBoundsException exc) {
							System.out.println("[Line " + lineNumber + ", Column " + i + "]");
							System.out.println("The number spaces cleaner is not prepared to deal with incomplete lines");
							System.out.println("Please run the Missing End Values cleaner beforehand");
						}
					}
					text = "\n";
					out.write(text, 0, text.length());
				}
			}
			finally {
				out.close();
				outLog.close();
				input.close();
			}
		}
		catch (IOException ex){
		  System.out.println("[cleanNumberFormatting] ERROR: input/output error.");
		  ex.printStackTrace();
		}
	}
	
//--------------------------------------------------------------------------------------------------------------------
//####################################################################################################################
//--------------------------------------------------------------------------------------------------------------------
	
	/**
	* ICU Valid Patients
	* Every patient admitied into the ICU should exist in the OR database. Age and Sex should match.
	*
	* @param ORDatabase Operating Room Database.
	* @param CCDatabase	Critical Care Room Database
	* @param ORTypesMap Utilitary file used to know each OR variable type.
	* @param CCTypesMap Utilitary file used to know each CCR variable type
	*/
	public void cleanICUPatients(TreeMap<String, FieldsBox> ORDatabase, TreeMap<String, FieldsBox> CCDatabase, TreeMap<String, Requirement> ORTypesMap, TreeMap<String, Requirement> CCTypesMap){		
		Writers writer = new Writers();
		// The Patients admited and crosschecked with the icu and not admited, we will not check these now
		TreeMap<String, FieldsBox> validPatients =  new TreeMap<String, FieldsBox>();
		// Patients admited into the ICU that don't exist in there
		TreeMap<String, FieldsBox> invalidPatients = new TreeMap<String, FieldsBox>();
		// Patients with wrong sex or age
		TreeMap<String, FieldsBox> sendmailPatients = new TreeMap<String, FieldsBox>();
		// The inconsistencies found will be stored here
		String inconsistencies = "";
		
		for(String patientID: CCDatabase.keySet()){
			if(ORDatabase.containsKey(patientID)){
				FieldsBox patient = ORDatabase.get(patientID);
				BooleanValue admited = (BooleanValue)patient.getValue("ADMIS_CRIT_CARE");
				if(admited.getBoolean())
					validPatients.put(patientID, CCDatabase.get(patientID));
				else{
					NumberValue age1 = (NumberValue)patient.getValue("AGE");
					NumberValue sex1 = (NumberValue)patient.getValue("SEX");
					FieldsBox icupatient = CCDatabase.get(patientID);
					NumberValue age2 = (NumberValue)icupatient.getValue("AGE");
					NumberValue sex2 = (NumberValue)icupatient.getValue("SEX");
					
					if(age1.equals(age2) && sex1.equals(sex2)){
						patient.insertValue("ADMIS_CRIT_CARE", new BooleanValue("10"));
						ORDatabase.put(patientID,patient);
						validPatients.put(patientID, CCDatabase.get(patientID));
					}
					else{
						sendmailPatients.put(patientID,CCDatabase.get(patientID));
					}
				}
			}
			else{
				inconsistencies += ("Patient with ID: " + patientID + " not found in the Operating Room Database\n");
				invalidPatients.put(patientID, CCDatabase.get(patientID));
			}
		}
	
		writer.writeFile("inconsistencyFile ICU", inconsistencies, ".rtf");
		writer.writeDatabase("Valid CCR Patients", validPatients, "ICU");
		writer.writeDatabase("Invalid CCR Patients", invalidPatients, "ICU");
		writer.writeDatabase("age or sex is wrong", sendmailPatients, "ICU");
	}
	/**
	* OR Valid Patients
	* Every patient admitied into the OR with "ADMIS_TO_CRIT_CARE" vshould have a matching in the ICU
	*
	* @param ORDatabase Operating Room Database.
	* @param CCDatabase	Critical Care Room Database
	* @param ORTypesMap Utilitary file used to know each OR variable type.
	* @param CCTypesMap Utilitary file used to know each CCR variable type
	*/
	public void cleanORPatients(TreeMap<String, FieldsBox> ORDatabase, TreeMap<String, FieldsBox> CCDatabase, TreeMap<String, Requirement> ORTypesMap, TreeMap<String, Requirement> CCTypesMap){		
		Writers writer = new Writers();
		// The Patients not admited and crosschecked with the icu and not admited, we will not check these now
		TreeMap<String, FieldsBox> validPatients =  new TreeMap<String, FieldsBox>();
		// Patients admited into the ICU that don't exist in there
		TreeMap<String, FieldsBox> invalidPatients = new TreeMap<String, FieldsBox>();
		// Patients not admited into ICU
		TreeMap<String, FieldsBox> okPatients =  new TreeMap<String, FieldsBox>();
		// The inconsistencies found will be stored here
		String inconsistencies = "";
		// Go through entire database
		for(FieldsBox patient: ORDatabase.values()){
			BooleanValue admited = (BooleanValue) patient.getValue("ADMIS_CRIT_CARE");
			TextValue id = (TextValue) patient.getValue("EUSOS_ID");	
				if(admited.getBoolean()){
					if(CCDatabase.containsKey(id.getID()))
						validPatients.put(id.getID(), patient);
					else{
						invalidPatients.put(id.getID(), patient); 
						inconsistencies += ("Patient with ID: " + id.getID() + " not found in the Critical Care Room Database" + "\n");
					}
				}
				else{
					okPatients.put(id.getID(), patient);
				}
		}
		writer.writeFile("inconsistencyFile OR", inconsistencies, ".rtf");
		writer.writeDatabase("Valid OR Patients", validPatients, "OR");
		writer.writeDatabase("Invalid OR Patients", invalidPatients, "OR");
		writer.writeDatabase("OK Patients", okPatients, "OR");
	}	
	
	/**
	* Writes to a file the patients which have survival to hospital discharge missing
	* This is currently not being used
	* @param database Operative Room Database	
	*/
	public void cleanMissingSurvival(TreeMap<String, FieldsBox> database){
		Writers writer = new Writers();
		// The Patients with missing SURV_HOSP_DISCH
		TreeMap<String, FieldsBox> missing =  new TreeMap<String, FieldsBox>();
		for(FieldsBox patient : database.values()){
				BooleanValue admited = (BooleanValue)patient.getValue("SURV_HOSP_DISCH");
				if(admited.getBoolean() == null){
					TextValue id = (TextValue) patient.getValue("EUSOS_ID");
					missing.put(id.getID(), patient);
				}
		}
		writer.writeDatabase("Missing Survival", missing, "OR");
	}	
	
	/**
	*	Given two databases, one with IDs that are duplicated in the second,
	*	compares the "age" and "sex" fields for that ID and checks if they are the same.
	*	@param databaseDuplicate	Database containing the duplicated patients
	*	@param ORDatabase	Operating Room database
	*	@return Number of duplicates	
	*/
	public int verifyDuplicatePatients(TreeMap<String, FieldsBox> databaseDuplicate, TreeMap<String, FieldsBox> ORDatabase){
		int duplicate = 0;
		int diferent = 0;
		for(FieldsBox duplicatePatient : databaseDuplicate.values()){
			String eusosID = ((TextValue) duplicatePatient.getValue("EUSOS_ID")).getID();
			if(ORDatabase.containsKey(eusosID)){
				FieldsBox originalPatient = ORDatabase.get(eusosID);
				NumberValue age1 = (NumberValue)duplicatePatient.getValue("AGE");
				NumberValue sex1 = (NumberValue)duplicatePatient.getValue("SEX");
				NumberValue age2 = (NumberValue)originalPatient.getValue("AGE");
				NumberValue sex2 = (NumberValue)originalPatient.getValue("SEX");
				if(!age1.equals(age2) || !sex1.equals(sex2))
					diferent++;
			}
			else{
				duplicate++;
			}
		}
		System.out.println(diferent);
		return duplicate;
	}	
	
	/**
	*	Corrects the units for:
	*	-> Haemoglobin
	*	-> Urea/BUN
	*	-> Creatinine
	*	-> Arterial PaO2 (lowest value
	*	-> Bilirubin (highest value
	*	@param	database	Database to be processed
	*	@param	databaseType	Type of database: OR or ICU
	*	@param	finalName	Name of the new, correted database to be saved
	*/	
	public void correctUnits(TreeMap<String, FieldsBox> database, String databaseType, String finalName){
		Fixers fixer = new Fixers();
		Writers writer = new Writers();
		if(databaseType.equals("OR")){
			for(FieldsBox patient: database.values()){
				patient.insertValue("NCREATININE", fixer.fixCreatinine(patient.getValue("CREATININE")));//fixCreatinine
				patient.insertValue("nHAEMO", fixer.fixHaemoglobin(patient.getValue("HAEMO")));//fixHaemoglobin
				patient.insertValue("NUREA", fixer.fixUrea(patient.getValue("UREA"),patient.getValue("UN_UREA")));//fixUrea
			}	
		
		}
		if(databaseType.equals("ICU")){
			for(FieldsBox patient: database.values()){
				patient.insertValue("NCREATININE", fixer.fixCreatinine(patient.getValue("CREATININE")));//fixCreatinine
				patient.insertValue("NART_PAO2", fixer.fixPao2(patient.getValue("ART_PAO2")));//fixPao2
				patient.insertValue("NBILLIRUBIN", fixer.fixBilirubin(patient.getValue("BILLIRUBIN")));//fixBilirubin
				// First 24 Hours,
				patient.insertValue("N24_CREATININE", fixer.fixCreatinine(patient.getValue("24_CREATININE")));//fixCreatinine
				patient.insertValue("N24_ART_PAO2", fixer.fixPao2(patient.getValue("24_ART_PAO2")));//fixPao2
				patient.insertValue("N24_BILLIRUBIN", fixer.fixBilirubin(patient.getValue("24_BILLIRUBIN")));//fixBilirubin
				// First 48 Hours
				patient.insertValue("N48_CREATININE", fixer.fixCreatinine(patient.getValue("48_CREATININE")));//fixCreatinine
				patient.insertValue("N48_ART_PAO2", fixer.fixPao2(patient.getValue("48_ART_PAO2")));//fixPao2
				patient.insertValue("N48_BILLIRUBIN", fixer.fixBilirubin(patient.getValue("48_BILLIRUBIN")));//fixBilirubin
			}
		}
		writer.writeDatabase(finalName, database, databaseType);
	}
	
	/**
	*	This function checks and counts each entry of the database by country and 
	*	returns a counting of each patient by country.
	*	this is used to check where the invalid patients come from.
	*	@param countries	Database containing the countries and respective codes
	*	@param database	Database to be analyzed
	*	@return The number of ocurrencies per country
	*/
	public TreeMap<String, Integer> verifyCountries(TreeMap<String, String> countries, TreeMap<String, FieldsBox> database){
		TreeMap<String, Integer> countryCounters = new TreeMap<String, Integer>();		
		String databaseID;
		TextValue databaseIDValue;
		for(FieldsBox patient: database.values()){
			databaseIDValue = (TextValue) patient.getValue("EUSOS_ID");
			databaseID = databaseIDValue.getID();
			for(String countryCode : countries.values()){
				if(databaseID.startsWith(countryCode)){
					if(countryCounters.containsKey(countryCode)){
						int oldCounter = countryCounters.get(countryCode);
						countryCounters.remove(countryCode);
						countryCounters.put(countryCode, oldCounter+1);
					} else {
						countryCounters.put(countryCode, 1);
					}
						
					break;
				}
			}
		}
		return countryCounters;
	}	
	
	
	/**
	*	Counts number of age/sex mismatches between databases
	*	@param 	ORdatabase	Operative Room Database
	*	@param	CCdatabase	Critical Care Room Database
	*	@return	count of patients
	*/
	public int verifyAgeSexMismatch(TreeMap<String, FieldsBox> ORdatabase, TreeMap<String, FieldsBox> CCdatabase){
		int i=0;
		for(FieldsBox ORpatient : ORdatabase.values()){
			TextValue orid = (TextValue)ORpatient.getValue("EUSOS_ID");
			FieldsBox CCpatient = CCdatabase.get(orid.getID());
			NumberValue age1 = (NumberValue)ORpatient.getValue("AGE");
			NumberValue sex1 = (NumberValue)ORpatient.getValue("SEX");
			NumberValue age2 = (NumberValue)CCpatient.getValue("AGE");
			NumberValue sex2 = (NumberValue)CCpatient.getValue("SEX");
			if(!age1.equals(age2) || !sex1.equals(sex2)){
				i++;
				System.out.println(orid);
			}
		}
		return i;
	}

	/**
	*	This method should be ran for a complete analysis and cleaning / formatting
	*	of the databases.
	*	It corrects and documents, generating several files for:
	*	-> Field names
	*	-> Missing end values
	*	-> Missing values
	*	-> Spaces inside numbers
	*	-> Characters inside numbers
	*
	*	Requires the following files to be present in the same folder the program is being run in:
	*
	*	-> OperatingRoomUtil.csv
	*	-> CriticalCareRoomUtil.csv
	*	-> OR Pre-Cleaning.csv
	*	-> CCR Pre-Cleaning.csv
	*	-> Countries.csv
	*
	*	Will produce a new directory, Outputs, with all the logs and new databases.
	*/
	public void cleanAndFormatDatabases(){
		Cleaners cleaner = new Cleaners();
		Readers reader  = new Readers();
		Fixers fixer = new Fixers();
		
		// -------------------------Reading---------------------------
		System.out.println("Welcome.");
		System.out.println("Reading Operating Room Utils...");
		TreeMap<String,Requirement> ORTypes = reader.readOperatingRoomUtils(new File("OperatingRoomUtil.csv"));
		System.out.println("Done");
		System.out.println("Reading Critical Care Room Utils...");
		TreeMap<String,Requirement> UCITypes = reader.readCriticalCareRoomUtils(new File("CriticalCareRoomUtil.csv"));
		System.out.println("Done");
		System.out.println("Now reading OR Pre-Cleaning database...");
		TreeMap<String, FieldsBox> ORDatabase = reader.readDatabaseOperatingRoom(new File("OR Pre-Cleaning.csv"), ORTypes);
		System.out.println("Done");
		System.out.println("Now reading CCR Pre-Cleaning database...");
		TreeMap<String, FieldsBox> UCIDatabase = reader.readDatabaseCriticalCareRoom(new File("CCR Pre-Cleaning.csv"), UCITypes);
		System.out.println("Done");
		System.out.println("Now reading countries database...");
		TreeMap<String, String> countries = reader.readCountries(new File("Countries.csv"));
		System.out.println("Done\n");
		// -------------------------Cleaning---------------------------
		System.out.println("Correcting field names:");
		System.out.println("CCR...");
		cleaner.correctFieldNames(new File("CCR to clean.csv"), _firstColumnCC, _numberOfColumnsCC, "Log Inconsistencies - CCR 1 - FieldNames cleaned", "CCR 1 - FieldNames cleaned");
		System.out.println("OR...");
		cleaner.correctFieldNames(new File("OR to clean.csv"), _firstColumnOR, _numberOfColumnsOR, "Log Inconsistencies - OR 1 - FieldNames cleaned", "OR 1 - FieldNames cleaned");
		System.out.println("Finished analysing field names.\n");	
		System.out.println("Checking, documenting and correcting missing end values:");
		System.out.println("CCR...");
		cleaner.cleanMissingEndValues(new File("Outputs/CCR 1 - FieldNames cleaned.csv"), _firstColumnCC, _numberOfColumnsCC, "Log Inconsistencies - CCR 2 - EndValues cleaned", "CCR 2 - EndValues cleaned");
		System.out.println("OR...");
		cleaner.cleanMissingEndValues(new File("Outputs/OR 1 - FieldNames cleaned.csv"), _firstColumnOR, _numberOfColumnsOR, "Log Inconsistencies - OR 2 - EndValues cleaned", "OR 2 - EndValues cleaned");
		System.out.println("Finished analysing missing end values.\n");
		System.out.println("Checking, documenting and replacing missing values:");
		System.out.println("CCR...");
		cleaner.cleanAddMissingValues(new File("Outputs/CCR 2 - EndValues cleaned.csv"), _firstColumnCC, _numberOfColumnsCC, "Log Inconsistencies - CCR 3 - MissingValues cleaned", "CCR 3 - MissingValues cleaned");
		System.out.println("OR...");
		cleaner.cleanAddMissingValues(new File("Outputs/OR 2 - EndValues cleaned.csv"), _firstColumnOR, _numberOfColumnsOR, "Log Inconsistencies - OR 3 - MissingValues cleaned", "OR 3 - MissingValues cleaned");
		System.out.println("Finished analysing missing values.\n");
		System.out.println("Checking, documenting and removing spaces inside numbers:");
		System.out.println("CCR...");
		cleaner.cleanNumberSpaces(new File("Outputs/CCR 3 - MissingValues cleaned.csv"), _firstColumnCC, _numberOfColumnsCC, "Log Inconsistencies - CCR 4 - NumberSpaces cleaned", "CCR 4 - NumberSpaces cleaned", UCITypes);
		System.out.println("OR...");
		cleaner.cleanNumberSpaces(new File("Outputs/OR 3 - MissingValues cleaned.csv"), _firstColumnOR, _numberOfColumnsOR, "Log Inconsistencies - OR 4 - NumberSpaces cleaned", "OR 4 - NumberSpaces cleaned", ORTypes);
		System.out.println("Finished analysing number spaces.\n");
		System.out.println("Checking, documenting and removing characters inside numbers:");
		System.out.println("CCR...");
		cleaner.cleanNumberFormatting(new File("Outputs/CCR 4 - NumberSpaces cleaned.csv"), _firstColumnCC, _numberOfColumnsCC, "Log Inconsistencies - CCR 5 - NumberFormatting cleaned", "CCR 5 - NumberFormatting cleaned", UCITypes);
		System.out.println("OR...");
		cleaner.cleanNumberFormatting(new File("Outputs/OR 4 - NumberSpaces cleaned.csv"), _firstColumnOR, _numberOfColumnsOR, "Log Inconsistencies - OR 5 - NumberFormatting cleaned", "OR 5 - NumberFormatting cleaned", ORTypes);
		System.out.println("Finished analysing number characters.\n");
		System.out.println("OR Pre-Cleaning database finished.");
		System.out.println("CCR Pre-Cleaning database finished.");
		System.out.println("Cleaning successfully finished");
	}
	
	/**
	* Main method. currently reading the final files and correcting units for each of those files.
	*
	* VERY IMPORTANT NOTE: We left the 'main' methods in the files simply to show what kind of sequence
	* the operations should be run. However, running the current main fuctions will NOT produce a new, fixed 
	* database given raw input; such a thing only happens with the 'cleanAndFormatDatabases()' method present
	* in the 'Cleaners' class; that function is intended to work with raw databases.
	* It would be impossible to program complete main methods to automatically correct the raw databases, since
	* much of the work of correcting fields, based on the emails received, is done by hand and is very time
	* consuming, besides being impossible to be replicated by computer.
	* As such, we don't recommend running these main methods if the user is not familiar with the program.
	* If, however, you do wish to run the program, the authors are free of any responsability and you use these
	* methods and the program AT YOUR OWN RISK.
	*/
	public static void main(String args[]) {
		Cleaners cleaner = new Cleaners();
		Readers reader  = new Readers();
		Fixers fixer = new Fixers();
		Writers writer = new Writers();
		System.out.println("Reading Operating Room Utils...");
		TreeMap<String,Requirement> ORTypes = reader.readOperatingRoomUtils(new File("OperatingRoomUtil.csv"));
		System.out.println("Done");
		System.out.println("Reading Critical Care Room Utils...");
		TreeMap<String,Requirement> UCITypes = reader.readCriticalCareRoomUtils(new File("CriticalCareRoomUtil.csv"));
		System.out.println("Done");
		System.out.println("Now reading OR OK");
		TreeMap<String, FieldsBox> OROK = reader.readDatabaseOperatingRoom(new File("Outputs/Last/OK 3.2.csv"), ORTypes);
		System.out.println("Length: " + reader.getDuplicateOR().size());
		System.out.println("Now reading OR Valid");
		TreeMap<String, FieldsBox> ORValid = reader.readDatabaseOperatingRoom(new File("Outputs/Last/Valid OR 3.2.csv"), ORTypes);
		System.out.println("Length: " + reader.getDuplicateOR().size());
		System.out.println("Now reading OR Invalid");
		TreeMap<String, FieldsBox> ORInvalid = reader.readDatabaseOperatingRoom(new File("Outputs/Last/Invalid OR 3.2.csv"), ORTypes);
		System.out.println("Length: " + reader.getDuplicateOR().size());
		System.out.println("Now reading CCR Pre-Cleaning database...");
		TreeMap<String, FieldsBox> ICUValid = reader.readDatabaseCriticalCareRoom(new File("Outputs/Last/Valid ICU 3.2.csv"), UCITypes);
		System.out.println("Done");
		System.out.println("Now reading CCR Pre-Cleaning database...");
		TreeMap<String, FieldsBox> ICUInvalid = reader.readDatabaseCriticalCareRoom(new File("Outputs/Last/Invalid ICU 3.2.csv"), UCITypes);
		System.out.println("Done");
		System.out.println("Fixing");
		
		cleaner.correctUnits(ORValid,"OR","Valid OR 3.2");
		cleaner.correctUnits(OROK,"OR","OK 3.2");
		cleaner.correctUnits(ORInvalid,"OR","Invalid OR 3.2");
		cleaner.correctUnits(ICUValid,"ICU","Valid ICU 3.2");
		cleaner.correctUnits(ICUInvalid,"ICU","Invalid ICU 3.2");
		
	}
}