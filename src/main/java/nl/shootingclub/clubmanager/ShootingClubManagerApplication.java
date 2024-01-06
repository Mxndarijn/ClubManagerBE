package nl.shootingclub.clubmanager;

import nl.shootingclub.clubmanager.configuration.permission.AccountPermissionData;
import nl.shootingclub.clubmanager.model.*;
import nl.shootingclub.clubmanager.repository.AccountPermissionRepository;
import nl.shootingclub.clubmanager.service.AssociationService;
import nl.shootingclub.clubmanager.service.UserAssociationService;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ShootingClubManagerApplication {

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private AccountPermissionRepository accountPermissionRepository;

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
//			newUser.setFirstName("Merijn");
//			newUser.setLastName("Gommeren");
//			newUser.setEmail("merijn.gommeren@hotmail.com");
//			newUser.setPassword(encoder.encode("easy"));
//			newUser.setImage(newImage);
//
//			Association association = new Association();
//			association.setName("Shooting Club");
//            association.setContactEmail("nielszndiscord");
//			association.setActive(true);
//			association.setWelcomeMessage("Welkom, het werkt :D");
//
//
//
//			User savedUser = userService.createUser(newUser);
//			Association savedAssociation = associationService.createAssociation(association);
//			UserAssociation userAssociation = new UserAssociation();
//			userAssociation.setAssociation(savedAssociation);
//			userAssociation.setUser(savedUser);
//			userAssociation.setContributionPrice(10);
//
//			UserAssociationId id = new UserAssociationId();
//			id.setAssociationId(savedAssociation.getId());
//			id.setUserId(savedUser.getId());
//			userAssociation.setId(id);
//
//			userAssociationService.createUserAssociation(userAssociation);


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
		};
	}


}
