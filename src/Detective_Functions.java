import java.util.Scanner; 
import java.awt.Desktop;
import java.io.File;

public class Detective_Functions {
	int Punkte = 0;
	String DetectiveName = "";
	boolean Case_closed = false;
	boolean Ende_Durchsuchung = false;
	boolean Ende_Befragung = false;
	int Rangnumber = 0;
	String was_tue_ich = "";

	public void doCloseCase()
	{
		// Der Fall läuft solange weiter, bis man einen Verdacht abgegeben hat oder die Punkte ausgegangen sind.
		if(Case_closed)
		{
			// Wenn es aus dem Verdächtigungsmodus geht, startet es sonst neu, wenn die Punkte über 0 sind...
			Case_closed = true;
		}
		else if(Punkte > 0)
		{
			// Das ist nötig WÄHREND des Spielablaufs, damit man nicht weiterspielen kann.
			Case_closed = false;
		}
		else
		{
			Case_closed = true;
			System.err.println("-----------------------------------------------------\nDu konntest den Fall nicht lösen. Gehe nach Hause in \ndie Ecke und weine.\n");
			System.out.println("-----------------------------------------------------");
		}
	}

	public String doSpeak()
	{
		// Scanner liest Text ein
		@SuppressWarnings("resource")
		Scanner Speaker = new Scanner(System.in);

		return Speaker.next();
	}

	public int doSayNumber()
	{
		// Scanner liest Nummer ein
		@SuppressWarnings("resource")
		Scanner Number = new Scanner(System.in);

		return Number.nextInt();
	}

	// Der Hauptteil der Applikation mit dem Menü und den IF / Else - Abfragen
	public void Detective() throws Exception
	{
		Persons Functions = new Persons();
		Place CrimeScene = new Place();

		while(!Case_closed)
		{
			// Gamemenü einblenden, mit anwendbaren Befehlen
			System.out.println("Restliche Punkte: " + Punkte);
			System.out.println("Spielender Detektiv: " + DetectiveName);
			System.out.println("Was tue ich als nächstes...?");
			System.out.println("-----------------------------------------------------");
			System.out.println("* Notizen prüfen [n]");
			System.out.println("* Verdacht äussern [v]");
			System.out.println("* Spielhilfe aufrufen [help]");
			System.out.println("* Ort durchsuchen [s]");
			System.out.println("* Befragung [b]");
			System.out.println("-----------------------------------------------------");
			was_tue_ich = doSpeak();
			if(was_tue_ich.equalsIgnoreCase("n"))
			{
				// Notizen prüfen
				Functions.all_notices(false, "");
			}
			else if(was_tue_ich.equalsIgnoreCase("v"))
			{
				// Verdächtigungsmodus
				doGuess(Functions);
			}
			else if(was_tue_ich.equalsIgnoreCase("help"))
			{
				// Spielanleitung ausgeben
				Desktop.getDesktop().open(new File("Programmanleitung.pdf"));
			}
			else if(was_tue_ich.equalsIgnoreCase("b"))
			{
				// Personensmodus
				doAskPersons(Functions);
			}
			else if(was_tue_ich.equalsIgnoreCase("s"))
			{
				// Durchsuchungsmodus
				doSearch(CrimeScene);
			}
			else
			{
				System.out.println("Tut mir Leid, dieser Befehl gehört nicht zu meinem Repertoire...");
			}
			doCloseCase();
			MainProgram.doSaveGame();
		}
	}

	public void doAskPersons(Persons Functions) throws Exception
	{
		// Personensmodus
		System.out.println("Wen möchtest du befragen?");
		System.out.println("-----------------------------------------------------");
		Ende_Befragung = false;
		// unbedingt noch verkürzen
		while(!Ende_Befragung && !Case_closed) 
		{
			was_tue_ich = "";
			if(Case_closed) 
			{
				Ende_Befragung = true;
			}
			else
			{
				Functions.all_Persons();
				System.out.println("-----------------------------------------------------");
				System.out.println("Wen befragst du?: ");
				Functions.doSetPerson(doSpeak());

				if(Functions.isPersonFilled())
				{
					System.out.println("-----------------------------------------------------");
					Functions.all_questions();
					System.out.println("-----------------------------------------------------");
					System.out.println("Welche Frage stellst du? (id):");
					Functions.doSetQuestion(doSpeak());
					// Konnte die Person eindeutig einer der verfügbaren Personen zugewiesen werden oder war sie leer?
					if(Functions.isQuestionFilled())
					{
						// Gab es die Frage oder nicht und konnte somit nicht gefüllt werden?
						System.out.println("-----------------------------------------------------");
						// Falls keine Fehler, kann die Frage gestellt werden...
						Functions.ask_question();
						Punkte--;
					}	
					else
					{
						System.err.println("Diese Frage steht nicht zur Verfügung.");
					}
				}
				else
				{
					System.err.println("Diese Person steht nicht zur Verfügung.");
				}
				while(!was_tue_ich.equalsIgnoreCase("ja") && !was_tue_ich.equalsIgnoreCase("nein"))
				{
					System.out.println("Weiterfragen? (ja/nein):");
					was_tue_ich = doSpeak();
					if(was_tue_ich.equalsIgnoreCase("nein"))
					{
						Ende_Befragung = true;
					}
					else if(was_tue_ich.equalsIgnoreCase("ja"))
					{
						Ende_Befragung = false;
					}
				}
				MainProgram.doSaveGame();
				doCloseCase();
			}
		}			
	}

	public void doSearch(Place CrimeScene) throws Exception
	{
		// Durchsuchungsmodus
		Ende_Durchsuchung = false;
		// Tatort aufbauen
		CrimeScene.doCreateCrimeScene();
		while(!Ende_Durchsuchung && !Case_closed)
		{
			was_tue_ich = "";
			// Raum eingeben, den du aufrufen willst. 
			System.out.print("Welchen Raum nimmst du unter die Lupe?: ");
			CrimeScene.doSetRoom(doSpeak());
			// Wenn der Raum immer noch leer ist, dann konnte die Eingabe nicht erfolgen
			if(CrimeScene.isRoomFilled())
			{
				CrimeScene.doShowRoom();
				CrimeScene.doSetSearchword(doSpeak());
				// Wenn der Raum immer noch leer ist, dann konnte die Eingabe nicht erfolgen
				if(CrimeScene.isSearchwordFilled())
				{
					// Zeige das, was du zu dem Suchwort finden konntest.
					CrimeScene.doShowSearchword();
					Punkte--;
				}
				else
				{
					System.err.println("Dieses Suchwort steht nicht zur Verfügung...");
				}
			}
			else
			{
				System.err.println("Dieser Raum steht nicht zur Verfügung...");
			}

			while(!was_tue_ich.equalsIgnoreCase("ja") && !was_tue_ich.equalsIgnoreCase("nein"))
			{
				System.out.println("Weitersuchen? (ja/nein):");
				was_tue_ich = doSpeak();
				if(was_tue_ich.equalsIgnoreCase("nein"))
				{
					Ende_Durchsuchung = true;
				}
				else if(was_tue_ich.equalsIgnoreCase("ja"))
				{
					Ende_Durchsuchung = false;
				}
			}
			MainProgram.doSaveGame();
			doCloseCase();
		}
	}

	public void doGuess(Persons Functions) throws Exception
	{
		System.out.println("Mord oder Selbstmord?:");
		was_tue_ich = doSpeak();

		// Wenn du auf Mord gesetzt hast
		if(was_tue_ich.equalsIgnoreCase("Mord") || was_tue_ich.equalsIgnoreCase("Selbstmord"))
		{
			if(was_tue_ich.equalsIgnoreCase("Mord"))
			{	  
				// Zusätzliches Muster, das dazukommt, wenn man Mord genannt hat, ansonsten ist alles gleich
				System.out.println("Wer war es deiner Meinung nach?");
				System.out.println("-----------------------------------------------------");
				Functions.all_Persons();
				while(!Functions.isPersonFilled() || Functions.person.equalsIgnoreCase("back"))
				{
					System.out.print("Antwort:");
					Functions.doSetPerson(doSpeak());
				}
			}
			// Begründung des Verdachts
			System.out.println("-----------------------------------------------------");
			System.out.println("Warum ich das denke? Nuuun...:");
			if(was_tue_ich.equalsIgnoreCase("Mord"))
			{
				// gefiltertes Abrufen der Notizen nach verdächtigter Person
				Functions.all_notices(true, Functions.person);
			}
			else
			{
				// gefiltertes Abrufen der Notizen nach Selbstmord
				Functions.all_notices(true, was_tue_ich);
			}

			if(Functions.my_indices >= 2)
			{
				System.out.println("Du hast den Fall gelöst! Glückwunsch :)");
				if(was_tue_ich.equalsIgnoreCase("Mord"))
				{
					System.out.println(Functions.person + " bekommt wegen Mordes eine Freiheitsstrafe von \nlebenslanger Haft auferlegt.");
				}
				Case_closed = true;
				MainProgram.doSetHighscore();
				MainProgram.doSaveGame();
			}
			else
			{
				System.out.println("Ich befürchte, dass du den Fall nicht gelöst hast... Starte nochmal von vorne und probiere es erneut...");
				Case_closed = true;
				MainProgram.doSaveGame();
			}
		}

		else
		{
			System.out.println("-----------------------------------------------------");
			System.out.println("Wie bitte?");
			System.out.println("-----------------------------------------------------");
		}
	}
}