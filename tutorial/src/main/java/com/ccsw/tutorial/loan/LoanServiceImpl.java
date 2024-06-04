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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    public Page<Loan> findPage(LoanSearchDto dto) {
        return this.loanRepository.findAll(dto.getPageable().getPageable());
    }

    @Override
    public void save(LoanDto dto) {
        /**
         Date loanDate = dto.getLoan_date();
         Date returnDate = dto.getReturn_date();
         Long gameId = dto.getGame().getId();
         Long clientId = dto.getClient().getId();

         if (isGameAlreadyLoaned(gameId, loanDate, returnDate)) {
         throw new IllegalArgumentException("El juego ya está prestado en el rango de fechas especificado.");
         }

         if (hasClientExceededLoanLimit(clientId, loanDate, returnDate)) {
         throw new IllegalArgumentException("El cliente ya tiene más de dos juegos prestados en el rango de fechas especificado.");
         }
         **/

        Loan loan = new Loan();
        BeanUtils.copyProperties(dto, loan, "id", "client", "game");
        loan.setClient(clientService.get(dto.getClient().getId()));
        loan.setGame(gameService.get(dto.getGame().getId()));

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
     * @param clientName  o por
     * @param titleGame
     */
    @Override
    public List<Loan> find(String titleGame, String clientName, String date) {
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

            return loanRepository.findAll(spec);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     private boolean isGameAlreadyLoaned(Long gameId, Date loanDate, Date returnDate) {
     return loanRepository.existsByGameIdAndLoanDateBetweenOrReturnDateBetween(gameId, loanDate, returnDate, loanDate, returnDate);
     }

     private boolean hasClientExceededLoanLimit(Long clientId, Date loanDate, Date returnDate) {
     int loansCount = loanRepository.countByClientIdAndLoanDateBetweenOrReturnDateBetween(clientId, loanDate, returnDate, loanDate, returnDate);
     return loansCount >= 2;
     }
     **/
}
