package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional // JPA 데이터 변경은 반드시 트랜잭션 안에서 이루어짐.
public class JpaItemRepositoryV1 implements ItemRepository {

    private final EntityManager em; // jpa쓰려면 반드시 이 의존과계를 주입 받아야 crud 가능
    //JPA의 모든 동작은 엔티티 매니저를 통해서 이루어진다. 엔티티 매니저
    //는 내부에 데이터소스를 가지고 있고, 데이터베이스에 접근할 수 있다
    public JpaItemRepositoryV1(EntityManager em) {
        this.em = em;
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName()); // 업데이트 값을 가져와서 해당 find로 찾은 item에 넣어서 수정.
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());


    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);

    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
//        String jpql = "select i from Item i"; //i는 entity자체를 반환, Item은 domain의 Item을 가리킴

        String jpql = "select i from Item i";
        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }
        log.info("jpql={}", jpql);
        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();
    }
}
