package gui.fensteraussentueren;

import gui.basis.BasisView;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Klasse, welche das Fenster mit den Sonderwuenschen zu
 * Fenstern und Aussentueren bereitstellt.
 */
public class FensterAussentuerenView extends BasisView {

    // das Control-Objekt des Fenster-und-Aussentueren-Fensters
    private FensterAussentuerenControl fensterAussentuerenControl;

    /**
     * erzeugt ein FensterAussentuerenView-Objekt
     */
    public FensterAussentuerenView(FensterAussentuerenControl fensterAussentuerenControl,
            Stage fensterAussentuerenStage) {
        super(fensterAussentuerenStage);

        this.fensterAussentuerenControl = fensterAussentuerenControl;

        fensterAussentuerenStage.setTitle("Sonderwünsche zu Fenster und Außentüren");
        fensterAussentuerenStage.setWidth(620);
        fensterAussentuerenStage.setHeight(560);
        fensterAussentuerenStage.setResizable(false);

        this.initKomponenten();
    }

    /* initialisiert die Steuerelemente auf der Maske */
    protected void initKomponenten() {
        super.initKomponenten();

        super.getLblSonderwunsch().setText("Fenster und Außentüren");

        fuegeOptionHinzu("Schiebetür EG zur Terrasse", 1);
        fuegeOptionHinzu("Schiebetür DG zur Dachterrasse", 2);
        fuegeOptionHinzu("Erhöhter Einbruchschutz Haustür", 3);
        fuegeOptionHinzu("Vorbereitung elektrische Rollläden EG", 4);
        fuegeOptionHinzu("Vorbereitung elektrische Rollläden OG", 5);
        fuegeOptionHinzu("Vorbereitung elektrische Rollläden DG", 6);
        fuegeOptionHinzu("Elektrische Rollläden EG", 7);
        fuegeOptionHinzu("Elektrische Rollläden OG", 8);
        fuegeOptionHinzu("Elektrische Rollläden DG", 9);
    }

    /**
     * Fuegt einen Sonderwunsch mit Preisfeld und Checkbox zur Maske hinzu.
     */
    private void fuegeOptionHinzu(String bezeichnung, int zeile) {
        Label lblOption = new Label(bezeichnung);
        TextField txtPreis = new TextField();

        txtPreis.setEditable(false);
        txtPreis.setPrefColumnCount(6);

        CheckBox auswahl = new CheckBox();

        lblOption.setLabelFor(auswahl);
        auswahl.setAccessibleText(bezeichnung);

        getGridPaneSonderwunsch().addRow(
                zeile, lblOption, txtPreis, new Label("Euro"), auswahl
        );
    }

    /**
     * macht das FensterAussentuerenView-Objekt sichtbar.
     */
    public void oeffneFensterAussentuerenView() {
        super.oeffneBasisView();
    }

    /* berechnet den Preis der ausgesuchten Sonderwuensche und zeigt diesen an */
    protected void berechneUndZeigePreisSonderwuensche() {
    }

    /* speichert die ausgesuchten Sonderwuensche in der Datenbank ab */
    protected void speichereSonderwuensche() {
    }
}
