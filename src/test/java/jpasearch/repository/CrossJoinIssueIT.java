package jpasearch.repository;

import io.blep.spysql.SpyDataSource;
import io.blep.spysql.SqlListener;
import jpasearch.TestApplication;
import jpasearch.domain.EntityA;
import jpasearch.domain.EntityA_;
import jpasearch.domain.EntityB;
import jpasearch.domain.EntityB_;
import jpasearch.repository.query.SearchBuilder;
import jpasearch.repository.query.SearchParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author blep
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplication.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class CrossJoinIssueIT {

    @Inject
    private EntityARepository entityARepository;

    @Inject
    private SpyDataSource spyDataSource;

    @PersistenceContext
    private EntityManager em;

    private final SqlListener listener = new SqlListener() {
        @Override
        public void queryExecuted(String s) {
            assertThat(s).doesNotContain("cross");
        }
    };

    @Before
    public void setUp() throws Exception {
        spyDataSource.addListener(listener);
    }

    @After
    public void tearDown() throws Exception {
        spyDataSource.removeListener(listener);
    }

    @Test // --> This test fails
    public void searchParameter_should_not_use_cross_join_instead_of_inner() throws Exception {
        final SearchParameters<EntityA> sp = new SearchBuilder<EntityA>().on(EntityA_.b).to(EntityB_.value).equalsTo("test").build();

        entityARepository.find(sp);
    }

    @Test
    public void criteria_should_not_use_cross_join_instead_of_inner() throws Exception {
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<EntityA> q = cb.createQuery(EntityA.class);
            final Join<EntityA, EntityB> b = q.from(EntityA.class).join(EntityA_.b);
            q.where(cb.equal(b.get(EntityB_.value),"test"));

            em.createQuery(q).getResultList();
    }
}
