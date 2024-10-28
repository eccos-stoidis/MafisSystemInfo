package com.ep.sysinfo.MafisSyStemInfo.controller;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import com.ep.sysinfo.MafisSyStemInfo.model.Computer;
import com.ep.sysinfo.MafisSyStemInfo.repository.AnlageRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.ComputerRepository;
import com.ep.sysinfo.MafisSyStemInfo.service.ComputerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller to manage all computer-related information
 */
@Controller
public class ComputerController extends TabellenController {

    @Autowired
    private ComputerService computerService;

    @Autowired
    private ComputerRepository computerRepository;

    @Autowired
    private AnlageRepository anlageRepository;

    private Sort.Direction sortDirection = Sort.Direction.DESC;

    private static final long MEGABYTE = 1024L * 1024L;
    private static final Logger logger = LoggerFactory.getLogger(ComputerController.class);

    /**
     * Displays all computer data for the plants with optional search and pagination.
     *
     * @param model
     * @param seite
     * @param suchBegriff
     * @param sortierenNach
     * @param sortierReihenfolge
     * @param anzahlProSeite
     * @return
     */
    @GetMapping("/alleComputer")
    public String alleComputer(Model model,
                               @RequestParam(required = false) Optional<Integer> seite,
                               @RequestParam(required = false) Optional<String> suchBegriff,
                               @RequestParam(required = false) Optional<String> sortierenNach,
                               @RequestParam(required = false) Optional<String> sortierReihenfolge,
                               @RequestParam(required = false) Optional<String> anzahlProSeite) {
        try {
            logger.info("Search term: {}", suchBegriff.orElse("No search term provided"));
            Pageable paging = getPageable(seite.orElse(1),
                    sortierReihenfolge.orElse("DESC"),
                    sortierenNach.orElse("anlagenNr"),
                    anzahlProSeite.orElse("30"));

            Page<Computer> computerPage = suchBegriff
                    .filter(s -> !s.isEmpty())
                    .map(s -> computerService.sucheComputerMitAktivenAnlagen(s, paging))
                    .orElseGet(() -> computerService.findeComputerMitAktivenAnlagen(paging));
            logger.info("Found {} computers matching the search term '{}'", computerPage.getTotalElements(), suchBegriff);

            List<Computer> computers = computerPage.getContent().stream()
                    .peek(c -> {
                        Anlage anlage = anlageRepository.existsAnlage(c.getAnlagenNr());
                        if (anlage != null) {
                            c.setAnlagenName(anlage.getAnlagenName());
                        }
                    })
                    .collect(Collectors.toList());

            model = aktualisiereModel(model, seite.orElse(1),
                    sortierReihenfolge.orElse("DESC"),
                    anzahlProSeite.orElse("30"),
                    sortierenNach.orElse(""),
                    suchBegriff.orElse(""),
                    computerPage.getTotalPages(),
                    computerPage.getTotalElements());
            model.addAttribute("computers", computers);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Computerdaten: {}", e.getMessage(), e);
            model.addAttribute("ERROR", e.getMessage());
        }
        return "alleComputer";
    }

    /**
     * Fetches computer data for a specific plant by AnlagenNr.
     *
     * @param model
     * @param anlagenNr
     * @return
     */
    @GetMapping("/getComputer/{anlagenNr}")
    public String getComputer(Model model, @PathVariable Long anlagenNr) {
        try {
            Optional<Computer> computerOptional = Optional.ofNullable(computerRepository.findByAnlagenNr(anlagenNr));

            if (computerOptional.isPresent()) {
                Computer computer = computerOptional.get();
                Anlage anlage = anlageRepository.existsAnlage(anlagenNr);
                if (anlage != null) {
                    computer.setAnlagenName(anlage.getAnlagenName());
                }

                // Format memory, processors, drives, video cards, etc.
                formatComputerDetails(computer);

                model.addAttribute("computer", computer);
                return "computerInfo";
            } else {
                model.addAttribute("message", "Keine Information vorhanden!");
            }
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Computerdaten: {}", e.getMessage(), e);
            model.addAttribute("message", "Es ist ein Fehler aufgetreten, bitte versuchen Sie es später erneut.");
        }
        return "response";
    }

    /**
     * Formats computer-related information.
     *
     * @param computer
     */
    private void formatComputerDetails(Computer computer) {
        computer.setMemory(joinList(splitAndTrim(computer.getMemory())));  // Join the list back to a String
        computer.setAnzahlProcessors(joinList(splitAndTrim(computer.getProzessoren())));
        computer.setDrivesListe(formatDrives(computer.getDrives()));
        computer.setVideoCards(joinList(splitAndTrim(computer.getVideoCards())));
        computer.setPhysicalDiskInformation(joinList(splitAndTrim(computer.getPhysicalDiskInformation())));
        computer.setPrinter(joinList(splitAndTrim(computer.getPrinter())));
        computer.setCdRomInformation(joinList(splitAndTrim(computer.getCdRomInformation())));
    }

    // Helper method to join List<String> back into a comma-separated String
    private String joinList(List<String> list) {
        return String.join(", ", list);  // Joining with a comma and a space
    }

    private List<String> splitAndTrim(String data) {
        if (data == null || data.trim().isEmpty()) {
            // Return an empty list if data is null or empty
            return new ArrayList<>();
        }
        return Arrays.asList(data.split("\\s*,\\s*"));
    }



    /**
     * Formats drive information with GB units.
     *
     * @param drives
     * @return
     */
    private List<String> formatDrives(String drives) {
        List<String> formattedDrives = new ArrayList<>();
        DecimalFormat formatter = new DecimalFormat("###,##0.0");
        List<String> driveList = splitAndTrim(drives);

        for (String drive : driveList) {
            String[] driveData = drive.split("\\s");
            StringBuilder formattedDrive = new StringBuilder();
            boolean isSizeFound = false;

            for (String data : driveData) {
                try {
                    long sizeInBytes = Long.parseLong(data.trim());
                    double sizeInGB = (sizeInBytes / MEGABYTE) / 1000.0;
                    formattedDrive.append(isSizeFound ? " Gesamtspeicher " : formatter.format(sizeInGB) + " GB Frei / ");
                    isSizeFound = true;
                } catch (NumberFormatException e) {
                    formattedDrive.append(data).append(" ");
                }
            }
            formattedDrives.add(formattedDrive.toString());
        }
        return formattedDrives;
    }

    /**
     * Search for computers by a query.
     *
     * @param suchPattern
     * @param model
     * @return
     */
    @GetMapping("/sucheComputer")
    public String search(@RequestParam(value = "search", required = false) String suchPattern, Model model) {
        try {
            // Search for computers based on the search term
            List<Computer> computers = computerService.sucheComputer(suchPattern);

            // Add the list of computers to the model
            model.addAttribute("computers", computers);
        } catch (Exception e) {
            // Log the error and display a message to the user
            model.addAttribute("ERROR", "Fehler bei der Suche nach Computern.");
        }

        return "alleComputer";  // Return the view name
    }

    private void setAnlagenNames(List<Computer> computers) {
        computers.forEach(pc -> {
            Anlage anlage = anlageRepository.existsAnlage(pc.getAnlagenNr());
            if (anlage != null) {
                pc.setAnlagenName(anlage.getAnlagenName());
            }
        });
    }

    private List<Computer> getAllSortedComputers(Sort sort) {
        List<Computer> sortedComputers = computerRepository.findAll(sort);
        return sortedComputers.stream()
                .filter(pc -> {
                    Anlage anlage = anlageRepository.existsAnlage(pc.getAnlagenNr());
                    if (anlage != null) {
                        pc.setAnlagenName(anlage.getAnlagenName());
                        return anlage.isStatus();
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * Sort computers by a given field.
     *
     * @param model
     * @param sortBy
     * @return
     */
    @GetMapping("/sortComputer/{sortBy}")
    public String sortComputer(Model model, @PathVariable String sortBy) {
        try {
            sortDirection = sortDirection == Sort.Direction.DESC ? Sort.Direction.ASC : Sort.Direction.DESC;
            List<Computer> computers = getAllSortedComputers(Sort.by(sortDirection, sortBy));
            model.addAttribute("computers", computers);
        } catch (Exception e) {
            logger.error("Fehler beim Sortieren der Computer: {}", e.getMessage(), e);
        }
        return "alleComputer";
    }
}
