
package nl.shootingclub.clubmanager.resolver;

import nl.shootingclub.clubmanager.configuration.data.Language;
import nl.shootingclub.clubmanager.dto.ChangeProfilePictureDTO;
import nl.shootingclub.clubmanager.dto.UpdateMyProfileDTO;
import nl.shootingclub.clubmanager.dto.response.DefaultBooleanResponseDTO;
import nl.shootingclub.clubmanager.helper.ImageHelper;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.data.Image;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Optional;

@Controller
public class UserResolver {

    public UserResolver(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public UserResolver userQueries() {
        return this;
    }

    @MutationMapping
    public UserResolver userMutations() {
        return this;
    }


    private final UserService userService;

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return The user profile of the currently authenticated user.
     *         Returns null if the user does not exist.
     */
    @SchemaMapping(typeName = "UserQueries")
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.data.AccountPermissionData).GET_MY_PROFILE)")
    public User getMyProfile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userService.getUser(user);
        if(optionalUser.isPresent()) {
            User u = optionalUser.get();
            u.getAssociations().forEach(a -> {
                a.getAssociation().setUsers(null);
            });
            u.getRole().setUsers(null);
            return u;
        }
        return null;
    }

    /**
     * Updates the profile of the currently logged-in user.
     *
     * @param dto The object containing the updated user profile information.
     * @return A response object indicating the success or failure of the profile update.
     */
    @SchemaMapping(typeName = "UserMutations")
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.data.AccountPermissionData).GET_MY_PROFILE)")
    public DefaultBooleanResponseDTO updateMyProfile(@Argument UpdateMyProfileDTO dto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userService.getUser(user);
        if(optionalUser.isEmpty()) {
            DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
            responseDTO.setSuccess(false);
            responseDTO.setMessage("no-user-found");
            return responseDTO;
        }
        user = optionalUser.get();
        if(!userService.authenticate(user, dto.getOldPassword())) {
            DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
            responseDTO.setSuccess(false);
            responseDTO.setMessage("not-correct-password");
            return responseDTO;
        }
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        if(dto.getNewPassword()!= null && !dto.getNewPassword().isEmpty()) {
            user.setPassword(userService.encodePassword(dto.getNewPassword()));
        }

        User updatedUser = userService.saveUser(user);
        DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
        if (updatedUser != null) {
            responseDTO.setSuccess(true);
            responseDTO.setMessage("profile-updated");
        } else {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("profile-update-failed");
        }
        return responseDTO;


    }

    @SchemaMapping(typeName = "UserMutations")
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.data.AccountPermissionData).GET_MY_PROFILE)")
    public DefaultBooleanResponseDTO updateLanguage(@Argument String language) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userService.getUser(user);
        if(optionalUser.isEmpty()) {
            DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
            responseDTO.setSuccess(false);
            responseDTO.setMessage("no-user-found");
            return responseDTO;
        }
        user = optionalUser.get();
        Optional<Language> optionalLanguage = Language.fromString(language);
        DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
        if(optionalLanguage.isEmpty()) {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("language-not-found");
            return responseDTO;
        }

        user.setLanguage(language);

        User updatedUser = userService.saveUser(user);
        if (updatedUser != null) {
            responseDTO.setSuccess(true);
            responseDTO.setMessage("language-updated");
        } else {
            responseDTO.setSuccess(false);
            responseDTO.setMessage("language-update-failed");
        }
        return responseDTO;


    }

    /**
     * Updates the profile picture of the authenticated user.
     *
     * @param dto The DTO containing the new profile picture image data.
     * @return The response DTO indicating the result of the update operation.
     */
    @SchemaMapping(typeName = "UserMutations")
    @PreAuthorize("@permissionService.validatePermission(T(nl.shootingclub.clubmanager.configuration.data.AccountPermissionData).GET_MY_PROFILE)")
    public DefaultBooleanResponseDTO updateMyProfilePicture(@Argument ChangeProfilePictureDTO dto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userService.getUser(user);
        if(optionalUser.isEmpty()) {
            DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
            responseDTO.setSuccess(false);
            responseDTO.setMessage("no-user-found");
            return responseDTO;
        }
        user = optionalUser.get();
        Image i = user.getImage();

        try {
            i.setEncoded(ImageHelper.scaleImage(dto.getImage(), 480));
            user.setImage(i);
        } catch (IOException e) {
            System.out.println("error");
            DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
            responseDTO.setSuccess(false);
            responseDTO.setMessage("could-not-convert");
            return responseDTO;
        }

        userService.saveUser(user);



        DefaultBooleanResponseDTO responseDTO = new DefaultBooleanResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setMessage("changed");

        return responseDTO;


    }

}
