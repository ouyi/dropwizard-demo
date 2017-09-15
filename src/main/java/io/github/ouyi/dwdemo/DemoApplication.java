package io.github.ouyi.dwdemo;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.ouyi.dwdemo.api.TransformResource;
import io.github.ouyi.dwdemo.api.UploadResource;
import io.github.ouyi.dwdemo.io.FileStorage;
import io.github.ouyi.dwdemo.io.PersonDAO;
import liquibase.util.csv.opencsv.CSVParser;
import io.github.ouyi.dwdemo.business.Transformer;
import io.github.ouyi.dwdemo.mq.WorkQueuePublisher;
import org.skife.jdbi.v2.DBI;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by worker on 12/18/16.
 */
public class DemoApplication extends Application<DemoConfiguration>{
    public static void main(String[] args) throws Exception {
        new DemoApplication().run(args);
    }
    @Override
    public void run(DemoConfiguration configuration, Environment environment) throws Exception {

        final String storageRoot = configuration.getStorageRoot();
        Files.createDirectories(Paths.get(storageRoot));

        final FileStorage fileStorage = new FileStorage(storageRoot);
        final WorkQueuePublisher publisher = configuration.getMessageQueueFactory().createPublisher(environment);
        final UploadResource uploadResource = new UploadResource(fileStorage, publisher);

        final DBIFactory factory = new DBIFactory();
        final DBI dbi = factory.build(environment, configuration.getDataSourceFactory(), "h2");
        final PersonDAO personDAO = new PersonDAO(dbi);

        final ZoneId zoneId = ZoneId.of(configuration.getTimezone());
        final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(configuration.getDateTimePattern()).withZone(zoneId);
        final Transformer transformer = new Transformer(new CSVParser(), dateTimeFormat, personDAO);
        final TransformResource transformResource = new TransformResource(fileStorage, transformer);

        environment.jersey().register(uploadResource);
        environment.jersey().register(transformResource);
    }

    @Override
    public String getName() {
        return "dwdemo";
    }

    @Override
    public void initialize(Bootstrap<DemoConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<DemoConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(DemoConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }
}
