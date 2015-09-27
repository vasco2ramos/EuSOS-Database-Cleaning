import java.io.*;
import java.util.*;
import java.math.BigDecimal;
import Values.*;
import Requirements.*;

/** 
*	Class containing methods that fixes the wrong data present in databases.
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/

public class Fixers {

	/**
	* Tries to fix an ID by comparing "eusos_id" and "subjId" to see if they match.
	* @param	patient	Patient whose ID is to be fixed.
	* @return	The fixed ID or, if the function is unable to fix the id, the old eusosId
	*/
	public String fixIDBySubjID(FieldsBox patient){
		String subjId = ((TextValue) patient.getValue("SubjID")).getID();
		String eusosId = ((TextValue) patient.getValue("EUSOS_ID")).getID();
		String fixed = fixSubjID(subjId);
		if(fixed.equals(eusosId))
			return eusosId;
		else
			return fixed;
	}
	
	/**
	* Receives a malformed ID and produces a id with the correct format.
	* @param	subjId	Malformed id
	* @return	Fixed id
	*/
	public String fixSubjID(String subjId){
		// Will clean extra characters like spaces or unneeded separators
		String clean = subjId.replace(" ","");
		clean = clean.replace("-","");
		// Will fill missing 0's and add - separators
		String fixed = "";
		// Subj ID has type XXX.XXX.XXXX (. illustrate division and are not included)
		if(clean.length() == 10)
			fixed += (clean.substring(0,3) + "-" + clean.substring(3,6) + "-" + clean.substring(6,10));
		// Subj ID has type 0XX.XXX.XXXX, 0 is ommited by excel (. illustrate division and are not included)
		else if(clean.length() == 9)
			fixed += ("0" + clean.substring(0,2) + "-" + clean.substring(2,5) + "-" + clean.substring(5,9));
		else{
			System.out.println("Unable to fix id: " + subjId);
			return "error";
		}
		return fixed;
	}	
	/**
	* Reads a database file and a user list, crosschecks and fixes the country code and investigator code.
	* XXX-XXX-....(patient id not fixed), this writes 3 files, 1 with inconsistencies listed, a database with cleaned and fixed ids
	* and one database with wrong ids.
	* @param	database	Database collection.
	* @param	userList	Utilitary file used to know each user code and name.
	*/
	public void fixIDs(TreeMap<String, FieldsBox> database, ArrayList<Email> userList, String databaseType){	
		Writers writer = new Writers();
		// The Patients with correct IDs will be stored here
		TreeMap<String, FieldsBox> wrongPatients =  new TreeMap<String, FieldsBox>();
		// The Patients with incorrect IDs will be stored here
		TreeMap<String, FieldsBox> fixedPatients = new TreeMap<String, FieldsBox>();
		// The inconsistencies found will be stored here
		String inconsistencies = "";
		
		if(databaseType.equals("ICU") || databaseType.equals("UCI") || databaseType.equals("CCR")){
			for(FieldsBox fb : database.values()){
				int added = 0;
				TextValue username = (TextValue) fb.getValue("InterviewerName_A"); 
				for(Email e : userList){
					if(added == 1)
						break;
					else if(e.getUsername().equalsIgnoreCase(username.getID())){
						added=1;
						TextValue id = (TextValue) fb.getValue("EUSOS_ID");
						if(id.getID().startsWith(e.getID()))
							wrongPatients.put(id.getID(), fb);
						else{
							if(id.getID().length() <10){
								wrongPatients.put(id.getID(), fb);
								inconsistencies += "The following patient is still wrong: ";
								inconsistencies += (writer.writePatient(fb, new File("CCR Variables.csv")) + "\n");
							}	
							else{
								// Fix ID
								String wrongID = id.getID();
								FieldsBox fixedPatient = fb;
								String rightID = wrongID.replaceAll(wrongID.substring(0,7),e.getID());
								// Insert patient with fixed ID
								fixedPatient.insertValue("EUSOS_ID", new TextValue(rightID));
								inconsistencies += (wrongID + " is wrong and will be fixed to " + rightID + "\n");
								fixedPatients.put(rightID, fixedPatient);
							}
						}
					}
					
				}
				if(added==0){
					System.out.println("not found--->"+username.getID()+"<---");
					TextValue id = (TextValue) fb.getValue("EUSOS_ID");
					wrongPatients.put(id.getID(), fb);
				}
			}
			
		// Writing files, can be optimized to give database names automatically
		writer.writeFile("Wrong ID's ICU", inconsistencies, ".rtf");
		writer.writeDatabase("Fixed Patients ICU", fixedPatients, "ICU");
		writer.writeDatabase("Invalid again ICU", wrongPatients, "ICU");
		}
		else{
			for(FieldsBox fb : database.values()){
				int added = 0;
				TextValue username = (TextValue) fb.getValue("InterviewerName_A"); 
				for(Email e : userList){
					if(added == 1)
						break;
					else if(e.getUsername().equalsIgnoreCase(username.getID())){
						added=1;
						TextValue id = (TextValue) fb.getValue("EUSOS_ID");
						if(id.getID().startsWith(e.getID()))
							wrongPatients.put(id.getID(), fb);
						else{
							if(id.getID().length() <10){
								wrongPatients.put(id.getID(), fb);
								inconsistencies += "The following patient doesnt have an appropriate ID: ";
								inconsistencies += (writer.writePatient(fb, new File("OR Variables.csv")) + "\n");
							}
							else{
								// Fix ID
								String wrongID = id.getID();
								FieldsBox fixedPatient = fb;
								String rightID = wrongID.replaceAll(wrongID.substring(0,7),e.getID());
								// Insert patient with fixed ID
								fixedPatient.insertValue("EUSOS_ID", new TextValue(rightID));
								inconsistencies += (wrongID + " is wrong and will be fixed to " + rightID + "\n");
								fixedPatients.put(rightID, fixedPatient);
							}
						}
					}
				}
				if(added==0){
					System.out.println("not found--->"+username.getID()+"<---");
					TextValue id = (TextValue) fb.getValue("EUSOS_ID");
					wrongPatients.put(id.getID(), fb);
				}
			}
			// Writing files, can be optimized to give database names automatically
			writer.writeFile("Wrong ID's OR", inconsistencies, ".rtf");
			writer.writeDatabase("Fixed Patients OR", fixedPatients, "OR");
			writer.writeDatabase("Invalid again OR", wrongPatients, "OR");
		}
		
	}
	
	/**
	* Checks if duplicate patients sex and age match with their brother ID in the complete databsae
	* if they match they are removed from base and considered as really duplcate
	* @param	duplicateDatabase	Duplicate patients
	* @param	database			Complete database
	* @return	duplicated			Duplicated which are diferent from their brothers
	*/
	public ArrayList<FieldsBox> fixDuplicatePatients(ArrayList<FieldsBox> duplicateDatabase, TreeMap<String, FieldsBox> database){
		//Auxiliary with number of patient to be removed
		int rem = 0;
		//Duplicated database
		ArrayList<FieldsBox> duplicated = new ArrayList<FieldsBox>(duplicateDatabase);
		for(FieldsBox patient1 : duplicateDatabase){
			String eusosID = ((TextValue) patient1.getValue("EUSOS_ID")).getID();
			if(database.containsKey(eusosID)){
				FieldsBox patient2 = database.get(eusosID);
				// Getting age and sex
				NumberValue age1 = (NumberValue)patient1.getValue("AGE");
				NumberValue sex1 = (NumberValue)patient1.getValue("SEX");
				NumberValue age2 = (NumberValue)patient2.getValue("AGE");
				NumberValue sex2 = (NumberValue)patient2.getValue("SEX");
				// End of getting age and sex
				// Comparing
				if(age1.equals(age2) && sex1.equals(sex2)){
					duplicated.remove(rem);
					rem--;
				}
				
			}
			rem++;
		}
		return duplicated;
	}
	
	/**
	* Gets a creatinine value checks its unit and converts creatinine value if needed
	* @param	creatinine	Value of creatinine
	* @return	Creatinine with unit converted to micromol/L
	*/
	public Value fixCreatinine(Value creatinine){
		BigDecimal creat = ((NumberValue) creatinine).getBigDecimal();
		if(creat.doubleValue() == 0){
			return new NumberValue(new BigDecimal(-1), true);
		}
		else if(creat.doubleValue() < 20 && creat.doubleValue() != -1){
			creat = creat.multiply(new BigDecimal(88.4));
			BigDecimal rounded = creat.setScale(2, BigDecimal.ROUND_HALF_UP);
			return new NumberValue(rounded, true);
		}
		else
			return creatinine;
	}
	/**
	* Gets a haemoglobin value checks its unit and converts value if needed
	* @param	haemoglobin	Value of haemoglobin
	* @return	Haemoglobin with unit converted to g/L
	*/
	public Value fixHaemoglobin(Value haemoglobin){
		BigDecimal haemo = ((NumberValue) haemoglobin).getBigDecimal();
		if(haemo.doubleValue() == 0){
			return new NumberValue(new BigDecimal(-1), true);
		}
		else if(haemo.doubleValue() < 20 && haemo.doubleValue()!= -1){
			haemo = haemo.multiply(new BigDecimal(10));
			BigDecimal rounded = haemo.setScale(0, BigDecimal.ROUND_HALF_UP);
			return new NumberValue(rounded, true);
		}
		else
			return haemoglobin;
		
	}
	/**
	* Gets a urea value checks its unit and converts urea value if needed, might use unit since values might be close between 2 values
	* @param	urea	Value of urea
	* @param	unit	unit doctor specified
	* @return	Creatinine with unit converted to micromol/L
	*/
	public Value fixUrea(Value urea, Value unit){
		BigDecimal ur = ((NumberValue) urea).getBigDecimal();
		if(ur.doubleValue() == 0){
			return new NumberValue(new BigDecimal(-1), true);
		}
		else if(ur.doubleValue() > 7){
			if(ur.doubleValue() > 8){
				ur = ur.multiply(new BigDecimal(0.357));
				BigDecimal rounded = ur.setScale(2, BigDecimal.ROUND_HALF_UP);
				return new NumberValue(rounded, true);
			}
			else{
				String un = (NumberValue)urea + "";
				if(un.equals("1")){
				}
				else{
					ur = ur.multiply(new BigDecimal(0.357));
					BigDecimal rounded = ur.setScale(2, BigDecimal.ROUND_HALF_UP);
					return new NumberValue(rounded, true);
				}
			}
		}
		return urea;		
	}	

	/**
	* Gets a pao2 value checks its unit and converts pao2 value if needed
	* @param	pao2	Value of pao2
	* @return	pao2 with unit converted to kPa
	*/
	public Value fixPao2(Value pao2){
		BigDecimal pao = ((NumberValue) pao2).getBigDecimal();
		if(pao.doubleValue() > 30 && pao.doubleValue()!= -1 && pao.doubleValue() != 0){
			pao = pao.multiply(new BigDecimal(0.133));
			BigDecimal rounded = pao.setScale(2, BigDecimal.ROUND_HALF_UP);
			return new NumberValue(rounded, true);
		}
		else
			return pao2;
		
	}

	/**
	* Gets a bilirubin value checks its unit and converts bilirubin value if needed
	* @param	bilirubin	Value of bilirubin
	* @return	Bilirubin with unit converted to micromol/L
	*/
	public Value fixBilirubin(Value bilirubin){
		BigDecimal bili = ((NumberValue) bilirubin).getBigDecimal();
		if(bili.doubleValue() < 1.40 && bili.doubleValue()!= -1 && bili.doubleValue() != 0){
			bili = bili.multiply(new BigDecimal(17.1));
			BigDecimal rounded = bili.setScale(2, BigDecimal.ROUND_HALF_UP);
			return new NumberValue(rounded, true);
		}
		else
			return bilirubin;
	}
	/**
	* Fixes OR database M_SEN_ANAS and M_SEN_SURGEON columns which values might be switched comparing its values with the original database
	* @param	databaseOriginal original database
	* @param	databaseModified modified database
	* @param	finalName	name of the file produced
	*/
	public void fixORDatabase(TreeMap<String, FieldsBox> databaseOriginal, TreeMap<String, FieldsBox> databaseModified, String finalName){
		Writers writer = new Writers();
		for(FieldsBox patient : databaseModified.values()){
			int fix = 0;
			String id = ((TextValue) patient.getValue("EUSOS_ID")) +"";
			FieldsBox originalPatient = databaseOriginal.get(id);
			if(databaseOriginal.containsKey(id)){
				String original = originalPatient.getValue("M_SEN_ANAS") +"";
				String copy = patient.getValue("M_SEN_ANAS") +"";
				if(!original.equals(copy)&&fix!=1){fix=1;}
				original = originalPatient.getValue("M_SEN_SURGEON") +"";
				copy = patient.getValue("M_SEN_SURGEON") +"";
				if(!original.equals(copy)&&fix!=1){fix=1;}
				original = originalPatient.getValue("METAST_CANC") +"";
				copy = patient.getValue("METAST_CANC") +"";
				if(!original.equals(copy)&&fix!=1){fix=1;}
				original = originalPatient.getValue("MIN_ANASTH_INDUC") +"";
				copy = patient.getValue("MIN_ANASTH_INDUC") +"";
				if(!original.equals(copy)&&fix!=1){fix=1;}
				original = originalPatient.getValue("MIN_END_SURG") +"";
				copy = patient.getValue("MIN_END_SURG") +"";
				if(!original.equals(copy)&&fix!=1){fix=1;}
				original = originalPatient.getValue("MIN_POST_ANAS_RECOV") +"";
				copy = patient.getValue("MIN_POST_ANAS_RECOV") +"";
				if(!original.equals(copy)&&fix!=1){fix=1;}
			}
			if(fix==1){
				patient.insertValue("M_SEN_ANAS", originalPatient.getValue("M_SEN_ANAS"));
				patient.insertValue("M_SEN_SURGEON", originalPatient.getValue("M_SEN_SURGEON"));
				patient.insertValue("METAST_CANC", originalPatient.getValue("METAST_CANC"));
				patient.insertValue("MIN_ANASTH_INDUC", originalPatient.getValue("MIN_ANASTH_INDUC"));
				patient.insertValue("MIN_END_SURG", originalPatient.getValue("MIN_END_SURG"));
				patient.insertValue("MIN_POST_ANAS_RECOV", originalPatient.getValue("MIN_POST_ANAS_RECOV"));
			}
		}
		writer.writeDatabase(finalName, databaseModified, "OR");
	}	
	
	/**
	* Main method, currently fixing an OR database
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
	public static void main(String args[]){
		Readers reader  = new Readers();
		Cleaners cleaner = new Cleaners();
		Fixers fixer = new Fixers();
		Writers writer = new Writers();
		System.out.println("Reading Operating Room Utils...");
		TreeMap<String,Requirement> ORTypes = reader.readOperatingRoomUtils(new File("OperatingRoomUtil.csv"));
		System.out.println("Done");
		System.out.println("Now reading Original");
		TreeMap<String, FieldsBox> original = reader.readDatabaseOperatingRoom(new File("original.csv"), ORTypes);
		System.out.println("Done");
		System.out.println("Now reading OR Valid");
		TreeMap<String, FieldsBox> ORValid = reader.readDatabaseOperatingRoom(new File("Outputs/V3.0/OR/Invalid OR 2.0.csv"), ORTypes);
		System.out.println("Done");

		fixer.fixORDatabase(original, ORValid, "Invalid");
	}
}