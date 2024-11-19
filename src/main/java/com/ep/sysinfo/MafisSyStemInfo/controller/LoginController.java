package com.ep.sysinfo.MafisSyStemInfo.controller;

import com.ep.sysinfo.MafisSyStemInfo.model.Anlage;
import com.ep.sysinfo.MafisSyStemInfo.model.Automat;
import com.ep.sysinfo.MafisSyStemInfo.model.GuestInfo;
import com.ep.sysinfo.MafisSyStemInfo.service.AnlageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class LoginController {

    @Autowired
    private AnlageService anlageService;

    /**
     * Retrieves the login page.
     *
     * @return the name of the login page
     */
    @GetMapping("/login")
    String login() {
        return "login";
    }

    /**
     * Handles the default behavior after a successful login.
     *
     * @return the URL to redirect the user to based on their role
     */
    @RequestMapping("/homepage")
    public String defaultAfterLogin(Model model) {
        List<Anlage> anlagen = anlageService.findAllAnlagen();
        if (anlagen == null || anlagen.isEmpty()) {
            throw new ResourceNotFoundException("Keine Anlagen gefunden.");
        }
        long mavisVersionCount = anlagen.stream()
                .map(Anlage::getMafisVersion)
                .distinct()
                .count();

        // Group by mafivsVersion and count occurrences of each version
        Map<String, Object> mavisVersionCounts = anlagen.stream()
                .collect(Collectors.groupingBy(
                        anlage -> anlage.getMafisVersion() != null ? anlage.getMafisVersion() : "Unknown",
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.getFirst().getMafisVersion() == null ?
                                        list.stream().map(Anlage::getAnlagenName).collect(Collectors.toList()) :
                                        (long) list.size()
                        )
                ));

        // List of unique mafisVersions (keys from the map)
        List<String> mavisVersions = new ArrayList<>(mavisVersionCounts.keySet());


        // Filter GuestInfo where SystemInfo.lastModified is one day before
        LocalDateTime startOfYesterday = LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay();

        List<GuestInfo> guestInfos = anlagen.stream()
                .filter(anlage -> anlage.getSystem() != null) // Ensure SystemInfo exists
                .filter(anlage -> {
                    LocalDateTime lastModified = anlage.getSystem().getLastModified();
                    return lastModified != null &&
                            lastModified.isAfter(startOfYesterday); // Include everything after the start of yesterday
                })
                .map(anlage -> anlage.getSystem().getGuestsInfos()) // Extract GuestInfo
                .filter(Objects::nonNull) // Ensure GuestInfo is not null
                .toList(); // Collect as a list


        // Calculate the total number of adult guests
        long totalGuestsAdults = guestInfos.stream()
                .mapToLong(guestInfo -> guestInfo.getTotalGuestsAdult() != null ? guestInfo.getTotalGuestsAdult() : 0)
                .sum();

        // Calculate the total number of child guests
        long totalGuestsKinder = guestInfos.stream()
                .mapToLong(guestInfo -> guestInfo.getTotalGuestsChild() != null ? guestInfo.getTotalGuestsChild() : 0)
                .sum();

        // Calculate the total number of all guests
        long totalGuests = totalGuestsAdults + totalGuestsKinder;

        model.addAttribute("gesternDatum", startOfYesterday);
        model.addAttribute("mavisVersionCount", mavisVersionCount);
        model.addAttribute("mavisVersionCounts", mavisVersionCounts);
        model.addAttribute("mavisVersions", mavisVersions);
        model.addAttribute("totalGuestsAdults", totalGuestsAdults);
        model.addAttribute("totalGuestsKinder", totalGuestsKinder);
        model.addAttribute("totalGuests", totalGuests);

        return "homepage";
    }

}
