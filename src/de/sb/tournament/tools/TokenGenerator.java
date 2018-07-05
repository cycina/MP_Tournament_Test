package de.sb.tournament.tools;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import de.sb.toolbox.Copyright;
import de.sb.tournament.persistence.Token;


/**
 * Creates a given number of tokens (default 10) and prints their aliases to the console. Note that tokens can have arbitrary
 * unique aliases; however, in order to faciliate ease of use, they are generated as uniform lowercase zero-padded hexadecimal
 * representations of secure random unsigned 64-bit integers, with a length of 16 characters each. Each token is configured for
 * a validity period of 15 minutes after first use.
 */
@Copyright(year=2013, holders="Sascha Baumeister")
public class TokenGenerator {
	static private final Random RANDOMIZER = new SecureRandom();
	static private final long VALIDITY_PERIOD = TimeUnit.MINUTES.toMillis(15);

	/**
	 * Application entity point.
	 * @param args the sole optional argument is the number of tokens to be generated.
	 */
	public static void main (final String[] args) {
		final int tokenCount = args.length == 0 ? 10 : Integer.parseInt(args[0]);
		final List<String> tokenAliases = new ArrayList<>();

		final EntityManagerFactory tournamentFactory = Persistence.createEntityManagerFactory("tournament");
		try {
			final EntityManager tournamentManager = tournamentFactory.createEntityManager();
			try {
				tournamentManager.getTransaction().begin();
				tournamentManager
					.createQuery("delete from Token as t where t.activationTimestamp + t.validityDuration < :time")
					.setParameter("time", System.currentTimeMillis())
					.executeUpdate();
				tournamentManager.getTransaction().commit();

				tournamentManager.getTransaction().begin();
				for (int loop = tokenCount; loop > 0; --loop) {
					final String alias = String.format("%016x", new BigInteger(64, RANDOMIZER));
					final Token token = new Token(alias, VALIDITY_PERIOD);
					tournamentManager.persist(token);
					tokenAliases.add(alias);
				}
				tournamentManager.getTransaction().commit();
			} catch (final Exception exception) {
				if (tournamentManager.getTransaction().isActive()) tournamentManager.getTransaction().rollback();
				throw exception;
			} finally {
				tournamentManager.close();
			}
		} finally {
			tournamentFactory.close();
		}

		System.out.println("Generated token aliases:");
		for (final String alias : tokenAliases) {
			System.out.println(alias);
		}
	}
}