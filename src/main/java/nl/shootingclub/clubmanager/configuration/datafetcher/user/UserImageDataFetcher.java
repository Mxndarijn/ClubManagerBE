package nl.shootingclub.clubmanager.configuration.datafetcher.user;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.transaction.Transactional;
import nl.shootingclub.clubmanager.helper.UserHelper;
import nl.shootingclub.clubmanager.model.Image;
import nl.shootingclub.clubmanager.model.User;
import nl.shootingclub.clubmanager.model.UserAssociation;
import nl.shootingclub.clubmanager.repository.UserAssociationRepository;
import nl.shootingclub.clubmanager.repository.UserRepository;
import nl.shootingclub.clubmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
public class UserImageDataFetcher implements DataFetcher<Image> {
    private final UserHelper userHelper;

    public UserImageDataFetcher(UserHelper userHelper) {
        this.userHelper = userHelper;
    }

    @Override
    public Image get(DataFetchingEnvironment environment) throws Exception {
        User user =environment.getSource();


        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User contextUser) {
            if(contextUser.getId().equals(user.getId())) {
                return user.getImage();
            }
            if(userHelper.doUsersHaveSharedAssociation(user, contextUser)) {
                return user.getImage();
            }

        }
        throw new AccessDeniedException("You do not have permission to view this data");
    }
}
