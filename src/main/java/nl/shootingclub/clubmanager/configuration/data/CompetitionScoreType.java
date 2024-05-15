package nl.shootingclub.clubmanager.configuration.data;

import lombok.SneakyThrows;
import nl.shootingclub.clubmanager.helper.competition.CompetitionScoreHandler;
import nl.shootingclub.clubmanager.helper.competition.CompetitionScoreHandlerPoint;
import nl.shootingclub.clubmanager.helper.competition.CompetitionScoreHandlerTime;

public enum CompetitionScoreType {
    TIME(CompetitionScoreHandlerTime.class),
    POINT(CompetitionScoreHandlerPoint.class);

    private final Class<? extends CompetitionScoreHandler> handlerClass;

    CompetitionScoreType(Class<? extends CompetitionScoreHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    @SneakyThrows
    public CompetitionScoreHandler getHandlerInstance() {
        return  handlerClass.getDeclaredConstructor().newInstance();
    }
}
