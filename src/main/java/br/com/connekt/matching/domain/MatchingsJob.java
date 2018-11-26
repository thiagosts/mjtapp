package br.com.connekt.matching.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A MatchingsJob.
 */
@Entity
@Table(name = "matchings_job")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MatchingsJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cut_note")
    private Float cutNote;

    @Column(name = "jhi_order")
    private Integer order;

    @Column(name = "jhi_require")
    private String require;

    @Column(name = "opportunities_id")
    private String opportunitiesId;

    @OneToMany(mappedBy = "matchingsJob")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Matchings> matchings = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getCutNote() {
        return cutNote;
    }

    public MatchingsJob cutNote(Float cutNote) {
        this.cutNote = cutNote;
        return this;
    }

    public void setCutNote(Float cutNote) {
        this.cutNote = cutNote;
    }

    public Integer getOrder() {
        return order;
    }

    public MatchingsJob order(Integer order) {
        this.order = order;
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getRequire() {
        return require;
    }

    public MatchingsJob require(String require) {
        this.require = require;
        return this;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getOpportunitiesId() {
        return opportunitiesId;
    }

    public MatchingsJob opportunitiesId(String opportunitiesId) {
        this.opportunitiesId = opportunitiesId;
        return this;
    }

    public void setOpportunitiesId(String opportunitiesId) {
        this.opportunitiesId = opportunitiesId;
    }

    public Set<Matchings> getMatchings() {
        return matchings;
    }

    public MatchingsJob matchings(Set<Matchings> matchings) {
        this.matchings = matchings;
        return this;
    }

    public MatchingsJob addMatchings(Matchings matchings) {
        this.matchings.add(matchings);
        matchings.setMatchingsJob(this);
        return this;
    }

    public MatchingsJob removeMatchings(Matchings matchings) {
        this.matchings.remove(matchings);
        matchings.setMatchingsJob(null);
        return this;
    }

    public void setMatchings(Set<Matchings> matchings) {
        this.matchings = matchings;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MatchingsJob matchingsJob = (MatchingsJob) o;
        if (matchingsJob.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), matchingsJob.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MatchingsJob{" +
            "id=" + getId() +
            ", cutNote=" + getCutNote() +
            ", order=" + getOrder() +
            ", require='" + getRequire() + "'" +
            ", opportunitiesId='" + getOpportunitiesId() + "'" +
            "}";
    }
}
