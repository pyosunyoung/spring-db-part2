package hello.springtx.order;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    //jpa는 table은 order객체의 @Table때문에 자동 생성됨.
    @Test
    void complete() throws NotEnoughMoneyException {
        //given
        Order order = new Order();
        order.setUsername("정상");
        //when
        orderService.order(order);
        //then

        Order findOrder = orderRepository.findById(order.getId()).get();
        Assertions.assertThat(findOrder.getPayStatus()).isEqualTo("완료");


    }

    @Test
    void runtimeException() {
        //given
        Order order = new Order();
        order.setUsername("예외");
        //when
        Assertions.assertThatThrownBy(() -> orderService.order(order)).isInstanceOf(RuntimeException.class);

        //then
        Optional<Order> orderOptional = orderRepository.findById(order.getId());
        Assertions.assertThat(orderOptional.isEmpty()).isTrue(); // optional은 값이 있다 없다를 알 수 있음.
        //런타임은 롤백이 되어있어야하기 떄문에 안에 아무런 값도 없어야 한다. 즉 empty가 true
    }

    @Test
    void bizException() { //비즈니스 예외, 발생해도 커밋은 됨.
        //given
        Order order = new Order();
        order.setUsername("잔고부족");
        //when
        try {
            orderService.order(order);
            fail("잔고 부족 예외가 발생해야 합니다.");
        } catch (NotEnoughMoneyException e) {
            log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
        }
        //then
        Order findOrder = orderRepository.findById(order.getId()).get();
        Assertions.assertThat(findOrder.getPayStatus()).isEqualTo("대기");
    } //잔고부족 시 예외 발생하면 커밋은 되어야함. 다시 롤백하면 처음부터 다시 작성해야하니 결제 같은거?
} //그런식으로 설계하라는 것을 알려주는 것 같음.


//NotEnoughMoneyException 은 시스템에 문제가 발생한 것이 아니라, 비즈니스 문제 상황을 예외를 통해 알려준다.
// 마치 예외가 리턴 값 처럼 사용된다. 따라서 이 경우에는 트랜잭션을 커밋하는 것이 맞다. 이 경우 롤백하면 생성한 Order 자체가 사라진다. 그러면 고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내해도 주문( Order ) 자체가 사라지기 때문에 문제가 된다.
//그런데 비즈니스 상황에 따라 체크 예외의 경우에도 트랜잭션을 커밋하지 않고, 롤백하고 싶을 수 있다. 이때는rollbackFor 옵션을 사용하면 된다.
//런타임 예외는 항상 롤백된다. 체크 예외의 경우 rollbackFor 옵션을 사용해서 비즈니스 상황에 따라서 커밋과 롤백을 선택하면 된다.

//예외는 모두 예외로 설정할 수 있고, return 값으로 "잔고부족" 이런식으로 설정할 수가 있는데 이건 알아서 잘 선택해주면 된다.