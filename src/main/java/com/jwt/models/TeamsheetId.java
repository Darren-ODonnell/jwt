package com.jwt.models;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TeamsheetId implements Serializable {
    private static final long serialVersionUID = 1469351352979806288L;
    @Column(name = "fixture_id", nullable = false)
    private Long fixtureId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    public TeamsheetId() {
    }

    public TeamsheetId(Long fixtureId, Long playerId) {
        this.playerId = playerId;
        this.fixtureId = fixtureId;
    }

    public Long getFixtureId() {
        return fixtureId;
    }

    public void setFixtureId(Long fixtureId) {
        this.fixtureId = fixtureId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TeamsheetId entity = (TeamsheetId) o;
        return Objects.equals(this.fixtureId, entity.fixtureId) &&
                Objects.equals(this.playerId, entity.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fixtureId, playerId);
    }

}