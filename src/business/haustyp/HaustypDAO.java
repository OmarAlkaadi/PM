package business.haustyp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import business.db.DatenbankVerbindung;

/**
 * Klasse fuer den Datenbankzugriff auf die Bilder in der Tabelle Haustyp (F100).
 * Haustyp 1 = Haus mit Dachgeschoss, Haustyp 2 = Haus ohne Dachgeschoss.
 */
public class HaustypDAO {

	/** Haustyp fuer Haeuser mit Dachgeschoss */
	public static final int MIT_DACHGESCHOSS = 1;
	/** Haustyp fuer Haeuser ohne Dachgeschoss */
	public static final int OHNE_DACHGESCHOSS = 2;

	/**
	 * speichert ein Bild zu einem Haustyp in die Datenbank.
	 * @param idHaustyp 1 = mit DG, 2 = ohne DG
	 * @param bild Bilddaten
	 * @return boolean, true, falls der Haustyp gefunden und das Bild gespeichert wurde
	 * @throws SQLException, Fehler beim Zugriff auf die Datenbank
	 */
	public boolean speichereBild(int idHaustyp, byte[] bild) throws SQLException {
		String sql = "UPDATE Haustyp SET Bild = ? WHERE idHaustyp = ?";
		try (PreparedStatement ps = verbindung().prepareStatement(sql)) {
			ps.setBytes(1, bild);
			ps.setInt(2, idHaustyp);
			return ps.executeUpdate() == 1;
		}
	}

	/**
	 * liest eine Bilddatei ein und speichert sie zu einem Haustyp in die Datenbank.
	 * @param idHaustyp 1 = mit DG, 2 = ohne DG
	 * @param datei Pfad der Bilddatei
	 * @return boolean, true, falls das Bild gespeichert wurde
	 * @throws IOException, Datei konnte nicht gelesen werden
	 * @throws SQLException, Fehler beim Zugriff auf die Datenbank
	 */
	public boolean speichereBild(int idHaustyp, Path datei) throws IOException, SQLException {
		return speichereBild(idHaustyp, Files.readAllBytes(datei));
	}

	/**
	 * liest das Bild zu einem Haustyp aus der Datenbank.
	 * @param idHaustyp 1 = mit DG, 2 = ohne DG
	 * @return byte[], Bilddaten oder null, falls kein Bild gespeichert ist
	 * @throws SQLException, Fehler beim Zugriff auf die Datenbank
	 */
	public byte[] leseBild(int idHaustyp) throws SQLException {
		String sql = "SELECT Bild FROM Haustyp WHERE idHaustyp = ?";
		try (PreparedStatement ps = verbindung().prepareStatement(sql)) {
			ps.setInt(1, idHaustyp);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getBytes(1) : null;
			}
		}
	}

	private Connection verbindung() throws SQLException {
		return DatenbankVerbindung.getConnection();
	}
}
