package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SimpleJdbcInsert
 *
 */
@Slf4j
@Repository
public class JdbcTemplateItemRepositoryV3 implements ItemRepository {

//    private final JdbcTemplate template;
    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcTemplateItemRepositoryV3(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("item") // 테이블 지정
                .usingGeneratedKeyColumns("id"); // pk 컬럼명 지정
//                .usingColumns("item_name", "price", "quantity"); // 생략가능 =>  이건 simpleJdbcInsert는 알아서 dataSource를 통해 컬럼을 읽어줌.

    }

    @Override
    public Item save(Item item) {
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(item);
        Number key = jdbcInsert.executeAndReturnKey(param);
        item.setId(key.longValue());
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item " +
                "set item_name=:itemName, price=:price, quantity=:quantity " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource() // 위에 beanPropertySql과 결과는 동일하지만 방식은 다른 방식
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);
        template.update(sql, param);
        
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = :id";
        try {
            Map<String, Object> param = Map.of("id", id); // 이렇게 map으로도 param 삽입 가능.
            Item item = template.queryForObject(sql, param,itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } // 디비 반환 결과를 item 객체로 바꾸는 로직이 itemRowMapper()
        //Optional null일 수 있지만 queryForObject는 결과가 없으면 예외가 있어서 무조건 값이 있음 ㅋ
    }
    //item 객체로 바꿔주는 로직.
    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class); // result set가지고 알아서 item객체로 변경시켜줌.
    } ; //camel 변환 지원

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        SqlParameterSource param = new BeanPropertySqlParameterSource(cond); // cond 데이터를 param에 넣어둔것

        String sql = "select id, item_name, price, quantity from item";
//        template.query(sql, itemRowMapper()); //한개만 가져올 떄 objectQuery, 전체 가져오면 query
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= :maxPrice";
        }
        log.info("sql={}", sql);
        return template.query(sql, param ,itemRowMapper());
    }
}
