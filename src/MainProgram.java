
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MainProgram {

	static Detective_Functions Me = new Detective_Functions();
		
	public static void main(String[] args) throws Exception {
		// Die einzelnen Möglichkeiten werden durchgespielt = Highscore, Neues Spiel und altes Spiel laden.
		System.out.println("--------------------------------------------------------------------");
		System.out.println("KKKKK     KKKKKK   RRRRRRRRRR     IIIII     MMMM       MMMM    IIIII");
		System.out.println("KKKKK    KKKKKKK   RRR      RR    IIIII     MMMMM     MMMMM    IIIII");
		System.out.println("KKKKK  KKKKKKK     RRR     RRR    IIIII     MMMMMM   MMMMMM    IIIII");
		System.out.println("KKKKKKKKKKKK       RRRRRRRRR      IIIII     MMMMMMMMMMMMMMM    IIIII");
		System.out.println("KKKKKKKKKKKKK      RRR    RR      IIIII     MMM   MMM   MMM    IIIII");
		System.out.println("KKKKK  KKKKKKK     RRR     RR     IIIII     MMM    M    MMM    IIIII");
		System.out.println("KKKKK   KKKKKKKK   RRR     RR     IIIII     MMM         MMM    IIIII");
		System.out.println("KKKKKK   KKKKKKKK  RRR     RR     IIIII     MMM         MMM    IIIII");
		System.out.println("--------------------------------------------------------------------");
	
		System.out.println("Schlagzeile: Frau tot in Badewanne aufgefunden. Mord oder Selbstmord?");
		System.out.println("Du als Versicherungsdetektiv musst herausfinden, ob der Föhn durch \n Eigen- oder Fremdverschulden in die Badewanne gefallen ist.");
		
		System.out.println("Viel Spass beim Spielen ^_^.");
		System.out.println("-----------------------------------------------------");
		System.out.println("- Neues Spiel starten [new]");
		System.out.println("- Spiel laden [load]");
		System.out.println("- Highscoreliste ansehen [highscore]");
		System.out.println("-----------------------------------------------------");
		
		boolean Spielstart = false;
		
		while(!Spielstart)
		{
			System.out.print("Was tun?: ");
			Me.was_tue_ich = Me.doSpeak();
			if(Me.was_tue_ich.equalsIgnoreCase("new"))
			{
				Persons Functions = new Persons();
				Functions.doResetAllIndices();
				Functions.doResetAllQuestions();
				StartnewGame();
				Spielstart = true;
				
			}
			else if(Me.was_tue_ich.equalsIgnoreCase("load"))
			{
				if(LoadGame())
				{
				Spielstart = true;
				}
				else
				{
					System.out.println("Tut mir leid, dieser Spielstand ist bereits abgeschlossen");
					Spielstart = false;
				}
			}
			else if(Me.was_tue_ich.equalsIgnoreCase("highscore"))
			{	
				doShowHighscores();
			}	
			else
			{
				System.out.println("Hä? Das kann ich nicht.");
			}
		}
		
		Me.Detective();
	}

	public static void StartnewGame() throws Exception
	{
		// Ausgabe der Möglichkeiten
		System.out.println("\nIn welchem Level möchtest du spielen?");
		System.out.println("-----------------------------------------------------");
		System.out.println("* Hobbydetektiv (1)");
		System.out.println("* kleiner Profi (2)");
		System.out.println("* Sherlock Holmes like (3)");
		System.out.println("-----------------------------------------------------");
		while(Me.Punkte == 0)
		{
			// Die Punkte werden mit den Punkten gespeist, die für den jeweiligen Level / Rang verfügbar sind.
			int Level = Me.doSayNumber();
			
			if(Level == 1)
			{
				Me.Punkte = 30;
			}
			else if (Level == 2)
			{
				Me.Punkte = 20;
			}
			else if (Level == 3)
			{
				Me.Punkte = 10;
			}
			else
			{
				System.out.println("Erlaubt sind Zahlen von 1 bis und mit 3!");
				Me.Punkte = 0;
			}
		}
		
		while(Me.DetectiveName.equalsIgnoreCase(""))
		{
			// Es braucht eine Bezeichnung und eine Nummer für das XML für den Highscore - Teil des XMLs
			String Bezeichnung = "";
			int Rangnumber = 0;
			switch(Me.Punkte)
			{
			case 10:
				Rangnumber = 3;
				Bezeichnung = "Sherlock Holmes like";
				break;
			case 20:
				Rangnumber = 2;
				Bezeichnung = "kleiner Profi";
				break;
			case 30:
				Rangnumber = 1;
				Bezeichnung = "Hobbydetektiv";
				break;
			}
			
			System.out.println("Wie heisst du Detektiv des Ranges " + Bezeichnung + ": ");
			Me.DetectiveName = Me.doSpeak();
			
			DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder aufbau = fabrik.newDocumentBuilder();
	    	Document xmlbaum = aufbau.parse("Spiel.xml");
	    	
			Element Name = (Element) xmlbaum.getElementsByTagName("Name").item(0);
			Element Fallabgeschlossen = (Element) xmlbaum.getElementsByTagName("Fallabgeschlossen").item(0);
			Element Punkte = (Element) xmlbaum.getElementsByTagName("Punkte").item(0);
			Element Rang = (Element) xmlbaum.getElementsByTagName("Rang").item(0);
			// Die Elemente werden mit Werten gespeist, damit man den neu entstandenen Spielstand speichen kann.
			Name.setTextContent(Me.DetectiveName);
			Fallabgeschlossen.setTextContent("" + Me.Case_closed);
			Punkte.setTextContent(""+Me.Punkte);
			Rang.setTextContent(Bezeichnung);
			Rang.setAttribute("rangnummer", "" + Rangnumber);
			
			TransformerFactory.newInstance().newTransformer().transform(
                    new DOMSource(xmlbaum), new StreamResult(new FileOutputStream("Spielstand.xml")));
		}
	}

	public static boolean LoadGame() throws Exception
	{
		// Auf die Variabeln der Detective_Functions - Class die bestehenden Werte übertragen
		DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
    	DocumentBuilder aufbau = fabrik.newDocumentBuilder();
    	Document xmlbaum = aufbau.parse("Spiel.xml");
    	Me.DetectiveName = xmlbaum.getElementsByTagName("Name").item(0).getTextContent();
    	if(xmlbaum.getElementsByTagName("Fallabgeschlossen").item(0).getTextContent().equals("true") && !xmlbaum.getElementsByTagName("Fallabgeschlossen").item(0).getTextContent().equals(""))
    	{
    		Me.Case_closed = true;
    	}
    	else
    	{
    		Me.Case_closed = false;
    	}     
    	Me.Punkte = Integer.parseInt(xmlbaum.getElementsByTagName("Punkte").item(0).getTextContent());
    	Element Rang = (Element) xmlbaum.getElementsByTagName("Rang").item(0);
    	Me.Rangnumber = Integer.parseInt(Rang.getAttribute("rangnummer"));;
    	
    	return !Me.Case_closed;
    }
	
	public static void doSaveGame() throws Exception
	{
		// derzeitige Werte in den Variabeln in der Detective_Functions - Class im XML speichern
		DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
    	DocumentBuilder aufbau = fabrik.newDocumentBuilder();
    	Document xmlbaum = aufbau.parse("Spiel.xml");
    	
		Element Name = (Element) xmlbaum.getElementsByTagName("Name").item(0);
		Element Fallabgeschlossen = (Element) xmlbaum.getElementsByTagName("Fallabgeschlossen").item(0);
		Element Punkte = (Element) xmlbaum.getElementsByTagName("Punkte").item(0);
		
		Name.setTextContent(Me.DetectiveName);
		Fallabgeschlossen.setTextContent("" + Me.Case_closed);
		Punkte.setTextContent("" + Me.Punkte);
		
		TransformerFactory.newInstance().newTransformer().transform(
                new DOMSource(xmlbaum), new StreamResult(new FileOutputStream("Spiel.xml")));
	}
	
	public static void doSetHighscore() throws Exception
	{
		// Für den Fall, dass man gut genug gespielt hat, kann man auch einen Highscore setzen lassen.
		DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
    	DocumentBuilder aufbau = fabrik.newDocumentBuilder();
    	Document xmlbaum = aufbau.parse("Spiel.xml");
    	
		NodeList Highscores = xmlbaum.getElementsByTagName("Highscore");
    	
    	for(int h = 0; h < Highscores.getLength(); h++)
    	{
    		Element Highscore = (Element) Highscores.item(h);
    		System.out.println(Me.Rangnumber + " " + Highscore.getAttribute("rangnummer"));
    		if(Integer.parseInt(Highscore.getAttribute("rangnummer")) == Me.Rangnumber)
    		{
    			if(Integer.parseInt(Highscore.getAttribute("Punkte")) <= Me.Punkte && Me.Punkte > 0)
    			{
    				Highscore.setAttribute("Punkte", "" + Me.Punkte);
    				Highscore.setAttribute("Name", Me.DetectiveName);
    			}
    		}
    	}
    	TransformerFactory.newInstance().newTransformer().transform(
                new DOMSource(xmlbaum), new StreamResult(new FileOutputStream("Spiel.xml")));
	}
	
	public static void doShowHighscores() throws Exception
	{
		// zeigt alle Highscores, die bisher in den einzelnen Rängen gemacht wurden
		DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
    	DocumentBuilder aufbau = fabrik.newDocumentBuilder();
    	Document xmlbaum = aufbau.parse("Spiel.xml");
    	
		NodeList Highscores = xmlbaum.getElementsByTagName("Highscore");
		
		for(int h = 0; h < Highscores.getLength(); h++)
    	{
    		Element Highscore = (Element) Highscores.item(h);
    		if(h == 0)
    		{
    			System.out.println("Highscores des Ranges 1");
    			System.out.println("-----------------------------------------------------");
    		}
    		else if(h == 3)
    		{
    			System.out.println("Highscores des Ranges 2");
    			System.out.println("-----------------------------------------------------");
    		}
    		else if(h == 6)
    		{
    			System.out.println("Highscores des Ranges 3");
    			System.out.println("-----------------------------------------------------");
    		}
    		
    		System.out.println("Name: " + Highscore.getAttribute("Name") + " - Punkte: " + Highscore.getAttribute("Punkte"));
    	}
	}
	
}
