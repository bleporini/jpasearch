package jpasearch.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

/**
 * @author speralta
 */
@Entity
@Data
public class EntityA implements Identifiable<Integer> {

    @Id
    @GeneratedValue
    private Integer id;

    private String value;

    @ManyToOne
    private EntityB b;

    @Override
    public boolean isIdSet() {
        return id != null;
    }

}
