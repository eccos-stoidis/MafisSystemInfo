package com.ep.sysinfo.MafisSyStemInfo.service;

import com.ep.sysinfo.MafisSyStemInfo.model.Benutzer;

import java.time.LocalDateTime;
import java.util.List;

public interface BenutzerService {

    void updateKennwort(Long userId, String newKennwort, String currentUserName);

    boolean verifyOldPassword(String oldPassword, String storedPassword);

    void updateUser(Long userId, String username, String name, String email, LocalDateTime now, String currentUserName);

    void updateRole(String username, String s, LocalDateTime now);

    void updateUserRole(Long id, Long rId);

    List<Benutzer> searchUsers(String searchQuery, String j);
}
