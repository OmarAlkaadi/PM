package business.kunde;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.util.*;
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
	public static synchronized KundeModel getInstance(){
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

	// ---- CSV-Dateizugriffe -------------------

	private final Path datei = Paths.get("kunden.csv");

	private final String kopfzeile =
	        "Hausnummer;Kundennummer;Vorname;"
	        + "Nachname;Telefonnummer;Email";


	// Kunden anlegen
	public void speichereKunden(Kunde kunde) throws IOException {

	    List<Kunde> kunden = leseAlleKunden();

	    // Prüfen, ob für dieses Haus bereits ein Kunde existiert
	    for (Kunde k : kunden) {
	        if (k.getHausnummer() == kunde.getHausnummer()) {
	            throw new IOException(
	                "Für dieses Haus existiert bereits ein Kunde."
	            );
	        }
	    }

	    kunden.add(kunde);

	    schreibeAlleKunden(kunden);

	    this.kunde = kunde;
	}


	// Kunden ändern
	public void aendereKunden(Kunde kunde) throws IOException {

	    List<Kunde> kunden = leseAlleKunden();

	    for (int i = 0; i < kunden.size(); i++) {

	        if (kunden.get(i).getHausnummer()
	                == kunde.getHausnummer()) {

	            kunden.set(i, kunde);

	            schreibeAlleKunden(kunden);

	            this.kunde = kunde;

	            return;
	        }
	    }

	    throw new IOException(
	        "Es wurde kein Kunde für dieses Haus gefunden."
	    );
	}


	// Kunden löschen
	public void loescheKunden(int hausnummer) throws IOException {

	    List<Kunde> kunden = leseAlleKunden();

	    boolean geloescht = kunden.removeIf(
	        k -> k.getHausnummer() == hausnummer
	    );

	    if (!geloescht) {
	        throw new IOException(
	            "Es wurde kein Kunde zum Löschen gefunden."
	        );
	    }

	    schreibeAlleKunden(kunden);

	    if (this.kunde != null
	            && this.kunde.getHausnummer() == hausnummer) {
	        this.kunde = null;
	    }
	}


	
	public Kunde leseKunden(int hausnummer) throws IOException {

	    for (Kunde k : leseAlleKunden()) {

	        if (k.getHausnummer() == hausnummer) {
	            this.kunde = k;
	            return k;
	        }
	    }

	    this.kunde = null;

	    return null;
	}


	// Alle Kunden aus der CSV-Datei lesen
	private List<Kunde> leseAlleKunden() throws IOException {

	    List<Kunde> kunden = new ArrayList<>();

	    if (Files.notExists(datei)) {
	        return kunden;
	    }

	    try (BufferedReader reader = Files.newBufferedReader(
	            datei, Charset.defaultCharset())) {

	        // Kopfzeile lesen und prüfen
	        String ersteZeile = reader.readLine();

	        if (ersteZeile == null) {
	            return kunden;
	        }
	        if (!kopfzeile.equals(ersteZeile)) {
	            throw new IOException("Ungültige CSV-Kopfzeile.");
	        }

	        String zeile;

	        
	        while ((zeile = reader.readLine()) != null) {

	            if (zeile.trim().isEmpty()) {
	                continue;
	            }

	            String[] daten = zeile.split(";", -1);

	            if (daten.length != 6 || zeile.contains("\"")) {
	                throw new IOException("Ungültiger Datensatz.");
	            }

	            Kunde k = new Kunde();

	            k.setHausnummer(Integer.parseInt(daten[0]));
	            k.setKundennummer(daten[1]);
	            k.setVorname(daten[2]);
	            k.setNachname(daten[3]);
	            k.setTelefonnummer(daten[4]);
	            k.setEmail(daten[5]);

	            kunden.add(k);
	        }
	    } catch (NumberFormatException exc) {
	        throw new IOException("Ungültige Hausnummer.", exc);
	    }

	    return kunden;
	}


	// Alle Kunden in die CSV-Datei schreiben
	private void schreibeAlleKunden(List<Kunde> kunden)
	        throws IOException {

	    try (BufferedWriter writer = Files.newBufferedWriter(
	            datei, Charset.defaultCharset())) {

	        
	        writer.write(kopfzeile);
	        writer.newLine();

	        
	        for (Kunde k : kunden) {
	            writer.write(
	                k.getHausnummer() + ";" +
	                Objects.toString(k.getKundennummer(), "") + ";" +
	                Objects.toString(k.getVorname(), "") + ";" +
	                Objects.toString(k.getNachname(), "") + ";" +
	                Objects.toString(k.getTelefonnummer(), "") + ";" +
	                Objects.toString(k.getEmail(), "")
	            );

	            writer.newLine();
	        }
	    }
	}
}
