package com.ep.sysinfo.MafisSyStemInfo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import java.util.Optional;

/**
 * Diese Klasse dient als Oberklasse für Ansichten, die Tabellen enthalten. Die Klasse enthielt
 * insbesondere Instanzvariablen, die das Sortieren nach Spalten, Suchen und eine Variation der
 * Anzahl sichtbarer Tabellenzeilen ermöglicht. Die Unterklassen greifen unmittelbar auf diese Instanzvariablen
 * zu und modifizieren diese auch. Damit werden unnötig lange URL mit vielen URL-Parametern verhindert.
 *
 * @author fadilkallat
 */
public abstract class TabellenController {

    private static final Logger logger = LoggerFactory.getLogger(TabellenController.class);

    /**
     * Create a Pageable object based on the current page, sorting order, and page size.
     *
     * @param seite              the current page number
     * @param sortierReihenfolge the sorting direction (ASC/DESC)
     * @param sortierenNach      the column to sort by
     * @param anzahlProSeite     the number of entries per page
     * @return Pageable object for pagination
     */
    public Pageable getPageable(int seite, String sortierReihenfolge, String sortierenNach, String anzahlProSeite) {
        // Get the sorting direction with default value DESC
        Sort.Direction sortDirection = Optional.ofNullable(sortierReihenfolge)
                .flatMap(Sort.Direction::fromOptionalString)
                .orElse(Sort.Direction.DESC);
        logger.debug("anzahlProSeite value: {}", anzahlProSeite);
        // Parse the number of items per page
        int pageSize = Optional.ofNullable(anzahlProSeite)
                .filter(s -> !s.isEmpty())  // Make sure the string is not empty
                .map(Integer::parseInt)
                .orElse(30);  // Default to 30 if anzahlProSeite is null or empty


        // Create sorting and pageable object
        if (Optional.ofNullable(sortierenNach).filter(s -> !s.isEmpty()).isPresent()) {
            Sort sort = Sort.by(sortDirection, sortierenNach);
            return PageRequest.of(seite - 1, pageSize, sort); // PageRequest starts from 0, so subtract 1
        } else {
            return PageRequest.of(seite - 1, pageSize); // No sorting applied
        }
    }

    /**
     * Update the model with pagination and sorting information.
     *
     * @param model          the model to be updated
     * @param seite          the current page number
     * @param sortierReihenfolge the sorting direction (ASC/DESC)
     * @param anzahlProSeite the number of entries per page
     * @param sortierenNach  the column to sort by
     * @param suchbegriff    the search.js keyword
     * @param seitenAnzahl   the total number of pages
     * @param elementeAnzahl the total number of elements
     * @return the updated model
     */
    public Model aktualisiereModel(Model model,
                                   int seite,
                                   String sortierReihenfolge,
                                   String anzahlProSeite,
                                   String sortierenNach,
                                   String suchbegriff,
                                   int seitenAnzahl,
                                   long elementeAnzahl) {
        // Populate the model with the sorting, paging, and search.js information
        return model.addAttribute("sortierReihenfolge", Optional.ofNullable(sortierReihenfolge).orElse("DESC"))
                .addAttribute("anzahlProSeite", Optional.ofNullable(anzahlProSeite).orElse("30"))
                .addAttribute("sortierenNach", Optional.ofNullable(sortierenNach).orElse(""))
                .addAttribute("suchBegriff", Optional.ofNullable(suchbegriff).orElse(""))
                .addAttribute("seitenNummer", seite)
                .addAttribute("seitenAnzahl", seitenAnzahl)
                .addAttribute("elementeAnzahl", elementeAnzahl);
    }
}
