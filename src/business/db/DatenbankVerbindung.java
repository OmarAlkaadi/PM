package business.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Klasse, welche die Verbindung zur PostgreSQL-Datenbank bereitstellt.
 * Alle Klassen, die auf die Datenbank zugreifen, holen sich die Verbindung
 * ueber {@link #getConnection()}.
 *
 * Die Zugangsdaten stehen in der Datei db.properties im Projektordner
 * (Vorlage: db.properties.example). Die Datei wird nicht eingecheckt,
 * damit jeder seine eigenen Zugangsdaten verwenden kann.
 * Fehlt sie, werden die Standardwerte unten benutzt.
 */
public final class DatenbankVerbindung {

	/** Name der Konfigurationsdatei im Projektordner */
	public static final String KONFIG_DATEI = "db.properties";

	private static final String STANDARD_URL = "jdbc:postgresql://localhost:5432/pm_db";
	private static final String STANDARD_BENUTZER = "postgres";
	private static final String STANDARD_PASSWORT = "postgres";

	// die einzige Verbindung der Anwendung
	private static Connection connection;

	// Hilfsklasse, es werden keine Objekte erzeugt
	private DatenbankVerbindung() {
		super();
	}

	/**
	 * gibt die Verbindung zur Datenbank heraus. Besteht noch keine Verbindung
	 * oder wurde sie geschlossen, wird eine neue aufgebaut.
	 * @return Connection, Verbindung zur Datenbank
	 * @throws SQLException, Verbindung konnte nicht aufgebaut werden
	 */
	public static synchronized Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed()) {
			Properties konfig = leseKonfiguration();
			connection = DriverManager.getConnection(
				konfig.getProperty("db.url", STANDARD_URL),
				konfig.getProperty("db.benutzer", STANDARD_BENUTZER),
				konfig.getProperty("db.passwort", STANDARD_PASSWORT));
		}
		return connection;
	}

	/**
	 * schliesst die Verbindung zur Datenbank, z. B. beim Beenden der Anwendung.
	 */
	public static synchronized void schliessen() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException exc) {
				exc.printStackTrace();
			}
			connection = null;
		}
	}

	// liest db.properties ein; fehlt die Datei, bleiben die Standardwerte
	private static Properties leseKonfiguration() {
		Properties konfig = new Properties();
		try (InputStream in = new FileInputStream(KONFIG_DATEI)) {
			konfig.load(in);
		} catch (IOException exc) {
			System.err.println(KONFIG_DATEI + " nicht gefunden, Standardwerte werden benutzt.");
		}
		return konfig;
	}
}
