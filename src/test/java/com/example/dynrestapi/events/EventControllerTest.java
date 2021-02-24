package com.example.dynrestapi.events;

import com.example.dynrestapi.common.RestDocsConfiguration;
import com.example.dynrestapi.common.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTest {

    // 요청을 만들 수 있고 검증할 수 있음
    // 단위테스트보다는 만드는 객체가 더 많다.
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("정상적으로 이벤트 생성")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("Rest API")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,2,23,12,12))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,2,24,12,12))
                .beginEventDateTime(LocalDateTime.of(2021,2,25,12,12,12))
                .endEventDateTime(LocalDateTime.of(2021,2,26,12,12,12))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))   // json 형태로 바꿀 수 있다.
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(10L)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event"));
    }

    @Test
    @TestDescription("입력값 이외의 값이 들어온 경우 에러 발생")
    public void createFalseEvent() throws Exception {
        Event event = Event.builder()
                .id(10L)
                .name("Spring")
                .description("Rest API")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,2,23,12,12))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,2,24,12,12))
                .beginEventDateTime(LocalDateTime.of(2021,2,25,12,12,12))
                .endEventDateTime(LocalDateTime.of(2021,2,26,12,12,12))
                .basePrice(100)
                .maxPrice(200)
                .free(true)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))   // json 형태로 바꿀 수 있다.
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 null 일 때")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값 로직검증할 때")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development")
                .beginEnrollmentDateTime(LocalDateTime.of(2010, 11, 23, 14, 23))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 21, 14, 23))
                .beginEventDateTime(LocalDateTime.of(2018, 12, 24, 14, 30))
                .endEventDateTime(LocalDateTime.of(2018, 12, 6, 14, 30))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("D Start up Factory")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists());
    }
}