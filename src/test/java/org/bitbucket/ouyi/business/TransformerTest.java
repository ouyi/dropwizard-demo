package org.bitbucket.ouyi.business;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import liquibase.util.csv.opencsv.CSVParser;
import org.bitbucket.ouyi.io.PersonDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by worker on 12/22/16.
 */
public class TransformerTest {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss").withZone(ZoneId.of("Europe/Berlin"));

    @Captor
    private ArgumentCaptor<Iterator<Person>> argumentCaptor;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
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
        Stream<String> inputLines = Stream.of(
                "1,John,12-06-1980 12:00:12,Some observations",
                "1,John,12-06-1980 12:00:12,Some observation",
                "2,Mary,12-06-1981 12:00:12,Some other observations"
        );
        Person[] expected = {
                new Person(1, "john", ZonedDateTime.parse("12-06-1980 12:00:12", formatter).withZoneSameInstant(ZoneOffset.UTC)),
                new Person(2, "mary", ZonedDateTime.parse("12-06-1981 12:00:12", formatter).withZoneSameInstant(ZoneOffset.UTC)),
        };

        PersonDAO personDAO = mock(PersonDAO.class);
        Transformer transformer = new Transformer(new CSVParser(), formatter, personDAO);
        transformer.transform(inputLines);
        verify(personDAO).insertAll(argumentCaptor.capture());

        Iterator<Person> arg = argumentCaptor.getValue();
        Person[] actual = Iterables.toArray(Lists.newArrayList(arg), Person.class);
        assertThat(actual.length).isEqualTo(expected.length);
        assertThat(matchAll(actual, expected)).isTrue();
    }

    @Test
    public void transformRealData() throws Exception {
        Stream<String> inputLines = Stream.of(
                "Id,name,time_of_start,Obs.,,",
                "1,John,12-06-1980 12:00:12,Not satisfied,,",
                "1,John,12-06-1980 12:00:12,Satisfied,,",
                "2,Marrie Angelina,12-06-1981 12:00:12,Did not want to buy second plan,,",
                "3,,,,,",
                "4,,,,,",
                "1,John,12-06-1980 12:00:12,Not satisfied,,",
                "1,John,12-06-1980 12:00:12,Not satisfied,,",
                "4,,,,,",
                "4,,,,,",
                "4,,,,,",
                "2,Marrie Angelina,12-6-84 12:00:12,Did not want to buy second plan,,",
                "2,Marrie Angelina,12-6-89 12:00:12,Did not want to buy second plan,,",
                "2,Marrie Angelina,12-6-86 12:00:12,Did not want to buy second plan,,",
                "1,John McDonalds,12-6-86 12:00:12,McDonalds,,",
                ",,,,,"
        );
        Person[] expected = {
                new Person(2, "marrie angelina", ZonedDateTime.parse("12-06-1986 12:00:12", formatter).withZoneSameInstant(ZoneOffset.UTC)),
                new Person(1, "john mcdonalds", ZonedDateTime.parse("12-06-1986 12:00:12", formatter).withZoneSameInstant(ZoneOffset.UTC))
        };

        PersonDAO personDAO = mock(PersonDAO.class);
        Transformer transformer = new Transformer(new CSVParser(), formatter, personDAO);
        transformer.transform(inputLines);
        verify(personDAO).insertAll(argumentCaptor.capture());

        Iterator<Person> arg = argumentCaptor.getValue();
        Person[] actual = Iterables.toArray(Lists.newArrayList(arg), Person.class);
        assertThat(actual.length).isEqualTo(expected.length);
        assertThat(matchAll(actual, expected)).isTrue();
    }

    private boolean matchAll(Person[] a, Person[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!matchPerson(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean matchPerson(Person p1, Person p2) {
        if (p1.getId() != p2.getId()) {
            return false;
        }
        if (!p1.getName().equals(p2.getName())) {
            return false;
        }
        if (!p1.getTimeOfStart().equals(p2.getTimeOfStart())) {
            return false;
        }
        return true;
    }
}