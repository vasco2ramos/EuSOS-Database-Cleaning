package Requirements;

/** 
*	Requirement for fields of the type "boolean".
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/
public class BooleanRequirement extends Requirement{
	
	/** 
	*	Verifies if the input field contains the number 0 (meaning "false") or 10 (meaning true).
	*	@return	"true" if the value complies to this requirement, "false" otherwise
	**/
	public boolean isValid(String input){
		if(input.equals("0")  || input.equals("10"))
			return true;
		else
			return false;
	}
	
	/** 
	*	Returns the name of the class that this requirement should be applied to.
	**/
	public String getClassName(){
		return "Boolean";
	}

	/** 
	*	Class constructor.
	*	@param	isRequired	boolean value, specifying if the requirement is mandatory (true), or optional (false)
	**/
	public BooleanRequirement(boolean isRequired){
		super(isRequired);
	}
	
}