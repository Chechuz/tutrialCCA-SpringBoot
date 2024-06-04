package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoanService {
    /**
     *
     * Recupera un {@link Loan} a través de su ID
     *      *
     *      * @param id PK de la entidad
     *      * @return {@link Loan}
     */
    Loan get(Long id);

    /**
     * Método para recuperar un listado paginado de {@link com.ccsw.tutorial.loan.model.Loan}
     *
     * @param dto dto de búsqueda
     * @return {@link Page} de {@link com.ccsw.tutorial.loan.model.Loan}
     */
    Page<Loan> findPage(LoanSearchDto dto);

    /**
     * Método para crear o actualizar un {@link Loan}
     *
     * @param dto datos de la entidad
     */
    void save(LoanDto dto);

    /**
     * Método para crear o actualizar un {@link Loan}
     *
     * @param id PK de la entidad
     */
    void delete(Long id) throws Exception;

    /**
     * Recupera un listado de autores {@link Loan}
     *
     * @return {@link List} de {@link Loan}
     */
    List<Loan> find(String titleGame, String clientName, String date);

}
