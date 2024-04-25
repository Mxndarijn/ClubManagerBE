# Kies een basisimage met Java
FROM openjdk:17-jdk

# Definieer een argument voor het pad van het JAR-bestand
ARG JAR_FILE

# Stel de werkdirectory in de Docker-container in
WORKDIR /app

# Kopieer het JAR-bestand naar de Docker-image
COPY ${JAR_FILE} app.jar

# Specificeer het commando om de Java applicatie te starten
CMD ["java", "-jar", "app.jar"]

