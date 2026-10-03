package microarch.delivery.core.application.commands;

import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * <p>
 * Класс необходим, т.к. при возвращении Result.failure() Spring не считает это ошибкой, транзакция не откатится</>
 */
final class CommandTransactions {

    private CommandTransactions() {
    }

    static void rollbackOnFailure() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
