package controllers;

import jobs.Application;
import jobs.controllers.FCJobController;
import jobs.models.FCJob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class FCJobControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FCJobController fcJobController;

    @Before
    public void setup() throws Exception {

        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        FCJob fcJob = new FCJob(100);
        fcJobController.createFCJob(fcJob);
    }

    @Test
    public void getJob404() throws Exception {

        mockMvc.perform(get("/job/300"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void createJob() throws Exception {

        String jobJson = "{\"total\":199}";

        mockMvc.perform((post("/job"))
                .content(jobJson)
                .contentType(contentType))

                .andExpect(status().isOk());
    }

    @Test
    public void getJob() throws Exception {

        mockMvc.perform(get("/job/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void incrementJobProgress() throws Exception {

        mockMvc.perform(put("/job/1/addProgress/10"))
                .andExpect(status().isOk());

    }

    @Test
    public void setJobProgress() throws Exception {

        mockMvc.perform(put("/job/1/setProgress/75"))
                .andExpect(status().isOk());

    }

}