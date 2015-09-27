package Values;

import java.math.BigDecimal;

/** 
*	Value for fields containing a numeric value.
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/

public class NumberValue implements Value{

	/** 
	*	BigDecimal is used to assure value imutability.
	*	For more information, see http://download.oracle.com/javase/7/docs/api/java/math/BigDecimal.html
	*	When the value to be saved is a string, this variable should not be initialized, in order to save space
	**/
	private BigDecimal _numberValue;
	
	/** 
	*	Specifies if the saved value is an integer number (true) or a decimal number (false).
	*	NOTE: Currently NOT being updated, always kept as "false". As such, all numbers are
	*	currently being treated as DECIMAL numbers.
	**/
	private boolean _isInteger;
	
	/**
	*	Returns the numeric value saved in this class in its original form.
	**/
	public Number getNumber(){
		if(_isInteger)
			return _numberValue.intValue();
		else
			return _numberValue.doubleValue();
	}
	
	/**
	*	Returns the numeric value saved in this class as a BigDecimal.
	**/
	public BigDecimal getBigDecimal(){
		return _numberValue;
	}
	
	/**
	*	Returns "true" if the value saved is an integer number, false if it is a decimal number or a string.
	**/
	public Boolean isInteger(){
		return _isInteger;
	}
	
	/**
	*	Compares the saved numeric value to the input.
	*	For more information, see http://download.oracle.com/javase/1.4.2/docs/api/java/math/BigDecimal.html#compareTo%28java.math.BigDecimal%29
	*/
	public int compareTo(BigDecimal input){
		return _numberValue.compareTo(input);
	}
	
	/**
	*	Compares the saved numeric value to the input.
	*	For more information, see http://download.oracle.com/javase/1.4.2/docs/api/java/math/BigDecimal.html#compareTo%28java.math.BigDecimal%29
	*/
	public int compareTo(NumberValue input){
		return _numberValue.compareTo(input.getBigDecimal());
	}

	/**
	*	Returns the stored BigDecmal as an integer
	*/
	public int getAsInt() {
		return _numberValue.intValue();
	}
	
	/** 
	*	Returns "true" if the argument is equal to this object, "false" otherwise.
	*	@param	anObject	Object to be compared to this object.
	*/
	public boolean equals(Object anObject){
		if(anObject instanceof NumberValue){
			NumberValue input = (NumberValue) anObject;
			if(_numberValue.compareTo(input.getBigDecimal()) == 0)
				return true;
		}
		return false;
	}
	
	/**
	*	Class constructor.
	*	@param	value		Value to save as a BigDecimal
	*	@param	isInteger	Boolean value (true/false), specifying if the value to save is an integer number (true) or a decimal number (false)
	**/
	public NumberValue(BigDecimal value, boolean isInteger){
		_numberValue = value;
		_isInteger = isInteger;
	}
	
	/**
	*	Class constructor.
	*	@param	value		Value to save as a BigDecimal
	*	@param	isInteger	Boolean value (true/false), specifying if the value to save is an integer number (true) or a decimal number (false)
	**/
	public NumberValue(String value, boolean isInteger){
		_isInteger = isInteger;
		String valueRemovedSpaces = value.replace(" ", "");
		_numberValue = new BigDecimal(valueRemovedSpaces);
	}
	
	/**
	*	Class constructor. Currently assumes the number is not an integer.
	*	@param	value	Value to save as a BigDecimal
	**/
	public NumberValue(String value){
		// TODO: Verify if the number is an integer
		String valueRemovedSpaces = value.replace(" ", "");
		_isInteger = false;
		if(valueRemovedSpaces.length() > 0)
			_numberValue = new BigDecimal(valueRemovedSpaces);
		else
			_numberValue = new BigDecimal(-1);
	}
	
	/**
	*	Class printer
	**/
	public String toString(){
		return "" + _numberValue;
	}
		
}