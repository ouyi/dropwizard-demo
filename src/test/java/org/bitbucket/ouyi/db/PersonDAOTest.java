package org.bitbucket.ouyi.db;

import org.bitbucket.ouyi.business.Person;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by worker on 12/22/16.
 */
public class PersonDAOTest {

    @Test
    public void testInsertAll() {
        DBI dbi = new DBI("jdbc:h2:mem:test;IGNORECASE=TRUE;MODE=PostgreSQL");
        try (Handle handle = dbi.open()) {
            handle.createStatement("migrations/1-create-table-person.sql").execute();

            PersonDAO personDAO = new PersonDAO(dbi);
            List<Person> list = new LinkedList<>();

            ZonedDateTime timeOfStart = ZonedDateTime.now();
            list.add(new Person(1, "john", timeOfStart));
            list.add(new Person(2, "john", timeOfStart));

            personDAO.insertAll(list.iterator());

            System.out.println(handle.createQuery("select * from person").list());
        }
    }
}