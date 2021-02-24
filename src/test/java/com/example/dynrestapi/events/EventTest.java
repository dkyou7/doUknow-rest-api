package com.example.dynrestapi.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("event test Title")
                .description("event description")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){
        Event event = new Event();
        String name = "event test Title";
        String description = "spring";
        event.setName(name);
        event.setDescription(description);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    private Object[] paramsForTestFree(){
        return new Object[]{
                new Object[] {0,0,true},
                new Object[] {100,0,false},
                new Object[] {0,100,false}
        };
    }

    @Test
    @Parameters(method = "paramsForTestFree")
    public void testOffline(int basePrice,int maxPrice, boolean isFree){
        Event event = Event.builder().basePrice(basePrice).maxPrice(maxPrice).location("test location").build();
        event.update();
        assertThat(event.isFree()).isEqualTo(isFree);

        event = Event.builder().basePrice(basePrice).maxPrice(maxPrice).location("test location").build();
        event.update();
        assertThat(event.isFree()).isEqualTo(isFree);

        assertThat(event.isOffline()).isTrue();
        event = Event.builder().build();
        event.update();
        assertThat(event.isOffline()).isFalse();
    }
}