package business.kunde;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.SQLException;
import javafx.collections.*;
  
/** 
 * Klasse, welche das Model des Grundfensters mit den Kundendaten enthaelt.
 */
public final class KundeModel {
	
	// enthaelt den aktuellen Kunden
	private Kunde kunde;
	
	/* enthaelt die Plannummern der Haeuser, diese muessen vielleicht noch
	   in eine andere Klasse verschoben werden */
	ObservableList<Integer> plannummern = 
	    FXCollections.observableArrayList(
		1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24);
	

	// enthaelt das einzige KundeModel-Objekt
	private static KundeModel kundeModel;
	
	// privater Konstruktor zur Realisierung des Singleton-Pattern
	private KundeModel(){
		super();
	}
	
	/**
	 *  Methode zum Erhalt des einzigen KundeModel-Objekts.
	 *  Das Singleton-Pattern wird realisiert.
	 *  @return KundeModel, welches das einzige Objekt dieses
	 *          Typs ist.
	 */
	public static KundeModel getInstance(){
		if(kundeModel == null){
			kundeModel = new KundeModel();
		}
		return kundeModel;	
	}
	
	/**
	 * gibt die Ueberschrift zum Grundfenster mit den Kundendaten heraus
	 * @return String, Ueberschrift zum Grundfenster mit den Kundendaten 
	 */
	public String getUeberschrift(){
		return "Verwaltung der Sonderwunschlisten";
	}
	
	/**
	 * gibt saemtliche Plannummern der Haeuser des Baugebiets heraus.
	 * @return ObservableList<Integer> , enthaelt saemtliche Plannummern der Haeuser
	 */
	public ObservableList<Integer> getPlannummern(){
		return this.plannummern; 
	}
		 	
	// ---- Datenbankzugriffe -------------------
	
	/**
	 * speichert ein Kunde-Objekt in die Datenbank
	 * @param kunde, Kunde-Objekt, welches zu speichern ist
	 * @throws SQLException, Fehler beim Speichern in die Datenbank
	 * @throws Exception, unbekannter Fehler
	 */
	public void speichereKunden(Kunde kunde)
	    throws SQLException, Exception{
       
			//Jetzt nur CSV da wir kein DB haben

			File datei = new File("kunden.csv");
			boolean existiert = !datei.exists() || datei.length() == 0;
		
			try{
				BufferedWriter writer = new BufferedWriter(new FileWriter(datei, true));
				if (existiert) {
					writer.write("Hausnummer;Kundennummer;Vorname;"
                    + "Nachname;Telefonnummer;Email");
          		  	writer.newLine();
				}
				

				// Kundendaten speichern
				writer.write(
					kunde.getHausnummer() + ";" +
					csvWert(kunde.getKundennummer()) + ";" +
					csvWert(kunde.getVorname()) + ";" +
					csvWert(kunde.getNachname()) + ";" +
					csvWert(kunde.getTelefonnummer()) + ";" +
					csvWert(kunde.getEmail())
				);

				writer.newLine();

				// Datei schließen
				writer.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}


		// Speicherung des Kunden in der DB (Später, wenn DB vorhanden)
   	    this.kunde = kunde;
	}
	
	
	//Hilfsmethode, um Sonderzeichen in CSV-Dateien zu maskieren
	private String csvWert(String wert) {
    if (wert == null) {
        return "";
    }
    	return "\"" + wert.replace("\"", "\"\"") + "\"";
	}
}
