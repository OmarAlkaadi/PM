package gui.kunde;

import java.io.IOException;
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
    
    // Kunden anlegen
    public void speichereKunden(Kunde kunde) {
        List<String> fehler = KundeValidator.pruefe(kunde);
        if (!fehler.isEmpty()) {
            kundeView.zeigeFehlermeldung("Ungültige Eingabe", String.join("\n", fehler));
            return;
        }

        try {
            kundeModel.speichereKunden(kunde);
            MeldungFenster.zeigeErfolg("Der Kunde " + kunde.getVorname() + " "
                + kunde.getNachname() + " wurde erfolgreich angelegt.");
        } catch (IOException exc) {
            exc.printStackTrace();
            kundeView.zeigeFehlermeldung("Fehler beim Anlegen", exc.getMessage());
        }
    }

    // Kunden ändern
    public void aendereKunden(Kunde kunde) {
        List<String> fehler = KundeValidator.pruefe(kunde);
        if (!fehler.isEmpty()) {
            kundeView.zeigeFehlermeldung("Ungültige Eingabe", String.join("\n", fehler));
            return;
        }

        try {
            kundeModel.aendereKunden(kunde);
            MeldungFenster.zeigeErfolg("Die Daten des Kunden " + kunde.getVorname() + " "
                + kunde.getNachname() + " wurden erfolgreich geändert.");
        } catch (IOException exc) {
            exc.printStackTrace();
            kundeView.zeigeFehlermeldung("Fehler beim Ändern", exc.getMessage());
        }
    }

    // Kunden löschen
    public boolean loescheKunden(int hausnummer) {
        try {
            kundeModel.loescheKunden(hausnummer);
            MeldungFenster.zeigeErfolg("Der Kunde wurde erfolgreich gelöscht.");
            return true;
        } catch (IOException exc) {
            exc.printStackTrace();
            kundeView.zeigeFehlermeldung("Fehler beim Löschen", exc.getMessage());
            return false;
        }
    }

    // Kunden lesen
    public Kunde leseKunden(int hausnummer) {
        try {
            return kundeModel.leseKunden(hausnummer);
        } catch (IOException exc) {
            exc.printStackTrace();
            kundeView.zeigeFehlermeldung("Fehler beim Lesen", exc.getMessage());
            return null;
        }
    }
}
