package org.bitbucket.ouyi.business;

import liquibase.util.csv.opencsv.CSVParser;
import org.bitbucket.ouyi.io.PersonDAO;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by worker on 12/21/16.
 */
public class Transformer {

    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int TIME_INDEX = 2;

    private CSVParser parser;
    private DateTimeFormatter dateTimeFormat;
    private PersonDAO personDAO;

    public Transformer(CSVParser parser, DateTimeFormatter dateTimeFormat, PersonDAO personDAO) {
        this.parser = parser;
        this.dateTimeFormat = dateTimeFormat;
        this.personDAO = personDAO;
    }

    public void transform(Stream<String> lines) throws Exception {
        Iterator<Person> iterator = lines
                .map(parseLine.andThen(removeObs.andThen(nameToLowercase.andThen(toPersonUTC))))
                .distinct()
                .iterator();
        personDAO.insertAll(iterator);
    }

    protected Function<String, String[]> parseLine = l -> {
        try {
            return parser.parseLine(l);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    };

    // Assumption: Obs. is the last element
    protected Function<String[], String[]> removeObs = s -> Arrays.copyOfRange(s, 0, s.length - 1);

    protected Function<String[], String[]> nameToLowercase = s -> {
        s[NAME_INDEX] = s[NAME_INDEX].toLowerCase();
        return s;
    };

    protected Function<String[], Person> toPersonUTC = s -> {
        ZonedDateTime dateTime = ZonedDateTime.parse(s[TIME_INDEX], dateTimeFormat);
        return new Person(Integer.parseInt(s[ID_INDEX]), s[NAME_INDEX], dateTime.withZoneSameInstant(ZoneOffset.UTC));
    };
}
