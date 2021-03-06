package jpasearch.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import jpasearch.TestApplication;
import jpasearch.domain.EntityA;
import jpasearch.domain.EntityA_;
import jpasearch.repository.query.SearchBuilder;
import jpasearch.repository.query.SearchParameters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * @author speralta
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplication.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class EntityARepositoryIT {

    @Inject
    private EntityARepository entityARepository;

    @Test
    public void test() {
        final String testValue = "test";

        assertThat(entityARepository.findCount(findByValue(testValue))).isEqualTo(0);

        EntityA entityA = new EntityA();
        entityA.setValue(testValue);
        entityA = entityARepository.save(entityA);

        List<EntityA> founds = entityARepository.find(findByValue(testValue));

        assertThat(founds).containsExactly(entityA);
    }

    private SearchParameters<EntityA> findByValue(String value) {
        return new SearchBuilder<EntityA>() //
                .on(EntityA_.value).equalsTo(value) //
                .build();
    }

}
