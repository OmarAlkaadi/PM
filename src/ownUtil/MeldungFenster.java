package ownUtil;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MeldungFenster {

    public static void zeigeErfolg(String meldung) {
        Alert alert = new Alert(AlertType.INFORMATION);

        alert.setTitle("Erfolgreich");
        alert.setHeaderText("Kunde erfolgreich angelegt");
        alert.setContentText(meldung);

        alert.showAndWait();
    }

    public static void zeigeFehler(String meldung) {
        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle("Fehler");
        alert.setHeaderText("Speicherung fehlgeschlagen");
        alert.setContentText(meldung);

        alert.showAndWait();
    }
}
