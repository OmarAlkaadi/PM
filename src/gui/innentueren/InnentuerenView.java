package gui.innentueren;

import gui.basis.BasisView;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Klasse, welche das Fenster mit den Sonderwuenschen zu
 * Innentueren bereitstellt.
 */
public class InnentuerenView extends BasisView {

    // das Control-Objekt des Innentueren-Fensters
    private InnentuerenControl innentuerenControl;

    /**
     * erzeugt ein InnentuerenView-Objekt
     */
    public InnentuerenView(InnentuerenControl innentuerenControl,
            Stage innentuerenStage) {
        super(innentuerenStage);

        this.innentuerenControl = innentuerenControl;

        innentuerenStage.setTitle("Sonderwünsche zu Innentüren");
        innentuerenStage.setWidth(700);
        innentuerenStage.setHeight(400);
        innentuerenStage.setResizable(false);

        this.initKomponenten();
    }

    /* initialisiert die Steuerelemente auf der Maske */
    protected void initKomponenten() {
        super.initKomponenten();

        super.getLblSonderwunsch().setText("Innentüren");

        // fuer Klarglas und Milchglas wird zusaetzlich eine Stueckzahl eingegeben
        fuegeOptionHinzu("Glasausschnitt Klarglas", 1, true);
        fuegeOptionHinzu("Glasausschnitt Milchglas", 2, true);
        fuegeOptionHinzu("Innentür zur Garage als Holztür", 3, false);
    }

    /**
     * Fuegt einen Sonderwunsch mit Preisfeld und Checkbox zur Maske hinzu,
     * bei Bedarf zusaetzlich mit einem Eingabefeld fuer die Stueckzahl.
     */
    private void fuegeOptionHinzu(String bezeichnung, int zeile, boolean mitStueckzahl) {
        Label lblOption = new Label(bezeichnung);
        TextField txtPreis = new TextField();

        txtPreis.setEditable(false);
        txtPreis.setPrefColumnCount(6);

        CheckBox auswahl = new CheckBox();

        lblOption.setLabelFor(auswahl);
        auswahl.setAccessibleText(bezeichnung);

        getGridPaneSonderwunsch().addRow(
                zeile, lblOption, txtPreis, new Label("Euro je Tür"), auswahl
        );

        if (mitStueckzahl) {
            TextField txtStueckzahl = new TextField();
            txtStueckzahl.setPrefColumnCount(4);
            txtStueckzahl.setAccessibleText("Stückzahl " + bezeichnung);

            getGridPaneSonderwunsch().add(new Label("Stückzahl"), 4, zeile);
            getGridPaneSonderwunsch().add(txtStueckzahl, 5, zeile);
        }
    }

    /**
     * macht das InnentuerenView-Objekt sichtbar.
     */
    public void oeffneInnentuerenView() {
        super.oeffneBasisView();
    }

    /* berechnet den Preis der ausgesuchten Sonderwuensche und zeigt diesen an */
    protected void berechneUndZeigePreisSonderwuensche() {
    }

    /* speichert die ausgesuchten Sonderwuensche in der Datenbank ab */
    protected void speichereSonderwuensche() {
    }
}
