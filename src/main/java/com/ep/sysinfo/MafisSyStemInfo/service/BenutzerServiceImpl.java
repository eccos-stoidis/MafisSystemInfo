package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Benutzer;
import com.ep.sysinfo.MafisSyStemInfo.model.BenutzerRolle;
import com.ep.sysinfo.MafisSyStemInfo.model.Rolle;
import com.ep.sysinfo.MafisSyStemInfo.repository.BenutzerRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.BenutzerRolleRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.RolleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BenutzerServiceImpl implements BenutzerService {

    @Autowired
    private BenutzerRepository benutzerRepository;

    @Autowired
    private BenutzerRolleRepository benutzerRolleRepository;

    @Autowired
    private RolleRepository rolleRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public void updateKennwort(Long userId, String newKennwort, String currentUserName) {
        // Find the user by their ID
        Benutzer benutzer = benutzerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer not found with ID: " + userId));
        // Encode the new password
        String encodedPassword = encoder.encode(newKennwort.trim());

        // Update the user's password and the metadata
        benutzer.setPassword(encodedPassword);
        benutzer.setLastModified(LocalDateTime.now());
        benutzer.setBearbeitetVon(currentUserName);

        // Save the updated user back to the repository
        benutzerRepository.save(benutzer);
    }

    @Override
    public boolean verifyOldPassword(String oldPassword, String storedPassword) {
        // Use BCryptPasswordEncoder to check if the old password matches the stored one
        return encoder.matches(oldPassword, storedPassword);
    }

    @Override
    public void updateUser(Long userId, String username, String name, String email, LocalDateTime now, String currentUserName) {

        // Find the user by their ID
        Benutzer benutzer = benutzerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer not found with ID: " + userId));

        // Update the user's properties
        benutzer.setUsername(username);
        benutzer.setName(name);
        benutzer.setEmail(email);
        benutzer.setLastModified(now);
        benutzer.setBearbeitetVon(currentUserName);
        // Save the updated user back to the repository
        benutzerRepository.save(benutzer);
    }

    @Override
    public void updateRole(String username, String s, LocalDateTime now) {

    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, Long roleId) {
        // Find the Benutzer (user) by id
        Benutzer benutzer = benutzerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Get the currently authenticated user for the "bearbeitetVon" field
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        // Set bearbeiter and last modified date
        benutzer.setBearbeitetVon(currentUser);
        benutzer.setLastModified(LocalDateTime.now());

        // Remove all roles from the benutzer object (in-memory)
        benutzer.getUserRollen().clear();

        // Delete all existing roles for the user from the BenutzerRolle table
        benutzerRolleRepository.deleteByBenutzer(benutzer);


        // Find the Rolle (role) by its id
        Rolle role = rolleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rolle nicht gefunden"));

        // Create a new BenutzerRolle entity and assign the new role
        BenutzerRolle benutzerRolle = new BenutzerRolle();
        benutzerRolle.setBenutzer(benutzer);
        benutzerRolle.setRolle(role);
        benutzerRolle.setLastModified(LocalDateTime.now());

        // Save the new role assignment
        benutzerRolleRepository.save(benutzerRolle);

        // Save the updated Benutzer entity (to update bearbeitetVon and lastModified)
        benutzerRepository.save(benutzer);
    }

    @Override
    /**
     * Search for users based on a search pattern and enabled status
     *
     * @param searchPattern The search term to filter by name, username, or email
     * @param enabled       The enabled status ("J" for active users)
     * @return List of matching Benutzer objects
     */
    public List<Benutzer> searchUsers(String searchPattern, String enabled) {
        // Fetch all users with the given enabled status
        List<Benutzer> users = benutzerRepository.findByEnabled(enabled);

        // If there is a search pattern, filter the users by name, username, or email
        if (searchPattern != null && !searchPattern.isBlank()) {
            String lowerCasePattern = searchPattern.trim().toLowerCase();

            // Format to match the format displayed in the table
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            return users.stream()
                    .filter(user -> user.getName().toLowerCase().contains(lowerCasePattern)
                            || user.getUsername().toLowerCase().contains(lowerCasePattern)
                            || user.getEmail().toLowerCase().contains(lowerCasePattern)
                            || user.getUserRollen().stream()
                            .anyMatch(role -> role.getRolle().getRolleName().toLowerCase().contains(lowerCasePattern))
                            // Include the formatted LocalDateTime for the "lastModified" field
                            || (user.getLastModified() != null && user.getLastModified().format(formatter).toLowerCase().contains(lowerCasePattern)))
                    .collect(Collectors.toList());

        }
        // If no search pattern, return all users
        return users;
    }
}
