package nl.shootingclub.clubmanager;

import nl.shootingclub.clubmanager.model.Image;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ShootingClubManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShootingClubManagerApplication.class, args);

//		UserModel model = new UserModel();

//
//		System.out.println(model.getId());
	}

	@Bean
	public CommandLineRunner run(UserService userService) {
		return args -> {
			User newUser = new User();
			Image newImage = new Image();
			newImage.setEncoded("a");
		// Stel de eigenschappen van de nieuwe gebruiker in
			newUser.setFirstName("Merijn");
			newUser.setLastName("Gommeren");
			newUser.setUserName("Mxndarijn");
			newUser.setEmail("merijn.gommeren@hotmail.com");
			newUser.setInitials("M.F.G.");
			newUser.setPassword("easy");
			newUser.setImage(newImage);
			// ... meer eigenschappen ...

			User savedUser = userService.createUser(newUser);
			System.out.println("Nieuwe gebruiker aangemaakt met ID: " + savedUser.getId());
		};
	}


}
