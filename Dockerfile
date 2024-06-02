# Kies een basisimage met Java
FROM openjdk:17-jdk

# Definieer een argument voor het pad van het JAR-bestand
ARG JAR_FILE

# Voeg een argument toe voor Spring profiles
ARG SPRING_PROFILES_ACTIVE

# Stel de werkdirectory in de Docker-container in
WORKDIR /app

# Kopieer het JAR-bestand naar de Docker-image
COPY ${JAR_FILE} app.jar

# Stel de omgevingsvariabele in met de waarde van het build argument
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

# Specificeer het commando om de Java applicatie te starten
CMD ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]
