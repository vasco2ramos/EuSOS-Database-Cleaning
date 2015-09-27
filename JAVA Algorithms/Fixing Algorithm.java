		/*
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
		TreeMap<String, FieldsBox> UCIDatabase = reader.readDatabaseCriticalCareRoom(new File("Outputs/V1.1/ICU/Valid CCR Patients 1.1.csv"), UCITypes);
		System.out.println("Done");
		System.out.println("Now reading countries database...");
		TreeMap<String, String> countries = reader.readCountries(new File("Countries.csv"));
		System.out.println("Done\n");
		
		System.out.println("Age/Sex mismatches" + cleaner.verifyAgeSexMismatch(ORDatabase,UCIDatabase));
		*/
		// -------------------------------------------------------------
		// ------------------Crosschecking Databases--------------------
		/*System.out.println("Crosschecking UCI->OR Database");
		cleaner.cleanICUPatients(ORDatabase, UCIDatabase, ORTypes, UCITypes);
		System.out.println("Crosschecking OR->UCI Database");
		cleaner.cleanORPatients(ORDatabase, UCIDatabase, ORTypes, UCITypes);
		*/
		// ------------------------------------------------------
	/*
		// ------------------ICU FIX ID's--------------------
		System.out.println("Reading Mails");
		ArrayList<Email> mailDatabase = reader.readEmails(new File("mails.csv"));
		System.out.println("Reading Invalid ICU patients");
		UCIDatabase = reader.readDatabaseCriticalCareRoom(new File("Outputs/Invalid CCR Patients.csv"), UCITypes);
		System.out.println("Done");
		fixer.fixIDs(UCIDatabase,mailDatabase, "ICU");
		// ----------------------------------------------------		
		// --------------------OR FIX ID's---------------------
		System.out.println("Reading Invalid OR patients");
		ORDatabase = reader.readDatabaseOperatingRoom(new File("Outputs/Invalid OR Patients.csv"), ORTypes);
		System.out.println("Done");
		fixer.fixIDs(ORDatabase,mailDatabase,"OR");
		// ----------------------------------------------------
		*/
		/*
		// ------------------Counting patients by country--------------------
		TreeMap<String, Integer> counter = cleaner.verifyCountries(countries,UCIDatabase);
		System.out.println("For ICU");
		for(String country : counter.keySet()){
				System.out.println(country + ": " + counter.get(country));
			}
		System.out.println("For OR");
		counter = cleaner.verifyCountries(countries,ORDatabase);
		for(String country : counter.keySet()){
				System.out.println(country + ": " + counter.get(country));
		}
		// ----------------------------------------------------
		*/
		
		/*
		// ------------------Producing corrected databases--------------------
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
		System.out.println("Application successfully finished");*/