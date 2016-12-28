package org.bitbucket.ouyi.io;

import org.bitbucket.ouyi.business.Person;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by worker on 12/22/16.
 */
public class PersonDAOTest {

    @Test
    public void insertAll() {
        DBI dbi = new DBI("jdbc:h2:mem:test;IGNORECASE=TRUE;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        PersonDAO personDAO = new PersonDAO(dbi);
        try (Handle handle = dbi.open()) {
            handle.createStatement("migrations/1-create-table-person.sql").execute();
            handle.createStatement("delete from person").execute();
        }
        List<Person> list = new LinkedList<>();

        ZonedDateTime timeOfStart = ZonedDateTime.now();
        list.add(new Person(1, "john", timeOfStart));
        list.add(new Person(2, "mary", timeOfStart));
        personDAO.insertAll(list.iterator());

        try (Handle handle = dbi.open()) {
            assertThat(handle.createQuery("select * from person").list().size()).isEqualTo(2);
        }
    }
}