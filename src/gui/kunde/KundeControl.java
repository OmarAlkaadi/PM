package gui.kunde;

import java.sql.SQLException;
import java.util.List;

import business.kunde.Kunde;
import business.kunde.KundeModel;
import business.kunde.KundeValidator;
import gui.grundriss.GrundrissControl;
import javafx.stage.Stage;
import ownUtil.MeldungFenster;

/**
 * Klasse, welche das Grundfenster mit den Kundendaten kontrolliert.
 */
public class KundeControl {
       
    // das View-Objekt des Grundfensters mit den Kundendaten
	private KundeView kundeView;
    // das Model-Objekt des Grundfensters mit den Kundendaten
    private KundeModel kundeModel;
    /* das GrundrissControl-Objekt fuer die Sonderwuensche
       zum Grundriss zu dem Kunden */
    private GrundrissControl grundrissControl;
    
    /**
	 * erzeugt ein ControlObjekt inklusive View-Objekt und Model-Objekt zum 
	 * Grundfenster mit den Kundendaten.
	 * @param primaryStage, Stage fuer das View-Objekt zu dem Grundfenster mit den Kundendaten
	 */
    public KundeControl(Stage primaryStage) { 
        this.kundeModel = KundeModel.getInstance(); 
        this.kundeView = new KundeView(this, primaryStage, kundeModel);
    }
    
    /*
     * erstellt, falls nicht vorhanden, ein Grundriss-Control-Objekt.
     * Das GrundrissView wird sichtbar gemacht.
     */
    public void oeffneGrundrissControl(){
    	if (this.grundrissControl == null){
    		this.grundrissControl = new GrundrissControl(kundeModel);
      	}
    	this.grundrissControl.oeffneGrundrissView();
    }



	//Liest einen Kunden anhand der Plannummer aus der Datenbank.
    public void leseKunden(int hausnummer) {

		try {
			Kunde kunde = kundeModel.leseKunden(hausnummer);

			if (kunde != null) {
				kundeView.zeigeKunden(kunde);
			} else {
				kundeView.zeigeKunden(null);
			}

		} catch (SQLException exc) {
			exc.printStackTrace();

			kundeView.zeigeFehlermeldung(
				"Datenbankfehler",
				"Kundendaten konnten nicht geladen werden:\n"
				+ exc.getMessage()
			);
		}
	}

	
	/**
	 * prueft die Daten eines Kunde-Objekts und speichert es danach in die Datenbank.
	 * Bei ungueltigen Daten wird eine Fehlermeldung angezeigt und nicht gespeichert.
	 * @param kunde, Kunde-Objekt, welches zu speichern ist
	 */
    public void speichereKunden(Kunde kunde){
    	List<String> fehler = KundeValidator.pruefe(kunde);
    	if (!fehler.isEmpty()){
    		this.kundeView.zeigeFehlermeldung("Ungueltige Eingabe",
    			String.join("\n", fehler));
    		return;
    	}
      	try{
    		kundeModel.speichereKunden(kunde);

			  // Erfolgsmeldung anzeigen
				MeldungFenster.zeigeErfolg(
					"Der Kunde " + kunde.getVorname() + " "
					+ kunde.getNachname()
					+ " wurde erfolgreich gespeichert."
     
		);
    	}
    	catch(SQLException exc){
    		exc.printStackTrace();
    		this.kundeView.zeigeFehlermeldung("SQLException",
                "Fehler beim Speichern in die Datenbank:\n" + exc.getMessage());
    	}
    	catch(Exception exc){
    		exc.printStackTrace();
    		this.kundeView.zeigeFehlermeldung("Exception",
                "Unbekannter Fehler");
    	}
    }




    //andern
    public void aendereKunden(Kunde kunde){
    	List<String> fehler = KundeValidator.pruefe(kunde);
    	if (!fehler.isEmpty()){
    		this.kundeView.zeigeFehlermeldung("Ungueltige Eingabe",
    			String.join("\n", fehler));
    		return;
    	}
      	try{
    		boolean geaendert = kundeModel.aendereKunden(kunde);
    		if (geaendert) {
    			MeldungFenster.zeigeErfolg(
    				"Der Kunde " + kunde.getVorname() + " "
    				+ kunde.getNachname()
    				+ " wurde erfolgreich geaendert."
    			);
    		}else{
                    this.kundeView.zeigeFehlermeldung(
                    "Kunde nicht gefunden",
                    "Es existiert kein Kunde mit der Kundennummer "
                    + kunde.getKundennummer()
    );

            }
    	}
    	catch(SQLException exc){
    		exc.printStackTrace();
    		this.kundeView.zeigeFehlermeldung("SQLException",
                "Fehler beim Ändern in die Datenbank:\n" + exc.getMessage());
    	}
    	catch(Exception exc){
    		exc.printStackTrace();
    		this.kundeView.zeigeFehlermeldung("Exception",
                "Unbekannter Fehler");
    	}
    }


    //löschen
    public void loescheKunden(Kunde kunde){
    	try{
    		boolean geloescht = kundeModel.loescheKunden(kunde);
    		if (geloescht) {
    			MeldungFenster.zeigeErfolg(
    				"Der Kunde " + kunde.getVorname() + " "
    				+ kunde.getNachname()
    				+ " wurde erfolgreich gelöscht."
    			);
    		}else{
                    this.kundeView.zeigeFehlermeldung(
                    "Kunde nicht gefunden",
                    "Es existiert kein Kunde mit der Kundennummer "
                    + kunde.getKundennummer()
    );

            }
    	}
    	catch(SQLException exc){
    		exc.printStackTrace();
    		this.kundeView.zeigeFehlermeldung("SQLException",
                "Fehler beim Löschen in die Datenbank:\n" + exc.getMessage());
    	}
    	catch(Exception exc){
    		exc.printStackTrace();
    		this.kundeView.zeigeFehlermeldung("Exception",
                "Unbekannter Fehler");
    	}
    }
}

