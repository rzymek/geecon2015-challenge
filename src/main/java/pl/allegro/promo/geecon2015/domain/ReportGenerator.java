package pl.allegro.promo.geecon2015.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.allegro.promo.geecon2015.domain.stats.FinancialStatisticsRepository;
import pl.allegro.promo.geecon2015.domain.transaction.TransactionRepository;
import pl.allegro.promo.geecon2015.domain.user.UserRepository;

@Component
public class ReportGenerator {
    
    private final FinancialStatisticsRepository financialStatisticsRepository;
    
    private final UserRepository userRepository;
    
    private final TransactionRepository transactionRepository;

    @Autowired
    public ReportGenerator(FinancialStatisticsRepository financialStatisticsRepository,
                           UserRepository userRepository,
                           TransactionRepository transactionRepository) {
        this.financialStatisticsRepository = financialStatisticsRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public Report generate(ReportRequest request) {
        Report report = new Report();
		return financialStatisticsRepository.listUsersWithMinimalIncome(request.getMinimalIncome(), request.getUsersToCheck())
    		.stream().map(uuid -> {
    			String userName = getUserName(uuid);
    			BigDecimal transactionsSum = countTransactionsSum(transactionRepository.transactionsOf(uuid));
    			return new ReportedUser(uuid, userName, transactionsSum);
    		    
    		}).forEach(u -> report.add(u));
    }

	private String getUserName(UUID uuid) {
		try {
			return userRepository.detailsOf(uuid).getName();
		} catch(Exception e) {
			return "<failed>";
		}
	}

	private BigDecimal countTransactionsSum(UserTransactions userTransactions) {
		return userTransactions.getTransactions(). .stream()
                                         .map(UserTransaction::getAmount)
                                         .reduce(BigDecimal.ZERO, BigDecimal::add);
	}    
}

