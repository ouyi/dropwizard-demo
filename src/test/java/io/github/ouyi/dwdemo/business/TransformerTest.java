package io.github.ouyi.dwdemo.business;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.github.ouyi.dwdemo.io.PersonDAO;
import liquibase.util.csv.opencsv.CSVParser;
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
                "1,Alice,11-16-1980 12:00:12,Some observations",
                "1,Alice,11-16-1980 12:00:12,Some observation",
                "2,Bob,11-06-1981 12:00:12,Some other observations"
        );
        Person[] expected = {
                new Person(1, "alice", ZonedDateTime.parse("11-16-1980 12:00:12", formatter).withZoneSameInstant(ZoneOffset.UTC)),
                new Person(2, "bob", ZonedDateTime.parse("11-06-1981 12:00:12", formatter).withZoneSameInstant(ZoneOffset.UTC)),
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
    public void transformMoreData() throws Exception {
        Stream<String> inputLines = Stream.of(
                "Id,name,time_of_start,comments,,",
                "1,John,12-06-1980 12:00:12,Foo,,",
                "1,John,12-06-1980 12:00:12,Bar,,",
                "2,Charlie Foobar,12-06-1981 12:00:12,Foo bar,,",
                "3,,,,,",
                "4,,,,,",
                "1,John,12-06-1980 12:00:12,Not satisfied,,",
                "1,John,12-06-1980 12:00:12,Not satisfied,,",
                "4,,,,,",
                "4,,,,,",
                "4,,,,,",
                "2,Charlie Foobar,12-6-84 12:00:12,Foo bar,,",
                "2,Charlie Foobar,12-6-89 12:00:12,Foo bar,,",
                "2,Charlie Foobar,12-6-86 12:00:12,Foo bar,,",
                "1,Foo Bar,12-6-86 12:00:12,FooBar,,",
                ",,,,,",
                "7,This is the last line,28-08-2016 00:00:00, This column is supposed to be removed,,"
        );
        Person[] expected = {
                new Person(1, "john", ZonedDateTime.parse("12-06-1980 12:00:12", formatter).withZoneSameInstant(ZoneOffset.UTC)),
                new Person(2, "charlie foobar", ZonedDateTime.parse("12-06-1981 12:00:12", formatter).withZoneSameInstant(ZoneOffset.UTC))
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