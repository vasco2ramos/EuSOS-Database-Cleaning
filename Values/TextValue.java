package Values;

/** 
*	Value for fields containing a text value.
*
*	@author	Andre Alonso
*	@author	Vasco Ramos
**/

public class TextValue implements Value{

	/** 
	*	Stores the text value.
	**/
	private String _text;
	
	/**
	*	Returns the text saved.
	**/
	public String getID(){
		return _text;
	}
	
	/**
	*	Sets the text value to a given text value.
	*	@param	text	New text value to store
	**/
	public void setID(String text){
		_text = text;
	}

	/**
	*	Compares the saved numeric value to the input.
	*	For more information, see http://download.oracle.com/javase/1.4.2/docs/api/java/math/BigDecimal.html#compareTo%28java.math.BigDecimal%29
	*	@param	input	Value to be compared to the input
	*/
	public int compareTo(String input){
		return _text.compareTo(input);
	}
	
	/**
	*	Compares the saved numeric value to the input.
	*	For more information, see http://download.oracle.com/javase/1.4.2/docs/api/java/math/BigDecimal.html#compareTo%28java.math.BigDecimal%29
	*	@param	input	TextValue instance to be compared to the stored value
	*/
	public int compareTo(TextValue input){
		return _text.compareTo(input.getID());
	}
	
	/** 
	*	Returns true if the argument is equal to this object, false otherwise.
	*	@param	anObject	Object to be compared to this object.
	*/
	public boolean equals(Object anObject){
		if(anObject instanceof TextValue){
			TextValue input = (TextValue) anObject;
			return (input.compareTo(_text) == 0);
		}
		return false;
	}
	
	/**
	*	Class printer
	**/
	public String toString(){
		return _text;
	}
	
	/**
	*	Class constructor.
	*	@param	text	Text for the class to store.
	**/
	public TextValue(String text){
		_text = text;
	}
		
}