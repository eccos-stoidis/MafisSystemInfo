package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import com.ep.sysinfo.MafisSyStemInfo.model.Computer;
import com.ep.sysinfo.MafisSyStemInfo.repository.AnlageRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.ComputerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ComputerService {

    private final ComputerRepository computerRepository;
    private final AnlageRepository anlageRepository;

    public Page<Computer> findAllComputers(Pageable pageable) {
        return computerRepository.findAll(pageable);
    }

    public List<Computer>findAllComputers() {
    	return computerRepository.findAll();
    }



    /**
     * Fetches computers with active systems and filters them based on the search.js term.
     *
     * @param suchbegriff The search.js keyword.
     * @param pageable    Pagination details.
     * @return Page of filtered computers.
     */
    public Page<Computer> sucheComputerMitAktivenAnlagen(String suchbegriff, Pageable pageable) {

        return computerRepository.searchBySuchBegriff(suchbegriff, pageable);
    }

    /**
     * Filters the computer based on the provided search.js term.
     *
     * @param computer    The computer entity to filter.
     * @param suchbegriff The search.js keyword.
     * @return True if the computer matches the search.js term.
     */
    private boolean filterComputer(Computer computer, String suchbegriff) {
        String searchTerm = suchbegriff.toLowerCase();
        return (computer.getAnlagenNr().toString().contains(searchTerm) ||
                computer.getComputerName().toLowerCase().contains(searchTerm) ||
                computer.getBetriebsSystem().toLowerCase().contains(searchTerm) ||
                computer.getBetriebssystemVersion().toLowerCase().contains(searchTerm) ||
                computer.getServicePack().toLowerCase().contains(searchTerm) ||
                computer.getLastModified().toString().contains(searchTerm));
    }

    /**
     * Fetches and filters computers based on active Anlagen entities.
     *
     * @param  pageable   Pagination details for fetching computers.
     * @return           Page of computers filtered by active Anlagen.
     */
    public Page<Computer> findeComputerMitAktivenAnlagen(Pageable pageable) {
        // Fetch all computers using the repository and pagination
        Page<Computer> computerPage = computerRepository.findAll(pageable);

        // Collect anlagenNr values for all computers in the current page
        List<Long> anlagenNrList = computerPage.getContent().stream()
                .map(Computer::getAnlagenNr)
                .collect(Collectors.toList());

        // Fetch all active Anlage entities in a single query
        List<Anlage> activeAnlagenList = anlageRepository.findActiveAnlagenByAnlagenNrIn(anlagenNrList);

        // Create a set of active anlagenNr for quick lookup
        Set<Long> activeAnlagenNrSet = activeAnlagenList.stream()
                .map(Anlage::getAnlagenNr)
                .collect(Collectors.toSet());

        // Filter computers based on active Anlagen
        List<Computer> filteredComputers = computerPage.getContent().stream()
                .filter(c -> activeAnlagenNrSet.contains(c.getAnlagenNr()))
                .collect(Collectors.toList());

        // Return a new page with the filtered results
        return new PageImpl<>(filteredComputers, pageable, filteredComputers.size());
    }

    /**
     * Checks if the computer has an active Anlage.
     *
     * @param computer The computer entity.
     * @return True if the computer has an active Anlage.
     */
    private boolean hasActiveAnlage(Computer computer) {
        Anlage anlage = anlageRepository.existsAnlage(computer.getAnlagenNr());
        return anlage != null && anlage.isStatus();  // Check if Anlage exists and is active
    }

    public List<Computer> sucheComputer(String suchPattern) {
        // Fetch all computers
        List<Computer> allComputers = computerRepository.findAll();

        // If no search.js term is provided, return all computers
        if (suchPattern == null || suchPattern.trim().isEmpty()) {
            return allComputers;
        }

        // Parse the search.js pattern to check if it's a number (anlagenNr)
        Long searchId = null;
        try {
            searchId = Long.parseLong(suchPattern.trim());
        } catch (NumberFormatException e) {
            // searchId remains null
        }

        final Long finalSearchId = searchId;

// Filter computers based on the search.js pattern
        String lowerCaseSearchPattern = suchPattern.toLowerCase();
        return allComputers.stream()
                .filter(computer -> (computer.getAnlagenNr().equals(finalSearchId)) ||
                        computer.getComputerName().toLowerCase().contains(lowerCaseSearchPattern) ||
                        computer.getHostname().toLowerCase().contains(lowerCaseSearchPattern))
                .sorted((c1, c2) -> c1.getAnlagenNr().compareTo(c2.getAnlagenNr()))  // Sort by anlagenNr
                .collect(Collectors.toList());

    }
}
