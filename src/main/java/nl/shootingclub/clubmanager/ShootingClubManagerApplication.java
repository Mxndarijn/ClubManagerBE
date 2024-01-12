package nl.shootingclub.clubmanager;

import nl.shootingclub.clubmanager.configuration.images.DefaultImageData;
import nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData;
import nl.shootingclub.clubmanager.configuration.permission.AssociationPermissionData;
import nl.shootingclub.clubmanager.configuration.role.DefaultRoleAccount;
import nl.shootingclub.clubmanager.configuration.role.DefaultRoleAssociation;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.*;
import nl.shootingclub.clubmanager.service.AssociationService;
import nl.shootingclub.clubmanager.service.UserAssociationService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ShootingClubManagerApplication {

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


	public static void main(String[] args) {
		SpringApplication.run(ShootingClubManagerApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(UserService userService, AssociationService associationService, UserAssociationService userAssociationService) {
		return args -> {
//			User newUser = new User();
//			Image newImage = new Image();
//			newImage.setEncoded("a");
//
////
//			newUser.setFullName("jappie");
//			newUser.setEmail("merijn.gommereeeen@hotmail.com");
//			newUser.setPassword(encoder.encode("easy"));
//			newUser.setImage(newImage);
//
//			userService.createUser(newUser);
//
//			Association association = new Association();
//			association.setName("Test Associatie");
//            association.setContactEmail("merijnzndiscord");
//			association.setActive(true);
//			association.setWelcomeMessage("Halloooo");
////
////
////
//			User u = userService.loadUserByEmail("merijn.gommeren@hotmail.com").get();
//
//			Association savedAssociation = associationService.createAssociation(association);
//			UserAssociation userAssociation = new UserAssociation();
//			userAssociation.setAssociation(savedAssociation);
//			userAssociation.setUser(u);
//			userAssociation.setContributionPrice(10);
//
//			UserAssociationId id = new UserAssociationId();
//			id.setAssociationId(savedAssociation.getId());
//			id.setUserId(u.getId());
//			userAssociation.setId(id);

//			userAssociationService.createUserAssociation(userAssociation);

			//Load images into database
			for (DefaultImageData defaultImage : DefaultImageData.values()) {
				Optional<DefaultImage> image = defaultImageRepository.findByName(defaultImage.getName());
				if(image.isPresent()) {
					continue;
				}
				DefaultImage newDefaultImage = new DefaultImage();
				newDefaultImage.setName(defaultImage.getName());

				Image i = new Image();
				i.setEncoded("data:image/" + defaultImage.getFileType() +",base64," + defaultImage.getBase64EncodedImage());
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
		};
	}


}
