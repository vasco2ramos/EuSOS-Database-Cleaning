import java.io.*;
import java.util.*;

import Values.*;
import Requirements.*;


public class Readers{

	/**
	* Character field separator; we will use ';'.
	*/
	static private final String _separator = ";";
	
	/**
	* Determines where is the first column to be read since some columns are not part of the CRF.
	*/
	static private final int _firstColumnOR = 7;
	static private final int _firstColumnCC = 6;
	
	/**
	* Number of columns to be read in each database for the operating room and critical care, respectively.
	*/
	static private final int _numberOfColumnsOR = 50;
	static private final int _numberOfColumnsCC = 150;
	
	/**
	*	Collections containing patients with duplicated IDs.
	*/
	public ArrayList<FieldsBox> _duplicatedORPatients = new ArrayList<FieldsBox>();
	public ArrayList<FieldsBox> _duplicatedCCPatients = new ArrayList<FieldsBox>();
	
	/**
	*	Collection containing the field names for the databases.
	*/
	private ArrayList<String> _ORNames;
	private ArrayList<String> _CCNames;
	
	/**
	*	Returns the database of duplicated patients in the OR database.
	*/
	public ArrayList<FieldsBox> getDuplicateOR(){
		return _duplicatedORPatients;
	}
	
	/**
	*	Returns the database of duplicated patients in the CC database.
	*/
	public ArrayList<FieldsBox> getDuplicateCC(){
		return _duplicatedCCPatients;
	}
	
	/**
	*	Stores the given collection as the duplicated patients database for the OR.
	*	@param	db	Database to store
	*/
	public void setDuplicateOR(ArrayList<FieldsBox> db){
		_duplicatedORPatients.clear();
		_duplicatedORPatients.addAll(db);
	}
	
	/**
	*	Stores the given collection as the duplicated patients database for the CC.
	*	@param	db	Database to store
	*/
	public void setDuplicateCC(ArrayList<FieldsBox> db){
		_duplicatedCCPatients.clear();
		_duplicatedCCPatients.addAll(db);
	}
	
	/**
	*	Returns the collection of field names for the OR database.
	*/
	public ArrayList<String> getORNames() {
		return _ORNames;
	}
	
	/**
	*	Returns the collection of field names for the OR database.
	*/
	public ArrayList<String> getCCNames() {
		return _CCNames;
	}
	
	/**
	* Fetch the entire contents of a text file, with a Operations room based
	* variables, and return a vector of fieldbox that correspond to each line of the db.
	* This style of implementation does not throw Exceptions to the caller.
	*
	* There are (50 + '_firstColumn') columns, the last 50 are part of the key and CRF given, 
	* e.g. - this is, the first columns are not used.
	*
	* @param inputFile Operating Room database file.
	* @param typesMap Utilitary file used to know each variable type.
	* @return database	OR database
	*/
	public TreeMap<String, FieldsBox> readDatabaseOperatingRoom(File inputFile, TreeMap<String, Requirement> typesMap){	
		Fixers fixer = new Fixers();
		// Variable in which the database will be stored.
		TreeMap<String, FieldsBox> database = new TreeMap<String, FieldsBox>();
		// Variable in which the column names will be stored
		_ORNames = new ArrayList<String>();
		try {
			//Buffering reads one line at a time
			//FileReader assumes no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				String line = null;
				//Reads the first line to store variable names;
				// First line must have variable names
				line = input.readLine();
				String[] firstLine = line.split(_separator);
				for(int i=0; i<(_numberOfColumnsOR+_firstColumnOR);i++){
					_ORNames.add(firstLine[i]);
				}
				// Reading inputFile and storing each line in a fieldsBox
				while ((line = input.readLine()) != null){ 
					String[] lineValues = line.split(_separator);
					// Buffer to store the line to be read
					FieldsBox patientValues = new FieldsBox();
					// Checking types and creating correspondent class along the line		
					for(int i=0;i<_firstColumnOR;i++){
						patientValues.insertValue(_ORNames.get(i), new TextValue(lineValues[i]));
					}
					for(int i = _firstColumnOR; i < (_numberOfColumnsOR + _firstColumnOR); i++){
						// Fetch the name of the field
						String fieldName = _ORNames.get(i);
						// Fetch the type of the field
						Requirement type = typesMap.get(fieldName);
						// Fetchs which class should be used to store each value
						String classType = type.getClassName();
							try{
							if(classType.compareTo("Number") == 0 || classType.compareTo("Range") == 0) {
								patientValues.insertValue(fieldName, new NumberValue(lineValues[i]));
							}
							else if(classType.compareTo("Boolean") == 0){
								patientValues.insertValue(fieldName, new BooleanValue(lineValues[i]));
							}
							else if(classType.compareTo("ValueArray") == 0) {

								patientValues.insertValue(fieldName, new NumberArrayValue(lineValues[i]));
							}
							else if(classType.compareTo("Text") == 0){
								patientValues.insertValue(fieldName, new TextValue(lineValues[i]));
							}
							else if(classType.compareTo("Date") == 0) {
								patientValues.insertValue(fieldName, new DateValue(lineValues[i]));
							}
						} catch (ArrayIndexOutOfBoundsException exc) {
							// Acho que não é preciso o for, vou ter de ver depois - alonso
							//for(int j = i; j <(_numberOfColumnsCC+_firstColumnCC); j++){
								patientValues.insertValue(fieldName, new NumberValue("-1"));
							//}
						}
					}
					// Adds patient to digital database.
					if(database.containsKey(lineValues[26])){
						String fixed = fixer.fixIDBySubjID(patientValues);
						if(database.containsKey(fixed) || fixed.equals("error")){
							_duplicatedORPatients.add(patientValues);
							System.out.println(fixed);
						}
						else{
							patientValues.insertValue("EUSOS_ID", new TextValue(fixed));
							FieldsBox old = database.put(fixed, patientValues);
						}
					}
					else{
						database.put(lineValues[26], patientValues);
					}
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
		  System.out.println("[readDatabaseOperatingRoom] ERROR: Input/output error.");
		  ex.printStackTrace();
		}
		return database;
	}
	
	/**
	* Fetch the entire contents of a text file, with a Critical care room based
	* variables, and return a vector of fieldbox that correspond to each line of the db.
	* This style of implementation does not throw Exceptions to the caller.
	*
	* There are (150 + '_firstColumn') columns, the last 150 are part of the key and CRF given, 
	* e.g. - this is, the first columns are not used.
	*
	* @param inputFile Critical care database file.
	* @param typesMap Utilitary file used to know each variable type.
	* @return database UCI databse
	*/
	public TreeMap<String, FieldsBox> readDatabaseCriticalCareRoom(File inputFile, TreeMap<String, Requirement> typesMap){
		
		Fixers fixer = new Fixers();
		// Variable in which the database will be stored.
		TreeMap<String, FieldsBox> database = new TreeMap<String, FieldsBox>();
		// Variable in which the column names will be stored
		_CCNames = new ArrayList<String>();
		try {
			//Buffering reads one line at a time
			//FileReader assumes no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				String line = null;
				//Reads the first line to store variable names;
				// First line must have variable names
				line = input.readLine();
				String[] firstLine = line.split(_separator);
				for(int i=0; i<(_numberOfColumnsCC+_firstColumnCC);i++){
						_CCNames.add(firstLine[i]);
				}
				// Reading inputFile and storing each line in a fieldsBox
				while ((line = input.readLine()) != null){ 
					String[] lineValues = line.split(_separator);
					// Buffer to store the line to be read
					FieldsBox patientValues = new FieldsBox();
					// Adding unneeded values to the pacient.
					for(int i=0;i<_firstColumnCC;i++){
						patientValues.insertValue(_CCNames.get(i), new TextValue(lineValues[i]));
					}
					// Checking types and creating correspondent class along the line					
					for(int i=_firstColumnCC; i<(_numberOfColumnsCC+_firstColumnCC); i++){
						// Fetch the name of the field
						String fieldName = _CCNames.get(i);
						// Fetch the type of the field
						Requirement type = typesMap.get(fieldName);
						// Fetchs which class should be used to store each value
						String classType = type.getClassName();
						try{
							if(classType.compareTo("Number") == 0 || classType.compareTo("Range") == 0) {
								patientValues.insertValue(fieldName, new NumberValue(lineValues[i]));
							}
							else if(classType.compareTo("Boolean") == 0){
								patientValues.insertValue(fieldName, new BooleanValue(lineValues[i]));
							}
							else if(classType.compareTo("ValueArray") == 0) {
								patientValues.insertValue(fieldName, new NumberArrayValue(lineValues[i]));
							}
							else if(classType.compareTo("Text") == 0){
								patientValues.insertValue(fieldName, new TextValue(lineValues[i]));
							}
							else if(classType.compareTo("Date") == 0) {
								patientValues.insertValue(fieldName, new DateValue(lineValues[i]));
							}
						} catch (ArrayIndexOutOfBoundsException exc) {
							for(int j = i; j <(_numberOfColumnsCC+_firstColumnCC); j++){
								patientValues.insertValue(fieldName, new NumberValue("-1"));
							}
						}
					}
					// Adds patient to digital database.
					if(database.containsKey(lineValues[89])){
						String fixed = fixer.fixIDBySubjID(patientValues);
						if(database.containsKey(fixed) || fixed.equals("error")){
							_duplicatedCCPatients.add(patientValues);
						}
						else{
							patientValues.insertValue("EUSOS_ID", new TextValue(fixed));
							database.put(fixed, patientValues);
						}
					}
					else{
						database.put(lineValues[89], patientValues);
					}
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
		  System.out.println("[readDatabaseCriticalCareRoom] ERROR: Input/output error.");
		  ex.printStackTrace();
		}
		return database;
	}
	
	/**
	* Fetch the entire contents of a text file, with a Utilitary based variables, with info about
	* variables in the Critical Care Room. 
	* This returns a Treemap organized by variable name and respective requirement, this requirements
	* are then used to determine the range of each variable and if it is required or not.
	* e.g. - This will help determine which fields are right or wrong according to the CRF
	*
	* @param inputFile Utils file provided
	* @return Utilitary file with variables info
	*/
	public TreeMap<String, Requirement> readCriticalCareRoomUtils(File inputFile){
		// Variable where the utilitary file should be stored for further reading.
		TreeMap<String, Requirement> typesMap = new TreeMap<String, Requirement>();

		try {
			//Buffering reads one line at a time
			//FileReader assumes no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
				try {
					String line = null;
					/*
					* readLine():
					* returns line contents except newline character (/n)
					* returns null at EOF(End of file)
					* returns two (/n) if a line is empty
					* can throw IOException
					*/
					
					while ((line = input.readLine()) != null){
						String[] splitLine = line.split(_separator);
						Boolean _required = false;
						// Checks if the field is required
						if(splitLine[3].equals("yes"))
							_required = true;
						String type = splitLine[1];
						// Fetchs field type and creates respective requirement
						if(type.equals("integer") || type.equals("floating")){
							if(splitLine[2].equals("text ")){
								RangeRequirement _number = new RangeRequirement(splitLine[4], _required);
								typesMap.put(splitLine[0], _number);
								}
							else{
								ValuesRequirement _value = new ValuesRequirement(splitLine[2], _required);
								typesMap.put(splitLine[0], _value);
							}
						}
						else if(type.equals("Boolean")){
							BooleanRequirement _boolean = new BooleanRequirement(_required);
							typesMap.put(splitLine[0], _boolean);
						}
						else if(type.equals("character string")){
							TextRequirement _text = new TextRequirement(_required);
							typesMap.put(splitLine[0], _text);
						}
						else if(type.equals("date")){
							DateRequirement _date = new DateRequirement(_required);
							typesMap.put(splitLine[0], _date);
						}
					}
				} finally {
					input.close();
				}
		} catch (IOException ex){
		  System.out.println("[readCriticalCareRoomUtils] ERROR: Input/output error.");
		  ex.printStackTrace();
		}
		return typesMap;
	}	
	
	/**
	* Fetch the entire contents of a text file, with a Utilitary based variables, with info about
	* variables in the Operating Room. 
	* This returns a Treemap organized by variable name and respective requirement, this requirements
	* are then used to determine the range of each variable and if it is required or not.
	* e.g. - This will help determine which fields are right or wrong according to the CRF
	*
	* @param inputFile Utils file provided this should be changed to include further variables.
	* @return typesMap utilitary file with variables info
	*/
	public TreeMap<String, Requirement> readOperatingRoomUtils(File inputFile){
		// Variable where the utilitary file should be stored for further reading.
		TreeMap<String, Requirement> typesMap = new TreeMap<String, Requirement>();

		try {
			//Buffering reads one line at a time
			//FileReader assumes no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
				
				try {
					String line = null;
					/*
					* readLine():
					* returns line contents except newline character (/n)
					* returns null at EOF(End of file)
					* returns two (/n) if a line is empty
					* can throw IOException
					*/
					
					while ((line = input.readLine()) != null){
						String[] splitLine = line.split(_separator);
						// Checks if field is mandatory
						Boolean _required = false;
						// Checks if the field is required
						if(splitLine[3].equals("yes"))
							_required = true;
						String type = splitLine[1];
						
						// Fetchs field type and creates respective requirement
						if(type.equals("integer") || type.equals("floating")){
							if(splitLine[2].equals("text")){
								RangeRequirement _number = new RangeRequirement(splitLine[4], _required);
								typesMap.put(splitLine[0], _number);
								}
							else{
								ValuesRequirement _value = new ValuesRequirement(splitLine[2], _required);
								typesMap.put(splitLine[0], _value);
							}
						}
						else if(type.equals("Boolean")){
							BooleanRequirement _boolean = new BooleanRequirement(_required);
							typesMap.put(splitLine[0], _boolean);
						}
						else if(type.equals("character string")){
							TextRequirement _text = new TextRequirement(_required);
							typesMap.put(splitLine[0], _text);
						}
						else if(type.equals("date")){
							DateRequirement _date = new DateRequirement(_required);
							typesMap.put(splitLine[0], _date);
						}
						
					}
				} finally {
					input.close();
				}
		} catch (IOException ex){
		  System.out.println("[readOperatingRoomUtils] ERROR: Input/output error.");
		  ex.printStackTrace();
		}
		return typesMap;
	}	
	
	/**
	* Fetches the entire contents of a text file, with contact information based variables.
	* Does not throw Exceptions to the caller.
	*
	* @param 	inputFile	File containing doctor's emails
	* @return	emailInfo	Collection containing the emails information
	*/
	public ArrayList<Email> readEmails(File inputFile) {
		StringBuilder contents = new StringBuilder();
		ArrayList<Email> emailInfo = new ArrayList<Email>();
		try {
			//Buffering reads one line at a time
			//FileReader assumes no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				String line = null;
				/*
				* readLine():
				* returns line contents except newline character (/n)
				* returns null at EOF(End of file)
				* returns two (/n) if a line is empty
				* can throw IOException
				*/
				while (( line = input.readLine()) != null){ 
					String[] splitLine = line.split(_separator);
					// Is the line valid, may be or not removed for the program execution
					
					/**
					* 0 - username
					* 1 - Name
					* 2 - mail
					* 4 - location
					* 5 - id
					* (String id, String name, String email, String username, String location)
					**/
					if(line.lastIndexOf("moved") != -1 || splitLine[0].length() < 2){
					}
					else{		
						emailInfo.add(new Email(splitLine[5], splitLine[1], splitLine[2], splitLine[0], splitLine[4]));
					}
				}
			} finally {
				input.close();
			}
		} catch (IOException ex){
		  System.out.println("[readEmails] ERROR: Input/output error.");
		  ex.printStackTrace();
		}

		return emailInfo;
	}
	
	/**
	* Fetch the first line of a text file, with variable names
	* Does not throw Exceptions to the caller.
	*
	* @param	databaseType	Type: OR or ICU
	*/
	public ArrayList<String> readFieldNames(String databaseType){
		ArrayList<String> names = new ArrayList<String>();
		if(databaseType.equals("OR")){
			try {
				//Buffering reads one line at a time
				//FileReader assumes no encoding problems
				BufferedReader input = new BufferedReader(new FileReader(new File("OR Variables.csv")));
				try {
					String line = null;
					//Reads the first line to store variable names;
					// First line must have variable names
					line = input.readLine();
					String[] firstLine = line.split(_separator);
					for(int i=0; i<(_numberOfColumnsOR+_firstColumnOR);i++){
						names.add(firstLine[i]);
					}
				} finally {
					input.close();
				}
			} catch (IOException ex){
			  System.out.println("[readFieldNames] ERROR: Input/output error.");
			  ex.printStackTrace();
			}
		}
		else if(databaseType.equals("CCR") || databaseType.equals("ICU")){
			try {
				//Buffering reads one line at a time
				//FileReader assumes no encoding problems
				BufferedReader input = new BufferedReader(new FileReader(new File("CCR Variables.csv")));
				try {
					String line = null;
					//Reads the first line to store variable names;
					// First line must have variable names
					line = input.readLine();
					String[] firstLine = line.split(_separator);
					for(int i=0; i<(_numberOfColumnsCC+_firstColumnCC);i++){
						names.add(firstLine[i]);
					}
				} finally {
					input.close();
				}
			} catch (IOException ex){
			  System.out.println("[readFieldNames] ERROR: Input/output error.");
			  ex.printStackTrace();
			}		
		}
		else{
			System.out.println("[readFieldNames] ERROR: Invalid database type");
		}
		return names;
	}
	
	/**
	* Fetches the entire content of a text file, with country type variables
	* Does not throw Exceptions to the caller.
	*
	* @param inputFile	File containing doctor's emails
	* @return Countries database
	*/		
	public TreeMap<String, String> readCountries(File inputFile){		
		// Variable in which the database will be stored.
		TreeMap<String, String> database = new TreeMap<String, String>();
		
		try {
			//Buffering reads one line at a time
			//FileReader assumes no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				String line = null;
				//Reads the first line to store variable names;
				// First line must have variable names
				line = input.readLine();

				// Reading inputFile and storing each line in a fieldsBox
				while ((line = input.readLine()) != null){ 
					String[] lineValues = line.split(_separator);
					database.put(lineValues[0], lineValues[1]); 
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			System.out.println("[readCountries] ERROR: Invalid database type");
			ex.printStackTrace();
		}
		return database;
	}
	/**
	* Fetches the entire contents of a text file containing with duplicate patients based variables
	* Does not throw Exceptions to the caller.
	*
	* @param inputFile	File where we should fetch doctor's email's
	* @param typesMap Utilitary file used to know each variable type.
	*/
	public void readDuplicate(File inputFile, TreeMap<String, Requirement> typesMap, String databaseType){
		ArrayList<String> names = new ArrayList<String>();
		try {
			//Buffering reads one line at a time
			//FileReader assumes no encoding problems
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				if(databaseType.equals("ICU") || databaseType.equals("UCI") || databaseType.equals("CC")){
					String line = null;
					//Reads the first line to store variable names;
					// First line must have variable names
					line = input.readLine();
					String[] firstLine = line.split(_separator);
					for(int i=0; i<(_numberOfColumnsCC+_firstColumnCC);i++){
							names.add(firstLine[i]);
					}
					// Reading inputFile and storing each line in a fieldsBox
					while ((line = input.readLine()) != null){ 
						String[] lineValues = line.split(_separator);
						// Buffer to store the line to be read
						FieldsBox patientValues = new FieldsBox();
						// Adding unneeded values to the pacient.
						for(int i=0;i<_firstColumnCC;i++){
							patientValues.insertValue(names.get(i), new TextValue(lineValues[i]));
						}
						// Checking types and creating correspondent class along the line					
						for(int i=_firstColumnCC; i<(_numberOfColumnsCC+_firstColumnCC); i++){
							// Fetch the name of the field
							String fieldName = names.get(i);
							// Fetch the type of the field
							Requirement type = typesMap.get(fieldName);
							// Fetchs which class should be used to store each value
							String classType = type.getClassName();
							try{
								if(classType.compareTo("Number") == 0 || classType.compareTo("Range") == 0) {
									patientValues.insertValue(fieldName, new NumberValue(lineValues[i]));
								}
								else if(classType.compareTo("Boolean") == 0){
									patientValues.insertValue(fieldName, new BooleanValue(lineValues[i]));
								}
								else if(classType.compareTo("ValueArray") == 0) {
									patientValues.insertValue(fieldName, new NumberArrayValue(lineValues[i]));
								}
								else if(classType.compareTo("Text") == 0){
									patientValues.insertValue(fieldName, new TextValue(lineValues[i]));
								}
								else if(classType.compareTo("Date") == 0) {
									patientValues.insertValue(fieldName, new DateValue(lineValues[i]));
								}
							} catch (ArrayIndexOutOfBoundsException exc) {
								for(int j = i; j <(_numberOfColumnsCC+_firstColumnCC); j++){
									patientValues.insertValue(fieldName, new NumberValue("-1"));
								}
							}
						}
						// Adds patient to digital database.
						_duplicatedCCPatients.add(patientValues);
					}
				}
				else if(databaseType.equals("OR")){
					String line = null;
					//Reads the first line to store variable names;
					// First line must have variable names
					line = input.readLine();
					String[] firstLine = line.split(_separator);
					for(int i=0; i<(_numberOfColumnsOR+_firstColumnOR);i++){
							names.add(firstLine[i]);
					}
					// Reading inputFile and storing each line in a fieldsBox
					while ((line = input.readLine()) != null){ 
						String[] lineValues = line.split(_separator);
						// Buffer to store the line to be read
						FieldsBox patientValues = new FieldsBox();
						// Checking types and creating correspondent class along the line		
						for(int i=0;i<_firstColumnOR;i++){
							patientValues.insertValue(names.get(i), new TextValue(lineValues[i]));
						}
						for(int i=_firstColumnOR; i<(_numberOfColumnsOR+_firstColumnOR); i++){
							// Fetch the name of the field
							String fieldName = names.get(i);
							// Fetch the type of the field
							Requirement type = typesMap.get(fieldName);
							// Fetchs which class should be used to store each value
							String classType = type.getClassName();
								try{
								if(classType.compareTo("Number") == 0 || classType.compareTo("Range") == 0) {
									patientValues.insertValue(fieldName, new NumberValue(lineValues[i]));
								}
								else if(classType.compareTo("Boolean") == 0){
									patientValues.insertValue(fieldName, new BooleanValue(lineValues[i]));
								}
								else if(classType.compareTo("ValueArray") == 0) {
									patientValues.insertValue(fieldName, new NumberArrayValue(lineValues[i]));
								}
								else if(classType.compareTo("Text") == 0){
									patientValues.insertValue(fieldName, new TextValue(lineValues[i]));
								}
								else if(classType.compareTo("Date") == 0) {
									patientValues.insertValue(fieldName, new DateValue(lineValues[i]));
								}
							} catch (ArrayIndexOutOfBoundsException exc) {
								// Acho que não é preciso o for, vou ter de ver depois - alonso
								//for(int j = i; j <(_numberOfColumnsCC+_firstColumnCC); j++){
									patientValues.insertValue(fieldName, new NumberValue("-1"));
								//}
							}
						}
						// Adds patient to digital database.
						_duplicatedORPatients.add(patientValues);	
					}						
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			System.out.println("[readDuplicate] ERROR: Invalid database type");
			ex.printStackTrace();
		}
	
	}
	
	/*
	* Main method, currently reading the databases and attempting to fix duplicate patients.
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
		Readers reader  = new Readers();
		Cleaners cleaner = new Cleaners();
		Fixers fixer = new Fixers();
		Writers writer = new Writers();
		System.out.println("Reading Operating Room Utils...");
		TreeMap<String,Requirement> ORTypes = reader.readOperatingRoomUtils(new File("OperatingRoomUtil.csv"));
		System.out.println("Done");
		System.out.println("Reading Critical Care Room Utils...");
		TreeMap<String,Requirement> UCITypes = reader.readCriticalCareRoomUtils(new File("CriticalCareRoomUtil.csv"));
		System.out.println("Done");
		/*System.out.println("Now reading OR Duplicates database...");
		reader.readDuplicate(new File("Outputs/V2.0/OR/duplicate OR.csv"), ORTypes, "OR");
		System.out.println("Length: " + reader.getDuplicateOR().size());*/
		System.out.println("Now reading OR OK");
		TreeMap<String, FieldsBox> OROK = reader.readDatabaseOperatingRoom(new File("Outputs/V2.0/OR/OK Patients.csv"), ORTypes);
		System.out.println("Length: " + reader.getDuplicateOR().size());
		System.out.println("Now reading OR Valid");
		TreeMap<String, FieldsBox> ORValid = reader.readDatabaseOperatingRoom(new File("Outputs/V2.0/OR/Valid OR Patients 2.0.csv"), ORTypes);
		System.out.println("Length: " + reader.getDuplicateOR().size());
		System.out.println("Now reading OR Invalid");
		TreeMap<String, FieldsBox> ORInvalid = reader.readDatabaseOperatingRoom(new File("Outputs/V2.0/OR/Invalid OR PatientsFINAL.csv"), ORTypes);
		System.out.println("Length: " + reader.getDuplicateOR().size());
		System.out.println("Fixing");
		reader.setDuplicateOR(fixer.fixDuplicatePatients(reader.getDuplicateOR(), OROK));
		reader.setDuplicateOR(fixer.fixDuplicatePatients(reader.getDuplicateOR(), ORValid));
		reader.setDuplicateOR(fixer.fixDuplicatePatients(reader.getDuplicateOR(), ORInvalid));
		System.out.println("Length: " + reader.getDuplicateOR().size());
		writer.writeArray("duplicate OR", reader.getDuplicateOR(), "OR");
	}
}