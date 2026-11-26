package business.kunde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import business.db.DatenbankVerbindung;

/**
 * Tests zu den Datenbankzugriffen der Klasse KundeModel.
 *
 * Die Tests legen Kunden an und loeschen sie. Deshalb laufen sie nur gegen eine
 * lokale Datenbank (db.url mit localhost oder 127.0.0.1). Bei der gemeinsamen
 * Online-Datenbank oder ohne erreichbare Datenbank werden sie uebersprungen.
 * Benoetigt werden zwei freie Haeuser; vorhandene Kunden bleiben unveraendert.
 */
class KundeModelTest {

	private static final String LOKALE_URL =
		"jdbc:postgresql://(localhost|127\\.0\\.0\\.1)(:\\d+)?/.*";

	// Kundennummern, die nur von diesen Tests benutzt werden
	private static final String NUMMER_A = "99999000000000000001";
	private static final String NUMMER_B = "99999000000000000002";

	private final KundeModel model = KundeModel.getInstance();

	private int hausA;
	private int hausB;
	private boolean vorbereitet;

	@BeforeEach
	void bereiteDatenbankVor() throws SQLException {
		assumeTrue(istLokaleDatenbank(),
			"Uebersprungen: db.properties zeigt nicht auf eine lokale Datenbank.");
		try {
			DatenbankVerbindung.getConnection();
		} catch (SQLException exc) {
			assumeTrue(false, "Uebersprungen: keine Verbindung: " + exc.getMessage());
		}
		loescheTestkunden();
		vorbereitet = true;

		List<Integer> freie = freieHaeuser();
		assumeTrue(freie.size() >= 2, "Uebersprungen: weniger als zwei freie Haeuser.");
		hausA = freie.get(0);
		hausB = freie.get(1);
	}

	@AfterEach
	void raeumeAuf() throws SQLException {
		if (vorbereitet) {
			loescheTestkunden();
		}
	}

	// liest db.properties genauso wie DatenbankVerbindung
	private static boolean istLokaleDatenbank() {
		Properties konfig = new Properties();
		try (InputStream in = new FileInputStream(DatenbankVerbindung.KONFIG_DATEI)) {
			konfig.load(in);
		} catch (IOException exc) {
			return true; // ohne Datei benutzt die Anwendung localhost
		}
		String url = konfig.getProperty("db.url");
		return url == null || url.trim().matches(LOKALE_URL);
	}

	private void loescheTestkunden() throws SQLException {
		try (PreparedStatement ps = DatenbankVerbindung.getConnection().prepareStatement(
				"DELETE FROM Kunde WHERE Kundennummer IN (?, ?)")) {
			ps.setString(1, NUMMER_A);
			ps.setString(2, NUMMER_B);
			ps.executeUpdate();
		}
	}

	private List<Integer> freieHaeuser() throws SQLException {
		List<Integer> freie = new ArrayList<Integer>(model.getPlannummern());
		for (Kunde kunde : new KundeDAO().findeAlle()) {
			freie.remove(Integer.valueOf(kunde.getHausnummer()));
		}
		return freie;
	}

	private Kunde erzeugeKunden(String kundennummer, int hausnummer, String nachname) {
		Kunde kunde = new Kunde();
		kunde.setKundennummer(kundennummer);
		kunde.setHausnummer(hausnummer);
		kunde.setVorname("Erika");
		kunde.setNachname(nachname);
		kunde.setTelefonnummer("0234123456");
		kunde.setEmail("erika@mustermann.de");
		return kunde;
	}

	// ---- anlegen und lesen ----

	@Test
	void gespeicherterKundeWirdVollstaendigGelesen() throws Exception {
		model.speichereKunden(erzeugeKunden(NUMMER_A, hausA, "Mustermann"));

		Kunde gelesen = model.leseKunden(hausA);

		assertEquals(NUMMER_A, gelesen.getKundennummer());
		assertEquals(hausA, gelesen.getHausnummer());
		assertEquals("Erika", gelesen.getVorname());
		assertEquals("Mustermann", gelesen.getNachname());
		assertEquals("0234123456", gelesen.getTelefonnummer());
		assertEquals("erika@mustermann.de", gelesen.getEmail());
	}

	@Test
	void freiesHausLiefertKeinenKunden() throws Exception {
		assertNull(model.leseKunden(hausA));
	}

	@Test
	void leereEmailWirdAlsNullGelesen() throws Exception {
		Kunde kunde = erzeugeKunden(NUMMER_A, hausA, "Mustermann");
		kunde.setEmail("");
		model.speichereKunden(kunde);

		assertNull(model.leseKunden(hausA).getEmail());
	}

	@Test
	void zweiterKundeFuerDasselbeHausWirdAbgelehnt() throws Exception {
		model.speichereKunden(erzeugeKunden(NUMMER_A, hausA, "Erster"));

		assertThrows(SQLException.class,
			() -> model.speichereKunden(erzeugeKunden(NUMMER_B, hausA, "Zweiter")));
		assertEquals("Erster", model.leseKunden(hausA).getNachname());
	}

	@Test
	void doppelteKundennummerWirdAbgelehnt() throws Exception {
		model.speichereKunden(erzeugeKunden(NUMMER_A, hausA, "Erster"));

		assertThrows(SQLException.class,
			() -> model.speichereKunden(erzeugeKunden(NUMMER_A, hausB, "Zweiter")));
		assertNull(model.leseKunden(hausB));
	}

	// ---- aendern ----

	@Test
	void aendernBetrifftNurDenKundenMitDerKundennummer() throws Exception {
		model.speichereKunden(erzeugeKunden(NUMMER_A, hausA, "Eins"));
		model.speichereKunden(erzeugeKunden(NUMMER_B, hausB, "Zwei"));

		assertTrue(model.aendereKunden(erzeugeKunden(NUMMER_B, hausB, "Neu")));

		assertEquals("Eins", model.leseKunden(hausA).getNachname());
		assertEquals("Neu", model.leseKunden(hausB).getNachname());
	}

	@Test
	void aendernEinesUnbekanntenKundenLiefertFalse() throws Exception {
		assertFalse(model.aendereKunden(erzeugeKunden(NUMMER_A, hausA, "Niemand")));
	}

	@Disabled("Offen: laut /A11/ darf Aendern die Hausnummer nicht aendern")
	@Test
	void aendernLaesstDieHausnummerUnveraendert() throws Exception {
		model.speichereKunden(erzeugeKunden(NUMMER_A, hausA, "Mustermann"));

		model.aendereKunden(erzeugeKunden(NUMMER_A, hausB, "Mustermann"));

		assertEquals(NUMMER_A, model.leseKunden(hausA).getKundennummer());
		assertNull(model.leseKunden(hausB));
	}

	// ---- loeschen ----

	@Test
	void loeschenEntferntDenNamenNurBeiDiesemKunden() throws Exception {
		model.speichereKunden(erzeugeKunden(NUMMER_A, hausA, "Eins"));
		model.speichereKunden(erzeugeKunden(NUMMER_B, hausB, "Zwei"));

		assertTrue(model.loescheKunden(erzeugeKunden(NUMMER_A, hausA, "Eins")));

		// gilt fuer das heutige Verhalten und fuer /A11/
		Kunde geloescht = model.leseKunden(hausA);
		assertTrue(geloescht == null || geloescht.getNachname().isEmpty());
		assertEquals("Zwei", model.leseKunden(hausB).getNachname());
	}

	@Test
	void loeschenEinesUnbekanntenKundenLiefertFalse() throws Exception {
		assertFalse(model.loescheKunden(erzeugeKunden(NUMMER_A, hausA, "Niemand")));
	}

	@Disabled("Offen: laut /A11/ bleiben Kundennummer und Haus beim Loeschen erhalten")
	@Test
	void loeschenBehaeltKundennummerUndHaus() throws Exception {
		model.speichereKunden(erzeugeKunden(NUMMER_A, hausA, "Mustermann"));

		model.loescheKunden(erzeugeKunden(NUMMER_A, hausA, "Mustermann"));

		assertEquals(NUMMER_A, model.leseKunden(hausA).getKundennummer());
	}
}
