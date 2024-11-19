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
        // Process Memory
        String memory = computer.getMemory()
                .replace("”á", "öß")
                .replace("Verf�gbar", "Verfügbar");
        computer.setMemory(memory);

        // Process Drives
        String drives = computer.getDrives()
                .replace("„", "ä");
        computer.setDrives(drives);

        // Process AnzahlProcessors
        String processors = computer.getAnzahlProcessors().trim();

// Clean up the input format by normalizing spaces
        processors = processors.replaceAll("\\s+", " "); // Replace multiple spaces with a single space

// Remove labels and extract values
        processors = processors.replace("NumberOfCores", "")
                .replace("NumberOfLogicalProcessors,", "")
                .trim();

// Split into parts
        String[] parts = processors.split(" ");

// Validate and process parts
        if (parts.length == 2) {
            String kerne = parts[0].trim();
            String logischeProzessoren = parts[1].trim();
            String anzahlProcessors = String.format("Anzahl der Kerne: %s   Anzahl der Logische Prozessoren: %s", kerne, logischeProzessoren);
            computer.setAnzahlProcessors(anzahlProcessors);
        } else {
            // Log a warning if the input format is unexpected
            logger.warn("Unexpected format for AnzahlProcessors: {}", processors);
            computer.setAnzahlProcessors("Unrecognized format");
        }

        return computer;
    }



}

