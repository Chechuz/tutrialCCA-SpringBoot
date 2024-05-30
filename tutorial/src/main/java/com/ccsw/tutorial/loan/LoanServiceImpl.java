package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    LoanRepository loanRepository;

    @Override
    public Loan get(Long id) {
        return this.loanRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Loan> findPage(LoanSearchDto dto) {
        return this.loanRepository.findAll(dto.getPageable().getPageable());
    }

    @Override
    public void save(LoanDto data) {

        Loan loan = new Loan();
        BeanUtils.copyProperties(data, loan, "id");
        this.loanRepository.save(loan);
    }

    @Override
    public void delete(Long id) throws Exception {

        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }
        this.loanRepository.deleteById(id);
    }

    @Override
    public List<Loan> findAll() {
        return (List<Loan>) this.loanRepository.findAll();
    }

    /**
     * Metodo que filtra un {@link Loan}
     * por el
     * @param clientName  o por
     * @param titleGame
     */
    @Override
    public List<Loan> find(String titleGame, String clientName) {
        Specification<Loan> spec = Specification.where(null);

        if (clientName != null && !clientName.isEmpty()) {
            SearchCriteria clientCriteria = new SearchCriteria("client.name", ":", clientName);
            LoanSpecification clientSpec = new LoanSpecification(clientCriteria);
            spec = spec.and(clientSpec);
        }

        if (titleGame != null && !titleGame.isEmpty()) {
            SearchCriteria gameCriteria = new SearchCriteria("game.title", ":", titleGame);
            LoanSpecification gameSpec = new LoanSpecification(gameCriteria);
            spec = spec.and(gameSpec);
        }

        return loanRepository.findAll(spec);
    }

}
