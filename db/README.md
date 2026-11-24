# Datenbank (PostgreSQL) – Einrichtung für alle im Team

Grundlage ist das ER-Modell und der MySQL-Dump vom Professor, umgesetzt in PostgreSQL.
Ansprechpartner für die Datenbank: Taha.

## Schnellstart (gemeinsame Online-Datenbank)

Die Datenbank läuft online bei Supabase. **Man muss nichts installieren.**

1. `db.properties.example` im Projektordner nach `db.properties` kopieren.
2. Bei `db.passwort` das Passwort eintragen, das ihr privat von Taha bekommt.
3. Fertig. Alle arbeiten mit denselben Daten.

**Das Passwort niemals auf GitHub hochladen** (das Repo ist öffentlich).
`db.properties` steht in `.gitignore` und wird deshalb nicht eingecheckt.

## Lokal (optional, z. B. zum Ausprobieren ohne die gemeinsamen Daten zu ändern)

1. PostgreSQL installieren: https://www.postgresql.org/download/
2. Datenbank `pm_db` anlegen und `db/pm_db_postgres.sql` darin ausführen
   (pgAdmin: Rechtsklick auf pm_db → Query Tool → Skript einfügen → F5).
   Das Skript löscht die Tabellen und legt sie neu an.
3. In `db.properties` die lokalen Zeilen benutzen (Port prüfen, meist 5432).

## JDBC-Treiber in Eclipse

Der Treiber liegt in `lib/postgresql-42.7.4.jar` und ist im Build Path eingetragen.
Falls Eclipse ihn nicht findet: Rechtsklick aufs Projekt → Build Path →
Configure Build Path → Libraries → Add JARs → `lib/postgresql-42.7.4.jar`.

## Tabellen

| Tabelle | Inhalt |
|---|---|
| `Haustyp` | 1 = mit Dachgeschoss, 2 = ohne Dachgeschoss |
| `Haus` | die 24 Häuser (Plannummer) mit ihrem Haustyp |
| `Kunde` | Kundendaten, höchstens ein Kunde pro Haus |
| `Sonderwunschkategorie` | 20 Grundriss, 30 Fenster/Türen, 40 Innentüren, 50 Heizungen, 60 Sanitär, 70 Fliesen, 80 Parkett, 90 Außenanlagen |
| `Sonderwunsch` | die einzelnen Sonderwünsche mit Preis und Kategorie |
| `Sonderwunsch_has_Haus` | welches Haus welche Sonderwünsche gewählt hat |

Ergänzung zum ER-Modell: `Kunde.Kundennummer` (eindeutig, nur Ziffern), weil die
GUI eine Kundennummer erfasst.

## Views (fertige Abfragen)

| View | Liefert |
|---|---|
| `v_kunde_haus` | Kunde mit Plannummer und Haustyp |
| `v_kunde_sonderwuensche` | alle gewählten Sonderwünsche je Kunde |
| `v_kunde_preis_pro_kategorie` | Summe je Kunde und Kategorie (z. B. nur Heizungen) |
| `v_kunde_gesamtpreis` | Gesamtpreis aller Sonderwünsche je Kunde |

## Im Java-Code benutzen

Die Verbindung holt man immer über `business.db.DatenbankVerbindung.getConnection()`.
Für Kunden gibt es `business.kunde.KundeDAO`:

| Methode | Zweck |
|---|---|
| `speichere(kunde)` | neuen Kunden anlegen |
| `aendere(kunde)` | Kundendaten ändern (Kundennummer bleibt gleich) |
| `loesche(kundennummer)` | Kunden löschen (inkl. Sonderwünsche seines Hauses) |
| `findeZuHaus(plannummer)` | Kunden zu einem Haus laden, `null` wenn frei |
| `findeZuKundennummer(nr)` | Kunden zu einer Kundennummer laden |
| `findeAlle()` | alle Kunden |

Bei doppelter Kundennummer oder bereits vergebenem Haus wirft `speichere` eine
`SQLException`, deren Meldung man direkt anzeigen kann.

Regeln fürs Team: immer `PreparedStatement` mit `?` benutzen (keine Werte in
SQL-Strings zusammenbauen), und Änderungen am Datenbankschema bitte nur über
`db/pm_db_postgres.sql` und nach Absprache mit Taha.
