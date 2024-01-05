package nl.shootingclub.clubmanager.resolver;

import nl.shootingclub.clubmanager.model.Association;
import nl.shootingclub.clubmanager.service.AssociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Component
@Controller
public class AssociationQueryResolver {

    @Autowired
    private  AssociationService associationService;

    @QueryMapping
    public List<Association> getMyAssociations() {
        return associationService.getMyAssociations();
    }


}
