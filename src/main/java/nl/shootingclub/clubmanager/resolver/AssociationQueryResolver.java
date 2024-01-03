package nl.shootingclub.clubmanager.resolver;

import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.service.AssociationService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

@Component
@Controller
public class AssociationQueryResolver {

    private final AssociationService associationService;

    public AssociationQueryResolver(AssociationService associationService) {
        this.associationService = associationService;
    }

    @QueryMapping
    public List<Association> getMyAssociations() {
        return associationService.getMyAssociations();
    }


}
