package gui.fensteraussentueren;

import business.kunde.KundeModel;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Klasse, welche das Fenster mit den Sonderwuenschen zu Fenstern und
 * Aussentueren kontrolliert.
 */
public final class FensterAussentuerenControl {

	// das View-Objekt des Fenster-und-Aussentueren-Fensters
	private FensterAussentuerenView fensterAussentuerenView;

	/**
	 * erzeugt ein ControlObjekt inklusive View-Objekt zum Fenster fuer die
	 * Sonderwuensche zu Fenstern und Aussentueren.
	 * @param kundeModel, KundeModel, das Model-Objekt des Grundfensters
	 */
	public FensterAussentuerenControl(KundeModel kundeModel){
		Stage stageFensterAussentueren = new Stage();
		stageFensterAussentueren.initModality(Modality.APPLICATION_MODAL);
		this.fensterAussentuerenView =
			new FensterAussentuerenView(this, stageFensterAussentueren);
	}

	/**
	 * macht das FensterAussentuerenView-Objekt sichtbar.
	 */
	public void oeffneFensterAussentuerenView(){
		this.fensterAussentuerenView.oeffneFensterAussentuerenView();
	}
}
