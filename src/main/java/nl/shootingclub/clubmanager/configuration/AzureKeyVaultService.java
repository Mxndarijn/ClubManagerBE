package nl.shootingclub.clubmanager.configuration;

import com.azure.core.exception.AzureException;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AzureKeyVaultService {

    private final SecretClient secretClient;

    // Injecteer de Key Vault URL via application.properties
    public AzureKeyVaultService(@Value("${spring.cloud.azure.keyvault.secret.property-sources[0].endpoint}") String keyVaultUrl) {
        this.secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUrl)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    /**
     * Probeert een secret op te halen om de authenticatie te controleren.
     * @return true als authenticatie succesvol is, false anders
     */
    public boolean isAuthenticated() {
        try {
            // Probeer een bekende secret op te halen
            secretClient.getSecret("SPRING-MAIL-HOST");
            return true;
        } catch (AzureException ex) {
            // Log de fout (zonder gevoelige informatie)
            System.err.println("Authenticatie met Azure Key Vault mislukt: " + ex.getMessage());
            return false;
        }
    }


    /**
     * Haalt een secret op uit Azure Key Vault.
     *
     * @param secretName de naam van de secret
     * @return de waarde van de secret, null als er een fout optreedt
     */
    public String getSecret(String secretName) {
        try {
            return secretClient.getSecret(secretName).getValue();
        } catch (AzureException ex) {
            // Log de fout (zonder gevoelige informatie)
            System.err.println("Fout bij ophalen secret uit Azure Key Vault: " + ex.getMessage());
            return null;
        }
    }
}
