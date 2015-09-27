package Requirements;

/** 
*	Requirement for fields of the type "text" storing a date.
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/
public class DateRequirement extends Requirement{

	/** 
	*	Variable containing the form to obey, e.g. regexp: /\d{3}-\d{3}-\d{4}/
	**/
	private String _textRequirement;
	
	/** 
	*	Verifies if the input field is a date under the format "DD/MM/YYYY".
	*	@return	"true" if the value complies to this requirement, "false" otherwise
	**/
	public boolean isValid(String input){
		String[] splitDate = input.split("-");
		try {
			Integer.parseInt(splitDate[2]);
			Integer.parseInt(splitDate[1]);
			Integer.parseInt(splitDate[0]);
		} catch(NumberFormatException exception){
			System.err.println("### [DateRequirement] ERROR: unparseable integer in date ###");
			return false;
		} catch(ArrayIndexOutOfBoundsException exception){
			System.err.println("### [DateRequirement] ERROR: unknown date format ###");
			return false;
		}
		return true;
	}
	
	/** 
	*	Returns the name of the class that this requirement should be applied to.
	**/
	public String getClassName(){
		return "Date";
	}
	
	/** 
	*	Class constructor. Assumes the requirement is not mandatory.
	**/
	public DateRequirement(String textRequirement){
		super();
		_textRequirement = textRequirement;
	}

	/** 
	*	Class constructor. Assumes the requirement is not mandatory.
	*	@param	isRequired	boolean value, specifying if the requirement is mandatory (true), or optional (false)
	**/
	public DateRequirement(String textRequirement, boolean isRequired){
		super(isRequired);
		_textRequirement = textRequirement;
	}
	
	/** 
	*	Class constructor.
	*	@param	isRequired	boolean value, specifying if the requirement is mandatory (true), or optional (false)
	**/
	public DateRequirement(boolean isRequired){
		super(isRequired);
	}
}