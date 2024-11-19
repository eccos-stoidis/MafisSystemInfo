package com.ep.sysinfo.MafisSyStemInfo.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import com.ep.sysinfo.MafisSyStemInfo.model.Betreiber;
import com.ep.sysinfo.MafisSyStemInfo.repository.AnlageRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.BetreiberRepository;
import com.ep.sysinfo.MafisSyStemInfo.service.AnlageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * Alles was mit der Anlagen zu tun hat
 *
 */
@Controller
public class AnlagenController extends TabellenController {

    @Autowired
    private AnlageRepository anlageRepository;

    @Autowired
    private BetreiberRepository betreiberRepository;

    @Autowired
    private AnlageService anlageService;

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

            // Apply filtering if a search.js term is provided
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

    @GetMapping("/getAnlageByBetrieb")
    public String getAnlageByBetrieb(@RequestParam("betriebName") String betriebName, Model model) {
        // Retrieve the list of anlagen by betrieb name from the service
        List<Anlage> anlagenList = anlageService.findAnlagenByBetrieb(betriebName);
       // anlagenList.sort(Comparator.comparing(Anlage::getAnlagenName, Comparator.nullsLast(String::compareTo)));
        Integer anlagenCount =  anlagenList.size();

        model.addAttribute("anlagenList", anlagenList);
        model.addAttribute("anlagenCount", anlagenCount);
        model.addAttribute("headerText", "Suche bei Betriebe - Betrieb Name: " + betriebName);
        return "anlageList";
    }

    @GetMapping("/getAnlageByProfitCenter")
    public String getAnlageByProfitCenter(@RequestParam("zugProfitcenter") String zugProfitcenter, Model model) {
        List<Anlage> anlagenList = anlageService.findAnlagenByProfitCenter(zugProfitcenter);
        Integer anlagenCount =  anlagenList.size();

        model.addAttribute("anlagenList", anlagenList);
        model.addAttribute("zugProfitcenter", zugProfitcenter);
        model.addAttribute("anlagenCount", anlagenCount);
        model.addAttribute("headerText", "Suche bei Profitcenter - zugProfitcenter: " + zugProfitcenter);
        return "anlageList";
    }

    @GetMapping("/getAnlageByModulTyp")
    public String getAnlageByModulTyp(@RequestParam("modulTyp") String modulTyp, Model model) {
        List<Anlage> anlagenList = anlageService.findAnlagenByModulTyp(modulTyp);
        Integer anlagenCount =  anlagenList.size();

        model.addAttribute("anlagenList", anlagenList);
        model.addAttribute("anlagenCount", anlagenCount);
        model.addAttribute("headerText", "Suche bei Module - Modul Typ: " + modulTyp);
        return "anlageList";
    }

    @GetMapping("/getAnlageByKasse")
    public String getAnlageByKasse(@RequestParam("typ") List<String> kasseTypes, Model model) {
        List<Anlage> anlagenList = anlageService.findAnlagenByKasseTypes(kasseTypes);
        Integer anlagenCount =  anlagenList.size();
        model.addAttribute("anlagenList", anlagenList);
        model.addAttribute("anlagenCount", anlagenCount);
        model.addAttribute("headerText", "Suche bei Kassen - Kasse Typ: " + kasseTypes);
        return "anlageList";
    }

    @GetMapping("/getAnlageBySchnittstelle")
    public String getAnlageBySchnittstelle(
            @RequestParam(value = "typ", required = false) String typ,
            @RequestParam(value = "untertyp", required = false) String untertyp,
            Model model) {

        List<Anlage> anlagenList = anlageService.findAnlageBySchnittstellen(typ, untertyp);
        Integer anlagenCount =  anlagenList.size();
        // Construct the dynamic header text
        StringBuilder headerTextBuilder = new StringBuilder("Suche bei Schnittstelle");
        if (typ != null && !typ.isEmpty()) {
            headerTextBuilder.append(" - Typ: ").append(typ);
        }
        if (untertyp != null && !untertyp.isEmpty()) {
            headerTextBuilder.append(" - Untertyp: ").append(untertyp);
        }

        model.addAttribute("anlagenList", anlagenList);
        model.addAttribute("anlagenCount", anlagenCount);
        model.addAttribute("headerText", headerTextBuilder.toString());
        return "anlageList";
    }

    @GetMapping("/getAnlageByAutomaten")
    public String getAnlageByAutomaten(
            @RequestParam(value = "engineVersion", required = false) String engineVersion,
            @RequestParam(value = "fccVersion", required = false) String fccVersion,
            @RequestParam(value = "typ", required = false) String typ,
            @RequestParam(value = "unterTyp", required = false) String unterTyp,
            Model model) {

        List<Anlage> anlagenList = anlageService.findAnlageByAutomaten(engineVersion, fccVersion, typ, unterTyp);
        Integer anlagenCount =  anlagenList.size();

        // Dynamically construct the header text
        StringBuilder headerTextBuilder = new StringBuilder("Suche bei Automaten");
        if (engineVersion != null && !engineVersion.isEmpty()) {
            headerTextBuilder.append(" - Engine Version: ").append(engineVersion);
        }
        if (fccVersion != null && !fccVersion.isEmpty()) {
            headerTextBuilder.append(" - FCC Version: ").append(fccVersion);
        }
        if (typ != null && !typ.isEmpty()) {
            headerTextBuilder.append(" - Typ: ").append(typ);
        }
        if (unterTyp != null && !unterTyp.isEmpty()) {
            headerTextBuilder.append(" - Untertyp: ").append(unterTyp);
        }

        model.addAttribute("anlagenList", anlagenList);
        model.addAttribute("anlagenCount", anlagenCount);
        model.addAttribute("headerText", headerTextBuilder.toString());
        return "anlageList";
    }

    @GetMapping("/getAnlageByZutritts")
    public String getAnlageByZutritts(
            @RequestParam(value = "vonSektor", required = false) String vonSektor,
            @RequestParam(value = "nachSektor", required = false) String nachSektor,
            Model model) {

        List<Anlage> anlagenList = anlageService.findAnlageByZutritts(vonSektor, nachSektor);
        Integer anlagenCount =  anlagenList.size();

        // Dynamically construct the header text
        StringBuilder headerTextBuilder = new StringBuilder("Suche bei Zutritts");
        if (vonSektor != null && !vonSektor.isEmpty()) {
            headerTextBuilder.append(" - Von Sektor: ").append(vonSektor);
        }
        if (nachSektor != null && !nachSektor.isEmpty()) {
            headerTextBuilder.append(" - Nach Sektor: ").append(nachSektor);
        }
        model.addAttribute("anlagenList", anlagenList);
        model.addAttribute("anlagenCount", anlagenCount);
        model.addAttribute("headerText", headerTextBuilder.toString());
        return "anlageList";
    }

    @GetMapping("/getAnlageByMedienarten")
    public String getAnlageByMedienarten(
            @RequestParam(value = "typ", required = false) List<Integer> typ,
            Model model) {

        List<Anlage> anlagenList = anlageService.findAnlageByMedienarten(typ);
        Integer anlagenCount = anlagenList.size();

        //TODO: fix Suche bei Medienarten - Typ:
        model.addAttribute("anlagenList", anlagenList);
        model.addAttribute("anlagenCount", anlagenCount);
        model.addAttribute("headerText", "Suche bei Medienarten - Typ: " + typ);
        return "anlageList";
    }


    @GetMapping("/getAnlageByFiskaldatenTyp")
    public String getAnlageByFiskaldatenTyp(
            @RequestParam("typ") String typSelection,
            Model model) {

        List<Anlage> anlagenList = anlageService.findAnlageByFiskaldatenTyp(typSelection);
        Integer anlagenCount =  anlagenList.size();
        model.addAttribute("anlagenList", anlagenList);
        model.addAttribute("anlagenCount", anlagenCount);
        model.addAttribute("headerText", "Suche bei Fiskaldaten - Typ: " + typSelection);
        return "anlageList";
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
                // No search.js term provided, fetch all and apply sorting
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

    /**
     * Create or update the Betreiber entity
     *
     * @param  anlagenNr         the number of the Anlage
     * @param  betreiberName      the name of the Betreiber
     * @param  redirectAttributes the RedirectAttributes object
     * @return                   the string for redirection
     */
    @PostMapping("/BetreiberByAnlage")
    public String getBetreiberByAnlage(
            @RequestParam("anlagenNr") Long anlagenNr,
            @RequestParam("betreiberName") String betreiberName,
            RedirectAttributes redirectAttributes) {

        // Check if Betreiber with the same anlagenNr already exists
        Optional<Betreiber> existingBetreiber = betreiberRepository.findByAnlagenNr(anlagenNr);

        if (existingBetreiber.isPresent()) {
            // Update the existing record if necessary
            Betreiber betreiber = existingBetreiber.get();
            betreiber.setBetreiberName(betreiberName);
            betreiberRepository.save(betreiber);

            // Add success message for update
            redirectAttributes.addFlashAttribute("successMessage",
                    "Betreiber aktualisiert: " + betreiberName + " für Anlage:  " + anlagenNr);
        } else {
            // Create and save a new Betreiber if not found
            Betreiber newBetreiber = new Betreiber();
            newBetreiber.setAnlagenNr(anlagenNr);
            newBetreiber.setBetreiberName(betreiberName);
            betreiberRepository.save(newBetreiber);

            // Add success message for creation
            redirectAttributes.addFlashAttribute("successMessage",
                    "Betreiber erfolgreich gespeichert: " + betreiberName + " für Anlage:  " + anlagenNr);
        }

        return "redirect:/alleAnlagen";
    }


}
