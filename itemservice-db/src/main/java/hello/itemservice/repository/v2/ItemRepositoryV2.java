package hello.itemservice.repository.v2;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepositoryV2 extends JpaRepository<Item, Long> {
    //item의 기본적인 crud, 간단 조회가 들어감, 스프링 데이터 JPA는 BEAN에 자동 등록됨.
}
