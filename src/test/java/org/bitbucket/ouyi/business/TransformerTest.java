package org.bitbucket.ouyi.business;

import org.bitbucket.ouyi.db.PersonDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by worker on 12/22/16.
 */
public class TransformerTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void transform() throws Exception {

        DBI dbi = new DBI("jdbc:h2:mem:test;IGNORECASE=TRUE;MODE=PostgreSQL");
        try (Handle handle = dbi.open()) {
            handle.createStatement("migrations/1-create-table-person.sql").execute();

            PersonDAO personDAO = new PersonDAO(dbi);
            Transformer transformer = new Transformer(personDAO);
            Stream<String> lines = Stream.of(
                    "1,John,12-06-1980 12:00:12,Some observations",
                    "1,John,12-06-1980 12:00:12,Some observation",
                    "2,Mary,12-06-1981 12:00:12,Some other observations"
            );

            transformer.transform(lines);
            List<Map<String, Object>> records = handle.createQuery("select * from person").list();
            assertThat(records.size()).isEqualTo(2);
        }

    }

}