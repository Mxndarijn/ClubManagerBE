package nl.shootingclub.clubmanager;

import nl.shootingclub.clubmanager.configuration.data.*;
import nl.shootingclub.clubmanager.configuration.weapons.DefaultWeaponType;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.*;
import nl.shootingclub.clubmanager.service.AssociationService;
import nl.shootingclub.clubmanager.service.EmailService;
import nl.shootingclub.clubmanager.service.UserAssociationService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Optional;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
public class ShootingClubManagerApplication {

	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private AccountPermissionRepository accountPermissionRepository;

	@Autowired
	private AssociationPermissionRepository associationPermissionRepository;

	@Autowired
	private AssociationRoleRepository associationRoleRepository;

	@Autowired
	private AccountRoleRepository accountRoleRepository;

	@Autowired
	private DefaultImageRepository defaultImageRepository;

	@Autowired
	private WeaponTypeRepository weaponTypeRepository;

	@Autowired
	private ColorPresetRepository colorPresetRepository;


	public static void main(String[] args) {
		SpringApplication.run(ShootingClubManagerApplication.class, args);
	}

	/**
	 * Loads initial data into the database.
	 *
	 * @param userService           the UserService object to perform user-related operations
	 * @param associationService    the AssociationService object to perform association-related operations
	 * @param userAssociationService the UserAssociationService object to perform user-association related operations
	 *
	 * @return a CommandLineRunner object that executes the initial data loading
	 */
	@Bean
	public CommandLineRunner run(UserService userService, AssociationService associationService, UserAssociationService userAssociationService) {
		return args -> {
			//Load images into database
			for (DefaultImageData defaultImage : DefaultImageData.values()) {
				Optional<DefaultImage> image = defaultImageRepository.findByName(defaultImage.getName());
				if(image.isPresent()) {
					continue;
				}
				DefaultImage newDefaultImage = new DefaultImage();
				newDefaultImage.setName(defaultImage.getName());

				Image i = new Image();
				i.setEncoded(defaultImage.getBase64EncodedImage());
				newDefaultImage.setImage(i);
                defaultImageRepository.save(newDefaultImage);
			}

			// Load permissions into database
			for (AccountPermissionData perm : AccountPermissionData.values()) {
				Optional<AccountPermission> optionalAccountPermission = accountPermissionRepository.findByNameEquals(perm.getName());
				if(optionalAccountPermission.isEmpty()) {
					AccountPermission permission = new AccountPermission();
                    permission.setName(perm.getName());
                    permission.setDescription(perm.getDescription());
                    accountPermissionRepository.save(permission);
				}
			}

			//Account roles
			for (DefaultRoleAccount role : DefaultRoleAccount.values()) {
				Optional<AccountRole> optionalAccountRole = accountRoleRepository.findByName(role.getName());
				if(optionalAccountRole.isEmpty()) {
					AccountRole accountRole = new AccountRole();
					accountRole.setName(role.getName());
					accountRole.setCanBeDeleted(false);
					accountRoleRepository.save(accountRole);
				}
			}

			//Association Roles
			for (DefaultRoleAssociation role : DefaultRoleAssociation.values()) {
				Optional<AssociationRole> optionalAccountRole = associationRoleRepository.findByName(role.getName());
				if(optionalAccountRole.isEmpty()) {
					AssociationRole associationRole = new AssociationRole();
					associationRole.setName(role.getName());
					associationRole.setCanBeDeleted(false);
					associationRoleRepository.save(associationRole);
				}
			}
			// Load permissions for associations into database
			for (AssociationPermissionData perm : AssociationPermissionData.values()) {
				Optional<AssociationPermission> optionalAccountPermission = associationPermissionRepository.findByName(perm.getName());
				if(optionalAccountPermission.isEmpty()) {
					AssociationPermission permission = new AssociationPermission();
					permission.setName(perm.getName());
					permission.setDescription(perm.getDescription());
					associationPermissionRepository.save(permission);
				}
			}

			for (DefaultWeaponType weapon : DefaultWeaponType.values()) {
				Optional<WeaponType> optionalWeaponType = weaponTypeRepository.findByName(weapon.getName());
				if(optionalWeaponType.isEmpty()) {
					WeaponType weaponType = new WeaponType();
					weaponType.setName(weapon.getName());
					weaponTypeRepository.save(weaponType);
				}
			}

			//Default ColorPresets
			for (DefaultColorPreset color : DefaultColorPreset.values()) {
				Optional<ColorPreset> optionalColorPreset = colorPresetRepository.findByColorName(color.getName());
				if(optionalColorPreset.isEmpty()) {
					ColorPreset preset = new ColorPreset();
					preset.setColorName(color.getName());
					preset.setPrimaryColor(color.getPrimary());
					preset.setSecondaryColor(color.getSecondary());
					colorPresetRepository.save(preset);
				}
			}
		};
	}


}
