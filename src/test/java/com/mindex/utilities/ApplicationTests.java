package com.mindex.utilities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.mindex.utilities.ToDoRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ToDoRepository toDoRepository;

    @Before
    public void deleteAllBeforeTests() throws Exception {
        toDoRepository.deleteAll();
    }

    /*  As a user, I want to call a service endpoint that returns a list of To Do items
     *  Acceptance Criteria:
     *      Done when a GET returns a list of To Do items in JSON format
     */
    @Test
    public void shouldRetrieveAllEntities() throws Exception {

        mockMvc.perform(post("/toDo").content(
                "{\"title\": \"be born\", \"description\":\"It's your birthday!\", \"dueDate\":\"1985-01-08T08:00:00\"}"));
        mockMvc.perform(post("/toDo").content(
                "{\"title\": \"garbage\", \"description\":\"How about you take out the garbage?\", \"dueDate\":\"2017-07-07T18:00:00\"}"));

        mockMvc.perform(get("/toDo")).andExpect(
                status().isOk()).andExpect(
                        jsonPath("$._embedded.toDo[1].title").value("garbage"));
    }

    /*  As a user, I want to call a service endpoint that returns the details of a single To Do item
     *  Acceptance Criteria:
     *      Done when a GET for a valid To Do item ID returns Title, Description and Due Date in JSON format
     */
    @Test
    public void shouldRetrieveEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/toDo").content(
                "{\"title\": \"myTitle\", \"description\":\"myDescription\", \"dueDate\":\"2017-07-06T00:00:00\"}")).andExpect(
                        status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.title").value("myTitle")).andExpect(
                        jsonPath("$.description").value("myDescription")).andExpect(
                                jsonPath("$.dueDate").exists());
    }

    /*  As a user, I want to call a service endpoint that returns the details of a single To Do item
     *  Acceptance Criteria:
     *      Done when a GET for a To Do item ID that doesn’t exist returns an HTTP 404 status code
     */
    @Test
    public void shouldReturn404ForNonexistantId() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/toDo").content(
                "{\"title\": \"myTitle\", \"description\":\"myDescription\", \"dueDate\":\"2017-07-06T00:00:00\"}")).andExpect(
                        status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location + "314159")).andExpect(status().isNotFound());
    }


    /*  As a user, I want to call a service endpoint that updates a single To Do item
     *  Acceptance Criteria:
     *      Done when a PATCH to an existing item updates the Title, Description and/or Due Date
     */
    @Test
    public void shouldUpdateEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/toDo").content(
                "{\"title\": \"myTitle\", \"description\":\"myDescription\", \"dueDate\":\"2017-07-06T00:00:00\"}")).andExpect(
                        status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(patch(location).content(
                "{\"title\": \"myTitle\", \"description\":\"myNewDescription\", \"dueDate\":\"2017-07-06T00:00:00\"}")).andExpect(
                        status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.title").value("myTitle")).andExpect(
                        jsonPath("$.description").value("myNewDescription"));
    }

    /*  As a user, I want to call a service endpoint that updates a single To Do item
     *  Acceptance Criteria:
     *      Done when a PATCH to a non-existent To Do Item returns an HTTP 404 status code
     */
    @Test
    public void shouldNotUpdateEntityDueToInvalidId() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/toDo").content(
                "{\"title\": \"myTitle\", \"description\":\"myDescription\", \"dueDate\":\"2017-07-06T00:00:00\"}")).andExpect(
                        status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(patch(location + "314159").content(
                "{\"title\": \"myTitle\", \"description\":\"myDescription\"}")).andExpect(
                        status().isNotFound());
    }

    /*  As a user, I want to call a service endpoint that creates a new To Do item
     *  Acceptance Criteria:
     *      Done when a successful POST that includes Title, Description and Due Date returns an HTTP 201 status
     *      Done when a successful POST returns the created object
     */
    @Test
    public void shouldCreateEntityAndRespondWithEntity() throws Exception {

        mockMvc.perform(post("/toDo").content(
                "{\"title\": \"be born\", \"description\":\"It's your birthday!\", \"dueDate\":\"1985-01-08T08:00:00\"}")).andExpect(
                        status().isCreated()).andExpect(
                                header().string("Location", containsString("/toDo/")));
    }

    /*  As a user, I want to call a service endpoint that creates a new To Do item
     *  Acceptance Criteria:
     *      Done when an unsuccessful POST due to any reason returns an HTTP 400 status
     */
    @Test
    public void shouldReturnStatus400DueToUnsuccessfulPost() throws Exception {

        mockMvc.perform(post("/toDo").content(
                "{\"title\": \"be born\", \"description\":\"It's your birthday!\", \"dueDate\":\"invalid due date\"}")).andExpect(
                        status().isBadRequest());
    }




    /*
     * EXTRA CREDIT!
     */
    @Test
    public void shouldDeleteEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/toDo").content(
                "{ \"title\": \"myTitle\", \"description\":\"myDescription\"}")).andExpect(
                        status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(delete(location)).andExpect(status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotDeleteEntityDueToInvalidId() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/toDo").content(
                "{ \"title\": \"myTitle\", \"description\":\"myDescription\"}")).andExpect(
                        status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(delete(location + "314159")).andExpect(status().isNotFound());    }
}