
package nl.shootingclub.clubmanager.controller;

import nl.shootingclub.clubmanager.model.ColorPreset;
import nl.shootingclub.clubmanager.model.WeaponMaintenance;
import nl.shootingclub.clubmanager.repository.ColorPresetRepository;
import nl.shootingclub.clubmanager.service.WeaponMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class ColorPresetController {


    @Autowired
    private ColorPresetRepository colorPresetRepository;


    @QueryMapping
    public List<ColorPreset> getAllColorPresets() {
            return colorPresetRepository.findAll();
    }

}
