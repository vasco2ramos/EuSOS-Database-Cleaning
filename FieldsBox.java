import Values.*;

import java.util.Set;
import java.util.TreeMap;
import java.math.BigDecimal;

/**
*	Class that stores fields, indicating their name and field value. 
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
*/
public class FieldsBox {

	/** 
	*	Tree map where integer or decimal values (Value) are stored and sorted by field name (String).
	**/
	private TreeMap<String, Value> _fieldValues;
	
	/** 
	*	Saves a numeric value.
	*	@param	name		Name of the field from where the value was read
	*	@param	value		Numeric value represented as a BigDecimal
	*	@param	isInteger	Boolean variable (true/false), indicating if the value to be saved is an integer (true) or decimal (false)
	**/
	public void insertValue(String name, BigDecimal value, boolean isInteger){
		_fieldValues.put(name, new NumberValue(value, isInteger));
	}
	
	/** 
	*	Saves a value.
	*	@param	name		Name of the field from where the value was read
	*	@param	value		Value to be saved
	**/
	public void insertValue(String name, Value value){
		_fieldValues.put(name, value);
	}
	
	/** 
	*	Returns the value associated with the field's name.
	*	@param	name	Name of the field to which the value to return is associated
	**/
	public Value getValue(String name){
		return _fieldValues.get(name);
	}
	
	/** 
	*	Returns the fieldnames used as keys
	**/
	public Set<String> getFieldNames(){
		return _fieldValues.keySet();
	}

	/**
	*	Returns "true" if the value associated with the name of the field is a number, "false" otherwise.
	*	@param	name	Name of the field to which the value to return is associated
	*	@return boolean value specifying if it is a number
	**/
	public boolean isValueNumber(String name){
		return (_fieldValues.get(name) instanceof NumberValue);
	}
	
	/**
	*	Returns "true" if the value associated with the name of the field is a date, "false" otherwise.
	*	@param	name	Name of the field to which the value to return is associated
	*	@return boolean value specifying if it is a date
	**/
	public boolean isValueDate(String name){
		return (_fieldValues.get(name) instanceof DateValue);
	}
	
	/**
	*	Returns "true" if the value associated with the name of the field is an array of numbers, "false" otherwise.
	*	@param	name	Name of the field to which the value to return is associated
	*	@return boolean value specifying if it is an array of numbers
	**/
	public boolean isValueNumberArray(String name){
		return (_fieldValues.get(name) instanceof NumberArrayValue);
	}
	
	/**
	*	Returns the name of all fields saved in this FieldsBox.
	**/
	public int getSize(){
		return _fieldValues.size();
	}
	
	/**	
	*	Class constructor.
	*/
	public FieldsBox(){
		_fieldValues = new TreeMap<String, Value>();
	}
	
	/**
	*	Class writer.
	*/
	public String toString(){
		String s = "";
		for(Value v : _fieldValues.values())
			s += v + ";";
		return s;
	}
}
