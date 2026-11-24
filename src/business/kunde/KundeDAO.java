package business.kunde;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import business.db.DatenbankVerbindung;

/**
 * Klasse fuer den Datenbankzugriff auf die Tabelle Kunde
 * (Anlegen, Lesen, Aendern, Loeschen).
 * Der Kunde wird ueber seine Kundennummer identifiziert.
 */
public class KundeDAO {

	// SQLState von PostgreSQL bei Verletzung einer UNIQUE-Bedingung
	private static final String SQLSTATE_UNIQUE = "23505";

	private static final String SPALTEN =
		"Kundennummer, Haus_Hausnr, Vorname, Nachname, Telefon, email";

	/**
	 * speichert einen neuen Kunden in die Datenbank.
	 * @param kunde Kunde-Objekt, welches zu speichern ist
	 * @throws SQLException, z. B. wenn Kundennummer oder Haus schon vergeben sind
	 *         (die Meldung ist dann fuer die Anzeige geeignet)
	 */
	public void speichere(Kunde kunde) throws SQLException {
		String sql = "INSERT INTO Kunde (" + SPALTEN + ") VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = verbindung().prepareStatement(sql)) {
			setzeWerte(ps, kunde);
			ps.executeUpdate();
		} catch (SQLException exc) {
			throw uebersetze(exc);
		}
	}

	/**
	 * aendert die Daten eines vorhandenen Kunden. Die Kundennummer selbst
	 * kann nicht geaendert werden, sie identifiziert den Kunden.
	 * @param kunde Kunde-Objekt mit den neuen Daten
	 * @return boolean, true, falls ein Kunde geaendert wurde
	 * @throws SQLException, Fehler beim Zugriff auf die Datenbank
	 */
	public boolean aendere(Kunde kunde) throws SQLException {
		String sql = "UPDATE Kunde SET Haus_Hausnr = ?, Vorname = ?, Nachname = ?, "
			+ "Telefon = ?, email = ? WHERE Kundennummer = ?";
		try (PreparedStatement ps = verbindung().prepareStatement(sql)) {
			setzeHausnummer(ps, 1, kunde.getHausnummer());
			ps.setString(2, kunde.getVorname());
			ps.setString(3, kunde.getNachname());
			ps.setString(4, leerZuNull(kunde.getTelefonnummer()));
			ps.setString(5, leerZuNull(kunde.getEmail()));
			ps.setString(6, kunde.getKundennummer().trim());
			return ps.executeUpdate() == 1;
		} catch (SQLException exc) {
			throw uebersetze(exc);
		}
	}

	/**
	 * loescht einen Kunden. Die Sonderwuensche seines Hauses werden ebenfalls
	 * geloescht, damit ein spaeterer Kaeufer des Hauses ohne alte
	 * Sonderwuensche startet. Beides passiert in einer Transaktion.
	 * @param kundennummer Kundennummer des zu loeschenden Kunden
	 * @return boolean, true, falls ein Kunde geloescht wurde
	 * @throws SQLException, Fehler beim Zugriff auf die Datenbank
	 */
	public boolean loesche(String kundennummer) throws SQLException {
		Connection con = verbindung();
		boolean autoCommit = con.getAutoCommit();
		con.setAutoCommit(false);
		try (PreparedStatement psWuensche = con.prepareStatement(
				"DELETE FROM Sonderwunsch_has_Haus WHERE Haus_Hausnr = "
				+ "(SELECT Haus_Hausnr FROM Kunde WHERE Kundennummer = ?)");
			 PreparedStatement psKunde = con.prepareStatement(
				"DELETE FROM Kunde WHERE Kundennummer = ?")) {
			psWuensche.setString(1, kundennummer.trim());
			psWuensche.executeUpdate();
			psKunde.setString(1, kundennummer.trim());
			boolean geloescht = psKunde.executeUpdate() == 1;
			con.commit();
			return geloescht;
		} catch (SQLException exc) {
			con.rollback();
			throw exc;
		} finally {
			con.setAutoCommit(autoCommit);
		}
	}

	/**
	 * liest den Kunden zu einer Kundennummer.
	 * @param kundennummer Kundennummer des gesuchten Kunden
	 * @return Kunde, oder null, falls es keinen Kunden mit dieser Nummer gibt
	 * @throws SQLException, Fehler beim Zugriff auf die Datenbank
	 */
	public Kunde findeZuKundennummer(String kundennummer) throws SQLException {
		String sql = "SELECT " + SPALTEN + " FROM Kunde WHERE Kundennummer = ?";
		try (PreparedStatement ps = verbindung().prepareStatement(sql)) {
			ps.setString(1, kundennummer.trim());
			return ersterKunde(ps);
		}
	}

	/**
	 * liest den Kunden, der das Haus mit der angegebenen Plannummer gekauft hat.
	 * @param hausnummer Plannummer des Hauses (1 bis 24)
	 * @return Kunde, oder null, falls das Haus noch keinen Kunden hat
	 * @throws SQLException, Fehler beim Zugriff auf die Datenbank
	 */
	public Kunde findeZuHaus(int hausnummer) throws SQLException {
		String sql = "SELECT " + SPALTEN + " FROM Kunde WHERE Haus_Hausnr = ?";
		try (PreparedStatement ps = verbindung().prepareStatement(sql)) {
			ps.setInt(1, hausnummer);
			return ersterKunde(ps);
		}
	}

	/**
	 * liest alle Kunden, sortiert nach Plannummer.
	 * @return List<Kunde>, alle Kunden
	 * @throws SQLException, Fehler beim Zugriff auf die Datenbank
	 */
	public List<Kunde> findeAlle() throws SQLException {
		String sql = "SELECT " + SPALTEN + " FROM Kunde ORDER BY Haus_Hausnr, Kundennummer";
		List<Kunde> kunden = new ArrayList<Kunde>();
		try (PreparedStatement ps = verbindung().prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				kunden.add(erzeugeKunde(rs));
			}
		}
		return kunden;
	}

	// ---- Hilfsmethoden ------------------------------------------------

	private Connection verbindung() throws SQLException {
		return DatenbankVerbindung.getConnection();
	}

	private void setzeWerte(PreparedStatement ps, Kunde kunde) throws SQLException {
		ps.setString(1, kunde.getKundennummer().trim());
		setzeHausnummer(ps, 2, kunde.getHausnummer());
		ps.setString(3, kunde.getVorname());
		ps.setString(4, kunde.getNachname());
		ps.setString(5, leerZuNull(kunde.getTelefonnummer()));
		ps.setString(6, leerZuNull(kunde.getEmail()));
	}

	// Hausnummer 0 bedeutet "kein Haus" und wird als NULL gespeichert
	private void setzeHausnummer(PreparedStatement ps, int index, int hausnummer)
		throws SQLException {
		if (hausnummer <= 0) {
			ps.setNull(index, Types.INTEGER);
		} else {
			ps.setInt(index, hausnummer);
		}
	}

	private Kunde ersterKunde(PreparedStatement ps) throws SQLException {
		try (ResultSet rs = ps.executeQuery()) {
			return rs.next() ? erzeugeKunde(rs) : null;
		}
	}

	private Kunde erzeugeKunde(ResultSet rs) throws SQLException {
		Kunde kunde = new Kunde();
		kunde.setKundennummer(rs.getString("Kundennummer"));
		kunde.setHausnummer(rs.getInt("Haus_Hausnr")); // NULL -> 0
		kunde.setVorname(rs.getString("Vorname"));
		kunde.setNachname(rs.getString("Nachname"));
		kunde.setTelefonnummer(rs.getString("Telefon"));
		kunde.setEmail(rs.getString("email"));
		return kunde;
	}

	private String leerZuNull(String text) {
		return (text == null || text.trim().isEmpty()) ? null : text.trim();
	}

	// macht aus Verletzungen der UNIQUE-Bedingungen eine verstaendliche Meldung
	private SQLException uebersetze(SQLException exc) {
		if (SQLSTATE_UNIQUE.equals(exc.getSQLState()) && exc.getMessage() != null) {
			if (exc.getMessage().contains("uq_kunde_kundennummer")) {
				return new SQLException("Diese Kundennummer ist bereits vergeben.",
					exc.getSQLState(), exc);
			}
			if (exc.getMessage().contains("uq_kunde_haus")) {
				return new SQLException("Dieses Haus ist bereits einem Kunden zugeordnet.",
					exc.getSQLState(), exc);
			}
		}
		return exc;
	}
}
