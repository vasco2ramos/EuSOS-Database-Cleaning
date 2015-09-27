package Requirements;

/** 
*	General Class for the implementation of Requirements and field validating.
*	Requirements are created upon reading the requirements specification fields
*	and enforce the rules specified in said file, like range or date formats.
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/
public abstract class Requirement {

	/**
	*	Specifies if this requirement is mandatory
	**/
	protected boolean _isRequired;

	/** 
	*	Verifies if this requirement is mandatory.
	*	@return	"true" if this requirement is mandatory, "false" otherwise
	**/
	public boolean isRequired(){
		return _isRequired;
	}
	
	/** 
	*	Verifies if the input field is valid according to this requirement.
	*	@param	input	Value to be verified
	*	@return	"true" if the value complies to this requirement, "false" otherwise
	**/
	public abstract boolean isValid(String input);
	
	
	/** 
	*	Returns the name of the class that this requirement should be applied to.
	**/
	public abstract String getClassName();
	
	/** 
	*	Class constructor. Assumes the requirement to be created is not mandatory.
	**/
	public Requirement(){
		_isRequired = false;
	}

	/** 
	*	Class constructor.
	*	@param	isRequired	boolean value, specifying if the requirement is mandatory (true) or optional (false)
	**/
	public Requirement(boolean isRequired){
		_isRequired = isRequired;
	}
	
}