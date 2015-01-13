import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

import org.w3c.dom.*;


import java.util.Random;

public class Persons {
	
	String person = "";
	String questionid = "";
	String[] avaiable_persons = new String[15];
	String[] avaiable_questions = new String[15];
	int my_indices = 0;
	
	Detective_Functions Detective = new Detective_Functions();
	
	public NodeList createNodeList(String Filename, String Elementname) throws Exception
	{
		// Damit der Reader nicht andauernd wiederholt werden muss... 
		DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
        DocumentBuilder aufbau = fabrik.newDocumentBuilder();
        Document xmlbaum = aufbau.parse(Filename);
        
        NodeList newNodeList = xmlbaum.getElementsByTagName(Elementname);
        
        return newNodeList;
	}
	
	 public void all_Persons() throws Exception
	 {
		 // zeige alle Personen, die du bisher aufrufen kannst
	        NodeList Gespraeche = createNodeList("Gespraeche.xml", "Gespraech");
	        
	        int Anzahl_Gespraeche = Gespraeche.getLength();
	        	        	        
	        for(int i = 0; i < Anzahl_Gespraeche; i++)
	        {
	        	Element Gespraech = (Element) Gespraeche.item(i);
	        	String freigeschaltet = Gespraech.getAttribute("freigeschaltet");
	        	String Person = Gespraech.getAttribute("Person");
	        		        	
	        	if(indice_ask(freigeschaltet))
	        	{
	        		System.out.println(" * " + Person);
	        		avaiable_persons[i] = Person;
	  			}
	        }
	     
	 }
	
	
	public void all_questions() throws Exception
    {
		// Alle Fragen zeigen, die man bisher freigeschaltet hat
    	NodeList Gespraeche = createNodeList("Gespraeche.xml", "Gespraech");
    	
    	for(int g = 0; g<Gespraeche.getLength(); g++)
    	{
    		Element Gespraech = (Element) Gespraeche.item(g);
    		String Person = Gespraech.getAttribute("Person");
    		if(Person.equalsIgnoreCase(this.person))
    		{
    			NodeList Fragen = Gespraech.getElementsByTagName("Frage");
        	
    			for(int f = 0; f< Fragen.getLength(); f++)
    			{
    				Element Frage = (Element) Fragen.item(f);
    				// Wir wollen nun wissen, welche Frage für uns bereits freigeschaltet ist und welche wir noch nicht gefragt haben. Zuallererst müssen wir wissen, ob wir sie bereits gestellt haben. Wenn wir sie schon gestellt haben, sind sie natürlich schon freigeschaltet, umgekehrt nicht.
    				if(Frage.getAttribute("gefragt").equalsIgnoreCase("nein"))
    				{
    					if(indice_ask(Frage.getAttribute("notwendiges_wissen")))
    					{
    						System.out.println("(" + Frage.getAttribute("id") + ")" + " " + Frage.getTextContent());
    						avaiable_questions[f] = Frage.getAttribute("id");
    					}
    				}
    			}
    		}
    	}
    }

	public void ask_question() throws Exception
    {
		// Frage stellen, nachdem alle Angaben geprüft wurden
    	DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
        DocumentBuilder aufbau = fabrik.newDocumentBuilder();
        Document xmlbaum = aufbau.parse("Gespraeche.xml");
       
        NodeList Gespraeche = xmlbaum.getElementsByTagName("Gespraech");

        for(int g = 0; g < Gespraeche.getLength(); g++)
        {
        	Element Gespraech = (Element) Gespraeche.item(g);
        	if(Gespraech.getAttribute("Person").equalsIgnoreCase(this.person))
        	{
        		NodeList Fragen = Gespraech.getElementsByTagName("Frage");
        		
        		NodeList Antworten = Gespraech.getElementsByTagName("Antwort");
        		
        		for(int a = 0; a < Antworten.getLength(); a++)
        		{
        			Element Antwort = (Element) Antworten.item(a);
        			Element Frage = (Element) Fragen.item(a);
        			
        			if(Antwort.getAttribute("frageid").equals(this.questionid))
        			{
        				if(Frage.hasAttribute("minispiel"))
        				{
        					if(game_1())
        					{
        						set_question_true(Frage, xmlbaum);
        					}
        				}
        				else
        				{
        					set_question_true(Frage, xmlbaum);
        				}
        				
        				System.out.println(this.person + ": " + Antwort.getTextContent());
        			}
        		}
        	}
    	}
    }

	public void all_notices(boolean filtern, String belastet_wen) throws Exception
	{
		// Alle Notizen abrufen entweder gefiltert oder ungefiltert (nach Verdächtigten)
		my_indices = 0;
		
		NodeList Indizien = createNodeList("Fall.xml", "Indiz");

    	System.out.println("_______________________________________________");
    	for(int i = 0; i<Indizien.getLength(); i++)
    	{
    		Element Indiz = (Element) Indizien.item(i);
    		
    		if(indice_ask(Indiz.getAttribute("name")))
    		{
    			if(filtern & Indiz.getAttribute("entlastet_wen").equalsIgnoreCase(belastet_wen))
    			{
    				if(!Indiz.hasAttribute("in_verbindung_mit2") || (Indiz.hasAttribute("in_verbindung_mit2") && indice_ask(Indiz.getAttribute("in_verbindung_mit2"))))
    				{
    					System.err.println("+" + Indiz.getTextContent());
    				
    					if(my_indices >= 1)
    					{
    						// Was spricht FÜR den Angeklagten / die Angeklagte bzw... gegen die Behauptung
    						my_indices--; 
    					}
    				}
    			}
    			
    			if(filtern & Indiz.getAttribute("belastet_wen").equalsIgnoreCase(belastet_wen))
    			{
    				// Wenn man filtern möchte (für Verdächtigung) 
    				// Und es ein Indiz ist, das denjenigen belastet, den man belasten möchte 
    				// weitersuchen für den Fall, dass es gewisse nur in Verbindung mit anderen Beweisen gibt...
    				if(!Indiz.hasAttribute("in_verbindung_mit") || (Indiz.hasAttribute("in_verbindung_mit") & indice_ask(Indiz.getAttribute("in_verbindung_mit"))))
    				{
    						my_indices++; // Man hat einen weiteren Beweis
    						System.out.println("-" + Indiz.getTextContent());
    				}
    			}
    			else if(!filtern)
    			{
    				my_indices++;
    				System.out.println("-" + Indiz.getTextContent());
    			}
    		}	
    	}
    	System.out.println("Anzahl Indizien: " + my_indices);
    	System.out.println("_______________________________________________");
	}
    
	
	// geile Rückgabemethoden xD

	public void set_indice_true(String indice) throws Exception
    {
    	DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
    	DocumentBuilder aufbau = fabrik.newDocumentBuilder();
    	Document xmlbaum = aufbau.parse("Fall.xml");
    	
    	NodeList Indizien = xmlbaum.getElementsByTagName("Indiz");
    	
    	for(int i = 0; i<Indizien.getLength(); i++)
    	{
    		Element Indiz = (Element) Indizien.item(i);
    		
    		if(Indiz.getAttribute("name").equalsIgnoreCase(indice))
    		{
    			Indiz.setAttribute("entdeckt", "Ja");
    			TransformerFactory.newInstance().newTransformer().transform(
		                    new DOMSource(xmlbaum), new StreamResult(new FileOutputStream("Fall.xml")));
    		}
    	}
    	// Hier wird ein neues Indiz auf true gesetzt, das wir im Laufe unserer Ermittlungen noch brauchen könnten.
    }
	
	public void doResetAllIndices() throws Exception
	{
		// Alle Indizien werden zurück auf nicht entdeckt gestellt
		DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
    	DocumentBuilder aufbau = fabrik.newDocumentBuilder();
    	Document xmlbaum = aufbau.parse("Fall.xml");
    	
    	NodeList Indizien = xmlbaum.getElementsByTagName("Indiz");
    	
    	for(int i = 0; i < Indizien.getLength(); i++)
    	{
    		Element Indiz = (Element) Indizien.item(i);
    		if(!Indiz.getAttribute("name").equalsIgnoreCase("automatisch"))
    		{
    			Indiz.setAttribute("entdeckt", "Nein");
    		}
    		TransformerFactory.newInstance().newTransformer().transform(
                    new DOMSource(xmlbaum), new StreamResult(new FileOutputStream("Fall.xml")));
    	}
	}
	
	public void doResetAllQuestions() throws Exception
	{
		// Alle Fragen zurückstellen
		DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
    	DocumentBuilder aufbau = fabrik.newDocumentBuilder();
    	Document xmlbaum = aufbau.parse("Gespraeche.xml");
    	
    	NodeList Fragen= xmlbaum.getElementsByTagName("Frage");
    	
    	for(int f = 0; f < Fragen.getLength(); f++)
    	{
    		Element Frage = (Element) Fragen.item(f);
    		Frage.setAttribute("gefragt", "nein");
    	}
    	TransformerFactory.newInstance().newTransformer().transform(
                new DOMSource(xmlbaum), new StreamResult(new FileOutputStream("Gespraeche.xml")));
	}
	
	public boolean indice_ask(String indice) throws Exception
    {
    	boolean indiz_gefunden = false;
    	// Hier werden die Indizien abgefragt. Wenn sie = true sind, haben wir sie schon. Wenn sie gleich false sind, haben wir sie noch nicht.
    	if(!indice.equals(""))
    	{
    		NodeList Indizien = createNodeList("Fall.xml", "Indiz");
        	
    	for(int i = 0; i<Indizien.getLength(); i++)
    	{
    		Element Indiz = (Element) Indizien.item(i);
    		if(Indiz.getAttribute("name").equalsIgnoreCase(indice)) 
    		{
    			if(Indiz.getAttribute("entdeckt").equalsIgnoreCase("Nein")) // Ist als Wert Nein eingetragen, dann haben wir es noch nicht herausgefunden
    			{
    				indiz_gefunden = false; // Wir haben bisher noch nichts darüber hinausfinden können
    			}
    			else
    			{
    				indiz_gefunden = true; // Wir haben dieses Indiz bereits!
    			}
    		}
    	}
    	}
    	else
    	{
    		indiz_gefunden = true;
    	}
    	
    	return indiz_gefunden;
    	
    }
	
	public void doSetPerson(String Person_typed)
	{
		// Angabe für Person getestet einstellen.
		if(isPersonUsable(Person_typed))
		{
			// Wenn diese Person benutzbar ist
			this.person = Person_typed;
		}
		else
		{
			this.person = "";
		}
	}
	
	public void doSetQuestion(String Question_typed)
	{
		// Angabe für Frage getestet einstellen.
		if(isQuestionUsable(Question_typed))
		{
			// Wenn diese Person benutzbar ist
			this.questionid = Question_typed;
		}
		else
		{
			this.questionid = "";
		}
	}
	
	public boolean isPersonFilled()
	{
		// Konnte Person gefüllt werden?
		if(person.equals(""))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean isQuestionFilled()
	{
		// Konnte Frage gefüllt werden?
		if(questionid.equals(""))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
    public boolean isPersonUsable(String Person_typed) // isPersonUsable
    {
    	// Gibt es die Person überhaupt?
    	boolean avaiable = false;
    	for(int i = 0; i < avaiable_persons.length; i++)
    	{
    		if(avaiable_persons[i] == null)
    		{
    			avaiable = false;
    		}
    		
    		else if (avaiable_persons[i].equalsIgnoreCase(Person_typed))
    		{
    			avaiable = true;
    			break;
    		}
    	}
    	
    	return avaiable;
    }
    
    public boolean isQuestionUsable(String Question_typed) // isQuestionUsable
    {
    	// Gibt es die Frage überhaupt?
    	boolean avaiable = false;
    	for(int i = 0; i < avaiable_questions.length; i++)
    	{
    		if(avaiable_questions[i] == null)
    		{
    			avaiable = false;
    		}
    		
    		else if(avaiable_questions[i].equalsIgnoreCase(Question_typed))
    		{
    			avaiable = true;
    			break;
    		}
    	}
    	return avaiable;
    }
	
    public void set_question_true(Element Frage, Document xmlbaum) throws Exception
    {
    	// Stelle die Frage auf true => wurde gestellt
    	Frage.setAttribute("gefragt", "ja"); // Man kann diese Frage nicht noch einmal fragen
		if(Frage.hasAttribute("gibt_wissen"))
		{
			set_indice_true(Frage.getAttribute("gibt_wissen"));
			 TransformerFactory.newInstance().newTransformer().transform(
	                    new DOMSource(xmlbaum), new StreamResult(new FileOutputStream("Gespraeche.xml")));
		}
    }
    
	public boolean game_1()
	{
		// Minispiel 1 für die Fragen
		// Zufallszahlen kreieren
		int randomnumber; 
		int me = 0;
		int person2;
		Random RandomCreate = new Random();
		
		System.out.println("Minispiel 1 - Würfelspiel");
		System.out.println("*****************************************************************************************");
		System.out.println("Das ist ein Würfelspiel. Hier musst du schätzen, welche Zahl als nächstes gewürfelt wird.\n Die Zahl sollte zwischen 1 und 7 liegen (1-6)");
		System.out.println("*****************************************************************************************");
		// Zufallsnummern erstellen
		randomnumber = RandomCreate.nextInt(6) + 1;
		person2 = RandomCreate.nextInt(6) + 1;
	    while(me == 0)
	    {
		System.out.println("Bitte gebe die Zahl ein, die du schätzst:");
		me = Detective.doSayNumber();
	    }
		System.out.println("*****************************************************************************************");
		System.out.println("Schätzung des Gegners: " + person2);
		System.out.println("Tatsächliche Lösung: " + randomnumber);
		System.out.println("*****************************************************************************************");
		// prüfen, wer die richtige Nummer erraten hat ... 
		
		if(randomnumber == me)
		{
			System.out.println("Du hast gewonnen und erhälst nun deine Antworten.");
			return true;
		}
		else if (randomnumber == person2)
		{
			System.out.println("Satz mit x, das war wohl nix. Du hast leider verloren, kannst es aber nochmal versuchen.");
			return false;
		}
		else
		{
			System.out.println("Hm... unentschieden... so erhälst du deine Antworten jedenfalls nicht.");
			return false;
		}

	}
	
}
