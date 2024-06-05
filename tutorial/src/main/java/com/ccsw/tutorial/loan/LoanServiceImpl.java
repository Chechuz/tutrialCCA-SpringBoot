package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    LoanRepository loanRepository;
    @Autowired
    ClientService clientService;

    @Autowired
    GameService gameService;

    @Override
    public Loan get(Long id) {
        return this.loanRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Loan> findPage(LoanSearchDto dto, String titleGame, String clientName, String date) {
        Specification<Loan> spec = buildSpec(titleGame, clientName, date);
        Pageable pageable = dto.getPageable().getPageable();
        return loanRepository.findAll(spec, pageable);

    }

    @Override
    public void save(LoanDto dto) throws Exception {

        // Validar si el juego ya ha sido prestado
        boolean isGameAlreadyLoaned = loanRepository.existsByGameAndLoanDateBetween(dto.getGame().getId(), dto.getLoan_date(), dto.getReturn_date());
        if (isGameAlreadyLoaned) {
            throw new Exception("El juego ya está prestado a otro cliente en el mismo período.");
        }

        // Validar si el cliente ya tiene dos juegos prestados en el mismo período
        int clientLoanCount = loanRepository.countByClientAndLoanDateBetween(dto.getClient().getId(), dto.getLoan_date(), dto.getReturn_date());

        if (clientLoanCount >= 2) {
            throw new Exception("El cliente ya tiene dos juegos prestados en el mismo período.");
        }

        Loan loan = new Loan();
        BeanUtils.copyProperties(dto, loan, "id", "client", "game");
        loan.setClient(clientService.get(dto.getClient().getId()));
        loan.setGame(gameService.get(dto.getGame().getId()));
        System.out.println("DATOS RECIBIDOS:  " + loan);

        this.loanRepository.save(loan);
    }

    @Override
    public void delete(Long id) throws Exception {

        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }
        this.loanRepository.deleteById(id);
    }

    /**
     * Metodo que filtra un {@link Loan}
     * por el
     * @param clientName  (nombre del cliente) o por
     * @param titleGame (titulo del juego)
     */
    public Specification<Loan> buildSpec(String titleGame, String clientName, String date) {
        Specification<Loan> spec;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            SearchCriteria clientCriteria = new SearchCriteria("client.name", ":", clientName);
            LoanSpecification clientSpec = new LoanSpecification(clientCriteria);

            SearchCriteria gameCriteria = new SearchCriteria("game.title", ":", titleGame);
            LoanSpecification gameSpec = new LoanSpecification(gameCriteria);

            spec = Specification.where(clientSpec).and(gameSpec);

            if (date != null) {
                Date parsedDate = formatter.parse(date);
                SearchCriteria loanDateCriteria = new SearchCriteria("loan_date", "<=", parsedDate);
                LoanSpecification loanDateSpec = new LoanSpecification(loanDateCriteria);

                SearchCriteria returnDateCriteria = new SearchCriteria("return_date", ">=", parsedDate);
                LoanSpecification returnDateSpec = new LoanSpecification(returnDateCriteria);
                spec = Specification.where(clientSpec).and(gameSpec).and(loanDateSpec).and(returnDateSpec);
            }

            return spec;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
