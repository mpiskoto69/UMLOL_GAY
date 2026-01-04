package bank.storage;

public interface Storable {
	 String marshal(); 
	 void unmarshal(String data) throws UnMarshalingException;}
