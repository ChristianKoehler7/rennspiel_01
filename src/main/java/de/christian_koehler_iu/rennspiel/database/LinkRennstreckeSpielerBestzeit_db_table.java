package de.christian_koehler_iu.rennspiel.database;

public class LinkRennstreckeSpielerBestzeit_db_table {

    public final String TABELLENNAME = "Link_rennstrecke_spielerBestzeit";
    public final String SPALTENNAME_ID = "id";
    public final String SPALTENNAME_FK_RENNSTRECKE = "fk_rennstrecke";
    public final String SPALTENNAME_FK_SPIELER = "fk_spieler";
    public final String SPALTENNAME_BESTZEIT = "bestzeit";


    public String get_create_table_string(){
        return "CREATE TABLE IF NOT EXISTS " + TABELLENNAME + " (" +
                SPALTENNAME_ID + " TEXT PRIMARY KEY," +
                SPALTENNAME_FK_RENNSTRECKE + " TEXT NOT NULL," +
                SPALTENNAME_FK_SPIELER + " TEXT NOT NULL," +
                SPALTENNAME_BESTZEIT + " REAL NOT NULL," +
                "FOREIGN KEY ("+ SPALTENNAME_FK_RENNSTRECKE +") REFERENCES " + new Rennstrecke_db_table().TABELLENNAME + "("+new Rennstrecke_db_table().SPALTENNAME_NAME+")," +
                "FOREIGN KEY ("+ SPALTENNAME_FK_SPIELER +") REFERENCES "+ new Spieler_db_table().TABELLENNAME +"Spieler("+ new Spieler_db_table().SPALTENNAME_NAME +")" +
                ");";
    }
}
