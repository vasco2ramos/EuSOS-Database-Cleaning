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
		/*System.out.println("Reading Mails");
		TreeMap<String, Email> mailDatabase = reader.readEmails(new File("mails.csv"));*/
		System.out.println("Crosschecking OR->UCI Database");
		cleaner.ORValidPatients(ORDatabase, UCIDatabase, ORTypes, UCITypes);
		System.out.println("Crosschecking UCI->OR Database");
		cleaner.ICUValidPatients(ORDatabase, UCIDatabase, ORTypes, UCITypes);