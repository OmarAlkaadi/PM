package gui.kunde;

import business.kunde.*;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * Klasse, welche das Grundfenster mit den Kundendaten bereitstellt.
 */
public class KundeView{
 
	// das Control-Objekt des Grundfensters mit den Kundendaten
	private KundeControl kundeControl;
	// das Model-Objekt des Grundfensters mit den Kundendaten
	private KundeModel kundeModel;

    //---Anfang Attribute der grafischen Oberflaeche---
	private BorderPane borderPane 		= new BorderPane();
	private GridPane gridPane 			= new GridPane();
	private Label lblKunde    	      	= new Label("Kunde");
    private Label lblNummerHaus     	= new Label("Plannummer des Hauses");
    private ComboBox<Integer> 
        cmbBxNummerHaus                 = new ComboBox<Integer>();
    private Label lblVorname         	= new Label("Vorname");
    private TextField txtVorname     	= new TextField();
    
    // Eingabefelder für die Kundendaten(neue Labels)
    private Label lblKundennummer = new Label("Kundennummer");
    private TextField txtKundennummer = new TextField();

    private Label lblNachname = new Label("Nachname");
    private TextField txtNachname = new TextField();

    private Label lblTelefonnummer = new Label("Telefonnummer");
    private TextField txtTelefonnummer = new TextField();

    private Label lblEmail = new Label("E-Mail");
    private TextField txtEmail = new TextField();
    // Anzeige der Dachgeschossinformation
    private Label lblDachgeschoss = new Label("Dachgeschoss");
    private Label lblDachgeschossWert = new Label("-");
    private Button btnAnlegen	 	  	= new Button("Anlegen");
    private Button btnAendern 	      	= new Button("Ändern");
    private Button btnLoeschen 	 		= new Button("Löschen");
    private MenuBar mnBar 			  	= new MenuBar();
    private Menu mnSonderwuensche    	= new Menu("Sonderwünsche");
    private MenuItem mnItmGrundriss  	= new MenuItem("Grundrissvarianten");
    //-------Ende Attribute der grafischen Oberflaeche-------
  
    /**
     * erzeugt ein KundeView-Objekt und initialisiert die Steuerelemente der Maske
     * @param kundeControl KundeControl, enthaelt das zugehoerige Control
     * @param primaryStage Stage, enthaelt das Stage-Objekt fuer diese View
     * @param kundeModel KundeModel, enthaelt das zugehoerige Model
    */
    public KundeView (KundeControl kundeControl, Stage primaryStage, 
    	KundeModel kundeModel){
        this.kundeControl = kundeControl;
        this.kundeModel = kundeModel;
        
        primaryStage.setTitle(this.kundeModel.getUeberschrift());	
	    Scene scene = new Scene(borderPane, 650, 500); //Fenster vergrößert, damit alle Eingabefelder sichtbar sind
	    primaryStage.setScene(scene);
        primaryStage.show();

	    this.initKomponenten();
	    this.initListener();
    }

 
    /* initialisiert die Steuerelemente auf der Maske */
    private void initKomponenten(){
    	borderPane.setCenter(gridPane);
	    gridPane.setHgap(10);
	    gridPane.setVgap(10);
	    gridPane.setPadding(new Insets(25, 25, 25, 25));
       	
	    gridPane.add(lblKunde, 0, 1);
       	lblKunde.setMinSize(150, 40);
	    lblKunde.setFont(Font.font("Arial", FontWeight.BOLD, 24));
	    gridPane.add(lblNummerHaus, 0, 2);
	    gridPane.add(cmbBxNummerHaus, 1, 2);
	    cmbBxNummerHaus.setMinSize(150,  25);
	    cmbBxNummerHaus.setItems(this.kundeModel.getPlannummern());
	    
	    gridPane.add(lblKundennummer, 0, 3);
	    gridPane.add(txtKundennummer, 1, 3);
	    
	    gridPane.add(lblVorname, 0, 4);
	    gridPane.add(txtVorname, 1, 4);
	    //kundennummer, nachname, telefonnummer, email und dachgeschoss wurde hinzugefuegt 
	  

	    gridPane.add(lblNachname, 0, 5);
	    gridPane.add(txtNachname, 1, 5);

	    gridPane.add(lblTelefonnummer, 0, 6);
	    gridPane.add(txtTelefonnummer, 1, 6);

	    gridPane.add(lblEmail, 0, 7);
	    gridPane.add(txtEmail, 1, 7);

	    gridPane.add(lblDachgeschoss, 0, 8);
	    gridPane.add(lblDachgeschossWert, 1, 8);
	    
	    
	    
	    // Buttons
	    // Buttons unter den Eingabefeldern platzieren (auf 10 gesetzt !)
	    gridPane.add(btnAnlegen, 0, 10);
	    btnAnlegen.setMinSize(150,  25);
	    gridPane.add(btnAendern, 1, 10);
	    btnAendern.setMinSize(150,  25);
	    gridPane.add(btnLoeschen, 2, 10);
	    btnLoeschen.setMinSize(150,  25);
	    // MenuBar und Menu
	    borderPane.setTop(mnBar);
	    mnBar.getMenus().add(mnSonderwuensche);
	    mnSonderwuensche.getItems().add(mnItmGrundriss);
    }

    /* initialisiert die Listener zu den Steuerelementen auf de Maske */
    private void initListener(){
    	cmbBxNummerHaus.setOnAction(aEvent-> {
    		 holeInfoDachgeschoss();  
    		 leseKunden();
     	});
       	btnAnlegen.setOnAction(aEvent-> {
 	        legeKundenAn();
	    });
    	btnAendern.setOnAction(aEvent-> {
           	aendereKunden();
	    });
       	btnLoeschen.setOnAction(aEvent-> { 
           	loescheKunden();
	    });
      	mnItmGrundriss.setOnAction(aEvent-> {
 	        kundeControl.oeffneGrundrissControl(); 
	    });
    }
    
    private void holeInfoDachgeschoss(){
    	
    	Integer nummer = cmbBxNummerHaus.getValue(); // Ausgewählte Plannummer holen
    	
    	if (nummer == null) {
	        lblDachgeschossWert.setText("-");
	        return;
	    }
    	// Diese Häuser haben kein Dachgeschoss
	    if (nummer == 1 || nummer == 6 || nummer == 7 ||
	        nummer == 14 || nummer == 15 || nummer == 24) {
	        lblDachgeschossWert.setText("Nein");
	    } else {
	        lblDachgeschossWert.setText("Ja");
	    }
    	

    	   
    }
    
    private void leseKunden(){

        Integer plannummer = cmbBxNummerHaus.getValue();

        if (plannummer == null) {
            return;
        }

        kundeControl.leseKunden(plannummer);
    }


    public void zeigeKunden(Kunde kunde) {

        if (kunde == null) {

            txtKundennummer.clear();
            txtVorname.clear();
            txtNachname.clear();
            txtTelefonnummer.clear();
            txtEmail.clear();

            return;
        }

        txtKundennummer.setText(kunde.getKundennummer());
        txtVorname.setText(kunde.getVorname());
        txtNachname.setText(kunde.getNachname());
        txtTelefonnummer.setText(kunde.getTelefonnummer());
        txtEmail.setText(kunde.getEmail());
    }
    
    private void legeKundenAn(){
         Kunde kunde = new Kunde();
         Integer plannummer = cmbBxNummerHaus.getValue();
         // ohne Auswahl wird 0 gesetzt, die Pruefung lehnt dies ab
         kunde.setHausnummer(plannummer == null ? 0 : plannummer);
         kunde.setKundennummer(txtKundennummer.getText());
         kunde.setVorname(txtVorname.getText());
         kunde.setNachname(txtNachname.getText());
         kunde.setTelefonnummer(txtTelefonnummer.getText());
         kunde.setEmail(txtEmail.getText());
         kundeControl.speichereKunden(kunde);

		 
   	}
    
    private void aendereKunden() {

        Kunde kunde = new Kunde();

        Integer plannummer = cmbBxNummerHaus.getValue();

        kunde.setHausnummer(plannummer == null ? 0 : plannummer);
        kunde.setKundennummer(txtKundennummer.getText());
        kunde.setVorname(txtVorname.getText());
        kunde.setNachname(txtNachname.getText());
        kunde.setTelefonnummer(txtTelefonnummer.getText());
        kunde.setEmail(txtEmail.getText());

        kundeControl.aendereKunden(kunde);
    }
  	
    //loeschen
   	private void loescheKunden(){
        Kunde kunde = new Kunde();

        Integer plannummer = cmbBxNummerHaus.getValue();

        kunde.setHausnummer(plannummer == null ? 0 : plannummer);
        kunde.setKundennummer(txtKundennummer.getText());
        kunde.setVorname(txtVorname.getText());
        kunde.setNachname(txtNachname.getText());
        kunde.setTelefonnummer(txtTelefonnummer.getText());
        kunde.setEmail(txtEmail.getText());

        kundeControl.loescheKunden(kunde);
    }
   	
   	
   /** zeigt ein Fehlermeldungsfenster an
    * @param ueberschrift, Ueberschrift fuer das Fehlermeldungsfenster
    * @param meldung, String, welcher die Fehlermeldung enthaelt
    */
    public void zeigeFehlermeldung(String ueberschrift, String meldung){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Fehlermeldung");
        alert.setHeaderText(ueberschrift);
        alert.setContentText(meldung);
        alert.show();
    }

}


