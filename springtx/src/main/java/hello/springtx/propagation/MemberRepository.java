package hello.springtx.propagation;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    @Transactional //jpa 모든 데이터 변경은 트랜잭션에서 이루어져야함.
    public void save(Member member){
        log.info("member 저장");
        em.persist(member);
    }

    public Optional<Member> find(String username){
        return em.createQuery("select m from Member m where m.username=:username", Member.class).setParameter("username", username)
                .getResultList().stream().findAny();
    }


}
