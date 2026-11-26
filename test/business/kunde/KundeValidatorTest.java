package business.kunde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Komponententests zu der Klasse KundeValidator (Pflichtenheftfunktion /F10/).
 */
class KundeValidatorTest {

	// erzeugt einen Kunden, dessen Daten alle gueltig sind
	private Kunde erzeugeGueltigenKunden() {
		Kunde kunde = new Kunde();
		kunde.setHausnummer(2);
		kunde.setKundennummer("0042");
		kunde.setVorname("Erika");
		kunde.setNachname("Mustermann");
		kunde.setTelefonnummer("0234123456");
		kunde.setEmail("erika@mustermann.de");
		return kunde;
	}

	// ---- gueltige Eingaben ----

	@Test
	void gueltigerKundeWirdAkzeptiert() {
		assertTrue(KundeValidator.pruefe(erzeugeGueltigenKunden()).isEmpty());
		assertTrue(KundeValidator.istGueltig(erzeugeGueltigenKunden()));
	}

	@Test
	void nurTelefonnummerReicht() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setEmail("");
		assertTrue(KundeValidator.istGueltig(kunde));
	}

	@Test
	void nurEmailReicht() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setTelefonnummer("");
		assertTrue(KundeValidator.istGueltig(kunde));
	}

	@Test
	void vornameIstOptional() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setVorname(null);
		assertTrue(KundeValidator.istGueltig(kunde));
	}

	@Test
	void nullWerteBeiTelefonOderEmailSindErlaubtSolangeEinsVorhandenIst() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setTelefonnummer(null);
		assertTrue(KundeValidator.istGueltig(kunde));
	}

	// ---- 1.1 Nachname ----

	@Test
	void nachnameNullWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setNachname(null);
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void nachnameLeerWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setNachname("");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void nachnameNurLeerzeichenWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setNachname("   ");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	// ---- 1.2 Telefonnummer oder E-Mail ----

	@Test
	void wederTelefonNochEmailWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setTelefonnummer("");
		kunde.setEmail("");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void telefonUndEmailNurLeerzeichenWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setTelefonnummer("  ");
		kunde.setEmail("  ");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void telefonnummerMitBuchstabenWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setTelefonnummer("0234abc");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void telefonnummerMitPlusOderLeerzeichenWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setTelefonnummer("+49234123456");
		assertFalse(KundeValidator.istGueltig(kunde));
		kunde.setTelefonnummer("0234 123456");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void emailOhneAtWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setEmail("erika.mustermann.de");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void ungueltigeTelefonnummerWirdAuchBeiGueltigerEmailAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setTelefonnummer("abc");
		kunde.setEmail("erika@mustermann.de");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	// ---- 1.3 Plannummer ----

	@Test
	void plannummerGrenzenWerdenAkzeptiert() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setHausnummer(1);
		assertTrue(KundeValidator.istGueltig(kunde));
		kunde.setHausnummer(24);
		assertTrue(KundeValidator.istGueltig(kunde));
	}

	@Test
	void plannummerNullWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setHausnummer(0);
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void plannummerNegativWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setHausnummer(-5);
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void plannummer25WirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setHausnummer(25);
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	// ---- 1.4 Kundennummer ----

	@Test
	void kundennummerMitFuehrendenNullenWirdAkzeptiert() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setKundennummer("0001");
		assertTrue(KundeValidator.istGueltig(kunde));
	}

	@Test
	void kundennummerNullWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setKundennummer(null);
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void kundennummerLeerOderNurLeerzeichenWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setKundennummer("");
		assertFalse(KundeValidator.istGueltig(kunde));
		kunde.setKundennummer("   ");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void kundennummerMitBuchstabenWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setKundennummer("12a4");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	@Test
	void kundennummerMitSonderzeichenOderLeerzeichenWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setKundennummer("12-34");
		assertFalse(KundeValidator.istGueltig(kunde));
		kunde.setKundennummer("12 34");
		assertFalse(KundeValidator.istGueltig(kunde));
	}

	// ---- Zeichen, die die CSV-Datei zerstoeren wuerden ----

	@Test
	void semikolonImNachnamenWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setNachname("Muster;mann");
		assertEquals(1, KundeValidator.pruefe(kunde).size());
	}

	@Test
	void anfuehrungszeichenImVornamenWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setVorname("Eri\"ka");
		assertEquals(1, KundeValidator.pruefe(kunde).size());
	}

	@Test
	void zeilenumbruchInEmailWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setEmail("erika@muster\nmann.de");
		assertEquals(1, KundeValidator.pruefe(kunde).size());
		kunde.setEmail("erika@muster\rmann.de");
		assertEquals(1, KundeValidator.pruefe(kunde).size());
	}

	// ---- 1.5 Laengen (Spalten der Tabelle Kunde) ----

	private String ziffern(int anzahl) {
		return "1".repeat(anzahl);
	}

	@Test
	void werteMitMaximalerLaengeWerdenAkzeptiert() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setKundennummer(ziffern(20));
		kunde.setVorname("a".repeat(45));
		kunde.setNachname("b".repeat(45));
		kunde.setTelefonnummer(ziffern(45));
		kunde.setEmail("c".repeat(40) + "@d.de");
		assertTrue(KundeValidator.istGueltig(kunde));
	}

	@Test
	void zuLangeKundennummerWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setKundennummer(ziffern(21));
		assertEquals(1, KundeValidator.pruefe(kunde).size());
	}

	@Test
	void zuLangerVornameWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setVorname("a".repeat(46));
		assertEquals(1, KundeValidator.pruefe(kunde).size());
	}

	@Test
	void zuLangerNachnameWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setNachname("b".repeat(46));
		assertEquals(1, KundeValidator.pruefe(kunde).size());
	}

	@Test
	void zuLangeTelefonnummerWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setTelefonnummer(ziffern(46));
		assertEquals(1, KundeValidator.pruefe(kunde).size());
	}

	@Test
	void zuLangeEmailWirdAbgelehnt() {
		Kunde kunde = erzeugeGueltigenKunden();
		kunde.setEmail("c".repeat(41) + "@d.de");
		assertEquals(1, KundeValidator.pruefe(kunde).size());
	}

	// ---- Sonderfaelle ----

	@Test
	void keinKundeObjektWirdAbgelehnt() {
		assertFalse(KundeValidator.istGueltig(null));
		assertEquals(1, KundeValidator.pruefe(null).size());
	}

	@Test
	void mehrereFehlerWerdenAlleGemeldet() {
		Kunde kunde = new Kunde();
		kunde.setHausnummer(0);
		kunde.setKundennummer("");
		kunde.setNachname("");
		kunde.setTelefonnummer("");
		kunde.setEmail("");
		List<String> fehler = KundeValidator.pruefe(kunde);
		// Plannummer, Kundennummer, Nachname, fehlende Telefonnummer/E-Mail
		assertEquals(4, fehler.size());
	}
}
