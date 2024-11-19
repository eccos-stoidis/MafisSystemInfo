package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Benutzer;

import java.time.LocalDateTime;
import java.util.List;

public interface BenutzerService {

    /**
     * Aktualisiert das Passwort eines Benutzers.
     *
     * @param  userId          die ID des Benutzers
     * @param  newKennwort     das neue Passwort, das festgelegt werden soll
     * @param  currentUserName der Benutzername des Nutzers, der die Passwortänderung durchführt
     */
    void updateKennwort(Long userId, String newKennwort, String currentUserName);

    /**
     * Überprüft, ob das alte Passwort mit dem gespeicherten Passwort übereinstimmt.
     *
     * @param  oldPassword    das alte Passwort, das überprüft werden soll
     * @param  storedPassword das gespeicherte Passwort zum Vergleich
     * @return                true, wenn das alte Passwort mit dem gespeicherten Passwort übereinstimmt, andernfalls false
     */
    boolean verifyOldPassword(String oldPassword, String storedPassword);

    /**
     * Aktualisiert Benutzerinformationen im System.
     *
     * @param  userId           Die ID des Benutzers, der aktualisiert werden soll
     * @param  username         Der neue Benutzername
     * @param  name             Der neue Name
     * @param  email            Die neue E-Mail-Adresse
     * @param  now              Das aktuelle Datum und die Uhrzeit
     * @param  currentUserName  Der aktuelle Benutzer, der die Aktion ausführt
     */
    void updateUser(Long userId, String username, String name, String email, LocalDateTime now, String currentUserName);

    /**
     * Aktualisiert die Rolle eines Benutzers, der durch den Benutzernamen identifiziert wird.
     *
     * @param  username   der Benutzername des Benutzers
     * @param  s          Beschreibung des Parameters s
     * @param  now        das aktuelle Datum und die Uhrzeit
     */
    void updateRole(String username, String s, LocalDateTime now);

    /**
     * Aktualisiert die Rolle eines Benutzers mit der angegebenen ID.
     *
     * @param  id   die ID des Benutzers
     * @param  rId  die neue Rollen-ID
     */
    void updateUserRole(Long id, Long rId);

    /**
     * Sucht nach Benutzern basierend auf der angegebenen Suchanfrage.
     *
     * @param  searchQuery   die Suchanfrage, um Benutzer zu filtern
     * @param  j             zusätzliche Parameterbeschreibung
     * @return               eine Liste von Benutzer-Objekten, die den Suchkriterien entsprechen
     */
    List<Benutzer> searchUsers(String searchQuery, String j);
}
