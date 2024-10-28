package com.ep.sysinfo.MafisSyStemInfo.controller;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import com.ep.sysinfo.MafisSyStemInfo.repository.AnlageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * Alles was mit der Anlagen zu tun hat
 *
 */
@Controller
public class AnlagenController extends TabellenController {

    @Autowired
    private AnlageRepository anlageRepository;

    private static final Logger logger = LoggerFactory.getLogger(AnlagenController.class);

    private Boolean aktuelleStatus;
    private Sort.Direction sortierReihenfolge = Sort.Direction.ASC;

    /**
     * Liste aller Anlagen mit optionalem Suchbegriff und Sortierung.
     *
     * @param model
     * @param seite
     * @param suchBegriff
     * @param sortierenNach
     * @param sortierReihenfolge
     * @param anzahlProSeite
     * @return
     */
    @GetMapping("/alleAnlagen")
    public String anlagenListe(Model model,
                               @RequestParam(required = false) Optional<Integer> seite,
                               @RequestParam(required = false) Optional<String> suchBegriff,
                               @RequestParam(required = false) Optional<String> sortierenNach,
                               @RequestParam(required = false) Optional<String> sortierReihenfolge,
                               @RequestParam(required = false) Optional<String> anzahlProSeite) {
        try {
            // Setup pagination and sorting
            Pageable paging = getPageable(seite.orElse(1), sortierReihenfolge.orElse("DESC"), sortierenNach.orElse("anlagenName"), anzahlProSeite.orElse("30"));

            // Apply filtering if a search term is provided
            Page<Anlage> anlagePage = suchBegriff
                    .filter(s -> !s.isEmpty())
                    .map(s -> anlageRepository.filterAnlagen(s, null, paging))
                    .orElseGet(() -> anlageRepository.findAll(paging));

            // Retrieve content and populate model
            List<Anlage> anlagen = anlagePage.getContent();
            model = aktualisiereModel(model, seite.orElse(1), sortierReihenfolge.orElse("DESC"), anzahlProSeite.orElse("30"), sortierenNach.orElse(""), suchBegriff.orElse(""), anlagePage.getTotalPages(), anlagePage.getTotalElements());
            model.addAttribute("anlagen", anlagen);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Anlagen: {}", e.getMessage(), e);
        }
        return "alleAnlagen";
    }

    /**
     * Holen einer Anlage
     *
     * @param model
     * @param anlagenNr
     * @return
     */
    @GetMapping("/getAnlage/{anlagenNr}")
    public String getAnlage(Model model, @PathVariable Long anlagenNr) {
        try {
            List<Anlage> anlagen = new ArrayList<>();
            anlageRepository.findById(anlagenNr)
                    .ifPresent(anlagen::add);

            model.addAttribute("anlagen", anlagen);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Anlage: {}", e.getMessage(), e);
        }
        return "alleAnlagen";
    }

    /**
     * Sucht nach Anlagen mit einem optionalen Suchbegriff und Sortierung.
     *
     * @param q
     * @param model
     * @return
     */
    @GetMapping("/sucheAnlage")
    public String search(@RequestParam(value = "suchBegriff", required = false) String q,
                         Model model) {
        List<Anlage> anlagen = new ArrayList<>();
        String message = null;

        try {
            if (q != null && !q.trim().isEmpty()) {
                // Search by query and sort
                Pageable pageable = PageRequest.of(0, 30, Sort.by(Sort.Direction.ASC, "anlagenName"));
                Page<Anlage> anlagePage = anlageRepository.filterAnlagen(q.trim(), null, pageable);
                    anlagen = anlagePage.getContent();
            } else {
                // No search term provided, fetch all and apply sorting
                anlagen = fetchAllAnlagenWithSorting();
            }
        } catch (Exception e) {
            logger.error("Fehler bei der Suche nach Anlagen: {}", e.getMessage(), e);
            message = "Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.";
        }

        model.addAttribute("anlagen", anlagen);
        model.addAttribute("messageError", message);

        return "alleAnlagen";
    }

    private List<Anlage> fetchAllAnlagenWithSorting() {
        Sort sort = Sort.by(Sort.Direction.ASC, "anlagenName");
        return anlageRepository.findByStatus(Optional.ofNullable(aktuelleStatus).orElse(true), sort);
    }

    /**
     * Aktivieren oder Deaktivieren einer Anlage
     *
     * @param model
     * @param anlagenNr
     * @param status
     * @return
     */
    @RequestMapping(value = "/changeStatus/{anlagenNr}/{status}", method = RequestMethod.GET)
    public String changeStatus(Model model, @PathVariable Long anlagenNr, @PathVariable Boolean status) {
        try {
            anlageRepository.updateStatusAnlage(anlagenNr, status);

            List<Anlage> anlagen = anlageRepository.findAll(Sort.by(Sort.Direction.ASC, "anlagenName"));
            model.addAttribute("anlagen", anlagen);
        } catch (Exception e) {
            logger.error("Fehler beim Aktualisieren des Status: {}", e.getMessage(), e);
            model.addAttribute("message", "Datenbankfehler. Der Status konnte nicht aktualisiert werden!");
        }
        return "alleAnlagen";
    }

    /**
     * Liste aller aktiven oder inaktiven Anlagen
     *
     * @param model
     * @param status
     * @return
     */
    @GetMapping("/anlagenDeaktiv/{status}")
    public String deaktiveAnlagen(Model model, @PathVariable Boolean status) {
        try {
            aktuelleStatus = status;
            List<Anlage> anlagen = anlageRepository.listeAnlage(status);
            model.addAttribute("anlagen", anlagen);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der inaktiven Anlagen: {}", e.getMessage(), e);
            model.addAttribute("message", "Datenbankfehler. Es ist ein Fehler passiert!");
        }
        return "alleAnlagen";
    }

    /**
     * Sortiere die Anlagen nach dem angegebenen Kriterium
     *
     * @param model
     * @param sortBy
     * @return
     */
    @GetMapping("/sort/{sortBy}")
    public String sortAnlagen(Model model, @PathVariable String sortBy) {
        try {
            // Toggle the sorting direction
            sortierReihenfolge = (sortierReihenfolge == Sort.Direction.DESC) ? Sort.Direction.ASC : Sort.Direction.DESC;

            // Sort the list based on the sortBy field and current direction
            Sort sort = Sort.by(sortierReihenfolge, sortBy);
            List<Anlage> anlagen = anlageRepository.findByStatus(true, sort);

            // Add the sorted list to the model
            model.addAttribute("anlagen", anlagen);
        } catch (Exception e) {
            logger.error("Fehler bei der Sortierung der Anlagen: {}", e.getMessage(), e);
        }
        return "alleAnlagen";
    }
}
