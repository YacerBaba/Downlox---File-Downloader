package com.yacer.downlox.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DbUtils {
    public static String PERSISTENCE_UNIT = "com.yacer_test_jar_1.0-SNAPSHOTPU";
    public static EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    public static EntityManager manager = factory.createEntityManager();
}
