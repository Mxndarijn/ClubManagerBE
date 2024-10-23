package nl.shootingclub.clubmanager;

import nl.shootingclub.clubmanager.configuration.AzureKeyVaultService;
import nl.shootingclub.clubmanager.configuration.data.*;
import nl.shootingclub.clubmanager.configuration.weapons.DefaultWeaponType;
import nl.shootingclub.clubmanager.model.AssociationPermission;
import nl.shootingclub.clubmanager.model.AssociationRole;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.WeaponType;
import nl.shootingclub.clubmanager.model.account.AccountPermission;
import nl.shootingclub.clubmanager.model.account.AccountRole;
import nl.shootingclub.clubmanager.model.data.ColorPreset;
import nl.shootingclub.clubmanager.model.data.DefaultImage;
import nl.shootingclub.clubmanager.model.data.Image;
import nl.shootingclub.clubmanager.repository.*;
import nl.shootingclub.clubmanager.service.AssociationService;
import nl.shootingclub.clubmanager.service.UserAssociationService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
@EnableCaching
public class ShootingClubManagerApplication {

	private final AccountPermissionRepository accountPermissionRepository;

	private final AssociationPermissionRepository associationPermissionRepository;

	private final AssociationRoleRepository associationRoleRepository;

	private final AccountRoleRepository accountRoleRepository;

	private final DefaultImageRepository defaultImageRepository;

	private final WeaponTypeRepository weaponTypeRepository;

	private final ColorPresetRepository colorPresetRepository;

	private final PasswordEncoder passwordEncoder;

	private final UserRepository userRepository;

	private final AzureKeyVaultService secretService;


	public ShootingClubManagerApplication(AccountPermissionRepository accountPermissionRepository, AssociationPermissionRepository associationPermissionRepository, AssociationRoleRepository associationRoleRepository, AccountRoleRepository accountRoleRepository, DefaultImageRepository defaultImageRepository, WeaponTypeRepository weaponTypeRepository, ColorPresetRepository colorPresetRepository, PasswordEncoder passwordEncoder, AzureKeyVaultService secretService) {
		this.accountPermissionRepository = accountPermissionRepository;
		this.associationPermissionRepository = associationPermissionRepository;
		this.associationRoleRepository = associationRoleRepository;
		this.accountRoleRepository = accountRoleRepository;
		this.defaultImageRepository = defaultImageRepository;
		this.weaponTypeRepository = weaponTypeRepository;
		this.colorPresetRepository = colorPresetRepository;
        this.passwordEncoder = passwordEncoder;
        this.secretService = secretService;
        userRepository = null;

    }


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
				optionalAccountRole = accountRoleRepository.findByName(role.getName());
				if(optionalAccountRole.isEmpty()) {
					throw new Exception("Could not create Default Role: " + role.getName());
				}
				for(AccountPermissionData data : role.getPermissions()) {
					AccountPermission permission = accountPermissionRepository.findByNameEquals(data.getName()).orElseThrow();
                    optionalAccountRole.get().getPermissions().add(permission);
				}
				accountRoleRepository.save(optionalAccountRole.get());
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

			//Association Roles
			for (DefaultRoleAssociation role : DefaultRoleAssociation.values()) {
				Optional<AssociationRole> optionalAccountRole = associationRoleRepository.findByName(role.getName());
				if(optionalAccountRole.isEmpty()) {
					AssociationRole associationRole = new AssociationRole();
					associationRole.setName(role.getName());
					associationRole.setCanBeDeleted(false);
					associationRoleRepository.save(associationRole);
				}
				optionalAccountRole = associationRoleRepository.findByName(role.getName());
				if(optionalAccountRole.isEmpty()) {
					throw new Exception("Could not create Default Role: " + role.getName());
				}
				for(AssociationPermissionData data : role.getPermissions()) {
					AssociationPermission permission = associationPermissionRepository.findByName(data.getName()).orElseThrow();
					optionalAccountRole.get().getPermissions().add(permission);
				}
				associationRoleRepository.save(optionalAccountRole.get());
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
			for(AdminAccount admin : AdminAccount.values()) {
				Optional<User> optionalUser = userService.loadUserByEmail(admin.getEmail());
				if(optionalUser.isPresent())
					continue;
				User user = new User();
				user.setEmail(admin.getEmail());
				user.setPassword(passwordEncoder.encode(admin.getPassword()));
                user.setFullName(admin.getName());
                user.setRole(accountRoleRepository.findByName(DefaultRoleAccount.ADMIN.getName()).orElseThrow());
				user.setLanguage(admin.getLanguage());
				Optional<DefaultImage> image = defaultImageRepository.findByName(DefaultImageData.PROFILE_PICTURE.getName());
				if (image.isPresent()) {
					Image i = new Image();
					i.setEncoded(image.get().getImage().getEncoded());
					user.setImage(i);
				}
                userService.saveUser(user);
			}
		};
	}


}
