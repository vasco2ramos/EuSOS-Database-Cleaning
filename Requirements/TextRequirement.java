package Requirements;

/** 
*	Requirement for fields of the type "text".
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/
public class TextRequirement extends Requirement{

	/** 
	*	Variable containing the form to obey, e.g. regexp: /\d{3}-\d{3}-\d{4}/
	**/
	private String _textRequirement;
	
	/** 
	*	Verifies if the input field is valid according to this requirement.
	*	NOTE: Not currently functional, awaiting implementation. Currently accepts any form of text.
	*	@return	"true" if the value complies to this requirement, "false" otherwise
	**/
	public boolean isValid(String input){
		// not yet implemented
		return true;
	}
	
	/** 
	*	Returns the name of the class that this requirement should be applied to.
	**/
	public String getClassName(){
		return "Text";
	}
	
	/** 
	*	Class constructor. Assumes the requirement is not mandatory.
	*	@param	textRequirement	String containing the form to obey, e.g. regexp: /\d{3}-\d{3}-\d{4}/
	**/
	public TextRequirement(String textRequirement){
		super();
		_textRequirement = textRequirement;
	}

	/** 
	*	Class constructor.
	*	@param	textRequirement	String containing the form to obey, e.g. regexp: /\d{3}-\d{3}-\d{4}/
	*	@param	isRequired	boolean value, specifying if the requirement is mandatory (true), or optional (false)
	**/
	public TextRequirement(String textRequirement, boolean isRequired){
		super(isRequired);
		_textRequirement = textRequirement;
	}
	
	/** 
	*	Class constructor.
	*	@param	isRequired	boolean value, specifying if the requirement is mandatory (true), or optional (false)
	**/
	public TextRequirement(boolean isRequired){
		super(isRequired);
	}
}