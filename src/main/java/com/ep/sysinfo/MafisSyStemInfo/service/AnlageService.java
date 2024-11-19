package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;

import java.util.List;

public interface AnlageService {

    /**
     * Ruft alle Anlagen ab.
     *
     * @return         Liste aller Anlagen
     */
    List<Anlage> findAllAnlagen();

    /**
     * Finde Anlagen anhand des angegebenen Betriebs.
     *
     * @param  betriebName    der Name des Betriebs, nach dem gesucht werden soll
     * @return                eine Liste von Anlagen, die mit dem angegebenen Betrieb verbunden sind
     */
    List<Anlage> findAnlagenByBetrieb(String betriebName);

    /**
     * Finde Anlagen nach Profit Center.
     *
     * @param  profitcenter  das Profit Center, nach dem gesucht werden soll
     * @return               eine Liste von Anlagen, die dem Profit Center entsprechen
     */
    List<Anlage> findAnlagenByProfitCenter(String profitcenter);

    /**
     * Finde Anlagen nach Modul-Typ.
     *
     * @param  modulTyp   der Modul-Typ, nach dem gesucht werden soll
     * @return           eine Liste von Anlagen, die dem Modul-Typ entsprechen
     */
    List<Anlage> findAnlagenByModulTyp(String modulTyp);

    /**
     * Finde Anlagen nach Kasse-Typ.
     *
     * @param  kasseTypes  die Kasse-Typen, nach denen gesucht werden soll
     * @return             eine Liste von Anlagen, die den Kasse-Typen entsprechen
     */
    List<Anlage> findAnlagenByKasseTypes(List<String> kasseTypes);

    /**
     * Finde Anlagen nach Schnittstellen.
     *
     * @param  typ      Der Typ der Schnittstellen
     * @param  untertyp Der Untertyp der Schnittstellen
     * @return          Liste von Anlagen, die den Schnittstellen-Kriterien entsprechen
     */
    List<Anlage> findAnlageBySchnittstellen(String typ, String untertyp);

    /**
     * Finde Anlagen nach Automaten mit angegebenen Parametern.
     *
     * @param  engineVersion  die Engine-Version der Automaten
     * @param  fccVersion     die FCC-Version der Automaten
     * @param  typ            der Typ der Automaten
     * @param  unterTyp       der Untertyp der Automaten
     * @return                eine Liste von Anlagen, die den angegebenen Parametern entsprechen
     */
    List<Anlage> findAnlageByAutomaten(String engineVersion, String fccVersion, String typ, String unterTyp);

    /**
     * Finde Anlagen nach Zutritts.
     *
     * @param  vonSektor   der Ausgangssektor
     * @param  nachSektor  der Zielsektor
     * @return             eine Liste von Anlagen, die den Zutritts-Kriterien entsprechen
     */
    List<Anlage> findAnlageByZutritts(String vonSektor, String nachSektor);

    /**
     * Finde Anlagen nach Fiskaldaten-Typ.
     *
     * @param  typSelection  die ausgewählte Art
     * @return               die Liste der Anlagen
     */
    List<Anlage> findAnlageByFiskaldatenTyp(String typSelection);

    /**
     * Finde Anlagen nach Medienarten.
     *
     * @param  typ  die Medienarten
     * @return      die Liste der Anlagen
     */
    List<Anlage> findAnlageByMedienarten(List<Integer> typ);
}
