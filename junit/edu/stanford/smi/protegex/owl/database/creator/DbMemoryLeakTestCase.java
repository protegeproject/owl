package edu.stanford.smi.protegex.owl.database.creator;

import java.io.File;
import java.net.MalformedURLException;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.test.APITestCase;
import edu.stanford.smi.protegex.owl.jena.creator.notont.AbstractCreatorTestCase;

public class DbMemoryLeakTestCase extends AbstractCreatorTestCase {
    public static final int REPETITIONS = 20;
    public static final String PIZZA_FILE = "examples/pizza.owl";
    
    public void testStreamingParseMemoryLeak() throws MalformedURLException, OntologyLoadException {
        for (DBType dbt : DBType.values()) {
            APITestCase.setDBType(dbt);
            if (!APITestCase.dbConfigured()) {
                continue;
            }
            for (int counter  = 0; counter < REPETITIONS; counter++) {
                OwlDatabaseFromFileCreator creator = new OwlDatabaseFromFileCreator();
                configureDbCreator(creator);
                creator.setTable("JunitPizza");
                creator.setOntologySource(new File(PIZZA_FILE).toURL().toString());
                creator.create(errors);
                handleErrors();
                creator.getProject().dispose();
            }
        }
    }

}
