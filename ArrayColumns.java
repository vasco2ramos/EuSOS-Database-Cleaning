import java.util.TreeMap;

/**	
*	This class functions as a correspondency holder for number array values.
*	It stores the correspondencies for each variable and implements a converter
*	to turn number values into booleans, in the respective columns. 
*/

public class ArrayColumns {

	/** 
	*	Stores the information for each variable in a string array contaning
	*	the names of the subcolumns.
	*/
	private TreeMap<String,String[]> _correspondencies;

	/** 
	* Creates an array column correspondency according to
	* the name of the database provided.
	* @param	database	Name of the intended database.*/
	public ArrayColumns(String database){
		_correspondencies = new TreeMap<String,String[]>();
		if(database.equals("OR")) {
			// Anaesthetic technique
			String[] correspondent = {"General", "Spinal", "Epidural", "Sedation", "Local", "Other regional"};
			_correspondencies.put("ANASTH_TECH", correspondent);
			correspondent = null;
			// Cardiac output monitoring
			String[] correspondentCard = {"Doppler Ultrasound", "Art wave form", "Pulm art cat", "Other", "None"};
			_correspondencies.put("CARD_OUT_MONIT", correspondentCard);
			correspondentCard = null;
			/*
			NOTE: If you desire to print more elaborated columns pertaining
				  the following variables, un-comment them following code.
			
			// Urgency of surgery
			String[] correspondentUrg = {"Elective", "Urgent", "Emergency"};
			_correspondencies.put("URG_SURG", correspondentUrg);
			correspondentUrg = null;
			
			// ASA
			String[] correspondentAsa = {"I", "II", "III", "IV", "V"};
			_correspondencies.put("ASA", correspondent);
			correspondentAsa = null;
			
			// Most senior anaesthetist
			String[] correspondentSen = {"Attending", "Middle grade", "Junior"};
			_correspondencies.put("M_SEN_ANAS", correspondentSen);
			correspondentSen = null;
			// Most senior surgeon
			String[] correspondentSenSurg = {"Attending", "Middle grade", "Junior"};
			_correspondencies.put("M_SEN_SURGEON", correspondentSenSurg);
			correspondentSenSurg = null;

			// Severity of surgery
			String[] correspondentSurg = {"Minor", "Intermediate", "Major"};
			_correspondencies.put("GRADE_SURG", correspondentSurg);
			correspondentSurg = null;
			
			// Surgical procedure category (select single most appropiate)
			String[] correspondentCate = {"Select One", "Orthopaedics", "Breast", "Gynaecological", "Vascular",
											 "Upper gastro-intestinal", "Lower gastro-intestinal", "Hepato-biliary",
											 "Plastics/Cutaneous", "Urological", "Kidney", "Head and Neck", "Other"};
			_correspondencies.put("SURG_PROC_CATE", correspondentCate);
			correspondentCate = null;
			*/
		} else if(database.equals("ICU")){
			System.out.println("[ArrayColumns] ERROR: ICU not implemented.");
		} else
			System.out.println("[ArrayColumns] ERROR: Database name not recognized.");
	}
	
	/** 
	*	Returns the correspondent value for the received variable and number.
	*	@param	variable	Key of the correspondency.
	*	@param	number		Number of the correspondency.
	*/
	public String getCorrespondent(String variable, int number){
		String[] correspondency = _correspondencies.get(variable);
		int position = number;
		if((position % 10) == 0)
			position = number / 10;
		return correspondency[position - 1];
	}
	
	/** 
	*	Returns all the correspondencies for the given variable.
	*	@param	variable	Key of the correspondency.
	*/
	public String[] getCorrespondents(String variable){
		return _correspondencies.get(variable);
	}
	
	/** 
	*	Returns the number of correspondencies for a given variable.
	*	@param	variable	Key of the correspondency.
	*/
	public int getNumberOfColumns(String variable){
		String[] correspondency = _correspondencies.get(variable);
		return correspondency.length;
	}
	
	/**
	*	Checks if there is a correspondency for a given variable
	*	@param	variable	Key of the correspondency.
	*/
	public boolean hasCorrespondency(String variable){
		return _correspondencies.containsKey(variable);
	}
	
}