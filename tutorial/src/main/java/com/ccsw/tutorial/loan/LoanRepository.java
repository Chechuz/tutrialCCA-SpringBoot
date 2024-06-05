package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
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

    //Consulta para valider si un juego está prestado en un rango de fechas determinado
    @Query("SELECT COUNT(l) > 0 FROM Loan l WHERE l.game.id = :gameId AND l.loan_date <= :endDate AND l.return_date >= :startDate")
    boolean existsByGameAndLoanDateBetween(@Param("gameId") Long gameId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    //Consulta para validar que un cliente ya tiene o no dos juegos prestados en ese rango de fechas
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.client.id = :clientId AND l.loan_date <= :endDate AND l.return_date >= :startDate")
    int countByClientAndLoanDateBetween(@Param("clientId") Long clientId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

