package Values;

/** 
*	Value for fields of the type "boolean".
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/
public class BooleanValue implements Value{

	/** Boolean value to store */
	private Boolean _booleanValue;
	
	/** Boolean value checking if the field is empty */
	private Boolean _missing;
	
	/**
	*	Returns the stored boolean value.
	*	@return	"true" if the original value was "10", "false" if it was "0", "null" if the field was empty.
	*/
	public Boolean getBoolean(){
		if(_missing)
			return null;
		else
			return _booleanValue;
	}
	
	/**
	*	Class constructor.
	*	@param	value	Original field, containing "10" for true, "0" for false or possibly empty.
	*/
	public BooleanValue(String value){
		_missing = false;
		if(value.equals("10"))
			_booleanValue = true;
		else if(value.equals("0"))
			_booleanValue = false;
		else
			_missing = true;
	}
	
	/**
	* Class printer
	*/
	public String toString(){
		if(_missing)
			return "-1";
		else if(_booleanValue)
			return "10";
		else
			return "0";
	}
	
		
	public String getType(){
		return "Boolean";
	}
}