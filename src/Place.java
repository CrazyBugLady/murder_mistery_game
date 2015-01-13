import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* 
 * Places ist eine Class zum Erstellen und Durchsuchen von Tatorten
 * */

public class Place {

	/*
	 Variabelnbereich
	 */
	
	private String Room = ""; // der aktuelle Raum, in dem man sich gerade befindet
    private String Searchword = ""; // das aktuelle Suchobjekt, das man gerade untersucht
	Persons Functions = new Persons(); // Eine Verlinkung zu den Funktionen, die man innerhalb von Persons finden kann
	String rooms[] = new String[5]; // Alle Räume, die es an diesem Tatort gibt
	String searchwords[] = new String[30]; // Alle Suchwörter, die es im aktuellen Raum gibt 

// Aufbau des Tatorts
	
	// Füllt eine Liste mit allen Orten, die es bisher gibt
	
	public void doCreateCrimeScene() throws Exception
	{
		// Kreiere den Tatort mit Beschreibung und Aufbaumenü. 
		// Danach wird die Eingabe eingelesen. 
		System.out.println("----------------------TATORT-------------------------");
		Element Description = (Element) makeReader().getElementsByTagName("Description").item(0);
		System.out.println(Description.getTextContent()); 
		System.out.println("-----------------------------------------------------");
		doShowAllRooms();
	}
	
	public void doShowAllRooms() throws Exception
	{
		// Alle Räume werden in die Variabeln gefüllt
		NodeList Rooms = (NodeList) makeReader().getElementsByTagName("Room");
		for(int r = 0; r < Rooms.getLength(); r++)
		{
			Element Room = (Element) Rooms.item(r);
			rooms[r] = Room.getAttribute("name");
		}
	}
	// Füllt eine Liste mit allen Suchworten, die es zum derzeitigen Raum gibt
	private void doShowAllSearchWords(Element actualRoom) {
		NodeList RoomSearchwords = (NodeList) actualRoom.getElementsByTagName("searchword");
		
		for(int s = 0; s < RoomSearchwords.getLength(); s++)
		{
			Element Searchword = (Element) RoomSearchwords.item(s);
			searchwords[s] = Searchword.getAttribute("name");
		}
	}
	
	// zeigt eine Beschreibung des Raumes, den wir uns ansehen wollen.
	public void doShowRoom() throws Exception
	{
		NodeList AllRooms = (NodeList) makeReader().getElementsByTagName("Room");
		
		for(int i = 0; i < AllRooms.getLength(); i++)
		{
			Element ActualRoom = (Element) AllRooms.item(i);
			
			if(ActualRoom.getAttribute("name").equalsIgnoreCase(this.Room))
			{
				System.out.println("-----------------------------------------------------");
				System.out.println(ActualRoom.getFirstChild());
				System.out.println("-----------------------------------------------------");
				doShowAllSearchWords(ActualRoom);
				System.out.print("Was willst du genauer anschauen?: ");
			}
		}
	}
	// zeigt eine Beschreibung des Suchwortes, das wir uns ansehen wollen
	
	public void doShowSearchword() throws Exception
	{
		NodeList AllSearchwords = (NodeList) makeReader().getElementsByTagName("searchword");
		
		for(int i = 0; i < AllSearchwords.getLength(); i++)
		{
			Element ActualSearchword = (Element) AllSearchwords.item(i);
			
			if(ActualSearchword.getAttribute("name").equalsIgnoreCase(this.Searchword))
			{
				// Wir verwenden die Funktion in Persons, um das Indiz auf true zu setzen, und somit dafür zu sorgen, dass wir auch richtig nach Indizien suchen können
				Functions.set_indice_true(ActualSearchword.getAttribute("gibt_wissen"));
				System.out.println("-----------------------------------------------------");
				System.out.println(ActualSearchword.getTextContent());
				System.out.println("-----------------------------------------------------");
			}
		}
	}
	
	// Wie befülle ich die Variabeln
	public void doSetSearchword(String Searchword_typed)
	{
		// Wir prüfen, ob das eingetippte Suchwort überhaupt verwendbar ist und wenn nicht, dann können wir es nicht zuweisen.
		if(isSearchwordUsable(Searchword_typed))
		{
			Searchword = Searchword_typed;
		}
		else
		{
			Searchword = "";
		}
	}
	
	public void doSetRoom(String Room_typed)
	{
		// Wir prüfen, ob der eingetippte Raum überhaupt verwendbar ist und wenn nicht, dann können wir nicht in diesen Raum gehen.
			if(isRoomUsable(Room_typed))
			{
				System.out.println(Room_typed);
				Room = Room_typed;
			}
			else
			{
				Room = "";
			}
	}
	
/*
Funktionen mit Rückgabewert, die uns mehr über die Rooms und die Searchwords verraten können
*/	
	
	public Document makeReader() throws Exception
	{
		DocumentBuilderFactory fabrik = DocumentBuilderFactory.newInstance();
    	DocumentBuilder aufbau = fabrik.newDocumentBuilder();
    	Document xmlbaum = aufbau.parse("Tatort.xml");
 
    	return xmlbaum;
	}
	
	public boolean isRoomFilled()
	{
		// Konnte der Raum gefüllt werden?
		if(Room.equals(""))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean isSearchwordFilled()
	{
		// Konnte das Suchwort gefüllt werden?
		if(Searchword.equals(""))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean isRoomUsable(String Room_typed)
	{
		// Kann dieser Raum benutzt werden?
		boolean treffer = false;
		
		for(int i = 0; i < rooms.length; i++)
		{
			if (rooms[i] == null)
			{
				// ist der Scheiss leer?
				treffer = false;
			}
			else if(rooms[i].equalsIgnoreCase(Room_typed))
			{
				// Hier müssen die Punkte abgezogen werden, weil man bereits einen Hinweis gefunden hat, ausser der Index ist 1, 2, 3, 7 oder 10, weil es dann Zimmer sind.
				treffer = true;
				break;
			}
		}
		return treffer;
	}
	
	public boolean isSearchwordUsable(String Searchword_typed)
	{
		// Kann das Suchtwort benutzt werden?
		boolean treffer = false;
		
		for(int i = 0; i < searchwords.length; i++)
		{
			if(searchwords[i] == null)
			{
				// ist der Scheiss leer?
				treffer = false;
			}
			else if(searchwords[i].equalsIgnoreCase(Searchword_typed))
			{
				// Hier müssen die Punkte abgezogen werden, weil man bereits einen Hinweis gefunden hat, ausser der Index ist 1, 2, 3, 7 oder 10, weil es dann Zimmer sind.
				treffer = true;
				break;
				
			}
		}
		
		return treffer;
	}
}

