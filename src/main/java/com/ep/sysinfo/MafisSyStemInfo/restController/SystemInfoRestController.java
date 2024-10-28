package com.ep.sysinfo.MafisSyStemInfo.restController;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ep.sysinfo.MafisSyStemInfo.repository.*;
import com.ep.sysinfo.MafisSyStemInfo.model.*;

import common.Error;
import common.Response;

/**
 * RestController for system information of the facility.
 */
@RestController
@RequestMapping("/rest") // Same base path as the original
public class SystemInfoRestController {

    private static final Logger logger = LoggerFactory.getLogger(SystemInfoRestController.class);

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private AnlageRepository anlageRepository;

    @Autowired
    private ModulRepository modulRepository;

    @Autowired
    private SchnittstelleRepository schnittstelleRepository;

    @Autowired
    private KasseRepository kasseRepository;

    @Autowired
    private AutomatRepository automatRepository;

    @Autowired
    private ZutrittRepository zutrittRepository;

    @Autowired
    private UpdateRepository updateRepository;

    @Autowired
    private BetriebRepository betriebRepository;

    @Autowired
    private ProfitCenterRepository profitCenterRepository;

    @Autowired
    private SektorRepository sektorRepository;

    @Autowired
    private MedienArtRepository medienArtRepository;

    @Autowired
    private MedienTypRepository medienTypRepository;

    @Autowired
    private FiskalDatenRepository fiskalDatenRepository;

    @Autowired
    private FiskalRegRepository fiskalRegRepository;

    @Autowired
    private ArbeitsPlatzFiskalRepository arbeitsPlatzFiskalRepository;

    /**
     * Saves or updates system information. If the system already exists, it is updated, otherwise it is saved as a new record.
     *
     * @param systemInfo The system information to be saved or updated.
     * @return A response indicating success or failure.
     */
    @PostMapping(value = "/saveSystemInfo",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addSystemInformation(@RequestBody SystemInfo systemInfo) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");

        try {
            Optional<Anlage> existingAnlage = Optional.ofNullable(anlageRepository.existsAnlage(systemInfo.getAnlage().getAnlagenNr()));

            existingAnlage.ifPresentOrElse(anlage -> {
                logger.info("Anlage exists, updating: {}", anlage.getAnlagenName());
                updateAnlage(anlage, systemInfo);
            }, () -> {
                logger.info("Anlage is new, saving: {}", systemInfo.getAnlage().getAnlagenName());
                saveNewSystemInfo(systemInfo);
            });

            return ResponseEntity.ok(new Response(LocalDateTime.now(), Error.OK, "System information saved successfully."));
        } catch (Exception e) {
            logger.error("Error saving system information for Anlage: {}", systemInfo.getAnlage().getAnlagenName(), e);
            return ResponseEntity.badRequest().body(new Response(LocalDateTime.now(), Error.BAD_REQUEST, "Failed to save system information."));
        }
    }

    /**
     * Updates the Anlage entity with the information provided in the SystemInfo object.
     * It sets the Anlagen Name, Ort, Mafis Version, and Test Betrieb attributes of the Anlage.
     * Then it saves the updated Anlage entity using the Anlage Repository.
     * If the Anlage has a valid System ID, it updates the system using the System Repository.
     * Afterward, it clears existing system data and saves system-related data.
     *
     * @param  anlage      The Anlage entity to be updated.
     * @param  systemInfo  The SystemInfo object containing the new information.
     * @return             No return value.
     */
    private void updateAnlage(Anlage anlage, SystemInfo systemInfo) {
        anlage.setAnlagenName(systemInfo.getAnlage().getAnlagenName());
        anlage.setOrt(systemInfo.getAnlage().getOrt());
        anlage.setMafisVersion(systemInfo.getAnlage().getMafisVersion());
        anlage.setTestBetrieb(systemInfo.getAnlage().getTestBetrieb());
        anlageRepository.save(anlage);

        Long systemId = Optional.ofNullable(anlage.getSystem()).map(SystemInfo::getSystem_id).orElseThrow();
        systemRepository.updateSystem(systemId, LocalDateTime.now());

        clearExistingSystemData(systemId);

        saveSystemRelatedData(systemInfo, anlage.getSystem());
    }

    /**
     * Saves new system information by setting the last modified date, saving the system information,
     * updating the related Anlage with the system information, and saving the Anlage.
     *
     * @param  systemInfo The system information to be saved.
     */
    private void saveNewSystemInfo(SystemInfo systemInfo) {
        systemInfo.setLastModified(LocalDateTime.now());
        systemRepository.save(systemInfo);

        Anlage anlage = systemInfo.getAnlage();
        anlage.setSystem(systemInfo);
        anlage.setStatus(true);
        anlageRepository.save(anlage);

        saveSystemRelatedData(systemInfo, systemInfo);
    }

    /**
     * Clears existing data related to the given system ID across multiple repositories.
     *
     * @param  systemId   the ID of the system for which data needs to be cleared
     */
    private void clearExistingSystemData(Long systemId) {
        modulRepository.deleteBySystemId(systemId);
        updateRepository.deleteBySystemId(systemId);
        schnittstelleRepository.deleteBySystemId(systemId);
        kasseRepository.deleteBySystemId(systemId);
        automatRepository.deleteBySystemId(systemId);
        zutrittRepository.deleteBySystemId(systemId);
        betriebRepository.deleteBySystemId(systemId);
        profitCenterRepository.deleteBySystemId(systemId);
        sektorRepository.deleteBySystemId(systemId);
        medienArtRepository.deleteBySystemId(systemId);
        medienTypRepository.deleteBySystemId(systemId);
        fiskalDatenRepository.getFiskalDatenBySystemId(systemId).forEach(fiskal -> {
            fiskal.getRegListe().forEach(reg -> arbeitsPlatzFiskalRepository.deleteByRegId(reg.getId()));
            fiskalRegRepository.deleteBySystemId(fiskal.getFiskalId());
        });
        fiskalDatenRepository.deleteBySystemId(systemId);
    }

    /**
     * Saves the system-related data for the given SystemInfo and the savedSystemInfo.
     *
     * @param  systemInfo      the SystemInfo object to save related data for
     * @param  savedSystemInfo the saved SystemInfo object to associate the data with
     */
    private void saveSystemRelatedData(SystemInfo systemInfo, SystemInfo savedSystemInfo) {
        Optional.ofNullable(systemInfo.getUpdates()).ifPresent(update -> {
            update.setSystem(savedSystemInfo);
            updateRepository.save(update);
        });

        Optional.ofNullable(systemInfo.getModule()).stream()
                .flatMap(Collection::stream)
                .forEach(modul -> {
                    modul.setSystem(savedSystemInfo);
                    modulRepository.save(modul);
                });

        Optional.ofNullable(systemInfo.getSchnittstellen()).stream()
                .flatMap(Collection::stream)
                .forEach(schnittstelle -> {
                    schnittstelle.setSystem(savedSystemInfo);
                    schnittstelleRepository.save(schnittstelle);
                });

        Optional.ofNullable(systemInfo.getKassen()).stream()
                .flatMap(Collection::stream)
                .forEach(kasse -> {
                    kasse.setSystem(savedSystemInfo);
                    kasseRepository.save(kasse);
                });

        Optional.ofNullable(systemInfo.getAutomaten()).stream()
                .flatMap(Collection::stream)
                .forEach(automat -> {
                    automat.setSystem(savedSystemInfo);
                    automatRepository.save(automat);
                });

        Optional.ofNullable(systemInfo.getZutritts()).stream()
                .flatMap(Collection::stream)
                .forEach(zutritt -> {
                    zutritt.setSystem(savedSystemInfo);
                    zutrittRepository.save(zutritt);
                });

        Optional.ofNullable(systemInfo.getBetriebe()).stream()
                .flatMap(Collection::stream)
                .forEach(betrieb -> {
                    betrieb.setSystem(savedSystemInfo);
                    betriebRepository.save(betrieb);
                });

        Optional.ofNullable(systemInfo.getProfitCenter()).stream()
                .flatMap(Collection::stream)
                .forEach(profitcenter -> {
                    profitcenter.setSystem(savedSystemInfo);
                    profitCenterRepository.save(profitcenter);
                });

        Optional.ofNullable(systemInfo.getSektoren()).stream()
                .flatMap(Collection::stream)
                .forEach(sektor -> {
                    sektor.setSystem(savedSystemInfo);
                    sektorRepository.save(sektor);
                });

        Optional.ofNullable(systemInfo.getMedienArten()).stream()
                .flatMap(Collection::stream)
                .forEach(medienArt -> {
                    medienArt.setSystem(savedSystemInfo);
                    medienArtRepository.save(medienArt);
                });

        Optional.ofNullable(systemInfo.getMedienTypen()).stream()
                .flatMap(Collection::stream)
                .forEach(medienTyp -> {
                    medienTyp.setSystem(savedSystemInfo);
                    medienTypRepository.save(medienTyp);
                });

        Optional.ofNullable(systemInfo.getFiskalService()).stream()
                .flatMap(Collection::stream)
                .forEach(fiskal -> {
                    fiskal.setSystem(savedSystemInfo);
                    fiskalDatenRepository.save(fiskal);
                    fiskal.getRegListe().forEach(fiskalReg -> {
                        fiskalReg.setFiskal(fiskal);
                        fiskalRegRepository.save(fiskalReg);
                        Optional.ofNullable(fiskalReg.getArbeitsplatzListe()).ifPresent(arListe -> arListe.forEach(ar -> {
                            ar.setRegister(fiskalReg);
                            arbeitsPlatzFiskalRepository.save(ar);
                        }));
                    });
                });
    }

    /**
     * Retrieves all system information and returns it in a ResponseEntity.
     *
     * @return         ResponseEntity containing a list of SystemInfo objects
     */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SystemInfo>> listAllSystemInfo() {
        try {
            List<SystemInfo> infos = systemRepository.findAll();
            return ResponseEntity.ok(infos);
        } catch (Exception e) {
            logger.error("Error fetching all system information", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
