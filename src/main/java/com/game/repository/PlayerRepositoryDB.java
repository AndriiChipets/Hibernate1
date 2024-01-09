package com.game.repository;

import com.game.entity.Player;
import jakarta.annotation.PreDestroy;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;
    private static final String FIND_ALL_QUERY_SQL = "SELECT * FROM player";

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.HBM2DDL_AUTO, "validate");
        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        List<Player> players;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            NativeQuery<Player> query = session.createNativeQuery(FIND_ALL_QUERY_SQL, Player.class);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            players = query.list();
            transaction.commit();
        }
        return players;
    }

    @Override
    public int getAllCount() {
        int getAllCount;
        try (Session session = sessionFactory.openSession()) {
            Query<Player> getAllCountQuery = session.createNamedQuery("Get_All_Count", Player.class);
            getAllCount = getAllCountQuery.list().size();
        }
        return getAllCount;
    }

    @Override
    public Player save(Player player) {
        Player savedPlayer;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            savedPlayer = (Player) session.save(player);
            transaction.commit();
        }
        return savedPlayer;
    }

    @Override
    public Player update(Player player) {
        Player updatedPlayer;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            updatedPlayer = (Player) session.merge(player);
            transaction.commit();
        }
        return updatedPlayer;
    }

    @Override
    public Optional<Player> findById(long id) {
        Optional<Player> playerOptional;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            playerOptional = Optional.ofNullable(session.get(Player.class, id));
            transaction.commit();
        }
        return playerOptional;
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(player);
            transaction.commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}
