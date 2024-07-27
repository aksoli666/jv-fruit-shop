package core.basesyntax;

import core.basesyntax.dao.ReportDao;
import core.basesyntax.dao.impl.ReportDaoImpl;
import core.basesyntax.model.FruitTransaction;
import core.basesyntax.model.Operation;
import core.basesyntax.service.BalanceSetter;
import core.basesyntax.service.Reader;
import core.basesyntax.service.ReportGenerator;
import core.basesyntax.service.ReportSender;
import core.basesyntax.service.TransactionProcess;
import core.basesyntax.service.impl.BalanceSetterImpl;
import core.basesyntax.service.impl.ReaderImpl;
import core.basesyntax.service.impl.ReportGeneratorImpl;
import core.basesyntax.service.impl.ReportSenderImpl;
import core.basesyntax.service.impl.TransactionProcessImpl;
import core.basesyntax.strategy.StrategyFruitTransaction;
import core.basesyntax.strategy.impl.StrategyFruitTransactionImpl;
import core.basesyntax.transaction.OperationHandler;
import core.basesyntax.transaction.impl.PurchaseHandler;
import core.basesyntax.transaction.impl.ReturnHandler;
import core.basesyntax.transaction.impl.SupplyHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FruitShop {
    private static final String TRANSACTIONS_FILE_PATH
            = "src/main/java/resourses/transactions.csv";
    private static final String REPORTS_FILE_PATH
            = "src/main/java/resourses/reports.csv";
    private static final Map<Operation, OperationHandler> operations = new HashMap<>();

    static {
        operations.put(Operation.SUPPLY, new SupplyHandler());
        operations.put(Operation.PURCHASE, new PurchaseHandler());
        operations.put(Operation.RETURN, new ReturnHandler());
    }

    public static void main(String[] args) {
        StrategyFruitTransaction strategyFruitTransaction
                = new StrategyFruitTransactionImpl(operations);
        ReportDao reportDao = new ReportDaoImpl();
        Reader reader = new ReaderImpl();
        BalanceSetter balanceSetter = new BalanceSetterImpl(reportDao);
        TransactionProcess transactionProcess
                = new TransactionProcessImpl(strategyFruitTransaction, reportDao);
        ReportGenerator reportGenerator = new ReportGeneratorImpl(reportDao);
        ReportSender reportSender = new ReportSenderImpl();
        List<FruitTransaction> fruitTransactions = reader.read(TRANSACTIONS_FILE_PATH);
        balanceSetter.setBalance(fruitTransactions);
        fruitTransactions.forEach(transactionProcess::process);
        String report = reportGenerator.generate();
        reportSender.send(REPORTS_FILE_PATH, report);
    }
}
