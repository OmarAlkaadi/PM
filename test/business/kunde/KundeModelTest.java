package business.kunde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Komponententests zu den CSV-Dateizugriffen der Klasse KundeModel.
 * KundeModel benutzt fest die Datei "kunden.csv". Deshalb wird eine vorhandene
 * Datei vor jedem Test gesichert und danach wiederhergestellt.
 */
class KundeModelTest {

	private static final String KOPFZEILE =
		"Hausnummer;Kundennummer;Vorname;Nachname;Telefonnummer;Email";

	private final Path datei = Paths.get("kunden.csv");
	private final Path sicherung = Paths.get("kunden.csv.testsicherung");

	private final KundeModel model = KundeModel.getInstance();

	private boolean vorbereitet;
	private boolean dateiWarVorhanden;

	@BeforeEach
	void sichereDatei() throws IOException {
		// eine alte Sicherung wird nie ueberschrieben
		assertFalse(Files.exists(sicherung),
			"Bitte zuerst " + sicherung + " pruefen und umbenennen.");
		dateiWarVorhanden = Files.exists(datei);
		if (dateiWarVorhanden) {
			Files.move(datei, sicherung);
		}
		vorbereitet = true;
	}

	@AfterEach
	void stelleDateiWiederHer() throws IOException {
		if (!vorbereitet) {
			return;
		}
		Files.deleteIfExists(datei);
		if (dateiWarVorhanden) {
			Files.move(sicherung, datei);
		}
	}

	private Kunde erzeugeKunden(int hausnummer, String nachname) {
		Kunde kunde = new Kunde();
		kunde.setHausnummer(hausnummer);
		kunde.setKundennummer("0042");
		kunde.setVorname("Erika");
		kunde.setNachname(nachname);
		kunde.setTelefonnummer("0234123456");
		kunde.setEmail("erika@mustermann.de");
		return kunde;
	}

	private void schreibeDatei(String... zeilen) throws IOException {
		Files.write(datei, List.of(zeilen), Charset.defaultCharset());
	}

	// ---- anlegen und lesen ----

	@Test
	void gespeicherterKundeWirdVollstaendigGelesen() throws IOException {
		model.speichereKunden(erzeugeKunden(3, "Mustermann"));

		Kunde gelesen = model.leseKunden(3);

		assertEquals(3, gelesen.getHausnummer());
		assertEquals("0042", gelesen.getKundennummer());
		assertEquals("Erika", gelesen.getVorname());
		assertEquals("Mustermann", gelesen.getNachname());
		assertEquals("0234123456", gelesen.getTelefonnummer());
		assertEquals("erika@mustermann.de", gelesen.getEmail());
	}

	@Test
	void ersteZeileIstDieKopfzeile() throws IOException {
		model.speichereKunden(erzeugeKunden(3, "Mustermann"));

		assertEquals(KOPFZEILE,
			Files.readAllLines(datei, Charset.defaultCharset()).get(0));
	}

	@Test
	void zweiterKundeFuerDasselbeHausWirdAbgelehnt() throws IOException {
		model.speichereKunden(erzeugeKunden(3, "Erster"));

		assertThrows(IOException.class,
			() -> model.speichereKunden(erzeugeKunden(3, "Zweiter")));
		assertEquals("Erster", model.leseKunden(3).getNachname());
	}

	@Test
	void fehlendeWerteWerdenAlsLeererTextGelesen() throws IOException {
		Kunde kunde = erzeugeKunden(3, "Mustermann");
		kunde.setVorname(null);
		kunde.setEmail(null);
		model.speichereKunden(kunde);

		assertEquals("", model.leseKunden(3).getVorname());
		assertEquals("", model.leseKunden(3).getEmail());
	}

	@Test
	void ohneDateiWirdKeinKundeGefunden() throws IOException {
		assertNull(model.leseKunden(3));
	}

	// ---- aendern ----

	@Test
	void aendernBetrifftNurDenKundenDesHauses() throws IOException {
		model.speichereKunden(erzeugeKunden(1, "Eins"));
		model.speichereKunden(erzeugeKunden(2, "Zwei"));

		model.aendereKunden(erzeugeKunden(2, "Neu"));

		assertEquals("Eins", model.leseKunden(1).getNachname());
		assertEquals("Neu", model.leseKunden(2).getNachname());
	}

	@Test
	void aendernOhneVorhandenenKundenSchlaegtFehl() {
		assertThrows(IOException.class,
			() -> model.aendereKunden(erzeugeKunden(3, "Mustermann")));
	}

	// ---- loeschen ----

	@Test
	void loeschenEntferntNurDenKundenDesHauses() throws IOException {
		model.speichereKunden(erzeugeKunden(1, "Eins"));
		model.speichereKunden(erzeugeKunden(2, "Zwei"));

		model.loescheKunden(1);

		assertNull(model.leseKunden(1));
		assertEquals("Zwei", model.leseKunden(2).getNachname());
	}

	@Test
	void loeschenOhneVorhandenenKundenSchlaegtFehl() {
		assertThrows(IOException.class, () -> model.loescheKunden(3));
	}

	// ---- fehlerhafte Dateien ----

	@Test
	void falscheKopfzeileWirdAbgelehnt() throws IOException {
		schreibeDatei("falsch");

		assertThrows(IOException.class, () -> model.leseKunden(1));
	}

	@Test
	void datensatzMitZuWenigenSpaltenWirdAbgelehnt() throws IOException {
		schreibeDatei(KOPFZEILE, "1;0042;Erika");

		assertThrows(IOException.class, () -> model.leseKunden(1));
	}

	@Test
	void ungueltigeHausnummerWirdAbgelehnt() throws IOException {
		schreibeDatei(KOPFZEILE, "x;0042;Erika;Mustermann;123;a@b.de");

		assertThrows(IOException.class, () -> model.leseKunden(1));
	}

	@Test
	void leereZeilenWerdenUebersprungen() throws IOException {
		schreibeDatei(KOPFZEILE, "", "1;0042;Erika;Mustermann;123;a@b.de");

		assertEquals("Mustermann", model.leseKunden(1).getNachname());
	}
}
