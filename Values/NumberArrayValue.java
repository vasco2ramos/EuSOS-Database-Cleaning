package Values;

import java.util.ArrayList;
import java.math.BigDecimal;

/** 
*	Value for fields containing more than a numeric value.
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/

public class NumberArrayValue implements Value{

	/** 
	*	Saves a collection of numeric values.
	**/
	private ArrayList<NumberValue> _numberArrayValue = new ArrayList<NumberValue>();
	
	/**
	*	Returns true if the given BigDecimal is present in the stored collection as a NumberValue.
	*	@param	number	BigDecimal to search for.
	**/
	public boolean hasNumber(BigDecimal number){
		return _numberArrayValue.contains(new NumberValue(number, true));
	}
	
	/**
	*	Returns true if the given NumberValue is present in the stored collection.
	*	@param	number	NumberValue to be verified.
	**/
	public boolean hasNumber(NumberValue number){
		return _numberArrayValue.contains(number);
	}

	/** 
	*	Adds a new numeric value to the collection.
	*	@param	numberValue	NumberValue to add to the array
	*/
	public void add(NumberValue numberValue){
		_numberArrayValue.add(numberValue);
	}
	
	/**
	*	Returns the saved collection.
	*/
	public ArrayList getNumberValues(){
		return _numberArrayValue;
	}
	
	/**
	*	Class constructor.
	*	@param	values		Collection to be stored
	**/
	public NumberArrayValue(ArrayList<NumberValue> values){
		_numberArrayValue = values;
	}
	
	/**
	*	Class constructor.
	**/
	public NumberArrayValue(){
		_numberArrayValue = new ArrayList<NumberValue>();
	}
	
	/** 
	*	Class constructor.
	*	@param	values	String containing the sequence of value to be stored, separated by commas.
	**/
	public NumberArrayValue(String values){
		String[] numbers = values.split(",");
		if(numbers.length > 0){
			for(String p : numbers){
				if(p.endsWith("0")){
					NumberValue x = new NumberValue(p); 
					_numberArrayValue.add(x);
				}
				else{
					NumberValue x = new NumberValue(p.concat("0")); 
					_numberArrayValue.add(x);
				}
			}
		}
		else{
			NumberValue x = new NumberValue(values);
			_numberArrayValue.add(x);
		}
	
	}
	
	/**
	* Class printer.
	*/
	public String toString(){
		String answer = "";
		for(NumberValue n : _numberArrayValue){
			answer += (n + ",");
		}
		if(answer.equals("")){
			return "-1";
		}
		else{
			return answer.substring(0, answer.length()-2);
		}
	}
}