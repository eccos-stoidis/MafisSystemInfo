package com.ep.sysinfo.MafisSyStemInfo.restController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ep.sysinfo.MafisSyStemInfo.model.Computer;
import com.ep.sysinfo.MafisSyStemInfo.repository.ComputerRepository;
import common.Error;
import common.Response;

/**
 * RestController for handling computer data sent from Mafis.
 */
@RestController
@RequestMapping("/rest")
public class ComputerRestController {

    @Autowired
    private ComputerRepository computerRepository;

    private static final Logger logger = LoggerFactory.getLogger(ComputerRestController.class);

    /**
     * Saves or updates computer data. If the computer already exists, the old data is deleted before saving the new data.
     *
     * @param computer The computer information to be saved or updated.
     * @return A response indicating the result of the operation.
     */
    @PostMapping(value = "/saveComputer",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addComputerInformation(@RequestBody Computer computer) {
        try {
            // Check if the computer already exists
            Optional<Computer> existingComputer = Optional.ofNullable(computerRepository.findByAnlagenNr(computer.getAnlagenNr()));

            // If exists, delete old data and update
            existingComputer.ifPresent(pc -> {
                logger.info("Existing computer found. Deleting old data for AnlageNr: {}", computer.getAnlagenNr());
                computerRepository.deleteByAnlagenNr(computer.getAnlagenNr());
            });

            // Update last modified and prepare for saving
            computer.setLastModified(LocalDateTime.now());
            prepareSave(computer);
            computerRepository.save(computer);

            String message = "Computer information saved successfully.";
            logger.info(message + " AnlageNr: {}", computer.getAnlagenNr());
            return ResponseEntity.ok(new Response(LocalDateTime.now(), Error.OK, message));

        } catch (Exception e) {
            logger.error("Error saving computer information", e);
            return ResponseEntity.badRequest().body(new Response(LocalDateTime.now(), Error.BAD_REQUEST, "Failed to save computer information."));
        }
    }

    /**
     * Returns a list of all computer information.
     *
     * @return A list of all computer data in the system.
     */
    @GetMapping(value = "/allComputerInfos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Computer>> listAllComputers() {
        try {
            List<Computer> computers = computerRepository.findAll();
            return ResponseEntity.ok(computers);
        } catch (Exception e) {
            logger.error("Error retrieving computer information", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Prepares the computer data by cleaning up unusual characters in certain fields.
     *
     * @param computer The computer object to be cleaned.
     * @return The cleaned computer object.
     */
    private Computer prepareSave(Computer computer) {
        // Handle the 'memoryListe' field: trim each element and join them into a single string for storage
        if (computer.getMemoryListe() != null && !computer.getMemoryListe().isEmpty()) {
            String memory = computer.getMemoryListe().stream()
                    .map(item -> item.replaceAll("\\s+", " ").trim())  // Aggressively trim whitespace
                    .collect(Collectors.joining("\n"));  // Join elements with newline separator
            memory = memory.replace("”á", "öß")
                    .replace("Verf�gbar", "Verfügbar");
            computer.setMemory(memory); // Store the joined and cleaned string in the 'memory' field
        }

        // Handle the 'drivesListe' field: trim each element and join them into a single string for storage
        if (computer.getDrivesListe() != null && !computer.getDrivesListe().isEmpty()) {
            String drives = computer.getDrivesListe().stream()
                    .map(item -> item.replaceAll("\\s+", " ").trim())  // Trim each element
                    .collect(Collectors.joining("\n"));  // Join elements with newline separator
            drives = drives.replace("„", "ä");
            computer.setDrives(drives); // Store the joined and cleaned string in the 'drives' field
        }

        // Clean up the processor information from 'anzahlProcessorsListe'
        if (computer.getAnzahlProcessorsListe() != null && !computer.getAnzahlProcessorsListe().isEmpty()) {
            String processorInfo = computer.getAnzahlProcessorsListe().get(1).replaceAll("\\s+", " ").trim();
            if (processorInfo.contains(",")) {
                String[] parts = processorInfo.split(",");
                if (parts.length >= 2) {
                    String cores = parts[0].replace("NumberOfCores", "").trim();
                    String logicalProcessors = parts[1].replace("NumberOfLogicalProcessors", "").trim();
                    String formattedProcessorInfo = String.format("Anzahl der Kerne: %s   Anzahl der Logische Prozessoren: %s", cores, logicalProcessors);
                    computer.setAnzahlProcessors(formattedProcessorInfo); // Store the formatted info in the 'anzahlProcessors' field
                }
            } else {
                // If there's no comma, store the unformatted data and remove any extra whitespace
                processorInfo = processorInfo.replaceAll("\\s+", " ").trim();  // Remove extra whitespace
                computer.setAnzahlProcessors(processorInfo);  // Store the cleaned-up processor info
            }
        }

        // Handle the 'prozessorenListe' field: trim each element and join them into a single string for storage
        if (computer.getProzessorenListe() != null && !computer.getProzessorenListe().isEmpty()) {
            String prozessoren = computer.getProzessorenListe().stream()
                    .map(item -> item.replaceAll("\\s+", " ").trim())  // Trim each element
                    .collect(Collectors.joining("\n"));  // Join elements with newline separator
            computer.setProzessoren(prozessoren); // Store the joined string in the 'prozessoren' field
        }

        // Handle the 'videoCardsListe' field: trim each element and join them into a single string for storage
        if (computer.getVideoCardsListe() != null && !computer.getVideoCardsListe().isEmpty()) {
            String videoCards = computer.getVideoCardsListe().stream()
                    .map(item -> item.replaceAll("\\s+", " ").trim())  // Trim each element
                    .collect(Collectors.joining("\n"));  // Join elements with newline separator
            computer.setVideoCards(videoCards); // Store the joined string in the 'videoCards' field
        }

        // Handle the 'physicalDiskInformationListe' field: trim each element and join them into a single string for storage
        if (computer.getPhysicalDiskInformationListe() != null && !computer.getPhysicalDiskInformationListe().isEmpty()) {
            String physicalDiskInformation = computer.getPhysicalDiskInformationListe().stream()
                    .map(item -> item.replaceAll("\\s+", " ").trim())  // Trim each element
                    .collect(Collectors.joining("\n"));  // Join elements with newline separator
            computer.setPhysicalDiskInformation(physicalDiskInformation); // Store the joined string in the 'physicalDiskInformation' field
        }

        // Handle the 'cdRomInformationListe' field: trim each element and join them into a single string for storage
        if (computer.getCdRomInformationListe() != null && !computer.getCdRomInformationListe().isEmpty()) {
            String cdRomInformation = computer.getCdRomInformationListe().stream()
                    .map(item -> item.replaceAll("\\s+", " ").trim())  // Trim each element
                    .collect(Collectors.joining("\n"));  // Join elements with newline separator
            computer.setCdRomInformation(cdRomInformation); // Store the joined string in the 'cdRomInformation' field
        }

        // Handle the 'printerListe' field: trim each element and join them into a single string for storage
        if (computer.getPrinterListe() != null && !computer.getPrinterListe().isEmpty()) {
            String printer = computer.getPrinterListe().stream()
                    .map(item -> item.replaceAll("\\s+", " ").trim())  // Trim each element
                    .collect(Collectors.joining("\n"));  // Join elements with newline separator
            computer.setPrinter(printer); // Store the joined string in the 'printer' field
        }

        return computer; // Return the processed computer object
    }



}

