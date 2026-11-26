package gui.grundriss;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import gui.basis.BasisView;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Klasse, welche das Fenster mit den Sonderwuenschen zu 
 * den Grundrissvarianten bereitstellt.
 */
public class GrundrissView extends BasisView{
 
 	// das Control-Objekt des Grundriss-Fensters
	private GrundrissControl grundrissControl;
   
	private ImageView hausBild;
    //---Anfang Attribute der grafischen Oberflaeche---
    private Label lblWandKueche    	     
        = new Label("Wand zur Abtrennung der Kueche von dem Essbereich");
    private TextField txtPreisWandKueche 	= new TextField();
    private Label lblWandKuecheEuro 		= new Label("Euro");
    private CheckBox chckBxWandKueche 		= new CheckBox();
    //-------Ende Attribute der grafischen Oberflaeche-------
  
    /**
     * erzeugt ein GrundrissView-Objekt, belegt das zugehoerige Control
     * mit dem vorgegebenen Objekt und initialisiert die Steuerelemente der Maske
     * @param grundrissControl GrundrissControl, enthaelt das zugehoerige Control
     * @param grundrissStage Stage, enthaelt das Stage-Objekt fuer diese View
     */
    public GrundrissView (GrundrissControl grundrissControl, Stage grundrissStage){
    	super(grundrissStage);
        this.grundrissControl = grundrissControl;
        grundrissStage.setTitle("Sonderwuensche zu Grundriss-Varianten");
                
	    this.initKomponenten();
	    this.leseGrundrissSonderwuensche();
    }
  
    /* initialisiert die Steuerelemente auf der Maske */
    protected void initKomponenten(){
    	super.initKomponenten(); 
    	Image bild = new Image(
    		    getClass().getResourceAsStream("/gui/grundriss/Bild vom Haus GUI.png")
    		);

    		hausBild = new ImageView(bild);
    		hausBild.setFitWidth(300);
    		hausBild.setPreserveRatio(true);
       	super.getLblSonderwunsch().setText("Grundriss-Varianten");
       	super.getGridPaneSonderwunsch().add(hausBild, 0, 0, 4, 1);
       	super.getGridPaneSonderwunsch().add(lblWandKueche, 0, 1);
    	super.getGridPaneSonderwunsch().add(txtPreisWandKueche, 1, 1);
    	txtPreisWandKueche.setEditable(false);
        txtPreisWandKueche.setPrefColumnCount(6);
    	super.getGridPaneSonderwunsch().add(lblWandKuecheEuro, 2, 1);
    	super.getGridPaneSonderwunsch().add(chckBxWandKueche, 3, 1);
        fuegeGrundrissOptionHinzu("Tuer in der Wand zwischen Kueche und Essbereich", 2);
        fuegeGrundrissOptionHinzu("Grosses Zimmer im OG statt zwei kleinen Zimmern", 3);
        fuegeGrundrissOptionHinzu("Abgetrennter Treppenraum im DG", 4);
        fuegeGrundrissOptionHinzu("Vorrichtung eines Bades im DG", 5);
        fuegeGrundrissOptionHinzu("Ausfuehrung eines Bades im DG", 6);
    }

    /**
     * Fuegt eine Grundrissoption mit Preisfeld und Checkbox zur Maske hinzu.
     *
     * @param bezeichnung der Text, der fuer die Option angezeigt wird
     * @param zeile die Zeilennummer im Raster der Grundrissmaske
     */
    private void fuegeGrundrissOptionHinzu(String bezeichnung, int zeile) {
        Label lblVariante = new Label(bezeichnung);

        TextField txtPreis = new TextField();
        txtPreis.setEditable(false);
        txtPreis.setPrefColumnCount(6);

        CheckBox auswahl = new CheckBox();
        lblVariante.setLabelFor(auswahl);
        auswahl.setAccessibleText(bezeichnung);

        getGridPaneSonderwunsch().addRow(
            zeile, lblVariante, txtPreis, new Label("Euro"), auswahl
        );
    }
    
    /**
	 * macht das GrundrissView-Objekt sichtbar.
	 */
	public void oeffneGrundrissView(){ 
		super.oeffneBasisView();
	}
    
    private void leseGrundrissSonderwuensche(){
    	this.grundrissControl.leseGrundrissSonderwuensche();
    }
    
 	/* berechnet den Preis der ausgesuchten Sonderwuensche und zeigt diesen an */
  	protected void berechneUndZeigePreisSonderwuensche(){
  		// Es wird erst die Methode pruefeKonstellationSonderwuensche(int[] ausgewaehlteSw)
  		// aus dem Control aufgerufen, dann der Preis berechnet.
  	}
  	
   	/* speichert die ausgesuchten Sonderwuensche in der Datenbank ab */
  	protected void speichereSonderwuensche(){
 		// Es wird erst die Methode pruefeKonstellationSonderwuensche(int[] ausgewaehlteSw)
  		// aus dem Control aufgerufen, dann die Sonderwuensche gespeichert.
  	}
  	
 	
 }


