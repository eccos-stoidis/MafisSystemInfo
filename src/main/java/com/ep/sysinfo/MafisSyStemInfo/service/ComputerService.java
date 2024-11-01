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
     * Fetches computers with active systems and filters them based on the search term.
     *
     * @param suchbegriff The search keyword.
     * @param pageable    Pagination details.
     * @return Page of filtered computers.
     */
    public Page<Computer> sucheComputerMitAktivenAnlagen(String suchbegriff, Pageable pageable) {

        return computerRepository.searchBySuchBegriff(suchbegriff, pageable);
        // Fetch all computers using the repository and pagination
        //Page<Computer> computerPage = computerRepository.findAll(pageable);

        // Convert the page content to a list and filter it based on the search term
        //List<Computer> filteredComputers = computerPage.getContent().stream()
                //.filter(computer -> filterComputer(computer, suchbegriff))
                //.collect(Collectors.toList());

        // Return a new page with the filtered results
        //return new PageImpl<>(filteredComputers, pageable, filteredComputers.size());
    }

    /**
     * Filters the computer based on the provided search term.
     *
     * @param computer    The computer entity to filter.
     * @param suchbegriff The search keyword.
     * @return True if the computer matches the search term.
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

    public Page<Computer> findeComputerMitAktivenAnlagen(Pageable pageable) {
        // Fetch all computers using the repository and pagination
        Page<Computer> computerPage = computerRepository.findAll(pageable);

        // Filter computers that have an active Anlage
        List<Computer> filteredComputers = computerPage.getContent().stream()
                .filter(this::hasActiveAnlage)  // Custom filter method to check for active Anlage
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

        // If no search term is provided, return all computers
        if (suchPattern == null || suchPattern.trim().isEmpty()) {
            return allComputers;
        }

        // Parse the search pattern to check if it's a number (anlagenNr)
        Long searchId = null;
        try {
            searchId = Long.parseLong(suchPattern.trim());
        } catch (NumberFormatException e) {
            // searchId remains null
        }

        final Long finalSearchId = searchId;

// Filter computers based on the search pattern
        String lowerCaseSearchPattern = suchPattern.toLowerCase();
        return allComputers.stream()
                .filter(computer -> (computer.getAnlagenNr().equals(finalSearchId)) ||
                        computer.getComputerName().toLowerCase().contains(lowerCaseSearchPattern) ||
                        computer.getHostname().toLowerCase().contains(lowerCaseSearchPattern))
                .sorted((c1, c2) -> c1.getAnlagenNr().compareTo(c2.getAnlagenNr()))  // Sort by anlagenNr
                .collect(Collectors.toList());

    }
}
