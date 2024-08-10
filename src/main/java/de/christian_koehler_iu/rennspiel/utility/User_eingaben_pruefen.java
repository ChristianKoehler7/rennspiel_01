package de.christian_koehler_iu.rennspiel.utility;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Diese Klasse prüft Benutzereingaben auf Validität, insbesondere auf sichere und
 * zulässige Spielernamen. Sie bietet Methoden zur Validierung und zum Erhalten
 * von Fehlermeldungen bei ungültigen Eingaben.
 */
public class User_eingaben_pruefen {

    private final String benutzer_eingabe;
    private final int MIN_NAME_LENGTH = 3;
    private final int MAX_NAME_LENGTH = 20;
    private List<String> fehlermeldungen_string = new ArrayList<>();
    private List<String> fehlermeldungen_int = new ArrayList<>();
    private Integer eingabe_as_integer = null;

    // RegEx für erlaubte Zeichen (Buchstaben, Zahlen und Unterstriche)
    private final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_ ]+$");

    // RegEx für SQL-Schutz (verbietet SQL-Schlüsselwörter und spezielle Zeichen)
    private final Pattern SQL_INJECTION_PATTERN = Pattern.compile("(?i)(\\bselect\\b|\\bunion\\b|\\binsert\\b|\\bdelete\\b|\\bupdate\\b|--|;|\\bexec\\b|\\bexecute\\b|\\bdrop\\b)");

    /**
     * Konstruktor für die Klasse User_eingaben_pruefen.
     *
     * @param benutzer_eingabe Die Benutzereingabe, die überprüft werden soll.
     */
    public User_eingaben_pruefen(String benutzer_eingabe) {
        this.benutzer_eingabe = benutzer_eingabe;
        this.init_fehlermeldungen_string();
        this.init_int_eingabe();
    }

    /**
     * prüft ob eingabe-string
     *  - richtige länge hat
     *  - keine sql befehle enthält
     *  - nur erlaubte zeichen besitzt
     * falls fehler auftreten werden sie im attribut fehlermeldungen_string gespeichert
     */
    private void init_fehlermeldungen_string(){
        if (benutzer_eingabe == null || benutzer_eingabe.trim().isEmpty()) {
            fehlermeldungen_string.add("Der Name darf nicht null oder leer sein.");
            return;
        }

        String trimmedName = benutzer_eingabe.trim();
        int length = trimmedName.length();

        // Überprüfe die Länge des Namens
        if (length < MIN_NAME_LENGTH) {
            fehlermeldungen_string.add("Der Name muss mindestens " + MIN_NAME_LENGTH + " Zeichen lang sein.");
        }
        if (length > MAX_NAME_LENGTH) {
            fehlermeldungen_string.add("Der Name darf maximal " + MAX_NAME_LENGTH + " Zeichen lang sein.");
        }

        // Überprüfe die erlaubten Zeichen im Namen
        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            fehlermeldungen_string.add("Der Name darf nur Buchstaben, Zahlen und Unterstriche enthalten.");
        }

        // Überprüfe auf mögliche SQL-Injection-Risiken
        if (SQL_INJECTION_PATTERN.matcher(trimmedName).find()) {
            fehlermeldungen_string.add("Der Name enthält ungültige oder gefährliche Zeichen.");
        }

    }

    /**
     * prüft ob eingabe ein integer ist und einen wert >=0 hat
     *  falls fehler auftreten -> fehlermeldung der liste fehlermeldungen_int hinzufügen (attribut eingabe_as_integer=null)
     *  falls alles ok -> attribut eingabe_as_integer wert setzen (list fehlermeldungen_int ist leer)
     */
    private void init_int_eingabe() {
        // strings in integer umwandeln
        try {
            eingabe_as_integer = Integer.parseInt(benutzer_eingabe);
        } catch (NumberFormatException e) {
            fehlermeldungen_int.add("Bitte nur positive Ganzzahlen eingeben!");
            return;
        }

        if(eingabe_as_integer<=0){
            eingabe_as_integer = null;
            fehlermeldungen_int.add("Bitte nur positive Ganzzahlen (größer Null) eingeben!");
        }
    }

    /**
     * Überprüft, ob die Benutzereingabe gültig ist.
     * Diese Methode ruft get_fehlermeldungen() auf und prüft, ob keine Fehlermeldungen vorliegen.
     *
     * @return true, wenn die Eingabe gültig ist; andernfalls false.
     */
    public boolean is_gueltige_string_einagbe() {
        return fehlermeldungen_string.isEmpty();
    }

    /**
     * Gibt eine Liste von Fehlermeldungen zurück, wenn die Benutzereingabe ungültig ist.
     * Die Fehlermeldungen beschreiben die Gründe, warum die Eingabe nicht akzeptiert wurde.
     *
     * @return Eine Liste von Fehlermeldungen. Die Liste ist leer, wenn die Eingabe gültig ist.
     */
    public List<String> get_fehlermeldungen_string_eingabe() {
        return fehlermeldungen_string;
    }

    /**
     * Überprüft, ob die Benutzereingabe gültig ist.
     * Diese Methode ruft get_fehlermeldungen() auf und prüft, ob keine Fehlermeldungen vorliegen.
     *
     * @return true, wenn die Eingabe gültig ist; andernfalls false.
     */
    public boolean is_gueltige_int_einagbe() {
        return get_fehlermeldungen_string_eingabe().isEmpty();
    }

    @Nullable
    public Integer get_parsed_integer(){
        return eingabe_as_integer;
    }

    public List<String> get_fehlermeldungen_int(){
        return fehlermeldungen_int;
    }


}
