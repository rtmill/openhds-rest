package org.openhds.resource;

import org.junit.Test;
import org.openhds.domain.model.Location;
import org.openhds.domain.registration.LocationRegistration;
import org.openhds.repository.FieldWorkerRepository;
import org.openhds.repository.LocationHierarchyRepository;
import org.openhds.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by Ben on 5/26/15.
 */
public class LocationRestControllerTest extends AbstractRestControllerTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private LocationHierarchyRepository locationHierarchyRepository;

    private LocationRegistration makeLocationRegistration(String hierarchyName, String name) {
        Location location = new Location();
        location.setName(name);
        location.setExtId(name);

        LocationRegistration locationRegistration = new LocationRegistration();
        locationRegistration.setLocation(location);
        locationRegistration.setLocationHierarchyUuid(locationHierarchyRepository.findByExtId(hierarchyName).get().getUuid());
        locationRegistration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());

        return locationRegistration;
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void postNewLocation() throws Exception {

        String jsonBody = this.toJson(makeLocationRegistration("bottom-one", "test-location"));
        mockMvc.perform(post("/locations")
                .content(jsonBody)
                .contentType(regularJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(halJson));
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void putNewLocation() throws Exception {

        final String uuid = UUID.randomUUID().toString();

        String jsonBody = this.toJson(makeLocationRegistration("bottom-one", "test-location"));
        MvcResult mvcResult = mockMvc.perform(put("/locations/" + uuid)
                .content(jsonBody)
                .contentType(regularJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(halJson))
                .andReturn();

        String selfHref = extractJsonPath(mvcResult, "$._links.self.href");
        assertThat(selfHref, containsString(uuid));
    }
}