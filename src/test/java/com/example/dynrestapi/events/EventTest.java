package com.example.dynrestapi.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void testOffline(){
        Event event = Event.builder().location("test location").build();

        event.update();

        assertThat(event.isOffline()).isTrue();

        event = Event.builder().build();

        event.update();

        assertThat(event.isOffline()).isFalse();
    }
}