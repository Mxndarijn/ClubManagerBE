
package nl.shootingclub.clubmanager.resolver;

import nl.shootingclub.clubmanager.model.AssociationRole;
import nl.shootingclub.clubmanager.model.ColorPreset;
import nl.shootingclub.clubmanager.model.WeaponType;
import nl.shootingclub.clubmanager.repository.AssociationRoleRepository;
import nl.shootingclub.clubmanager.repository.ColorPresetRepository;
import nl.shootingclub.clubmanager.repository.WeaponTypeRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UtilResolver {

    private final ColorPresetRepository colorPresetRepository;
    private final WeaponTypeRepository weaponTypeRepository;

    private final AssociationRoleRepository associationRoleRepository;

    public UtilResolver(ColorPresetRepository colorPresetRepository, AssociationRoleRepository associationRoleRepository, WeaponTypeRepository weaponTypeRepository) {
        this.colorPresetRepository = colorPresetRepository;
        this.associationRoleRepository = associationRoleRepository;
        this.weaponTypeRepository = weaponTypeRepository;
    }

    @QueryMapping
    public UtilResolver utilQueries() {
        return this;
    }


    /**
     * Retrieves all color presets.
     *
     * @return a list of color presets
     */
    @SchemaMapping(typeName = "UtilQueries")
    public List<ColorPreset> getAllColorPresets() {
            return colorPresetRepository.findAll();
    }

    /**
     * Retrieves a list of all association roles.
     *
     * @return a list of AssociationRole objects representing all association roles.
     */
    @SchemaMapping(typeName = "UtilQueries")
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.data.AccountPermissionData).GET_ASSOCIATION_ROLES)")
    public List<AssociationRole> getAssociationRoles() {
        return associationRoleRepository.findAll();
    }

    @SchemaMapping(typeName = "UtilQueries")
    public List<WeaponType> getAllWeaponTypes() {
        return weaponTypeRepository.findAll();
    }

}
