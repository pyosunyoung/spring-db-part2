package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper // 이Mapper를 붙여야 MyBtis에서 인식할 수 있음
public interface ItemMapper {

    void save(Item item);

    void update(@Param("id") Long id, @Param("updateParam")ItemUpdateDto updateParam); // 파라미터 두개면 애노테이션 @Param 작성해야줘야함

    List<Item> findAll(ItemSearchCond itemSearch);

    Optional<Item> findById(Long id);
}
