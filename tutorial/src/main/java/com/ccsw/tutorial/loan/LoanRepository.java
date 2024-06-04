package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LoanRepository extends CrudRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {
    /**
     * Método para recuperar un listado paginado de {@link Loan}
     *
     * @param pageable pageable
     * @return {@link Page} de {@link Loan}
     */
    Page<Loan> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "game", "client" })
    List<Loan> findAll(Specification<Loan> spec);

    /**
     * Query que valida que un
     * @param gameId
     * no haya sido prestado mas de una vez en un dia o rango de fechas
     * @return
     */
    //@Query("SELECT COUNT(l) > 0 FROM Loan l WHERE l.game.id = :gameId AND (l.loanDate BETWEEN :loanDate AND :returnDate OR l.returnDate BETWEEN :loanDate AND :returnDate)")
    //boolean existsByGameIdAndLoanDateBetweenOrReturnDateBetween(Long gameId, Date loanDate, Date returnDate, Date loanDate, Date returnDate);

    /**
     * Query que valida si un
     * @param clientId
     *tiene juegos prestaods
     * @return
     */
    //@Query("SELECT COUNT(l) FROM Loan l WHERE l.client.id = :clientId AND (l.loanDate BETWEEN :loanDate AND :returnDate OR l.returnDate BETWEEN :loanDate AND :returnDate)")
    //int countByClientIdAndLoanDateBetweenOrReturnDateBetween(Long clientId, Date loanDate, Date returnDate, Date loanDate, Date returnDate);

}

