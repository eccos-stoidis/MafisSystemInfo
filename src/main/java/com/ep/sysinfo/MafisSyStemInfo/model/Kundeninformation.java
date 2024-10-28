package com.ep.sysinfo.MafisSyStemInfo.model;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Kundeninformation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1991375623795012882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Long anlagenNr;
    private String anlagenName;
    private String anlagenOrt;
    private LocalDateTime zuletzteAktualisiert;
    private Boolean isAktiv;
    private String mafisVersion;
    private Boolean isMafisTestbetrieb;
    private Boolean isMafisUpdateAktiv;
    private Boolean isMafisAutoUpdate;
    private String serverComputerName;
    private String serverIPAdresse;
    private String serverBetriebssystem;
    private String serverBetriebssystemVersion;
    private String serverJavaVersion;
    private String serverJavaHome;
    private String fiskalTyp;
    private LocalDateTime fiskalAktivAb;

    /**
     * Generates a CSV representation of the Kundeninformation fields.
     *
     * @return          a CSV string representing the Kundeninformation fields
     */
    public String exportAlsCsv() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<String> liste = new ArrayList<>();
        liste.add(getAnlagenNr() != null ? getAnlagenNr().toString() : "");
        liste.add(getAnlagenName() != null ? getAnlagenName() : "");
        liste.add(getAnlagenOrt() != null ? getAnlagenOrt() : "");
        liste.add(getZuletzteAktualisiert() != null ?  getZuletzteAktualisiert().format(formatter) : "");
        liste.add(getIsAktiv() != null ? getIsAktiv() ? "aktiv" : "inaktiv" : "");
        liste.add(getMafisVersion() != null ? getMafisVersion() : "");
        liste.add(getIsMafisTestbetrieb() != null ? getIsMafisTestbetrieb() ? "ja" : "nein" : "");
        liste.add(getIsMafisUpdateAktiv() != null ? getIsMafisUpdateAktiv() ? "ja" : "nein" : "");
        liste.add(getIsMafisAutoUpdate() != null ? getIsMafisAutoUpdate() ? "ja" : "nein" : "");
        liste.add(getServerComputerName() != null ? getServerComputerName() : "");
        liste.add(getServerIPAdresse() != null ? getServerIPAdresse() : "");
        liste.add(getServerBetriebssystem() != null ? getServerBetriebssystem() : "");
        liste.add(getServerBetriebssystemVersion() != null ? getServerBetriebssystemVersion() : "");
        liste.add(getServerJavaVersion() != null ? getServerJavaVersion() : "");
        liste.add(getServerJavaHome() != null ? getServerJavaHome() : "");
        liste.add(getFiskalTyp() != null ? getFiskalTyp() : "");
        // Use DateTimeFormatter for fiskalAktivAb
        liste.add(getFiskalAktivAb() != null ? getFiskalAktivAb().format(formatter) : "");
        return liste.stream().map(s -> s + "\t").collect(Collectors.joining(";"));
    }

    /**
     * Creates a new Kundeninformation object based on the provided object array.
     *
     * @param  object  the array of objects used to populate the Kundeninformation fields
     * @return         the created Kundeninformation object
     */
    public static Kundeninformation create(Object[] object) {
        Kundeninformation kundeninformation = new Kundeninformation();
        if (object[0] != null) kundeninformation.setAnlagenNr(Long.valueOf(String.valueOf(object[0])));
        if (object[1] != null) kundeninformation.setAnlagenName(String.valueOf(object[1]));
        if (object[2] != null) kundeninformation.setAnlagenOrt(String.valueOf(object[2]));
        if (object[3] != null) kundeninformation.setZuletzteAktualisiert((LocalDateTime) object[3]);
        if (object[4] != null) kundeninformation.setIsAktiv((boolean) object[4]);
        if (object[5] != null) kundeninformation.setMafisVersion(String.valueOf(object[5]));
        if (object[6] != null) kundeninformation.setIsMafisTestbetrieb((boolean) object[6]);
        if (object[7] != null) kundeninformation.setIsMafisUpdateAktiv((boolean) object[7]);
        if (object[8] != null) kundeninformation.setIsMafisAutoUpdate((boolean) object[8]);
        if (object[9] != null) kundeninformation.setServerComputerName(String.valueOf(object[9]));
        if (object[10] != null) kundeninformation.setServerIPAdresse(String.valueOf(object[10]));
        if (object[11] != null) kundeninformation.setServerBetriebssystem(String.valueOf(object[11]));
        if (object[12] != null) kundeninformation.setServerBetriebssystemVersion(String.valueOf(object[12]));
        if (object[13] != null) kundeninformation.setServerJavaVersion(String.valueOf(object[13]));
        if (object[14] != null) kundeninformation.setServerJavaHome(String.valueOf(object[14]));
        if (object[15] != null) kundeninformation.setFiskalTyp(String.valueOf(object[15]));
        if (object[16] != null) kundeninformation.setFiskalAktivAb((LocalDateTime) object[16]);
        return kundeninformation;
    }
}
