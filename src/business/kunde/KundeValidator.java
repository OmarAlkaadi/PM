package business.kunde;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse, welche die Eingaben zu einem Kunden prueft (Pflichtenheftfunktion /F10/).
 * Gepruefte Bedingungen:
 * 1.1 Der Nachname muss vorhanden sein.
 * 1.2 Telefonnummer oder E-Mail-Adresse muessen vorhanden sein. Die Telefonnummer
 *     darf nur aus Ziffern bestehen, die E-Mail-Adresse muss ein @ enthalten.
 * 1.3 Die Plannummer des Hauses muss zwischen 1 und 24 liegen.
 */
public final class KundeValidator {

	/** kleinste erlaubte Plannummer */
	public static final int MIN_PLANNUMMER = 1;
	/** groesste erlaubte Plannummer */
	public static final int MAX_PLANNUMMER = 24;

	// Hilfsklasse, es werden keine Objekte erzeugt
	private KundeValidator() {
		super();
	}

	/**
	 * prueft die Daten eines Kunden.
	 * @param kunde Kunde-Objekt, dessen Daten zu pruefen sind
	 * @return List<String>, enthaelt die Fehlermeldungen. Ist die Liste leer,
	 *         sind alle Daten gueltig.
	 */
	public static List<String> pruefe(Kunde kunde) {
		List<String> fehler = new ArrayList<String>();
		if (kunde == null) {
			fehler.add("Es wurden keine Kundendaten angegeben.");
			return fehler;
		}

		// 1.3 Plannummer
		int nummer = kunde.getHausnummer();
		if (nummer < MIN_PLANNUMMER || nummer > MAX_PLANNUMMER) {
			fehler.add("Die Plannummer muss zwischen " + MIN_PLANNUMMER
				+ " und " + MAX_PLANNUMMER + " liegen.");
		}

		// 1.1 Nachname
		if (istLeer(kunde.getNachname())) {
			fehler.add("Der Nachname muss angegeben werden.");
		}

		// 1.2 Telefonnummer oder E-Mail-Adresse
		boolean telefonVorhanden = !istLeer(kunde.getTelefonnummer());
		boolean emailVorhanden = !istLeer(kunde.getEmail());
		if (!telefonVorhanden && !emailVorhanden) {
			fehler.add("Telefonnummer oder E-Mail-Adresse muss angegeben werden.");
		}
		if (telefonVorhanden && !bestehtNurAusZiffern(kunde.getTelefonnummer().trim())) {
			fehler.add("Die Telefonnummer darf nur aus Ziffern bestehen.");
		}
		if (emailVorhanden && !kunde.getEmail().contains("@")) {
			fehler.add("Die E-Mail-Adresse muss ein @ enthalten.");
		}
		return fehler;
	}

	/**
	 * prueft, ob die Daten eines Kunden gueltig sind.
	 * @param kunde Kunde-Objekt, dessen Daten zu pruefen sind
	 * @return boolean, true, falls alle Daten gueltig sind
	 */
	public static boolean istGueltig(Kunde kunde) {
		return pruefe(kunde).isEmpty();
	}

	// ein Text ist leer, falls er null ist oder nur aus Leerzeichen besteht
	private static boolean istLeer(String text) {
		return text == null || text.trim().isEmpty();
	}

	// Character.isDigit wird nicht benutzt, da es auch Ziffern anderer Schriften erlaubt
	private static boolean bestehtNurAusZiffern(String text) {
		for (int i = 0; i < text.length(); i++) {
			char zeichen = text.charAt(i);
			if (zeichen < '0' || zeichen > '9') {
				return false;
			}
		}
		return true;
	}
}
