package com.ep.sysinfo.MafisSyStemInfo.controller;

import com.ep.sysinfo.MafisSyStemInfo.controller.GlobalException.InvalidDriveFormatException;
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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
     * Displays all computer data for the plants with optional search.js and pagination.
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
        logger.info("Search term: {}", suchBegriff.orElse("No search.js term provided"));

        // Configure pagination and sorting
        Pageable paging = getPageable(seite.orElse(1),
                sortierReihenfolge.orElse("DESC"),
                sortierenNach.orElse("anlagenNr"),
                anzahlProSeite.orElse("30"));

        // Fetch paginated computer data
        Page<Computer> computerPage = suchBegriff
                .filter(s -> !s.isEmpty())
                .map(s -> computerService.sucheComputerMitAktivenAnlagen(s, paging))
                .orElseGet(() -> computerService.findeComputerMitAktivenAnlagen(paging));

        logger.info("Found {} computers matching the search term '{}'", computerPage.getTotalElements(), suchBegriff.orElse(""));

        // Collect anlagenNr values
        List<Long> anlagenNrList = computerPage.getContent().stream()
                .map(Computer::getAnlagenNr)
                .toList();

        // Fetch and map Anlage records
        List<Anlage> anlagenList = anlageRepository.findByAnlagenNrIn(anlagenNrList);
        Map<Long, Anlage> anlageMap = anlagenList.stream()
                .collect(Collectors.toMap(Anlage::getAnlagenNr, anlage -> anlage));

        // Associate Computers with their Anlagen
        List<Computer> computers = computerPage.getContent().stream()
                .peek(c -> {
                    Anlage anlage = anlageMap.get(c.getAnlagenNr());
                    if (anlage != null) {
                        c.setAnlagenName(anlage.getAnlagenName());
                    }
                })
                .collect(Collectors.toList());

        // Update model
        model = aktualisiereModel(model, seite.orElse(1),
                sortierReihenfolge.orElse("DESC"),
                anzahlProSeite.orElse("30"),
                sortierenNach.orElse(""),
                suchBegriff.orElse(""),
                computerPage.getTotalPages(),
                computerPage.getTotalElements());
        model.addAttribute("computers", computers);

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
        // Find the computer or throw a custom exception if not found
        Computer computer = Optional.ofNullable(computerRepository.findByAnlagenNr(anlagenNr))
                .orElseThrow(() -> new ResourceNotFoundException("Computer mit AnlagenNr " + anlagenNr + " wurde nicht gefunden."));

        // Find the associated Anlage
        Anlage anlage = anlageRepository.existsAnlage(anlagenNr);
        if (anlage != null) {
            computer.setAnlagenName(anlage.getAnlagenName());
        }
        // Format details
        formatComputerDetails(computer);

        // Add the computer to the model
        model.addAttribute("computer", computer);
        return "computerInfo";
    }

    /**
     * Formats computer-related information.
     *
     * @param computer
     */
    private void formatComputerDetails(Computer computer) {
        computer.setMemoryListe(splitAndTrim(computer.getMemory()));  // Join the list back to a String
        computer.setProzessorenListe(splitAndTrim(computer.getProzessoren()));
        computer.setDrivesListe(processDrives(computer.getDrives()));
        computer.setVideoCardsListe(splitAndTrim(computer.getVideoCards()));
        computer.setPhysicalDiskInformationListe(splitAndTrim(computer.getPhysicalDiskInformation()));
        computer.setPrinterListe(splitAndTrim(computer.getPrinter()));
        computer.setCdRomInformationListe(splitAndTrim(computer.getCdRomInformation()));
    }


    private List<String> splitAndTrim(String input) {
        return Optional.ofNullable(input)
                .map(s -> Arrays.stream(s.split("\\s*,\\s*"))
                        .map(String::trim)
                        .toList())
                .orElseGet(List::of);
    }

    private List<String> processDrives(String drives) {
        if (drives == null || drives.isBlank()) {
            return List.of();
        }

        return Arrays.stream(drives.split("\\s*,\\s*"))
                .map(this::formatDrive)
                .toList();
    }
    private String formatDrive(String drive) {
        final double MEGABYTE = 1024 * 1024;
        String[] parts = drive.split("\\s+");
        StringBuilder formattedDrive = new StringBuilder();
        boolean isFirstSize = true;
        //NumberFormatException
        for (String part : parts) {
            long size = validateAndParseDriveSize(part);
            double sizeInGb = (size / MEGABYTE) / 1000.0;
            String formattedSize = new DecimalFormat("###,##0.0").format(sizeInGb);
            if (isFirstSize) {
                formattedDrive.append(formattedSize).append(" GB Frei / ");
                isFirstSize = false;
            } else {
                formattedDrive.append("Gesamtspeicher ").append(formattedSize).append(" GB ");
            }
        }

        return formattedDrive.toString().trim();
    }

    private long validateAndParseDriveSize(String part) {
        if (part == null || part.isBlank()) {
            throw new InvalidDriveFormatException("Drive size part is empty or null.");
        }
        return Long.parseLong(part); // Safe to parse since it passed validation
    }


    /**
     * Search for computers by a query.
     *
     * @param suchPattern
     * @param model
     * @return
     */
    @GetMapping("/sucheComputer")
    public String search(@RequestParam(value = "search.js", required = false) String suchPattern, Model model) {
        // Search for computers based on the search.js term
        List<Computer> computers = computerService.sucheComputer(suchPattern);
        // Add the list of computers to the model
        model.addAttribute("computers", computers);

        return "alleComputer"; // Return the view name
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
        // Toggle the sort direction
        sortDirection = sortDirection == Sort.Direction.DESC ? Sort.Direction.ASC : Sort.Direction.DESC;
        // Fetch sorted computers
        List<Computer> computers = getAllSortedComputers(Sort.by(sortDirection, sortBy));
        model.addAttribute("computers", computers);

        return "alleComputer";
    }

}
