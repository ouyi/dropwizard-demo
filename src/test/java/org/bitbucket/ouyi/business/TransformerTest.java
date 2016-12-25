package org.bitbucket.ouyi.business;

import liquibase.util.csv.opencsv.CSVParser;
import org.bitbucket.ouyi.io.PersonDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by worker on 12/22/16.
 */
public class TransformerTest {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss").withZone(ZoneId.of("Europe/Berlin"));

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void parseLine() {
        PersonDAO personDAO = mock(PersonDAO.class);
        Transformer transformer = new Transformer(new CSVParser(), formatter, personDAO);
        String[] parsed = transformer.parseLine.apply("a,b,c");
        assertThat(parsed).containsOnly("a", "b", "c").hasSize(3);
    }

    @Test
    public void transform() throws Exception {

        DBI dbi = new DBI("jdbc:h2:mem:test;IGNORECASE=TRUE;MODE=PostgreSQL");
        try (Handle handle = dbi.open()) {
            handle.createStatement("migrations/1-create-table-person.sql").execute();

            PersonDAO personDAO = new PersonDAO(dbi);

            Transformer transformer = new Transformer(new CSVParser(), formatter, personDAO);
            Stream<String> lines = Stream.of(
                    "1,John,12-06-1980 12:00:12,Some observations",
                    "1,John,12-06-1980 12:00:12,Some observation",
                    "2,Mary,12-06-1981 12:00:12,Some other observations"
            );

            transformer.transform(lines);
            List<Person> records = handle.createQuery("select id, name, time_of_start from person").map(new PersonDAO.PersonMapper()).list();
            assertThat(records.size()).isEqualTo(2);

            Person john = handle.createQuery("select id, name, time_of_start from person where id = :id").bind("id", 1).map(new PersonDAO.PersonMapper()).first();
            assertThat(john).isNotNull();
            assertThat(john.getName()).isEqualTo("john");
            assertThat(john.getTimeOfStart().format(formatter)).isEqualTo("12-06-1980 12:00:12");
        }

    }
}