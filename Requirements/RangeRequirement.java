package Requirements;

import java.math.BigDecimal;

/** 
*	Requirement for fields of the type "number" that must be contained inside a range,
*	i.e., above a minimum value and below a maximum value.
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/
public class RangeRequirement extends Requirement{
	
	/** 
	* Specifies the range's minimum limit value
	*/
	private BigDecimal _minLimit;
	
	/** 
	* Specifies the range's maximum limit value
	*/
	private BigDecimal _maxLimit;
	
	/** 
	*	Verifies if the input field is valid according to this requirement.
	*	@return	"true" if the value complies to this requirement, "false" otherwise
	**/
	public boolean isValid(String input){
		// Verify if the input is a number
		try{
			Double.parseDouble(input);
		}
		catch(NumberFormatException nfe){
			System.err.println("### [RangeRequirement] ERROR: unparseable number in " + input);
			return false;
		}
		// Verify if the number is contained inside the range
		BigDecimal number = new BigDecimal(input);
		if(number.compareTo(_minLimit) < 0){
			System.err.println("### [RangeRequirement] ERROR: input " + input + " below acceptable limit " + _minLimit);
			return false;
		}
		if(number.compareTo(_maxLimit) > 0){
			System.err.println("### [RangeRequirement] ERROR: input " + input + " above acceptable limit: " + _maxLimit);
			return false;
		}
		return true;
	}

	/** 
	*	Returns the name of the class that this requirement should be applied to.
	**/
	public String getClassName(){
		return "Range";
	}
	
	/** 
	*	Class constructor. Assumes the requirement is not mandatory.
	*	@param	range	range represented as "func: range(x,y)"
	**/
	public RangeRequirement(String range){
		super();
		String[] temp = range.split("-");
		_minLimit = new BigDecimal(temp[0]);
		_maxLimit = new BigDecimal(temp[1]);
	}
	
	/** 
	*	Class constructor.
	*	@param	range		range represented as "func: range(x,y)"
	*	@param	isRequired	boolean value, specifying if the requirement is mandatory (true) or optional (false).
	**/
	public RangeRequirement(String range, boolean isRequired){
		super(isRequired);
		String[] temp = range.split("-");
		_minLimit = new BigDecimal(temp[0]);
		_maxLimit = new BigDecimal(temp[1]);
	}
}