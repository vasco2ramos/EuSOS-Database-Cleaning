import java.io.*;
import java.util.*;
import java.math.BigDecimal;

import Values.*;
import Requirements.*;


public class Writers{

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
	* Creates a buffer for a file with the given text and file name.
	* @param	fileName	Name of the file that will be created.
	* @param	text		Contents of the text file.
	* @param	extension	Extension of that file.
	*/
	public BufferedWriter openBuffer(String fileName, String text, String extension){
		BufferedWriter out = null;
		try{
			File file;
			boolean exist;
			int fileNumber = 1;
			String finalFileName = fileName + extension;
			file = new File("Outputs/" + finalFileName);
			exist = file.createNewFile();
			/*while(exist){
				file = new File("Outputs/" + finalFileName);
				exist = file.createNewFile();
				finalFileName = fileName + "(" + fileNumber + ")" + extension;
			}*/
			FileWriter fstream = new FileWriter("Outputs/" + finalFileName);
			out = new BufferedWriter(fstream);
		} catch(IOException ioe){
			System.out.println("[openBuffer] ERROR: Error creating file " + fileName);
		}
		return out;
	}
	
	/**
	* Creates a file with the given text and file name.
	* @param fileName Name of the file that will be created
	* @param text contents of the text file
	* @param extension extension of that file
	*/
	public void writeFile(String fileName, String text, String extension){
		try{
			File file;
			boolean exist;
			int fileNumber = 1;
			String finalFileName = fileName + extension;
			file = new File("Outputs/" + finalFileName);
			exist = file.createNewFile();
			/*while(exist){
				file = new File("Outputs/" + finalFileName);
				exist = file.createNewFile();
				finalFileName = fileName + "(" + fileNumber + ")" + extension;
			}*/
			FileWriter fstream = new FileWriter("Outputs/" + finalFileName);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(text, 0, text.length());
			out.close();
		} catch(IOException ioe){
			System.out.println("[writeFile] ERROR: Error creating file " + fileName);
		}
	}
	
	/**
	* Writes a patient to a string
	* @param patient to be written
	* @param inputFile patient type
	* @return Patient in string form
	*/	
	public String writePatient(FieldsBox patient, File inputFile){
		String output = "";
		ArrayList<String> names = new ArrayList<String>();
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
				// OR or UCI database
				if(firstLine.length < _numberOfColumnsOR + _firstColumnOR + 3){
					for(int i=0; i<(_numberOfColumnsOR+_firstColumnOR);i++){
						names.add(firstLine[i]);
					}
				}
				else {
					for(int i=0; i<(_numberOfColumnsCC+_firstColumnCC);i++){
						names.add(firstLine[i]);
					}
				}
				for(String s: names){
					output += (patient.getValue(s) + ";");
				}
			}
		finally {
				input.close();
			}
		} catch (IOException ex){
		  System.out.println("[writePatient] ERROR: Input/output error");
		  ex.printStackTrace();
		}
		return output;
	}	
	
	/**
	* Writes a database to a file
	* @param fileName Name of the file that will be created
	* @param database contents of the database
	* @param databaseType OR or UCI database type
	*/
	public void writeDatabase(String fileName, TreeMap<String, FieldsBox> database, String databaseType){
		Readers reader  = new Readers();
		try{
			File file;
			boolean exist;
			int fileNumber = 1;
			String finalFileName = fileName + ".csv";
			file = new File("Outputs/" + finalFileName);
			exist = file.createNewFile();
			/*while(exist){
				file = new File("Outputs/" + finalFileName);
				exist = file.createNewFile();
				finalFileName = fileName + "(" + fileNumber + ")" + extension;
			}*/
			FileWriter fstream = new FileWriter("Outputs/" + finalFileName);
			BufferedWriter out = new BufferedWriter(fstream);
			if(databaseType.equals("OR")){
				ArrayList<String> fieldNames = reader.readFieldNames("OR");
				// Print 1st line with fieldnames
				for(String s: fieldNames){
					out.write((s +";"), 0, (s.length() + 1));
				}
				out.write("\n",0,1);
				for(FieldsBox fb : database.values()){
					for(String fieldName : fieldNames){
						String text = fb.getValue(fieldName) + ";";
						out.write(text, 0, text.length());
					}
					// Writing procedure duration
					String text = writeProcDuration(fb); 
					out.write("\n",0,1);
				}
				out.close();
			}
			else if(databaseType.equals("CCR") || databaseType.equals("ICU")){
				ArrayList<String> fieldNames = reader.readFieldNames("CCR");
				for(String s: fieldNames){
					out.write((s +";"), 0, (s.length() + 1));
				}
				out.write("\n",0,1);
				for(FieldsBox fb : database.values()){
					for(String fieldName : fieldNames){
						String text = fb.getValue(fieldName) + ";";
						out.write(text, 0, text.length());
					}
					out.write("\n",0,1);
				}
				out.close();
			}
			else{
				System.out.println("[writeDatabase] ERROR: Wrong database type, OR or UCI/CCR");
				
			}
		} catch(IOException ioe){
			System.out.println("[writeDatabase] ERROR: Error creating file " + fileName);
		}
	}
	
	/**
	* Writes a database from a given array.
	* @param	filename	Name of the file to save
	* @param	database	Database to be saved
	* @param	databaseType	Type: OR or ICU
	*/
	public void writeArray(String fileName, ArrayList<FieldsBox> database, String databaseType){
		Readers  reader  = new Readers();
		try{
			File file;
			boolean exist;
			int fileNumber = 1;
			String finalFileName = fileName + ".csv";
			file = new File("Outputs/" + finalFileName);
			exist = file.createNewFile();
			/*while(exist){
				file = new File("Outputs/" + finalFileName);
				exist = file.createNewFile();
				finalFileName = fileName + "(" + fileNumber + ")" + extension;
			}*/
			FileWriter fstream = new FileWriter("Outputs/" + finalFileName);
			BufferedWriter out = new BufferedWriter(fstream);
			if(databaseType.equals("OR")){
				ArrayList<String> fieldNames = reader.readFieldNames("OR");
				for(FieldsBox fb : database){
					for(String fieldName : fieldNames){
						String text = fb.getValue(fieldName) + ";";
						out.write(text, 0, text.length());
					}
					out.write("\n",0,1);
				}
				out.close();
			}
			else if(databaseType.equals("CCR") || databaseType.equals("ICU")){
				ArrayList<String> fieldNames = reader.readFieldNames("CCR");
				for(FieldsBox fb : database){
					for(String fieldName : fieldNames){
						String text = fb.getValue(fieldName) + ";";
						out.write(text, 0, text.length());
					}
					out.write("\n",0,1);
				}
				out.close();
			}
			else{
				System.out.println("[writeArray] ERROR: Wrong database type, OR or UCI/CCR");
				
			}
		} catch(IOException ioe){
			System.out.println("[writeArray] ERROR: Error creating file " + fileName);
		}
	}
	
	/**
	*	Returns a string specifying if the patient is under 16 years of age
	*	@param	patient	to be checked
	*	@return	10 for true 0 for false
	*/
	public String writeUnderage(FieldsBox patient){
		NumberValue age = (NumberValue)patient.getValue("AGE");
		if(age.getAsInt() < 16){
			return "10;";
		}
		return "0;";
	}
	
	/**
	*	Calculates and writes LeeScore, with
	*	1.     Any of intra-abdominal (surgical technique) or intra-thoracic (surgical technique) or vascular surgery (surgical procedure category)
	*
	*	2.     History of ischemic heart disease (co-morbid disease)
	*
	*	3.     History of congestive heart failure (co-morbid disease)
	*
	*	4.     History of cerebrovascular disease (co-morbid disease)
	*
	*	5.     Preoperative treatment with insulin (co-morbid disease)
	*
	*	6.     Creatinine > 2.0 mg/dL (or 177 µmol/L) (Biochemical & haematological tests before surgery)
	*	@param	patient	to be checked
	*	@return	lee score
	*/
	public String writeLeeScore(FieldsBox patient){
		// Getting values
		String abdomen = patient.getValue("INTRA_ABD_SURG") +"";
		String thorac = patient.getValue("INTRA_THORAC_SURG") +"";
		String category = patient.getValue("SURG_PROC_CATE") +"";
		
		NumberValue creatinineValue = (NumberValue)patient.getValue("CREATININE");
		BigDecimal creatinine = creatinineValue.getBigDecimal();

		// Calculating		
		int score = 0;
		if(abdomen.equals("10") || thorac.equals("10") || category.equals("40"))
			score++;
		String ischaemic = patient.getValue("STROKE") +"";
		if(ischaemic.equals("10"))
			score++;
		String congestive = patient.getValue("CONG_HEART_FAIL")+"";
		if(congestive.equals("10"))
			score++;
		String cvd = patient.getValue("CORO_ARTERY_DIS")+"";
		if(cvd.equals("10"))
			score++;
		String insulin = patient.getValue("DIAB_MELL_INSUL") +"";
		if(insulin.equals("10"))
			score++;
		if(creatinine.compareTo(new BigDecimal(177)) > 0) {
			//System.out.println("Creatinine " + creatinine + " bigger than 177");
			score++;
		}
		
		String text = score +";";
		return text;
	}
	
	/**
	*	Rounds a number to any number of decimal places
	*	@param	value	Value to round
	*	@param	decimalPlace	Number of decimal spaces
	*	@return	Rounded value
	*/
	public static double round(double value, int decimalPlace){
      double power_of_ten = 1;
      // floating point arithmetic can be very tricky.
      // that's why I introduce a "fudge factor"
      double fudge_factor = 0.05;
      while (decimalPlace-- > 0) {
         power_of_ten *= 10.0d;
         fudge_factor /= 10.0d;
      }
      return Math.round((value + fudge_factor)* power_of_ten)  / power_of_ten;
    }
	
	/**
	*	Returns the number of days a given patient stayed in the ICU
	*	@param	patient
	*	@return	Length of stay in the icu
	*/
	public String writeTime(FieldsBox patient){
		DateValue admittanceValue = (DateValue)patient.getValue("DATE_CC_ADMIT");
		DateValue dischargeValue = (DateValue)patient.getValue("DATE_CRIT_DISCH");
		
		// If the year is below 2000, return -1
		if(admittanceValue.getYear() < 2000 || dischargeValue.getYear() < 2000) {
			//System.out.println("Discharge " + dischargeValue + " or admittance " + admittanceValue + " lower than 2000.");
			return "-1;";
		}
		
		// Calculating number of days spent in the CCU
		//admittance.set(admittanceValue.getYear(), admittanceValue.getMonth(), admittanceValue.getDay);
		Calendar admittance = admittanceValue.getCalendar();
		Calendar discharge = dischargeValue.getCalendar();
		Calendar counterDate = (Calendar) admittance.clone();  
		
		long daysBetween = 0;  
		while (counterDate.before(discharge)) {  
			counterDate.add(Calendar.DAY_OF_MONTH, 1);  
			daysBetween++;  
		}
		// If the discharge date is higher than the admittance date, return -1
		if(daysBetween == 0 && admittanceValue.getDay() != dischargeValue.getDay()) {
			//System.out.println("Discharge " + dischargeValue + " higher than admittance " + admittanceValue);
			return "-1;";
		}
		return daysBetween + ";";
	}
	
	/**
	*	Subtracts two days returning days in between
	*	@param 	date1
	*	@param	date2
	*	@return	days in between
	*/
	public static int subtractDays(Date date1, Date date2){
		GregorianCalendar gc1 = new GregorianCalendar();  gc1.setTime(date1);
		GregorianCalendar gc2 = new GregorianCalendar();  gc2.setTime(date2);

		int days1 = 0;
		int days2 = 0;
		int maxYear = Math.max(gc1.get(Calendar.YEAR), gc2.get(Calendar.YEAR));

		GregorianCalendar gctmp = (GregorianCalendar) gc1.clone();
		for (int f = gctmp.get(Calendar.YEAR);  f < maxYear;  f++)
		  {days1 += gctmp.getActualMaximum(Calendar.DAY_OF_YEAR);  gctmp.add(Calendar.YEAR, 1);}

		gctmp = (GregorianCalendar) gc2.clone();
		for (int f = gctmp.get(Calendar.YEAR);  f < maxYear;  f++)
		  {days2 += gctmp.getActualMaximum(Calendar.DAY_OF_YEAR);  gctmp.add(Calendar.YEAR, 1);}

		days1 += gc1.get(Calendar.DAY_OF_YEAR) - 1;
		days2 += gc2.get(Calendar.DAY_OF_YEAR) - 1;

		return (days1 - days2);
	}  
	
	public int writeProcDuration(FieldsBox patient){
		DateValue induction = DateValue)patient.getValue("DATE_ANASTH_INDUC");
		DateValue endSurg = (DateValue)patient.getValue("DATE_END_SURG");
		if()
		
	}
	
	/**
	*	Calculatess and writes Estimated Glomerular Filtration rate 
	*	eGFR (ml/min) = 186 x (Creatinine / 88.4)-1.154 x (Age)-0.203 x (0.742 if female) x (1.210 if black)
	*	@param	patient
	*	@return	estimated glomerular filtration rate
	*/
	public String writeEgfr(FieldsBox patient){
		// Getting values
		String text;
		NumberValue creatinineValue = (NumberValue)patient.getValue("CREATININE");
		NumberValue ageValue = (NumberValue)patient.getValue("AGE");
		String sex = patient.getValue("SEX")+"";
		String ethnicity = patient.getValue("ETHNICITY")+"";
		BigDecimal creatinine = creatinineValue.getBigDecimal();
		BigDecimal age = ageValue.getBigDecimal();
		if(age.compareTo(new BigDecimal(18)) < 0){
			//System.out.println("Age " + creatinine + " lower than 18");
			return "-1";
		}
		
		// Calculating
		/*System.out.println("Creatinine: " + creatinine);
		System.out.println("Creatinine value: " + creatinine.doubleValue());
		System.out.println("Age: " + age);
		System.out.println("Age value: " + age.doubleValue());*/
		double eGFR = 186; 
		eGFR *= Math.pow((creatinine.doubleValue() / 88.4), -1.154); 
		eGFR *= Math.pow(age.doubleValue(), -0.203);
		if(sex.equals("20"))
			eGFR *= 0.742;
		if(ethnicity.equals("10"))
			eGFR *= 1.21;
		if(eGFR <= 0.0){
			text = "-1;";
			return text;
		} else {
			text = round(eGFR,2) +";";
		}
		return text;
	}
	
	/**
	*	Writes the database given, adding columns where specified in the class ArrayColumns
	*	@param	database	Database to be analysed
	*	@param	fieldNames	The field names of the database given
	*	@param	firstColumn	Number of the first column where to start analyzing
	*	@param	numberOfColumns	Number of Columns to analyze
	*	@param	databaseFile	Name of the new database file to be saved
	*	@param	typesMap	Collection specifying the types of each field in the database
	*/
	public void writeAddColumns(TreeMap<String, FieldsBox> database, ArrayList<String> fieldNames, int firstColumn, int numberOfColumns, String databaseFile, TreeMap<String, Requirement> typesMap){	
		try {
			// Creating the file writers
			FileWriter fstream = new FileWriter("Outputs/" + databaseFile + ".csv");
			BufferedWriter out = new BufferedWriter(fstream);
			
			String text;
			
			// Generating the correspondencies responsible for the new columns
			ArrayColumns correspondencies = new ArrayColumns("OR");
			
			// Writing the field names
			for(String fieldName : fieldNames){ //database.get(database.firstKey()).getFieldNames()
				if(correspondencies.hasCorrespondency(fieldName)) {
					int numberSubColumns = correspondencies.getNumberOfColumns(fieldName);
					String[] correspondents = correspondencies.getCorrespondents(fieldName);
					for(String correspondency : correspondents){
						text = fieldName + "_" + correspondency.toUpperCase() + ";";
						text = text.replaceAll(" ", "_");
						out.write(text, 0, text.length());
					}
				} else {
					text = fieldName + ";";
					out.write(text, 0, text.length());
				}
			}
			text = "\n";
			out.write(text, 0, text.length());
			
			// Writing the values
			for(String patientId : database.keySet()) {
				FieldsBox patient = database.get(patientId);
				for(String fieldName : fieldNames){ //database.get(database.firstKey()).getFieldNames()
					if(correspondencies.hasCorrespondency(fieldName)) {
						int numberSubColumns = correspondencies.getNumberOfColumns(fieldName);
						String[] correspondents = correspondencies.getCorrespondents(fieldName);
						String array = patient.getValue(fieldName) +";";
						int i=1;
						for(String correspondency : correspondents){
							if(array.indexOf(i+"")>=0){
								text = "10;";
							}
							else{
								text = "0;";
							}	
							out.write(text, 0, text.length());
							i++;
						}
					} else {
						text = patient.getValue(fieldName) +";";
						out.write(text, 0, text.length());
					}
				}
				//Writing lee score
				text = writeLeeScore(patient);
				out.write(text, 0, text.length());
				//Writing egfr
				text=writeEgfr(patient);
				out.write(text, 0, text.length());
				//Writing underage
				text=writeUnderage(patient);
				out.write(text, 0, text.length());
				//writing newline
				text = "\n";
				out.write(text, 0, text.length());
			}
		out.close();

		} catch (IOException ex){
		  System.out.println("[writeAddColumns] ERROR: Input/output error.");
		  ex.printStackTrace();
		}

	}
	/**
	*	Calculatess and writes Estimated Glomerular Filtration rate for the icu
	*	eGFR (ml/min) = 186 x (Creatinine / 88.4)-1.154 x (Age)-0.203 x (0.742 if female) x (1.210 if black)
	*	@param	patient	Patient from OR to get ethnicity
	*	@param	patient2	Patient from ICU
	*	@return	estimated glomerular filtration rate from entry, 24 and 48 hours
	*/
	public String writeEgfr2(FieldsBox patient, FieldsBox patient2){
		// Getting values
		String text = "";
		NumberValue ageValue = (NumberValue)patient.getValue("AGE");
		String sex = patient.getValue("SEX")+"";
		String ethnicity = patient2.getValue("ETHNICITY")+"";
		BigDecimal age = ageValue.getBigDecimal();
				
		if(age.compareTo(new BigDecimal(18)) < 0){
			return "-1";
		}

		// entry EGFR
		double eGFR = 186;
		// 24 hours EGFR
		double eGFR24 = 186;
		// 48 hours EGFR
		double eGFR48 = 186;
		//Calculating with entry creatinine
		NumberValue creatinineValue = (NumberValue)patient.getValue("CREATININE");
		BigDecimal creatinine = creatinineValue.getBigDecimal();
		eGFR *= Math.pow((creatinine.doubleValue() / 88.4), -1.154); 
		//Calculating with 24 hours creatinine
		creatinineValue = (NumberValue)patient.getValue("24_CREATININE");
		creatinine = creatinineValue.getBigDecimal();
		eGFR24 *= Math.pow((creatinine.doubleValue() / 88.4), -1.154);
		//Calculating with 48 hours creatinine
		creatinineValue = (NumberValue)patient.getValue("48_CREATININE");
		creatinine = creatinineValue.getBigDecimal();
		eGFR48 *= Math.pow((creatinine.doubleValue() / 88.4), -1.154);
		//Calculates with age
		eGFR *= Math.pow(age.doubleValue(), -0.203);
		eGFR24 *= Math.pow(age.doubleValue(), -0.203);
		eGFR48 *= Math.pow(age.doubleValue(), -0.203);
		
		// Checks the sex
		if(sex.equals("20")){
			eGFR *= 0.742;
			eGFR24 *= 0.742;
			eGFR48 *= 0.742;	
		}
		// Checks the ethnicity
		if(ethnicity.equals("10")){
			eGFR *= 1.21;
			eGFR24 *= 1.21;
			eGFR48 *= 1.21;
		}
		text += round(eGFR,2) +";";
		text += round(eGFR24,2) +";";
		text += round(eGFR48,2) +";";
		return text;
	}
	
	/**
	*	Writes an ICU final file writing aditional columns as required
	*	@param	database	Final icu database
	*	@param	database2	Or database to get ethnicity
	*	@param	fileName	final file name
	*/
	public void writeFinalFile(TreeMap<String, FieldsBox> database, TreeMap<String, FieldsBox> database2, String fileName){
		Readers reader  = new Readers();
		try{
			File file;
			boolean exist;
			String text;
			int fileNumber = 1;
			String finalFileName = fileName + ".csv";
			file = new File("Outputs/" + finalFileName);
			exist = file.createNewFile();
			FileWriter fstream = new FileWriter("Outputs/" + finalFileName);
			BufferedWriter out = new BufferedWriter(fstream);
			ArrayList<String> fieldNames = reader.readFieldNames("CCR");
			for(String s: fieldNames){
				out.write((s +";"), 0, (s.length() + 1));
			}
			out.write("\n",0,1);
			for(FieldsBox fb : database.values()){
				for(String fieldName : fieldNames){
					text = fb.getValue(fieldName) + ";";
					out.write(text, 0, text.length());
				}
				//Writing time in icu
				text=writeTime(fb);
				out.write(text, 0, text.length());
				//Writing underage
				text=writeUnderage(fb);
				out.write(text, 0, text.length());
				//Write egfr
				text = writeEgfr2(fb, database2.get(fb.getValue("EUSOS_ID")+""));
				out.write(text, 0, text.length());
				out.write("\n",0,1);
			}
			out.close();
		}
		catch(IOException ioe){
			System.out.println("[writeFinalFile] ERROR: Error creating file " + fileName);
		}
	}
	
	/**
	*	Checks matching IDs in both databases printing this to screen.
	*	@param	database 	Any Database
	*	@param	database2	Any Database
	*/
	public void check(TreeMap<String, FieldsBox> database, TreeMap<String, FieldsBox> database2){
		int i = 0;
		for(String id : database.keySet()){
			if(database2.containsKey(id)){
				i++;
			}
		}
	}
	
	/**
	* Main method currently writing ICU final files with new columns
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
		System.out.println("Now reading OR OK");
		TreeMap<String, FieldsBox> OROK = reader.readDatabaseOperatingRoom(new File("Outputs/V3.2/OR/OK 3.2.csv"), ORTypes);
		System.out.println("Length: " + reader.getDuplicateOR().size());
		System.out.println("Now reading OR Valid");
		TreeMap<String, FieldsBox> ORValid = reader.readDatabaseOperatingRoom(new File("Outputs/V3.2/OR/Valid OR 3.2.csv"), ORTypes);
		System.out.println("Length: " + reader.getDuplicateOR().size());
		System.out.println("Now reading OR Invalid");
		TreeMap<String, FieldsBox> ORInvalid = reader.readDatabaseOperatingRoom(new File("Outputs/V3.2/OR/Invalid OR 3.2.csv"), ORTypes);
		System.out.println("Length: " + reader.getDuplicateOR().size());
		System.out.println("Now reading CCR Pre-Cleaning database...");
		TreeMap<String, FieldsBox> ICUValid = reader.readDatabaseCriticalCareRoom(new File("Outputs/V3.2/ICU/Valid ICU 3.2.csv"), UCITypes);
		System.out.println("Done");
		System.out.println("Now reading CCR Pre-Cleaning database...");
		TreeMap<String, FieldsBox> ICUInvalid = reader.readDatabaseCriticalCareRoom(new File("Outputs/V3.2/ICU/Invalid ICU 3.2.csv"), UCITypes);
		System.out.println("Done");
		System.out.println("Fixing");
		/*System.out.println("Printing with new columns...");
		writer.writeAddColumns(ORValid, reader._ORNames, _firstColumnOR, _numberOfColumnsOR, "OR valid", ORTypes);
		System.out.println("Done");
		System.out.println("Printing with new columns...");
		writer.writeAddColumns(ORInvalid, reader._ORNames, _firstColumnOR, _numberOfColumnsOR, "OR invalid", ORTypes);
		System.out.println("Done");
		System.out.println("Printing with new columns...");
		writer.writeAddColumns(OROK, reader._ORNames, _firstColumnOR, _numberOfColumnsOR, "OR ok", ORTypes);
		System.out.println("Done");*/
		writer.writeFinalFile(ICUValid,ORValid, "ICU valid");
	}
}