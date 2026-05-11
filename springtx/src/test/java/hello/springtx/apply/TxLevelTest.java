package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest

public class TxLevelTest {


    @Autowired LevelService service;

    @Test
    void orderTest() {
        service.write();
        service.read();
    }

    @TestConfiguration
    static class TxLevelTestConfig{
        @Bean
        LevelService levelService(){
            return new LevelService();
        }

    }


    @Slf4j
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    static class LevelService{

        @Transactional(readOnly = false) //false가 기본값이라
        public void write(){
            log.info("call write");
        }

        public void read(){
            log.info("call read");
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive); //트랜잭션 적용 여부
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("tx readOnly={}", readOnly); // 트랜잭션 readOnly냐?
        }
    }
}
