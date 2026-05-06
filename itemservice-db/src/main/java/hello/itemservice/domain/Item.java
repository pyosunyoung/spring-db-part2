package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity

public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //pk, db에서 값을 넣어주는 값 자동 증가
    private Long id;

    @Column(name = "item_name", length = 10)
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}

//name = "item_name" : 객체는 itemName 이지만 테이블의 컬럼은 item_name 이므로 이렇게 매핑
//했다.
//length = 10 : JPA의 매핑 정보로 DDL( create table )도 생성할 수 있는데, 그때 컬럼의 길이 값으
//로 활용된다. ( varchar 10 )
