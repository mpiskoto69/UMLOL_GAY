package bank.storage;

public interface Storable {
	 String marshal(); // Returns object data as String content
	 void unmarshal(String data) throws UnMarshalingException;}
