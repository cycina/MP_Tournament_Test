package de.sb.tournament.persistence;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MatchEntityTest extends EntityTest{
	@BeforeClass
	@Test
	public void testConstraints() {
		
		Match entity = new Match() ;
		 javax.validation.Validator validator = this.getEntityValidatorFactory().getValidator();
		Set<ConstraintViolation<Match>> constraintViolations = validator.validate(entity);
		assertEquals( 0, constraintViolations.size() );
      
	}
  
	@AfterClass
	
	@BeforeClass
	@Test
	public void testLifeCycle() {

        EntityManager em = this.getEntityManagerFactory().createEntityManager();
		em.getTransaction().begin();
		Match entity = new Match() ;
		em.persist(entity);
		em.getTransaction().commit();
		try {
			EntityTest obj = em.getReference(EntityTest.class, entity.getIdentity());
		   
		} catch (EntityNotFoundException e) {
		    // Entity Not Found
		}
		em.flush();
		this.getWasteBasket().add( entity.getIdentity());
	    
	     javax.persistence.Cache cache = em.getEntityManagerFactory().getCache();
	    cache.evict(entity.getClass(), entity.getIdentity());
	    
	    em.close();
	    em.clear();
	}
	

}
