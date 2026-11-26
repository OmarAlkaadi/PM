package gui.innentueren;

import business.kunde.KundeModel;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Klasse, welche das Fenster mit den Sonderwuenschen zu Innentueren
 * kontrolliert.
 */
public final class InnentuerenControl {

	// das View-Objekt des Innentueren-Fensters
	private InnentuerenView innentuerenView;

	/**
	 * erzeugt ein ControlObjekt inklusive View-Objekt zum Fenster fuer die
	 * Sonderwuensche zu Innentueren.
	 * @param kundeModel, KundeModel, das Model-Objekt des Grundfensters
	 */
	public InnentuerenControl(KundeModel kundeModel){
		Stage stageInnentueren = new Stage();
		stageInnentueren.initModality(Modality.APPLICATION_MODAL);
		this.innentuerenView = new InnentuerenView(this, stageInnentueren);
	}

	/**
	 * macht das InnentuerenView-Objekt sichtbar.
	 */
	public void oeffneInnentuerenView(){
		this.innentuerenView.oeffneInnentuerenView();
	}
}
