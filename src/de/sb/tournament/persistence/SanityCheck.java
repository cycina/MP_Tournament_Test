package de.sb.tournament.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;


/**
 * Application for sanity checking the tournament-model JPA setup.
 */
public class SanityCheck {

	public static void main (final String[] args) {
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("tournament");
		final EntityManager em = emf.createEntityManager();
		final Query query = em.createQuery("select t from Tournament as t");
		System.out.println(query.getResultList());
	}
}
