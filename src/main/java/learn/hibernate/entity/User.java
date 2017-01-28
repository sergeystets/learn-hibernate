package learn.hibernate.entity;

import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 @author Sergii Stets
 Created: 01.02.2016
 **/
@Entity
@Table(name = "user")
public class User {
    @Id
    private BigInteger id;
    private String name;

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
