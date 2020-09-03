package ua.com.store.demo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(of = {"symbol", "name", "price","exchange"})
@Getter
@Setter
public class CompanyJson {
    private String symbol;
    private String name;
    private String price;
    private String exchange;
}
