package com.ep.sysinfo.MafisSyStemInfo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.ep.sysinfo.MafisSyStemInfo.model.Benutzer;
import com.ep.sysinfo.MafisSyStemInfo.model.BenutzerRolle;
import com.ep.sysinfo.MafisSyStemInfo.model.KennwortDTO;
import com.ep.sysinfo.MafisSyStemInfo.model.Rolle;
import com.ep.sysinfo.MafisSyStemInfo.repository.BenutzerRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.BenutzerRolleRepository;
import com.ep.sysinfo.MafisSyStemInfo.repository.RolleRepository;
import com.ep.sysinfo.MafisSyStemInfo.service.BenutzerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Benutzerverwaltung Controller für Benutzeroperationen
 */
@Controller
public class BenutzerController {

    @Autowired
    private BenutzerRepository benutzerRepository;

    @Autowired
    private BenutzerRolleRepository benutzerRolleRepository;

    @Autowired
    private RolleRepository rolleRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private BenutzerService benutzerService;

    private static String currentUserName;

    private static  Boolean isAdmin ;

    private static final Logger logger = LoggerFactory.getLogger(BenutzerController.class);

    /**
     * Liste aller Benutzer
     * Nur Admin kann diese Liste abrufen.
     *
     * @param model Das Model, um die Liste der Benutzer zu speichern
     * @return Seite mit Benutzerliste oder Fehlermeldung
     */
    @GetMapping("/alleUser")
    public String getUserListe(Model model) {
        if (isAdmin()) {
            try {
                List<Benutzer> users = benutzerRepository.findeAktiveUser("J");
                model.addAttribute("users", users);
                return "alleUser";
            }catch (Exception e) {
                logger.error("Fehler in getUserListe", e);
                model.addAttribute("message", "DatenbankFehler!");
            }
        } else {
            model.addAttribute("message", "Sie haben keine Admin-Rechte");
        }
        return "response";
    }

    /**
     * Bearbeiten eines Benutzers
     *
     * @param id Die ID des Benutzers, der bearbeitet werden soll
     * @param model Das Model, um den Benutzer zu speichern
     * @return Seite zur Bearbeitung des Benutzers oder Fehlermeldung
     */
    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        if (isAdmin()) {
            try {
                Benutzer benutzer;
                if (id == null || id == 0) {
                    benutzer = new Benutzer();
                } else {
                    benutzer = benutzerRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
                }
                model.addAttribute("benutzer", benutzer);
                return "editUser";
            } catch (Exception e) {
                logger.error("Fehler in editUser", e);
                model.addAttribute("message", "Es ist ein Datenbankfehler passiert!");
            }
        } else {
            model.addAttribute("message", "Sie haben keine Admin-Rechte!");
        }
        return "response";
    }

    @GetMapping("/confirmDelete/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        if (isAdmin()) {
            try {
                Benutzer benutzer = benutzerRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
                benutzer.setEnabled("N");
                benutzer.setBearbeitetVon(holeAngemeldeteUser());
                benutzer.setLastModified(LocalDateTime.now());
                logger.info("Benutzer object : {}", benutzer);
                benutzerRepository.save(benutzer);
            } catch (Exception e) {
                logger.error("Fehler in deleteUser", e);
                model.addAttribute("message", "Es ist ein Datenbankfehler passiert!");
            }
        }else {
            model.addAttribute("message", "Sie haben keine Admin-Rechte!");
        }
        return "redirect:/alleUser";
    }

    @GetMapping("/editRolle/{id}/{rId}")
    public String editUserRolle(@PathVariable Long id,
                                @PathVariable Long rId,
                                Model model) {
        if (isAdmin()) {
            try {
                benutzerService.updateUserRole(id, rId);
            } catch (Exception e) {
                logger.error("Fehler in resetKennwort" , e.getMessage());
                model.addAttribute("message", "Datenbankfehler. Rechte konnte nicht geändert werden!");
            }
        }else{
            model.addAttribute("message", "Sie haben keine Admin-Rechte!");
        }
        return "redirect:/alleUser";
    }


    /**
     * Method to add or update a user
     *
     * @param neuUser Benutzer object to add
     * @param result  BindingResult for validation
     * @param model   Model object to store attributes
     * @return View name (String) to redirect to
     */
    @PostMapping("/addUser")
    public String addUser(@Validated @ModelAttribute("benutzer") Benutzer neuUser,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        if (result.hasErrors()) {
            return "editUser";
        } else {
            try {
                String encodedPassword;
                if (isAdmin()) {
                    initRollen();
                    String currentUserName = holeAngemeldeteUser();
                    // Check if the user already exists (update or create new)
                    if (neuUser.getUserId() != null && neuUser.getUserId() > 0) {
                        // Update existing user
                        Benutzer existingUser = benutzerRepository.findById(neuUser.getUserId()).orElse(null);
                        if (existingUser == null) {
                            result.rejectValue("userId", "Benutzer", "Dieser Benutzer existiert nicht");
                            return "editUser";
                        }
                        if(benutzerRepository.existsByUsername(neuUser.getUsername()) && !Objects.equals(existingUser.getUsername(), neuUser.getUsername())) {
                            result.rejectValue("username", "Benutzername", "Dieser Benutzername ist bereits vergeben");
                            return "editUser";

                        }
                        if(benutzerRepository.existsByEmail(neuUser.getEmail())&& !Objects.equals(existingUser.getEmail(), neuUser.getEmail())) {
                            result.rejectValue("email", "E-Mail", "Diese E-Mail Adresse ist bereits vergeben");
                            return "editUser";

                        }

                        benutzerService.updateUser(neuUser.getUserId(), neuUser.getUsername(),
                                neuUser.getName(), neuUser.getEmail(), LocalDateTime.now(), currentUserName);

                        logger.info("Die Änderungen wurden erfolgreich gespeichert!");
                        redirectAttributes.addFlashAttribute("benutzerSuccess", true);

                    } else {
                        // Validate for new user creation
                        if (benutzerRepository.existsByUsername(neuUser.getUsername())) {
                            result.rejectValue("username", "Benutzername", "Dieser Benutzername ist bereits vergeben");
                            return "editUser";
                        }
                        if (benutzerRepository.existsByEmail(neuUser.getEmail())) {
                            result.rejectValue("email", "E-Mail", "Diese E-Mail Adresse ist bereits vergeben");
                            return "editUser";
                        }

                        // Create new user
                        encodedPassword = encoder.encode("mafis"); // Default password
                        neuUser.setPassword(encodedPassword);
                        neuUser.setEnabled("J");
                        LocalDateTime lastModified = LocalDateTime.now();
                        neuUser.setLastModified(lastModified);
                        neuUser.setAngemeldetAm(lastModified);
                        neuUser.setBearbeitetVon(currentUserName);

                        benutzerRepository.save(neuUser);

                        // Create user role
                        BenutzerRolle userRolle = new BenutzerRolle();
                        Rolle rolle = rolleRepository.findeRolleByName("USER");
                        userRolle.setRolle(rolle);
                        userRolle.setBenutzer(neuUser);
                        benutzerRolleRepository.save(userRolle);

                        model.addAttribute("message", "Neuer Benutzer wurde erfolgreich hinzugefügt!");
                    }

                    // Refresh the user list after adding/updating
                    List<Benutzer> users = benutzerRepository.findeAktiveUser("J");
                    redirectAttributes.addFlashAttribute("benutzerSuccess", true);
                    model.addAttribute("users", users);

                    return "redirect:/alleUser";
                } else {
                    model.addAttribute("message", "Sie haben keine Admin-Rechte");
                    return "response";
                }
            } catch (Exception ex) {
                logger.error("Error while adding/updating user", ex);
                model.addAttribute("message", "Ein Fehler bei der Datenbank ist passiert!");
                return "response";
            }
        }
    }

    @GetMapping("/resetKennwort/{id}")
    public String resetKennwort(@PathVariable Long id,
    		                    RedirectAttributes redirectAttributes,
                                Model model) {
        if(isAdmin()) {
            try {
                String currentUsername = holeAngemeldeteUser();
                benutzerService.updateKennwort(id, "mafis", currentUsername);
            }catch (Exception e) {
                logger.error("Fehler in resetKennwort" , e.getMessage());
                model.addAttribute("message", "Datenbankfehler. Das Kennwort konnte nicht zurückgesetzt werden!");
            }
        }else {
            model.addAttribute("message", "Sie haben keine Admin-Rechte");
        }
        redirectAttributes.addFlashAttribute("kennwortSuccess", true);
        return "redirect:/alleUser";
        }

    @GetMapping("/searchUser")
    public String searchUser(@RequestParam(value = "search.js", required = false) String searchQuery, Model model) {
        try {
            // Delegate the search.js to the service layer
            List<Benutzer> users = benutzerService.searchUsers(searchQuery, "J");  // Only active users
            model.addAttribute("users", users);
            return "alleUser";  // Return the view with the list of users
        } catch (Exception e) {
            logger.error("Fehler in searchUser", e);
            model.addAttribute("message", "Datenbankfehler beim Suchen der Benutzer!");
            return "response";  // Return error response view
        }
    }


    @GetMapping("editKennwort")
    public String editKennwort(Model model) {
        KennwortDTO kennwortDTO = new KennwortDTO();
        model.addAttribute("kennwort", kennwortDTO);
        return "editKennwort";
    }

    @PostMapping("/saveKennwort")
    public String saveKennwort(@ModelAttribute KennwortDTO kennwortDTO, Model model) {
        // Fetch the currently authenticated user
        String currentUsername = holeAngemeldeteUser();
        Benutzer benutzer = benutzerRepository.findByUsername(currentUsername);

        // Verify if the old password matches the stored password
        if (!encoder.matches(kennwortDTO.getAltesKennwort(), benutzer.getPassword())) {
            // Error if the old password does not match
            model.addAttribute("error", "Falsches altes Kennwort.");
            model.addAttribute("kennwort", kennwortDTO);
            return "editKennwort";
        }

        // Check if the new password and confirmation password match
        if (!kennwortDTO.getNeuesKennwort().equals(kennwortDTO.getWiederholung())) {
            // Error if the new password and confirmation do not match
            model.addAttribute("error", "Das neue Kennwort stimmt nicht mit der Wiederholung überein.");
            model.addAttribute("kennwort", kennwortDTO);
            return "editKennwort";
        }
        // Update the user's password via the service
        benutzerService.updateKennwort(benutzer.getUserId(), kennwortDTO.getNeuesKennwort(), currentUsername);
        // Redirect back to the editKennwort page with a success message
        return "redirect:/editKennwort?success=true";
    }



    private static String holeAngemeldeteUser() {
        Authentication authentication= null;
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        return currentUserName;
    }





    /**
     * Prüfen, ob der aktuelle Benutzer Admin ist
     *
     * @return True, wenn der Benutzer Admin ist
     */
    public static Boolean isAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        isAdmin= false;
        for (GrantedAuthority item: auth.getAuthorities()) {
            if(item.getAuthority().equalsIgnoreCase("ADMIN")) {
                isAdmin= true;
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes the default roles in the system if they do not exist: "USER" and "ADMIN".
     */
    private void initRollen() {

        Rolle rolle= rolleRepository.findeRolleByName("USER");
        if(rolle == null) {
            rolle = new Rolle();
            rolle.setRolleName("USER");
            rolleRepository.save(rolle);
        }
        rolle= rolleRepository.findeRolleByName("ADMIN");
        if(rolle == null) {
            rolle = new Rolle();
            rolle.setRolleName("ADMIN");
            rolleRepository.save(rolle);
        }

    }
}