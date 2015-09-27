package Requirements;

import java.math.BigDecimal;

/** 
*	Requirement for fields containing multiple integer or double values.
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/
public class ValuesRequirement extends Requirement{
	
	/**
	*	Possible values
	**/
	private BigDecimal[] _values;

	/** 
	*	Verifies if the input field contains only number values.
	*	@return	"true" if the value complies to this requirement, "false" otherwise
	**/
	public boolean isValid(String input){
		// Verifies if the input contains multiple integer / double values sperated by ","
		String[] splitValues = input.split(",");
		for(int i = 0; i < splitValues.length; i++){	
			try{
				Double.parseDouble(splitValues[i]);
			}
			catch(NumberFormatException nfe){
				System.err.println("### [ValuesRequirement] ERROR: unparseable number in " + splitValues[i]  + ", part of " + input);
				return false;
			}
		}
		return true;
	}

	/** 
	*	Returns the name of the class that this requirement should be applied to.
	**/
	public String getClassName(){
		return "ValueArray";
	}
	
	/** 
	*	Class constructor. Assumes the requirement is not mandatory.
	*	@param	values	Input string containing the values to store, separated by commas.
	**/
	public ValuesRequirement(String values){
		super();
		String[] splitValues = values.split(",");
		_values = new BigDecimal[splitValues.length];
		for(int i = 0; i < splitValues.length; i++){
			_values[i] = new BigDecimal(splitValues[i]);
		}
	}
	
	/** 
	*	Class constructor
	*	@param	values	Input string containing possible values to be accepted, separated by commas.
	*	@param	isRequired	boolean value, specifying if the requirement is mandatory (true), or optional (false)
	**/
	public ValuesRequirement(String values, boolean isRequired){
		super(isRequired);
		String[] splitValues = values.split(", ");
		_values = new BigDecimal[splitValues.length];
		for(int i = 0; i < splitValues.length; i++){
			if(i == splitValues.length - 1){
				String temp = splitValues[i];
				_values[i] = new BigDecimal(temp.substring(0,temp.length()-1));
			}else
				_values[i] = new BigDecimal(splitValues[i]);
		}
		
	}
	
	
	
}