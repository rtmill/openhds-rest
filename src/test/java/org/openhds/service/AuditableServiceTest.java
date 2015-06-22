package org.openhds.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.OpenHdsRestApplication;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.repository.util.SampleDataGenerator;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by wolfe on 6/17/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenHdsRestApplication.class)
@WebAppConfiguration
public abstract class AuditableServiceTest<T extends AuditableEntity,
        U extends AbstractAuditableService>{

    @Autowired
    protected SampleDataGenerator sampleDataGenerator;

    protected U service;

    protected abstract T makeInvalidEntity();
    protected abstract T makeValidEntity(String name, String id);
    protected abstract void initialize(U service);

    @Before
    public void setup() throws Exception {
        initialize(service);
        sampleDataGenerator.clearData();
        sampleDataGenerator.generateSampleData();
    }

    @Test
    public void findAll(){
        assertNotNull(service.findAll());
    }


}