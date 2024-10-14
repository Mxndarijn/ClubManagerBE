package nl.shootingclub.clubmanager.service;

import nl.shootingclub.clubmanager.model.data.ColorPreset;
import nl.shootingclub.clubmanager.repository.ColorPresetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ColorPresetService {

    @Autowired
    private ColorPresetRepository colorPresetRepository;

    public List<ColorPreset> getAllColorPresets() {
        return colorPresetRepository.findAll();
    }

    public ColorPreset createColorPreset(ColorPreset colorPreset) {
        return colorPresetRepository.save(colorPreset);
    }

    public Optional<ColorPreset> getByID(UUID colorPresetID) {
        return colorPresetRepository.findById(colorPresetID);
    }

    public ColorPreset saveColorPreset(ColorPreset colorPreset) {
        return colorPresetRepository.save(colorPreset);
    }
}