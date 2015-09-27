package Requirements;

/** 
*	Requirement for fields of the type "number".
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/
public class NumberRequirement extends Requirement{
	
	/** 
	*	Verifies if the input field contains a numeric value.
	*	@return	"true" if the value complies to this requirement, "false" otherwise
	**/
	public boolean isValid(String input){
		try {
			Double.parseDouble(input);
			return true;
		}
		catch(NumberFormatException nfe){
			System.err.println("### [NumberRequirement] ERROR: unparseable number in " + input);
			return false;
		}
	}
	
	/** 
	*	Returns the name of the class that this requirement should be applied to.
	**/
	public String getClassName(){
		return "Number";
	}
	
	/** 
	*	Class constructor. Assumes the requirement is not mandatory.
	**/
	public NumberRequirement(){
		super();
	}
	
	/** 
	*	Class constructor.
	*	@param	isRequired	boolean value, specifying if the requirement is mandatory (true), or optional (false)
	**/
	public NumberRequirement(boolean isRequired){
		super(isRequired);
	}

}