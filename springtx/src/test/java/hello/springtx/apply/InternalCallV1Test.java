package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired CallService callService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
    }

    @Test
    void internalCall() {
        callService.internal();
    }

    @Test
    void externalCall() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {

        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    static class CallService{

        public void external() {
            log.info("call external");
            printTxInfo();
            internal(); //this.internal이 생략되어있음 그래서 실제 대상 객체의 인스턴스를 뜻해서 프록시를 거치지 않고 호출됨. 그래서 트랜잭션 적용 x
            //즉 target.internal이렇게 직접 호출되어지는 것임. 프록시를 거쳐야 하는데 객체 그대로를 호출해서 이런 현상 발생
        } //this는 자기 자신을 가리키므로 실제 대상 객체의 인스턴스를 뜻한다. 결과적으로 내부 호출은 프록시를 거치지 않는다.
        //이것 해결방법은 실무에선 internal 클래스를 분리해서 적용하는 방법이 있다.
        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }


        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive); //트랜잭션 적용 여부
        }
    }


}
