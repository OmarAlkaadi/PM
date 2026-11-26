package business.haustyp;

import java.nio.file.Path;

import business.db.DatenbankVerbindung;

/**
 * Speichert die beiden Hausbilder aus db/bilder einmalig in die Datenbank.
 * Start in Eclipse: Rechtsklick -> Run As -> Java Application.
 */
public class BilderSpeichern {

	public static void main(String[] args) throws Exception {
		HaustypDAO dao = new HaustypDAO();
		try {
			boolean mit = dao.speichereBild(HaustypDAO.MIT_DACHGESCHOSS,
					Path.of("db", "bilder", "haus_mit_dg.jpg"));
			boolean ohne = dao.speichereBild(HaustypDAO.OHNE_DACHGESCHOSS,
					Path.of("db", "bilder", "haus_ohne_dg.jpg"));
			System.out.println("Bild mit DG gespeichert:  " + mit);
			System.out.println("Bild ohne DG gespeichert: " + ohne);
		} finally {
			DatenbankVerbindung.schliessen();
		}
	}
}
