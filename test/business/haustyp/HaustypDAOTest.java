package business.haustyp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import business.db.DatenbankVerbindung;

/**
 * Komponententests zu der Klasse HaustypDAO (Bild vom Haus, F100).
 * Die Tests lesen nur aus der Datenbank und aendern nichts.
 */
class HaustypDAOTest {

	private final HaustypDAO dao = new HaustypDAO();

	@AfterAll
	static void verbindungSchliessen() {
		DatenbankVerbindung.schliessen();
	}

	// prueft, ob die Daten mit der JPG-Kennung FF D8 FF beginnen
	private static boolean istJpg(byte[] bild) {
		return bild.length > 3
			&& (bild[0] & 0xFF) == 0xFF
			&& (bild[1] & 0xFF) == 0xD8
			&& (bild[2] & 0xFF) == 0xFF;
	}

	@Test
	void bildMitDachgeschossIstVorhanden() throws SQLException {
		byte[] bild = dao.leseBild(HaustypDAO.MIT_DACHGESCHOSS);
		assertNotNull(bild);
		assertTrue(bild.length > 0);
	}

	@Test
	void bildOhneDachgeschossIstVorhanden() throws SQLException {
		byte[] bild = dao.leseBild(HaustypDAO.OHNE_DACHGESCHOSS);
		assertNotNull(bild);
		assertTrue(bild.length > 0);
	}

	@Test
	void beideBilderSindJpg() throws SQLException {
		assertTrue(istJpg(dao.leseBild(HaustypDAO.MIT_DACHGESCHOSS)));
		assertTrue(istJpg(dao.leseBild(HaustypDAO.OHNE_DACHGESCHOSS)));
	}

	@Test
	void bilderSindUnterschiedlich() throws SQLException {
		byte[] mit = dao.leseBild(HaustypDAO.MIT_DACHGESCHOSS);
		byte[] ohne = dao.leseBild(HaustypDAO.OHNE_DACHGESCHOSS);
		assertFalse(Arrays.equals(mit, ohne));
	}

	@Test
	void unbekannterHaustypLiefertNull() throws SQLException {
		assertNull(dao.leseBild(99));
	}
}
