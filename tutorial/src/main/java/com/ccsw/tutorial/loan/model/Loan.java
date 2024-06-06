package com.ccsw.tutorial.loan.model;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.game.model.Game;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "loan")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Temporal(TemporalType.DATE)
    @Column(name = "loan_date", nullable = false)
    private Date loan_date;

    @Temporal(TemporalType.DATE)
    @Column(name = "return_date", nullable = false)
    private Date return_date;

    /**
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id new value of {@link #getId}.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return category
     */
    public Game getGame() {
        return game;
    }

    /**
     *
     * @param game new value of {@link #getGame}
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     *
     * @return client
     */
    public Client getClient() {
        return client;
    }

    /**
     *
     * @param client new value {@link #client}
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     *
     * @return loan_date
     */
    public Date getLoan_date() {
        return loan_date;
    }

    /**
     *
     * @param loan_date new value {@link #loan_date}
     */
    public void setLoan_date(Date loan_date) {
        this.loan_date = loan_date;
    }

    /**
     *
     * @return return_date
     */
    public Date getReturn_date() {
        return return_date;
    }

    /**
     *
     * @param return_date new value {@link #return_date}
     */
    public void setReturn_date(Date return_date) {
        this.return_date = return_date;
    }
}
